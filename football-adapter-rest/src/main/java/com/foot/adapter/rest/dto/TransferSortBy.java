package com.foot.adapter.rest.dto;

public enum TransferSortBy {
    DATE("transferDate"),
    TRANSFER_PRICE("transferPrice"),
    PLAYER_ID("playerId");

    private final String value;

    TransferSortBy(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}

