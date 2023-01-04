package com.federal.services;

import com.federal.dao.MetroRankDao;
import com.federal.dao.MetroRankDaoImpl;
import com.federal.dao.ScatterplotItemDao;
import com.federal.dao.ScatterplotItemDaoImpl;
import com.federal.model.AggregateStatistic;
import com.federal.model.ScatterplotEntity;
import com.federal.model.TransitAggregateType;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;

@Service
public class ScatterplotQueryService {

    private DataSource dataSource;
    private ScatterplotItemDao dao;

    public ScatterplotQueryService(DataSource dataSource) {
        this.dataSource = dataSource;
        this.dao = new ScatterplotItemDaoImpl(dataSource);
    }

    public List<ScatterplotEntity> getEntities(AggregateStatistic statistic,
                                               TransitAggregateType type,
                                               int populationLimit) {
        return dao.getEntities(statistic, type, populationLimit);
    }

    public List<ScatterplotEntity> getEntitiesForPopulation(int populationLimit) {
        return dao.getEntitiesForPopulation(populationLimit);
    }
}
