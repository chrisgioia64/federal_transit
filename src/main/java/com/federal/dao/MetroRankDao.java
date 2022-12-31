package com.federal.dao;

import com.federal.model.MetroRankInfo;
import com.federal.model.AggregateStatistic;
import com.federal.model.TransitAggregateType;

import java.util.List;

public interface MetroRankDao {

    MetroRankInfo getRankInfo(String metroName, AggregateStatistic statistic);

    List<String> getStates();

    public List<String> getLargeMetropolitanAreasByState(String state);

    MetroRankInfo getTransitInfo(String metroName, AggregateStatistic statistic,
                                 TransitAggregateType transitType);

}
