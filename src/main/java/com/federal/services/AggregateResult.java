package com.federal.services;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AggregateResult {

    private int aggregateStatistic;
    private String entityName;

}
