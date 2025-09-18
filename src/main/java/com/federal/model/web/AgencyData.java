package com.federal.model.web;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AgencyData {

    private String agencyName;
    private String metroName;
    private int totalOperationCost;
    private double operationCostPerPerson;
    private double totalFares;
    private double fareboxRecovery;

    private double milesPerTrip;
    private double operatingExpensePerTrip;
    private double operatingExpensePerMile;

}
