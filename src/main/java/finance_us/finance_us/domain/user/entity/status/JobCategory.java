package finance_us.finance_us.domain.user.entity.status;

public enum JobCategory {
    STUDENT("학생"),
    PLANNING_STRATEGY("기획/전략"),
    MARKETING_ADVERTISING("마케팅/광고"),
    SALES("영업/판매"),
    FINANCE_ACCOUNTING("재무/회계"),
    HR_EDUCATION("인사/교육"),
    CUSTOMER_SERVICE("고객 서비스"),
    IT_DEVELOPMENT("IT/개발"),
    DESIGNER("디자이너"),
    MANUFACTURING_QUALITY_CONTROL("생산/품질 관리"),
    RESEARCH_DESIGN("연구/설계"),
    PROFESSIONAL("전문직(의료/법률/회계 등)"),
    ENTREPRENEUR_FREELANCER("창업/프리랜서"),
    PUBLIC_ADMINISTRATION("공공/행정직"),
    OTHERS("기타 (직접 입력)");

    private final String displayName;

    // 생성자 정의
    JobCategory(String displayName) {
        this.displayName = displayName;
    }

    // Getter 메서드 추가
    public String getDisplayName() {
        return displayName;
    }
}
