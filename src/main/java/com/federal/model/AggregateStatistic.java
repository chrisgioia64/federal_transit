package com.federal.model;

import lombok.Getter;

public enum AggregateStatistic {
    UPT("upt", "Unlinked Passenger Trips"),
    PASSENGER_MILES( "passenger_miles", "Passenger Miles"),
    OPERATING_EXPENSES("operating_expenses", "Operating Expenses"),
    FARE("fares", "Fares");

    private AggregateStatistic(String columnName, String displayName) {
        this.columnName = columnName;
        this.displayName = displayName;
    }

    @Getter
    private String columnName;

    @Getter
    private String displayName;
}
