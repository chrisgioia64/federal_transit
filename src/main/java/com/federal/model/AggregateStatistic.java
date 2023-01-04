package com.federal.model;

import lombok.Getter;

public enum AggregateStatistic {
    UPT("upt", "Unlinked Passenger Trips"),
    PASSENGER_MILES( "passenger_miles", "Passenger Miles"),
    OPERATING_EXPENSES("operating_expenses", "Operating Expenses"),
    FARE("fares", "Fares"),
    POPULATION("urbanized_area", "Population");

    // The column name for the population is on the Agency whereas for the four other enums
    // it is under the Agency Mode

    private AggregateStatistic(String columnName, String displayName) {
        this.columnName = columnName;
        this.displayName = displayName;
    }

    @Getter
    private String columnName;

    @Getter
    private String displayName;
}
