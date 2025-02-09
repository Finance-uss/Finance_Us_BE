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
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
public class AccountImageExtractService {

    public AccountRequest.AccountRequestDTO extractAccountFromReceipt(List<String> extractedText) {
        if (extractedText == null || extractedText.isEmpty()) {
            throw new GeneralException(ErrorStatus.IMAGE_TEXT_FAILD);
        }

        List<String> splitText = Arrays.asList(extractedText.get(0).split("\n"));
        String storeName = extractStoreName(splitText);
        String date = extractDate(splitText);
        String amount = extractAmount(splitText);
        String subAssetName = extractPaymentMethod(splitText);
        List<String> products = extractProducts(splitText);
        String subCategory = categorizeTransaction(storeName, products);

        System.out.println("Store Name: " + storeName);
        System.out.println("Date: "+date);
        System.out.println("Amout: "+amount);
        System.out.println("SubAssestName: "+subAssetName);
        System.out.println("SubCategory: "+subCategory);

        if (storeName == null || date == null || amount == null) {
            throw new GeneralException(ErrorStatus.IMAGE_TEXT_FAILD);
        }

        LocalDate formattedDate = parseDate(date);
        long parsedAmount = parseAmount(amount);


        return AccountRequest.AccountRequestDTO.builder()
                .date(formattedDate)
                .amount(parsedAmount)
                .title(storeName)
                .content(products.isEmpty() ? "상품 정보 없음" : String.join(", ", products))
                .subName(subCategory)
                .subAssetName(subAssetName != null ? subAssetName : "기타")
                .accountType("expense")
                .status(false)
                .score(3)
                .build();
    }

    private String extractStoreName(List<String> splitText) {
        for (String line : splitText) {
            line = normalizeText(line);
            if (line.matches(".*(가맹점명:|점포명|상호명|GS25|이마트).*")) {
                return line.replaceAll(".*?\s*:", "").trim();
            }
        }
        return null;
    }

    private String extractDate(List<String> splitText) {
        for (String line : splitText) {
            Matcher matcher = Pattern.compile("\\d{4}[-/]\\d{2}[-/]\\d{2} ?(?:\\d{2}:\\d{2})?").matcher(line);
            if (matcher.find()) {
                return matcher.group();
            }
        }
        return null;
    }

    private String extractAmount(List<String> splitText) {
        for (int i = 0; i < splitText.size(); i++) {
            String line = splitText.get(i).trim();  // 라인에서 공백을 제거

            // "합" 또는 "계"가 포함된 줄을 찾기
            if (line.contains("합") || line.contains("계")) {
                if (i + 1 < splitText.size()) {
                    String nextLine = splitText.get(i + 1).trim(); // 그 다음 줄의 금액을 추출
                    if(nextLine.contains("계")) continue;
                    // 금액이 "원"과 ","를 포함한 경우 이를 제거하고 숫자만 추출
                    String amount = nextLine.replaceAll("[^0-9]", ""); // 숫자만 남기기
                    return amount;  // 금액을 반환
                }
            }
        }
        return null;  // 금액이 없으면 null 반환
    }


    private String extractPaymentMethod(List<String> splitText) {
        for (String line : splitText) {
            if (line.contains("카드") || line.contains("현금") || line.contains("계좌")) {
                if (line.contains("신용카드")) return "신용카드";
                if (line.contains("체크카드")) return "체크카드";
                if (line.contains("선불카드")) return "선불카드";
                if (line.contains("현금")) return "현금";
                if (line.contains("급여 통장")) return "급여 통장";
                if (line.contains("CMA")) return "CMA 계좌";
            }
        }
        return "기타";
    }

    private List<String> extractProducts(List<String> splitText) {
        List<String> products = new ArrayList<>();

        for (int i = 0; i < splitText.size(); i++) { // 한 줄씩 검사
            String currentLine = splitText.get(i).trim();
            System.out.println("Line: " + currentLine);

            // 상품번호가 포함된 상품명 줄을 찾기 (예: "01 먹태깡 청양마요맛")
            if (currentLine.matches("^\\d{2}\\s+.*")) {  // "01" 이후 공백이나 다른 문자가 있는 경우를 처리
                // 상품번호(예: "01") 이후 텍스트만 추출 (숫자와 공백을 제외한 부분)
                String productName = currentLine.replaceAll("^\\d{2}\\s*", "").trim();  // 상품번호 이후 부분만 추출

                // 가격, 수량 등의 정보가 포함된 줄을 제외하려는 조건을 수정하여, 상품명만 추출
                // 예: '먹태깡 청양마요맛'만 추출하고 '1,360 3' 같은 숫자 부분은 제외하지 않음
                products.add(productName); // 상품명을 리스트에 추가
            }
        }

        // 추출된 상품명 출력
        System.out.println("Extracted Products: " + products);

        return products;
    }


    private String categorizeTransaction(String storeName, List<String> products) {
        if (storeName == null) return "기타";
        if (storeName.matches(".*(맥도날드|버거|피자|스타벅스|이디야).*")) return "외식";
        if (storeName.contains("배달")) return "배달";
        if (storeName.matches(".*(GS25|CU|이마트|코스트코).*")) return "식재료 구매";
        if (storeName.matches(".*(버스|지하철|택시).*")) return "대중교통";
        if (storeName.matches(".*(주유소|주유).*")) return "주유";
        if (storeName.matches(".*(월세|임대료).*")) return "월세";
        if (storeName.matches(".*(전기|수도|가스).*")) return "공과금";
        if (storeName.matches(".*(병원|의원).*")) return "병원비";
        if (storeName.contains("약국")) return "약국";
        if (storeName.matches(".*(영화|공연|여행).*")) return "영화/공연";

        for (String product : products) {
            if (product.contains("휘발유") || product.contains("경유") || product.contains("주유")) {
                return "주유";
            }
        }
        return "기타";
    }

    private LocalDate parseDate(String dateText) {
        if (dateText.contains(" ")) dateText = dateText.split(" ")[0];
        List<DateTimeFormatter> formatters = List.of(
                DateTimeFormatter.ofPattern("yyyy/MM/dd"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd"),
                DateTimeFormatter.ofPattern("yy.MM.dd")
        );
        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDate.parse(dateText, formatter);
            } catch (DateTimeParseException ignored) {}
        }
        throw new GeneralException(ErrorStatus.IMAGE_TEXT_FAILD);
    }

    private long parseAmount(String text) {
        if (text == null || text.isEmpty()) throw new GeneralException(ErrorStatus.IMAGE_TEXT_FAILD);
        return Long.parseLong(text);
    }

    private String normalizeText(String text) {
        return text.replaceAll("[^가-힣a-zA-Z0-9:/.-]", "").trim();
    }
}
