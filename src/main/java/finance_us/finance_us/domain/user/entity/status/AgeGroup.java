package finance_us.finance_us.domain.user.entity.status;

public enum AgeGroup {
    TEENS("10대"),
    TWENTIES("20대"),
    THIRTIES("30대"),
    FORTIES("40대"),
    FIFTIES("50대"),
    SIXTIES_AND_ABOVE("60대 이상");

    private final String displayName;

    AgeGroup(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
