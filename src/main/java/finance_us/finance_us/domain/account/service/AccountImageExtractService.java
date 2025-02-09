package finance_us.finance_us.domain.account.service;

import finance_us.finance_us.domain.account.dto.AccountRequest;
import finance_us.finance_us.domain.account.dto.AccountResponse;
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

    public AccountResponse.AccountImageResponseDTO extractAccountFromReceipt(List<String> extractedText) {
        if (extractedText == null || extractedText.isEmpty()) {
            throw new GeneralException(ErrorStatus.IMAGE_TEXT_FAILD);
        }

        List<String> splitText = Arrays.asList(extractedText.get(0).split("\n"));
        String storeName = extractStoreName(splitText);
        String date = extractDate(splitText);
        String amount = extractAmount(splitText);
        List<String> products = extractProducts(splitText);


        if (storeName == null && date == null && amount == null) {
            throw new GeneralException(ErrorStatus.IMAGE_TEXT_FAILD);
        }

        LocalDate formattedDate = parseDate(date);
        long parsedAmount = parseAmount(amount);

//        System.out.println("Store Name: " + storeName);
//        System.out.println("Date: "+formattedDate);
//        System.out.println("Amout: "+parsedAmount);
//        System.out.println("Extracted Products: " + products);

        return AccountResponse.AccountImageResponseDTO.builder()
                .amout(parsedAmount)
                .title(storeName)
                .date(formattedDate)
                .content(products)
                .build();
    }

    private String extractStoreName(List<String> splitText) {
        List<String> knownStoreNames = Arrays.asList(
                "GS25", "이마트", "CU", "세븐일레븐", "롯데마트", "홈플러스", "미니스톱", "스타벅스", "버거킹",
                "맘스터치", "피자헛", "도미노피자", "롯데리아", "교촌치킨", "BBQ", "파리바게뜨", "뚜레쥬르",
                "엔젤리너스", "투썸플레이스", "설빙", "홍콩반점", "청년다방", "비엔나커피", "커피빈", "빽다방",
                "동대문엽기떡볶이", "한솥도시락", "신라면세점", "아이파크몰", "현대백화점", "신세계백화점",
                "롯데백화점", "강남역", "광화문", "명동", "동대문", "가로수길", "홍대", "신촌", "압구정",
                "이태원", "합정", "여의도", "판교", "분당", "청담", "강남역", "다이소", "상계", "구리", "양주"
        );

        for (String line : splitText) {
            line = normalizeText(line);  // 텍스트 정규화

            // 가게명이 포함된 라인 찾기
            for (String storeName : knownStoreNames) {
                if (line.contains(storeName)) {
                    return storeName;  // 가게명 매칭
                }
            }

            // "가맹점명:" 등 특수한 키워드 뒤에 있는 가게명 추출
            if (line.matches(".*(가맹점명:|점포명|상호명).*")) {
                return line.replaceAll(".*?\s*:", "").trim();
            }
        }
        return null;  // 가게명이 없으면 null 반환
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
                    if(nextLine.contains("계")) continue;  // "계"가 포함된 라인은 건너뛰기
                    // 금액이 "원"과 ","를 포함한 경우 이를 제거하고 숫자만 추출
                    String amount = nextLine.replaceAll("[^0-9]", "");

                    // 금액이 비어있지 않고, 숫자만 포함된 경우
                    if (!amount.isEmpty() && amount.length()<=7 ) {
                        return amount;  // 금액을 반환
                    }
                }
            }
        }
        return null;  // 금액이 없으면 null 반환
    }



    private List<String> extractProducts(List<String> splitText) {
        List<String> products = new ArrayList<>();

        for (int i = 0; i < splitText.size(); i++) { // 한 줄씩 검사
            String currentLine = splitText.get(i).trim();

            // 상품번호가 포함된 상품명 줄을 찾기 (예: "01 먹태깡 청양마요맛")
            if (currentLine.matches("^\\d{2,3}\\s+.*")) {  // "01" 이후 공백이나 다른 문자가 있는 경우를 처리
                // 상품번호(예: "01") 이후 텍스트만 추출 (숫자와 공백을 제외한 부분)
                String productName = currentLine.replaceAll("^\\d{2}\\s*", "").trim();  // 상품번호 이후 부분만 추출

                products.add(productName); // 상품명을 리스트에 추가
            }
        }

        return products;
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
