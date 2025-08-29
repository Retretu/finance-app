package org.gouenji.financeapp.entity.enums.records;

public enum IncomeCategory{
    SALARY("Salary"),
    BONUS("Bonus"),
    INVESTMENT("Investment"),
    UNDERWORKING("Underworking");

    private final String displayName;

    IncomeCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
