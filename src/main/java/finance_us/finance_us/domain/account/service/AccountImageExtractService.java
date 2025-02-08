package finance_us.finance_us.domain.account.service;

import finance_us.finance_us.domain.account.dto.AccountRequest;
import finance_us.finance_us.global.code.status.ErrorStatus;
import finance_us.finance_us.global.exception.GeneralException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class AccountImageExtractService {

    public AccountRequest.AccountRequestDTO extractAccountFromReceipt(List<String> extractedText) {
        if (extractedText == null || extractedText.isEmpty()) {
            throw new GeneralException(ErrorStatus.IMAGE_TEXT_FAILD);
        }

        String storeName = null;
        String date = null;
        String amount = null;
        List<String> products = new ArrayList<>();

        for (String line : extractedText) {
            line = normalizeText(line);

            if (storeName == null && (line.contains("가맹점명") || line.contains("점포명") || line.contains("상호명"))) {
                storeName = line.replaceAll(".*: ", "").trim();
            }
            if (date == null && (line.contains("결제일시") || line.contains("거래 일시"))) {
                date = line.replaceAll(".*: ", "").trim();
            }
            if (amount == null && (line.contains("합계") || line.contains("총액") || line.contains("결제 금액"))) {
                amount = parseAmount(line) + "";
            }
        }

        products = extractProducts(extractedText);

        if (storeName == null) throw new GeneralException(ErrorStatus.IMAGE_TEXT_FAILD);
        if (date == null) throw new GeneralException(ErrorStatus.IMAGE_TEXT_FAILD);
        if (amount == null) throw new GeneralException(ErrorStatus.IMAGE_TEXT_FAILD);

        LocalDate formattedDate = parseDate(date);
        long parsedAmount = Long.parseLong(amount);

        //카테고리 분류
        String[] categoryData = categorizeTransaction(storeName, products);
        String category = categoryData[0];
        String subCategory = categoryData[1];


        return AccountRequest.AccountRequestDTO.builder()
                .date(formattedDate)
                .amount(parsedAmount)
                .title(storeName)
                .content(products.isEmpty() ? "상품 정보 없음" : String.join(", ", products))
                .subName(category)
                .subAssetName(subCategory)
                .accountType("EXPENSE")
                .status(false)
                .score(3)
                .build();

    }

    //정규화
    private String normalizeText(String text) {
        if (text == null) return "";
        return text.replaceAll("[^가-힣a-zA-Z0-9:\\-/. ]", "").trim();
    }


    //날짜 추출
    private LocalDate parseDate(String dateText) {
        List<DateTimeFormatter> formatters = List.of(
                DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
                DateTimeFormatter.ofPattern("yy.MM.dd HH:mm"),
                DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH:mm")
        );

        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDate.parse(dateText, formatter);
            } catch (DateTimeParseException ignored) {}
        }
        throw new GeneralException(ErrorStatus.IMAGE_TEXT_FAILD);
    }


    //결제 금액 추출
    private long parseAmount(String text) {
        text = text.replaceAll("[^0-9]", ""); // 숫자만 남김
        if (text.isEmpty()) {
            throw new GeneralException(ErrorStatus.IMAGE_TEXT_FAILD);
        }
        return Long.parseLong(text);
    }


    //상품명 추출
    private List<String> extractProducts(List<String> extractedText) {
        List<String> products = new ArrayList<>();
        boolean isProductSection = false;

        for (String line : extractedText) {
            if (line.contains("상품명")) {
                isProductSection = true;
                continue;
            }

            if (isProductSection) {
                String productName = line.replaceAll("\\d{1,3}(,\\d{3})*(원|₩)?", "").trim();
                if (!productName.isEmpty()) {
                    products.add(productName);
                }
            }
        }
        return products;
    }

    //카테고리 자동 분류
    private String[] categorizeTransaction(String storeName, List<String> products) {
        if (storeName == null) return new String[]{"기타", "기타"};

        if (storeName.contains("맥도날드") || storeName.contains("버거") || storeName.contains("피자") || storeName.contains("스타벅스") || storeName.contains("이디야")) {
            return new String[]{"식비", "외식"};
        }
        if (storeName.contains("배달")) {
            return new String[]{"식비", "배달"};
        }
        if (storeName.contains("GS25") || storeName.contains("CU") || storeName.contains("이마트") || storeName.contains("코스트코")) {
            return new String[]{"식비", "식재료 구매"};
        }
        if (storeName.contains("버스") || storeName.contains("지하철") || storeName.contains("택시")) {
            return new String[]{"교통", "대중교통"};
        }
        if (storeName.contains("주유소") || storeName.contains("주유")) {
            return new String[]{"교통", "주유"};
        }
        if (storeName.contains("월세") || storeName.contains("임대료")) {
            return new String[]{"주거비", "월세"};
        }
        if (storeName.contains("전기") || storeName.contains("수도") || storeName.contains("가스")) {
            return new String[]{"주거비", "공과금"};
        }
        if (storeName.contains("병원") || storeName.contains("의원")) {
            return new String[]{"건강/의료", "병원비"};
        }
        if (storeName.contains("약국")) {
            return new String[]{"건강/의료", "약국"};
        }
        if (storeName.contains("영화") || storeName.contains("공연") || storeName.contains("여행")) {
            return new String[]{"여가/취미", "영화/공연"};
        }

        // 상품명을 기반으로 추가 분류
        for (String product : products) {
            if (product.contains("휘발유") || product.contains("경유") || product.contains("주유")) {
                return new String[]{"교통", "주유"};
            }
        }

        return new String[]{"기타", "기타"};
    }

}
