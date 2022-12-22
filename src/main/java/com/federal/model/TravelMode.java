package com.federal.model;

import lombok.Getter;

public enum TravelMode {

    LR("LR", "Light Rail"),
    MB("MB", "Metro Bus"),
    CB("CB", "Commuter Bus"),
    RB("RB", "Bus Rapid Transit"),
    SR("SR", "Streetcar"),
    MG("MG", "Monorail"),
    CR("CR", "Commuter rail"),
    DR("DR", "Demand Response"),
    FB("FB", "Ferry Boat"),
    TB("TB", "Trolley Bus"),
    VP("VP", "Vanpool");

    private TravelMode(String abbreviation, String fullName) {
        this.abbreviation = abbreviation;
        this.fullName = fullName;
    }

    @Getter
    private String abbreviation;

    @Getter
    private String fullName;

}
