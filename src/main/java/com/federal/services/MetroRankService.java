package com.federal.services;

import com.federal.dao.*;
import com.federal.model.AggregateStatistic;
import com.federal.model.RidershipData;
import com.federal.model.web.*;
import com.federal.model.TransitAggregateType;
import org.springframework.stereotype.Service;
import lombok.extern.log4j.Log4j2;

import jakarta.annotation.PreDestroy;
import javax.sql.DataSource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Log4j2
@Service
public class MetroRankService {

    private DataSource dataSource;

    private MetroRankDao dao;
    private RidershipDataDao ridershipDataDao;
    private MetroSummaryDao metroSummaryDao;
    
    // Thread pool for parallel database queries
    // Size of 8 allows for concurrent queries without overwhelming the database
    private final ExecutorService executorService = Executors.newFixedThreadPool(8);

    public MetroRankService(DataSource dataSource) {
        this.dataSource = dataSource;
        this.dao = new MetroRankDaoImpl(dataSource);
        this.ridershipDataDao = new RidershipDataDaoImpl(dataSource);
        this.metroSummaryDao = new MetroSummaryDaoImpl(dataSource);
    }

    public String getSummary() {
        return metroSummaryDao.getSummary();
    }

    public String getSummaryAgencies() {
        return metroSummaryDao.getUptUsageSummary();
    }

    public MetroRankInfo getRankInfo(String metroName, AggregateStatistic statistic) {
        return dao.getRankInfo(metroName, statistic);
    }

    public List<MetroRankInfo> getRankInfo(String metroName) {
        // Execute all 4 queries in parallel using CompletableFuture
        CompletableFuture<MetroRankInfo> futureUPT = CompletableFuture.supplyAsync(() -> 
            dao.getRankInfo(metroName, AggregateStatistic.UPT), executorService);
        CompletableFuture<MetroRankInfo> futurePassengerMiles = CompletableFuture.supplyAsync(() -> 
            dao.getRankInfo(metroName, AggregateStatistic.PASSENGER_MILES), executorService);
        CompletableFuture<MetroRankInfo> futureOperatingExpenses = CompletableFuture.supplyAsync(() -> 
            dao.getRankInfo(metroName, AggregateStatistic.OPERATING_EXPENSES), executorService);
        CompletableFuture<MetroRankInfo> futureFare = CompletableFuture.supplyAsync(() -> 
            dao.getRankInfo(metroName, AggregateStatistic.FARE), executorService);
        
        // Wait for all queries to complete
        CompletableFuture.allOf(futureUPT, futurePassengerMiles, futureOperatingExpenses, futureFare).join();
        
        // Collect results in the correct order, handling null results gracefully
        List<MetroRankInfo> list = new LinkedList<>();
        try {
            MetroRankInfo uptInfo = futureUPT.join();
            MetroRankInfo passengerMilesInfo = futurePassengerMiles.join();
            MetroRankInfo operatingExpensesInfo = futureOperatingExpenses.join();
            MetroRankInfo fareInfo = futureFare.join();
            
            // Only add non-null results (some metros may not have data for certain statistics)
            if (uptInfo != null) {
                list.add(uptInfo);
            }
            if (passengerMilesInfo != null) {
                list.add(passengerMilesInfo);
            }
            if (operatingExpensesInfo != null) {
                list.add(operatingExpensesInfo);
            }
            if (fareInfo != null) {
                list.add(fareInfo);
            }
            
            // Log warning if no data found
            if (list.isEmpty()) {
                log.warn("No rank info found for metro: {}", metroName);
            }
        } catch (Exception e) {
            log.error("Error executing parallel queries for metro: " + metroName, e);
            throw new RuntimeException("Failed to retrieve metro rank information", e);
        }
        return list;
    }

    public List<String> getStates() {
        return dao.getStates();
    }

    public List<String> getLargeMetropolitanAreasByState(String state) {
        return dao.getLargeMetropolitanAreasByState(state);
    }

    public List<String> getMetropolitanAreas() {
        return dao.getMetropolitanAreas();
    }

