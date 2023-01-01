package com.federal.web.query;

import com.federal.dao.*;
import com.federal.etl.DatabaseSchemaService;
import com.federal.etl.TransitRidershipExcelReaderService;
import com.federal.model.*;
import com.federal.services.AggregateQueryService;
import com.federal.services.AggregateResult;
import com.federal.services.MetroRankService;
import lombok.Getter;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.util.List;

@CrossOrigin
@RestController
@Log4j2
public class QueryController {

    @Autowired
    private AggregateQueryService service;

    @Autowired
    private MetroRankService metroRankService;

    public QueryController(AggregateQueryService service,
                           MetroRankService metroRankService) {
        this.service = service;
        this.metroRankService = metroRankService;
    }

    @PostMapping("/query/get_aggregate")
    public ResponseEntity loadMasterDatabase(@RequestBody AggregateQueryObject obj) {
        AggregateStatistic stat = AggregateStatistic.valueOf(obj.getAggregateStatistic());
        AggregateEntity entity = AggregateEntity.valueOf(obj.getAggregateEntity());
        TravelMode mode = TravelMode.valueOf(obj.getTravelMode());
        List<AggregateResult> list = service.getResult(stat, entity, mode);
        return ResponseEntity.ok(list);
    }

    @PostMapping("/query/metro_rank")
    public ResponseEntity getMetroRankInfo(@RequestBody StringHolder metroName) {
        log.info("Metro Name: " + metroName);
        List<MetroRankInfo> list = metroRankService.getRankInfo(metroName.getValue());
        return ResponseEntity.ok(list);
    }

    @PostMapping("/query/states")
    public ResponseEntity getStates() {
        List<String> list = metroRankService.getStates();
        return ResponseEntity.ok(list);
    }

    @PostMapping("/query/metro_by_state")
    public ResponseEntity getMetropolitanAreaByState(@RequestBody StringHolder state) {
        List<String> list = metroRankService.getLargeMetropolitanAreasByState(state.getValue());
        return ResponseEntity.ok(list);
    }

    @PostMapping("/query/metro_rank/transit")
    public ResponseEntity getMetroRankByTransitMode(@RequestBody StringHolder metroName) {
        List<MetroRankInfo> list = metroRankService.getTransitInfo(metroName.getValue());
        return ResponseEntity.ok(list);
    }

}
