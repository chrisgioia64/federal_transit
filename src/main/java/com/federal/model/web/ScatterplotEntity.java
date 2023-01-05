package com.federal.model.web;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ScatterplotEntity {

    /** The name of the statistic of interest (e.g. UPT, Passenger Miles). */
    private String statisticName;

    /** The group type (if applicable, e.g. bus or rail transit type). */
    private String groupType;

    /** The metropolitan area (e.g. New York, NY). */
    private String metropolitanArea;

    private double totalAmount;
    private int totalRank;

}
