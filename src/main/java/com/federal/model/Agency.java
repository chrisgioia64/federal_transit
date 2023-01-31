package com.federal.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Agency {

    private int id;

    private int ntdId;
    private String agencyName;
    private String city;
    private String state;
    private String metro;
    private int urbanizedArea;
    private int urbanizedPopulation;
    private int servicePopulation;

}
