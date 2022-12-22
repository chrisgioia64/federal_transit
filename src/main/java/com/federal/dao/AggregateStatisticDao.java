package com.federal.dao;

import com.federal.model.AggregateEntity;
import com.federal.model.AggregateStatistic;
import com.federal.model.TravelMode;
import com.federal.services.AggregateResult;

import java.util.List;

public interface AggregateStatisticDao {

    public List<AggregateResult> getResult(AggregateStatistic statistic,
                                           AggregateEntity entity,
                                           TravelMode mode);

}
