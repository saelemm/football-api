package com.foot.adapter.rest.dto;

public enum SortDirection {
    ASC("asc"),
    DESC("desc");

    private final String value;

    SortDirection(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}

