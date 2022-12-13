package com.federal.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AgencyMode {

    private int id;

    private int ntdId;
    private String mode;
    private String typeOfService;
    private int reportYear;
    private int numberOfMonths;
    private int passengerMiles;
    private int upt;
    private int fares;
    private int operatingExpenses;

}
