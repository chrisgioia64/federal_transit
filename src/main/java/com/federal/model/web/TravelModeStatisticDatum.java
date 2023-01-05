package com.federal.model.web;

import lombok.Getter;
import lombok.Setter;

/**
 * Used for the stacked bar chart that shows usage by agency and then by travel mode
 */
@Getter
@Setter
public class TravelModeStatisticDatum {

    private String agencyName;
    private String travelMode;
    private double amount;

}
