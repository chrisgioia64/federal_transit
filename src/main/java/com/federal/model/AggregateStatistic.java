package com.federal.model;

import lombok.Getter;

public enum AggregateStatistic {
    UPT("upt"),
    PASSENGER_MILES( "passenger_miles"),
    OPERATING_EXPENSES("operating_expenses"),
    FARE("fares");

    private AggregateStatistic(String columnName) {
        this.columnName = columnName;
    }

    @Getter
    private String columnName;
}
