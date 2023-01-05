package com.federal.model.web;


import com.federal.model.web.ScatterplotEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ScatterplotMergedEntity {

    /** The metropolitan area (e.g. New York, NY). */
    private String metropolitanArea;

    private ScatterplotEntity entity1;

    private ScatterplotEntity entity2;

}
