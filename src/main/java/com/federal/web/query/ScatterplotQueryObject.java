package com.federal.web.query;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ScatterplotQueryObject {

    private String aggregateStatistic1;
    private String transitAggregateType1;

    private String aggregateStatistic2;
    private String transitAggregateType2;

    private int populationLimit;

}
