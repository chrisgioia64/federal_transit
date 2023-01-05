package com.federal.test.query;

import com.federal.model.AggregateStatistic;
import com.federal.model.web.MetroRankInfo;
import com.federal.services.MetroRankService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;

@SpringBootTest
@Log4j2
public class MetroRankServiceTest {

    @Autowired
    private MetroRankService service;

    @Autowired
    private DataSource dataSource;

    @BeforeEach
    public void test() {
        this.service = new MetroRankService(dataSource);
    }

    @Test
    public void test1() {
        MetroRankInfo rankInfo = service.getRankInfo("San Francisco-Oakland, CA", AggregateStatistic.UPT);
        log.info(rankInfo);
    }

}
