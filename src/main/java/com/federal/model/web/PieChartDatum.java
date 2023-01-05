package com.federal.model.web;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PieChartDatum {

    private String entityName;
    private List<Portion> portions;

    @Getter
    @Setter
    public static class Portion {
        private String category;
        private double data;
    }

}
