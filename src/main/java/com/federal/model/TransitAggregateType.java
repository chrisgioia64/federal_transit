package com.federal.model;

import lombok.Getter;

import java.util.List;

public enum TransitAggregateType {

    RAIL("Rail",List.of("LR", "HR", "YR", "MG", "CR")),
    BUS("Bus", List.of("MB", "CB", "RB", "TB")),
    DEMAND("Demand", List.of("DR", "VP")),
    ALL("All", List.of(""));

    private TransitAggregateType(String transitTypeName, List<String> transitModes) {
        this.transitTypeName = transitTypeName;
        this.transitModes = transitModes;
    }

    @Getter
    private List<String> transitModes;

    @Getter
    private String transitTypeName;

}
