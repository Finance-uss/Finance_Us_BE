package finance_us.finance_us.domain.category.entity.category.status;


public enum CategoryType
{
    INCOME("수입"),
    EXPENSE("지출");

    private final String displayName;

    CategoryType(String displayName){
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
