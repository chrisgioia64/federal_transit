package com.federal.services;

import com.federal.dao.MetroRankDao;
import com.federal.dao.MetroRankDaoImpl;
import com.federal.dao.RidershipDataDao;
import com.federal.dao.RidershipDataDaoImpl;
import com.federal.model.AggregateStatistic;
import com.federal.model.RidershipData;
import com.federal.model.web.*;
import com.federal.model.TransitAggregateType;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.*;

@Service
public class MetroRankService {

    private DataSource dataSource;

    private MetroRankDao dao;
    private RidershipDataDao ridershipDataDao;

    public MetroRankService(DataSource dataSource) {
        this.dataSource = dataSource;
        this.dao = new MetroRankDaoImpl(dataSource);
        this.ridershipDataDao = new RidershipDataDaoImpl(dataSource);
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

    public PieChartDatum getAggregateAmount(String metropolitanArea,
                                            AggregateStatistic statistic) {
        List<PieChartDatum.Portion> portions = new LinkedList<>();
        PieChartDatum datum = new PieChartDatum();
        List<TransitAggregateType> types = List.of(TransitAggregateType.BUS,
                TransitAggregateType.RAIL,
                TransitAggregateType.DEMAND);
        double sum = 0;
        for (TransitAggregateType type : types) {
            Double value = dao.getAggregateAmount(metropolitanArea, statistic, type);
            sum += value;
            PieChartDatum.Portion portion = new PieChartDatum.Portion();
            portion.setCategory(type.getTransitTypeName());
            portion.setData(value);
            if (value > 0) {
                portions.add(portion);
            }
        }
        double total = dao.getAggregateAmount(metropolitanArea, statistic, TransitAggregateType.ALL);
        double other = total - sum;

        PieChartDatum.Portion portion = new PieChartDatum.Portion();
        portion.setCategory("Other");
        portion.setData(other);
        if (other > 0) {
            portions.add(portion);
        }
        datum.setEntityName(metropolitanArea);
        datum.setPortions(portions);
        return datum;
    }

    private final static String OTHER = "other";

    public List<TravelModeStatisticDatum> getTravelModeStatisticDatums(
            String metropolitanArea, String ridershipDataType, int year) {
        List<TravelModeStatisticDatum> list =
                dao.getTravelModeStatisticDatumsByYear(metropolitanArea, year, ridershipDataType);
        Set<String> agencyNames = new HashSet<>();
        for (TravelModeStatisticDatum datum : list) {
            agencyNames.add(datum.getAgencyName());
        }
        Map<String, Map<String, Double>> map = new HashMap<>();
        for (String agencyName : agencyNames) {
            Map<String, Double> innerMap = new HashMap<>();
            innerMap.put(TransitAggregateType.BUS.getTransitTypeName(), 0.0);
            innerMap.put(TransitAggregateType.RAIL.getTransitTypeName(), 0.0);
            innerMap.put(TransitAggregateType.DEMAND.getTransitTypeName(), 0.0);
            innerMap.put(OTHER, 0.0);
            map.put(agencyName, innerMap);
        }
        for (TravelModeStatisticDatum datum : list) {
            String type = getTransitType(datum);
            double current = map.get(datum.getAgencyName()).get(type);
            map.get(datum.getAgencyName()).put(type, current + datum.getAmount());
        }
        return createFromMap(map);
    }

    public List<TravelModeStatisticDatum> getTravelModeStatisticDatums(
            String metropolitanArea, AggregateStatistic statistic) {
        List<TravelModeStatisticDatum> list =
                dao.getTravelModeStatisticDatums(metropolitanArea, statistic);
        Set<String> agencyNames = new HashSet<>();
        for (TravelModeStatisticDatum datum : list) {
            agencyNames.add(datum.getAgencyName());
        }
        Map<String, Map<String, Double>> map = new HashMap<>();
        for (String agencyName : agencyNames) {
            Map<String, Double> innerMap = new HashMap<>();
            innerMap.put(TransitAggregateType.BUS.getTransitTypeName(), 0.0);
            innerMap.put(TransitAggregateType.RAIL.getTransitTypeName(), 0.0);
            innerMap.put(TransitAggregateType.DEMAND.getTransitTypeName(), 0.0);
            innerMap.put(OTHER, 0.0);
            map.put(agencyName, innerMap);
        }
        for (TravelModeStatisticDatum datum : list) {
            String type = getTransitType(datum);
            double current = map.get(datum.getAgencyName()).get(type);
            map.get(datum.getAgencyName()).put(type, current + datum.getAmount());
        }
        return createFromMap(map);
    }

    public List<Integer> getAvailableYears(String metropolitanArea, String ridershipDataType) {
        return dao.getAvailableYears(metropolitanArea, ridershipDataType);
    }

    public List<TravelModeStatisticDatum> createFromMap(Map<String, Map<String, Double>> map) {
        List<TravelModeStatisticDatum> list = new LinkedList<>();
        for (String agencyName : map.keySet()) {
            Map<String, Double> innerMap = map.get(agencyName);
            for (Map.Entry<String, Double> entry : innerMap.entrySet()) {
                TravelModeStatisticDatum datum = new TravelModeStatisticDatum();
                datum.setAgencyName(agencyName);
                datum.setTravelMode(entry.getKey());
                datum.setAmount(entry.getValue());
                list.add(datum);
            }
        }
        return list;
    }

    public String getTransitType(TravelModeStatisticDatum datum) {
        for (TransitAggregateType value : TransitAggregateType.values()) {
            if (value.getTransitModes().contains(datum.getTravelMode())) {
                return value.getTransitTypeName();
            }
        }
        return OTHER;
    }

    public List<AgencyDatum> getAgenciesForMetropolitanArea(String metropolitanArea) {
        return dao.getAgenciesForMetropolitanArea(metropolitanArea);
    }

    // Service methods for the time series data

    public List<AgencyModeDatum> getAgencyModes(String agencyName) {
        return dao.getAgencyModes(agencyName);
    }

    public List<AgencyModeDatum> getAgencyModes(int ntdId) {
        return dao.getAgencyModes(ntdId);
    }

    public List<RidershipData> getRidershipData(int ntdId, String mode,
                                                String typeOfService, String type) {
        return ridershipDataDao.getRidershipData(ntdId, mode, typeOfService, type);
    }
}
