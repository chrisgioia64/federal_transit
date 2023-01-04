package com.federal.dao;

import com.federal.model.AggregateStatistic;
import com.federal.model.ScatterplotEntity;
import com.federal.model.TransitAggregateType;

import java.util.List;

public interface ScatterplotItemDao {

    public List<ScatterplotEntity> getEntities(AggregateStatistic statistic,
                                               TransitAggregateType type,
                                               int populationLimit);

    public List<ScatterplotEntity> getEntitiesForPopulation(int populationLimit);

}
