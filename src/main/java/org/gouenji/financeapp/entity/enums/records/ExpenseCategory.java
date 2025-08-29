package org.gouenji.financeapp.entity.enums.records;

public enum ExpenseCategory{
    FOOD("Food"),
    TRANSPORT("Transport"),
    FUN("Fun");

    private final String displayName;

    ExpenseCategory(String displayName){
        this.displayName = displayName;
    }

    public String getDisplayName(){
        return displayName;
    }
}
