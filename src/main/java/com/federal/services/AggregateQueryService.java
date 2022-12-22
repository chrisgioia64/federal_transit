package com.federal.services;

import com.federal.dao.*;
import com.federal.model.AggregateEntity;
import com.federal.model.AggregateStatistic;
import com.federal.model.TravelMode;
import lombok.Getter;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;

@Service
public class AggregateQueryService {

    private DataSource dataSource;

    private AggregateStatisticDao dao;

    public AggregateQueryService(DataSource dataSource) {
        this.dataSource = dataSource;
        this.dao = new AggregateStatisticDaoImpl(dataSource);
    }

    public List<AggregateResult> getResult(AggregateStatistic statistic,
                                           AggregateEntity entity,
                                           TravelMode mode) {
        return dao.getResult(statistic, entity, mode);
    }

}
