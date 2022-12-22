package com.federal.model;

import lombok.Getter;

public enum AggregateEntity {
    AGENCY("agency_name"),
    CITY("city"),
    STATE("state"),
    AGENCY_MODE("mode");

    private AggregateEntity(String columnName) {
        this.columnName = columnName;
    }

    @Getter
    private String columnName;

}
