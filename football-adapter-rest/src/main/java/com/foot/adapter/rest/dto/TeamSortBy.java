package com.foot.adapter.rest.dto;

public enum TeamSortBy {
    NAME("name"),
    ACRONYM("acronym"),
    BUDGET("budget");

    private final String value;

    TeamSortBy(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}

