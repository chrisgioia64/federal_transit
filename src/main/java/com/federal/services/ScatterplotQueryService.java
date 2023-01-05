package com.federal.services;

import com.federal.dao.ScatterplotItemDao;
import com.federal.dao.ScatterplotItemDaoImpl;
import com.federal.model.*;
import com.federal.model.web.PieChartDatum;
import com.federal.model.web.ScatterplotEntity;
import com.federal.model.web.ScatterplotMergedEntity;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.*;

@Service
public class ScatterplotQueryService {

    private DataSource dataSource;
    private ScatterplotItemDao dao;

    public ScatterplotQueryService(DataSource dataSource) {
        this.dataSource = dataSource;
        this.dao = new ScatterplotItemDaoImpl(dataSource);
    }

    public List<ScatterplotEntity> getEntities(AggregateStatistic statistic,
                                               TransitAggregateType type,
                                               int populationLimit) {
        return dao.getEntities(statistic, type, populationLimit);
    }

    public List<ScatterplotEntity> getEntitiesForPopulation(int populationLimit) {
        return dao.getEntitiesForPopulation(populationLimit);
    }

    public List<ScatterplotMergedEntity> mergeLists(List<ScatterplotEntity> xList,
                                                    List<ScatterplotEntity> yList) {
        List<ScatterplotMergedEntity> result = new LinkedList<>();
        Map<String, ScatterplotEntity> map1 = new LinkedHashMap<>();
        Map<String, ScatterplotEntity> map2 = new LinkedHashMap<>();
        for (ScatterplotEntity entity : xList) {
            map1.put(entity.getMetropolitanArea(), entity);
        }
        for (ScatterplotEntity entity : yList) {
            map2.put(entity.getMetropolitanArea(), entity);
        }
        for (String metropolitanArea : map1.keySet()) {
            ScatterplotEntity entity1 = map1.get(metropolitanArea);
            ScatterplotEntity entity2 = map2.get(metropolitanArea);
            if (entity1 != null && entity2 != null) {
                result.add(mergeEntity(entity1, entity2));
            }
        }
        return result;
    }

    private ScatterplotMergedEntity mergeEntity(ScatterplotEntity entity1,
                                                ScatterplotEntity entity2) {
        ScatterplotMergedEntity mergedEntity = new ScatterplotMergedEntity();
        mergedEntity.setMetropolitanArea(entity1.getMetropolitanArea());
        mergedEntity.setEntity1(entity1);
        mergedEntity.setEntity2(entity2);
        return mergedEntity;
    }


}
