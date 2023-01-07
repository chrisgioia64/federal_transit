package com.federal.dao;

import com.federal.model.web.AgencyDatum;
import com.federal.model.web.AgencyModeDatum;
import com.federal.model.web.MetroRankInfo;
import com.federal.model.AggregateStatistic;
import com.federal.model.TransitAggregateType;
import com.federal.model.web.TravelModeStatisticDatum;

import java.util.List;

public interface MetroRankDao {

    MetroRankInfo getRankInfo(String metroName, AggregateStatistic statistic);

    /**
     * Returns all the states that have a transit agency associated with them
     */
    List<String> getStates();

    public List<String> getLargeMetropolitanAreasByState(String state);

    MetroRankInfo getTransitInfo(String metroName, AggregateStatistic statistic,
                                 TransitAggregateType transitType);

    /**
     * Used for generating the pie chart of UPT/passenger mile usage by travel mode
     */
    public double getAggregateAmount(String metropolitanArea,
                                     AggregateStatistic statistic,
                                     TransitAggregateType type);

    /**
     * Used for generating the stacked bar chart
     */
    public List<TravelModeStatisticDatum> getTravelModeStatisticDatums(
            String metropolitanArea,
            AggregateStatistic statistic);

    public List<AgencyDatum> getAgenciesForMetropolitanArea(String metropolitanArea);

    public List<AgencyModeDatum> getAgencyModes(String agencyName);

    public List<AgencyModeDatum> getAgencyModes(int ntdId);


}
