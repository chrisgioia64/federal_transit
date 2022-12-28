package com.federal.dao;

import com.federal.model.MetroRankInfo;
import com.federal.model.AggregateStatistic;

public interface MetroRankDao {

    MetroRankInfo getRankInfo(String metroName, AggregateStatistic statistic);

}
