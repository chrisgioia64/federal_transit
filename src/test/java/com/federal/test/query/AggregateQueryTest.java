package com.federal.test.query;

import com.federal.dao.*;
import com.federal.etl.TransitRidershipExcelReaderService;
import com.federal.model.AggregateEntity;
import com.federal.model.AggregateStatistic;
import com.federal.model.TravelMode;
import com.federal.services.AggregateQueryService;
import com.federal.services.AggregateResult;
import com.federal.web.DatabaseLoaderController;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.util.List;

@SpringBootTest
@Log4j2
public class AggregateQueryTest {

    @Autowired
    private AggregateQueryService service;

    @Autowired
    private DataSource dataSource;

    @BeforeEach
    public void test() {
        this.service = new AggregateQueryService(dataSource);
    }

    @Test
    public void test1() {
        List<AggregateResult> result = service.getResult(AggregateStatistic.UPT,
                AggregateEntity.AGENCY, TravelMode.CB);
        for (AggregateResult aggregateResult : result) {
            log.info("Result: " + aggregateResult.toString());
        }
    }

}
