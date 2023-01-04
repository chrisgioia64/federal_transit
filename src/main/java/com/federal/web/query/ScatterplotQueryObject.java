package com.federal.web.query;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ScatterplotQueryObject {

    private String aggregateStatistic;
    private String transitAggregateType;
    private int populationLimit;

}