    public List<MetroRankInfo> getTransitInfo(String metroName) {
        // Execute both queries in parallel using CompletableFuture
        CompletableFuture<MetroRankInfo> futureRail = CompletableFuture.supplyAsync(() -> 
            dao.getTransitInfo(metroName, AggregateStatistic.UPT, TransitAggregateType.RAIL), executorService);
        CompletableFuture<MetroRankInfo> futureBus = CompletableFuture.supplyAsync(() -> 
            dao.getTransitInfo(metroName, AggregateStatistic.UPT, TransitAggregateType.BUS), executorService);
        
        // Wait for both queries to complete
        CompletableFuture.allOf(futureRail, futureBus).join();
        
        // Collect results in the correct order, handling null results gracefully
        List<MetroRankInfo> list = new LinkedList<>();
        try {
            MetroRankInfo railInfo = futureRail.join();
            MetroRankInfo busInfo = futureBus.join();
            
            // Only add non-null results (some metros may not have Rail or Bus data)
            if (railInfo != null) {
                list.add(railInfo);
            }
            if (busInfo != null) {
                list.add(busInfo);
            }
            
            // Log warning if no data found
            if (list.isEmpty()) {
                log.warn("No transit info found for metro: {}", metroName);
            }
        } catch (Exception e) {
            log.error("Error executing parallel transit queries for metro: " + metroName, e);
            throw new RuntimeException("Failed to retrieve transit information", e);
        }
        return list;
    }

    public PieChartDatum getAggregateAmount(String metropolitanArea,
                                            AggregateStatistic statistic) {
        // Execute all 4 queries in parallel using CompletableFuture
        CompletableFuture<Double> futureBus = CompletableFuture.supplyAsync(() -> 
            dao.getAggregateAmount(metropolitanArea, statistic, TransitAggregateType.BUS), executorService);
        CompletableFuture<Double> futureRail = CompletableFuture.supplyAsync(() -> 
            dao.getAggregateAmount(metropolitanArea, statistic, TransitAggregateType.RAIL), executorService);
        CompletableFuture<Double> futureDemand = CompletableFuture.supplyAsync(() -> 
            dao.getAggregateAmount(metropolitanArea, statistic, TransitAggregateType.DEMAND), executorService);
        CompletableFuture<Double> futureAll = CompletableFuture.supplyAsync(() -> 
            dao.getAggregateAmount(metropolitanArea, statistic, TransitAggregateType.ALL), executorService);
        
        // Wait for all queries to complete
        CompletableFuture.allOf(futureBus, futureRail, futureDemand, futureAll).join();
        
        // Collect results and build response
        List<PieChartDatum.Portion> portions = new LinkedList<>();
        PieChartDatum datum = new PieChartDatum();
        
        try {
            Double busValue = futureBus.join();
            Double railValue = futureRail.join();
            Double demandValue = futureDemand.join();
            Double total = futureAll.join();
            
            double sum = busValue + railValue + demandValue;
            
            // Add portions for each transit type
            if (busValue > 0) {
                PieChartDatum.Portion portion = new PieChartDatum.Portion();
                portion.setCategory(TransitAggregateType.BUS.getTransitTypeName());
                portion.setData(busValue);
                portions.add(portion);
            }
            if (railValue > 0) {
                PieChartDatum.Portion portion = new PieChartDatum.Portion();
                portion.setCategory(TransitAggregateType.RAIL.getTransitTypeName());
                portion.setData(railValue);
                portions.add(portion);
            }
            if (demandValue > 0) {
                PieChartDatum.Portion portion = new PieChartDatum.Portion();
                portion.setCategory(TransitAggregateType.DEMAND.getTransitTypeName());
                portion.setData(demandValue);
                portions.add(portion);
            }
            
            double other = total - sum;
            if (other > 0) {
                PieChartDatum.Portion portion = new PieChartDatum.Portion();
                portion.setCategory("Other");
                portion.setData(other);
                portions.add(portion);
            }
        } catch (Exception e) {
            log.error("Error executing parallel aggregate amount queries for metro: " + metropolitanArea, e);
            throw new RuntimeException("Failed to retrieve aggregate amount information", e);
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

    public List<RidershipData> getRidershipDataByMonth(int ntdId, String mode,
                                                       String typeOfService, String type) {
        return ridershipDataDao.getRidershipDataByMonth(ntdId, mode, typeOfService, type);
    }

    public List<AgencyData> getAgencyDatums(String metropolitanArea) {
        return dao.getAgencyDatums(metropolitanArea);
    }

    public String getAgencyDataAsString() {
        return metroSummaryDao.getAgencyDataAsString();
    }

    public List<MetroWithCoordinatesDTO> getMetropolitanAreasWithCoordinates() {
        return dao.getMetropolitanAreasWithCoordinates();
    }
    
    /**
     * Cleanup method called when the Spring bean is destroyed.
     * Shuts down the executor service gracefully.
     */
    @PreDestroy
    public void shutdown() {
        log.info("Shutting down MetroRankService executor service");
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
                if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                    log.warn("Executor service did not terminate");
                }
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
            log.error("Error shutting down executor service", e);
        }
    }

}
