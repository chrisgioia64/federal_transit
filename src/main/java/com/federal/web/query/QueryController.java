package com.federal.web.query;

import com.federal.model.*;
import com.federal.model.web.*;
import com.federal.services.AggregateQueryService;
import com.federal.services.AggregateResult;
import com.federal.services.MetroRankService;
import com.federal.services.ScatterplotQueryService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;

@CrossOrigin
@RestController
@Log4j2
public class QueryController {

    @Autowired
    private AggregateQueryService service;

    @Autowired
    private MetroRankService metroRankService;

    @Autowired
    private ScatterplotQueryService scatterplotQueryService;

    public QueryController(AggregateQueryService service,
                           MetroRankService metroRankService,
                           ScatterplotQueryService scatterplotQueryService) {
        this.service = service;
        this.metroRankService = metroRankService;
        this.scatterplotQueryService = scatterplotQueryService;
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

    @PostMapping("/query/scatterplot")
    public ResponseEntity getScatterplot(@RequestBody ScatterplotQueryObject obj) {
        log.info("Object: " + obj.toString());
        List<ScatterplotEntity> entityList1 = getEntities(
                obj.getAggregateStatistic1(), obj.getTransitAggregateType1(), obj.getPopulationLimit());
        List<ScatterplotEntity> entityList2 = getEntities(
                obj.getAggregateStatistic2(), obj.getTransitAggregateType2(), obj.getPopulationLimit());
        List<ScatterplotMergedEntity> mergedEntities =
                scatterplotQueryService.mergeLists(entityList1, entityList2);
        return ResponseEntity.ok(mergedEntities);
    }

    private List<ScatterplotEntity> getEntities(String statistic,
                                                String transitType, int populationLimit) {
        AggregateStatistic stat = AggregateStatistic.valueOf(statistic);
        TransitAggregateType type = TransitAggregateType.valueOf(transitType);
        List<ScatterplotEntity> list = new LinkedList<>();
        if (stat.equals(AggregateStatistic.POPULATION)) {
            list = scatterplotQueryService.getEntitiesForPopulation(populationLimit);
        } else {
            list = scatterplotQueryService.getEntities(stat, type, populationLimit);
        }
        return list;
    }

    @PostMapping("/query/piechart")
    public ResponseEntity getPieChart(@RequestBody PieChartQueryObject obj) {
        String metropolitanArea = obj.getMetropolitanArea();
        AggregateStatistic statistic = AggregateStatistic.valueOf(obj.getStatistic());
        PieChartDatum datum = metroRankService.getAggregateAmount(metropolitanArea, statistic);
        return ResponseEntity.ok(datum);
    }

    @PostMapping("/query/stacked_bar_chart")
    public ResponseEntity getStackedBarChart(@RequestBody MetropolitanStatisticQueryObject obj) {
        String metropolitanArea = obj.getMetropolitanArea();
        AggregateStatistic statistic = AggregateStatistic.valueOf(obj.getStatistic());
        List<TravelModeStatisticDatum> list = metroRankService.getTravelModeStatisticDatums(
                metropolitanArea, statistic
        );
        return ResponseEntity.ok(list);
    }

    @PostMapping("/query/stacked_bar_chart_by_year")
    public ResponseEntity getStackedBarChartByYear(@RequestBody MetropolitanStatisticYearQueryObject obj) {
        String metropolitanArea = obj.getMetropolitanArea();
        List<TravelModeStatisticDatum> list = metroRankService.getTravelModeStatisticDatums(
                metropolitanArea, obj.getStatistic(), obj.getYear()
        );
        return ResponseEntity.ok(list);
    }

    @PostMapping("/query/get_years_of_metro")
    public ResponseEntity getYearsForMetropolitanArea(@RequestBody MetropolitanStatisticQueryObject obj) {
        String metropolitanArea = obj.getMetropolitanArea();
        List<Integer> list = metroRankService.getAvailableYears(metropolitanArea, obj.getStatistic());
        return ResponseEntity.ok(list);
    }

    @PostMapping("/query/agency_name_by_metro")
    public ResponseEntity getAgencies(@RequestBody StringHolder obj) {
        String metropolitanArea = obj.getValue();
        List<AgencyDatum> list = metroRankService.getAgenciesForMetropolitanArea(
                metropolitanArea);
        return ResponseEntity.ok(list);
    }

    @PostMapping("/query/agency_modes")
    public ResponseEntity getAgencyModes(@RequestBody StringHolder obj) {
        int ntdId = Integer.parseInt(obj.getValue());
        List<AgencyModeDatum> list = metroRankService.getAgencyModes(ntdId);
        return ResponseEntity.ok(list);
    }

    @PostMapping("/query/ridership_data")
    public ResponseEntity getRidershipData(@RequestBody RidershipDataQueryObject queryObj) {
        List<RidershipData> list = metroRankService.getRidershipData(
                queryObj.getNtdId(), queryObj.getMode(), queryObj.getTypeOfService(),
                queryObj.getType());
        return ResponseEntity.ok(list);
    }

    @PostMapping("/query/ridership_data_month")
    public ResponseEntity getRidershipDataByMonth(@RequestBody RidershipDataQueryObject queryObj) {
        List<RidershipData> list = metroRankService.getRidershipDataByMonth(
                queryObj.getNtdId(), queryObj.getMode(), queryObj.getTypeOfService(),
                queryObj.getType());
        return ResponseEntity.ok(list);
    }

}
