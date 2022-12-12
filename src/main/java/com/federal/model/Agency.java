package com.federal.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
public class Agency {

    private int id;

    private int ntdId;
    private String agencyName;
    private String city;
    private String state;
    private int urbanizedArea;
    private int urbanizedPopulation;
    private int servicePopulation;

}
