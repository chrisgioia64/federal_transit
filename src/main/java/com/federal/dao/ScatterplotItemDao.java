package com.federal.dao;

import com.federal.model.AggregateStatistic;
import com.federal.model.web.ScatterplotEntity;
import com.federal.model.TransitAggregateType;

import java.util.List;

public interface ScatterplotItemDao {

    /**
     * Return the scatterplot data for a particular aggregate statistic and transit type
     */
    public List<ScatterplotEntity> getEntities(AggregateStatistic statistic,
                                               TransitAggregateType type,
                                               int populationLimit);

    public List<ScatterplotEntity> getEntitiesForPopulation(int populationLimit);


}
