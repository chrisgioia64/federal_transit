package com.federal.web.query;

import com.federal.dao.*;
import com.federal.etl.DatabaseSchemaService;
import com.federal.etl.TransitRidershipExcelReaderService;
import com.federal.model.AggregateEntity;
import com.federal.model.AggregateStatistic;
import com.federal.model.TravelMode;
import com.federal.services.AggregateQueryService;
import com.federal.services.AggregateResult;
import lombok.Getter;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.sql.DataSource;
import java.util.List;

@Controller
@Log4j2
public class QueryController {

    @Autowired
    private AggregateQueryService service;

    public QueryController(AggregateQueryService service) {
        this.service = service;
    }

    @PostMapping("/query/get_aggregate")
    public ResponseEntity loadMasterDatabase(@RequestBody AggregateQueryObject obj) {
        AggregateStatistic stat = AggregateStatistic.valueOf(obj.getAggregateStatistic());
        AggregateEntity entity = AggregateEntity.valueOf(obj.getAggregateEntity());
        TravelMode mode = TravelMode.valueOf(obj.getTravelMode());
        List<AggregateResult> list = service.getResult(stat, entity, mode);
        return ResponseEntity.ok(list);
    }

}
