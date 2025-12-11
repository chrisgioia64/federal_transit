package com.federal.model.web;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO for returning metropolitan areas with their coordinates.
 * Used by the /query/metros_with_coordinates endpoint.
 */
@Getter
@Setter
public class MetroWithCoordinatesDTO {
    private String name;
    private String state;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Long population;
}


