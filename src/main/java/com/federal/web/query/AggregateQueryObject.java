package com.federal.web.query;

import com.federal.model.AggregateEntity;
import com.federal.model.AggregateStatistic;
import com.federal.model.TravelMode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AggregateQueryObject {

    private String aggregateStatistic;
    private String aggregateEntity;
    private String travelMode;

}
