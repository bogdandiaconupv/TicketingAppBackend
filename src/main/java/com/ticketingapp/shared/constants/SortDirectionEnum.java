package com.ticketingapp.shared.constants;

public enum SortDirectionEnum {
    asc("asc"),
    desc("desc");

    private final String sort;

    SortDirectionEnum(String sort) {
        this.sort = sort;
    }

    public String getSort() {
        return sort;
    }
}
