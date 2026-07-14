package com.foot.adapter.rest.dto;

public enum PlayerSortBy {
    NAME("name"),
    ACRONYM("acronym"),
    MARKET_PRICE("marketPrice");

    private final String value;

    PlayerSortBy(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}

