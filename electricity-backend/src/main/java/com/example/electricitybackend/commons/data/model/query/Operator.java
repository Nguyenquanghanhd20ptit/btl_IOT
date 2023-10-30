package com.example.electricitybackend.commons.data.model.query;

public enum Operator {
    IN("in"),
    NIN("nin"),
    EQUAL("eq"),
    LIKE("like"),
    LIKE_IGNORE("like_ignore_case"),
    NOT_EQUAL("ne"),
    GREATER_THAN("gt"),
    LESS_THAN("lt"),
    GREATER_THAN_OR_EQUAL("gte"),
    LESS_THAN_OR_EQUAL("lte"),
    NONE("none");

    private String operator;

    Operator(String operator) {
        this.operator = operator;
    }

    public String getOperator() {
        return operator;
    }

    public static Operator from(String operator) {
        for (Operator status : values()) {
            if (status.getOperator().equals(operator))
                return status;
        }
        return NONE;
    }
}
