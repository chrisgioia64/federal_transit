package com.federal.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MetroRankInfo {

    /** The name of the statistic of interest (e.g. UPT, Passenger Miles). */
    private String statisticName;

    private String metropolitanArea;
    private int numEntities;
    private int population;
    private int populationRank;

    private long totalAmount;
    private double perCapitaAmount;

    private int totalRank;
    private int perCapitaRank;

}
