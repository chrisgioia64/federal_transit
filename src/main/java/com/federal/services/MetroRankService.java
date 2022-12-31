package com.federal.services;

import com.federal.dao.MetroRankDao;
import com.federal.dao.MetroRankDaoImpl;
import com.federal.model.AggregateStatistic;
import com.federal.model.MetroRankInfo;
import com.federal.model.TransitAggregateType;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.LinkedList;
import java.util.List;

@Service
public class MetroRankService {

    private DataSource dataSource;

    private MetroRankDao dao;

    public MetroRankService(DataSource dataSource) {
        this.dataSource = dataSource;
        this.dao = new MetroRankDaoImpl(dataSource);
    }

    public MetroRankInfo getRankInfo(String metroName, AggregateStatistic statistic) {
        return dao.getRankInfo(metroName, statistic);
    }

    public List<MetroRankInfo> getRankInfo(String metroName) {
        List<MetroRankInfo> list = new LinkedList<>();
        list.add(dao.getRankInfo(metroName, AggregateStatistic.UPT));
        list.add(dao.getRankInfo(metroName, AggregateStatistic.PASSENGER_MILES));
        list.add(dao.getRankInfo(metroName, AggregateStatistic.OPERATING_EXPENSES));
        list.add(dao.getRankInfo(metroName, AggregateStatistic.FARE));
        return list;
    }

    public List<String> getStates() {
        return dao.getStates();
    }

    public List<String> getLargeMetropolitanAreasByState(String state) {
        return dao.getLargeMetropolitanAreasByState(state);
    }

    public List<MetroRankInfo> getTransitInfo(String metroName) {
        List<MetroRankInfo> list = new LinkedList<>();
        list.add(dao.getTransitInfo(metroName, AggregateStatistic.UPT, TransitAggregateType.RAIL));
        list.add(dao.getTransitInfo(metroName, AggregateStatistic.UPT, TransitAggregateType.BUS));
        return list;
    }
}
