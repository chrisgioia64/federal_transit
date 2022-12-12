package com.federal.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AgencyMode {

    private int id;

    private int ntdId;
    private String agencyName;
    private String mode;
    private String typeOfService;
    private int reportYear;
    private int numberOfMonths;
    private int passengerMiles;
    private int upt;
    private int fares;
    private int operatingExpenses;

}
