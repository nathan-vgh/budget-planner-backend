package com.budget_planner.budget_planner.expense.domain;

public enum Color {

    RED("#FF0000"),
    GREEN("#00FF00"),
    BLUE("#0000FF"),
    BLACK("#000000");

    private final String hex;

    Color (String hex) {
        this.hex = hex;
    }

    public String getHex () {
        return hex;
    }
}
