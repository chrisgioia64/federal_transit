package com.federal.dao;

import com.federal.model.AggregateStatistic;
import com.federal.model.ScatterplotEntity;
import com.federal.model.TransitAggregateType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ScatterplotItemDaoImpl implements ScatterplotItemDao {

    private DataSource dataSource;
    private JdbcTemplate template;

    private final static String COL_METRO = "metro";
    private final static String COL_RATE = "rate";
    private final static String COL_RATE_RANK = "rate_rank";

    public ScatterplotItemDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
        this.template = new JdbcTemplate(dataSource);
    }

    public static class ScatterplotItemInfoMapper implements RowMapper<ScatterplotEntity> {

        @Override
        public ScatterplotEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
            ScatterplotEntity entity = new ScatterplotEntity();
            entity.setMetropolitanArea(rs.getString(COL_METRO));
            entity.setTotalAmount(rs.getDouble(COL_RATE));
            entity.setTotalRank(rs.getInt(COL_RATE_RANK));
            return entity;
        }
    }

    @Override
    public List<ScatterplotEntity> getEntities(AggregateStatistic statistic,
                                               TransitAggregateType type,
                                               int populationLimit) {
        String sql =
                String.format("SELECT * FROM (SELECT metro, total, rate, " +
                "ROW_NUMBER() OVER (ORDER BY rate DESC) rate_rank " +
                " FROM (SELECT agency.metro, SUM(agency_mode.%s) AS total, agency.urbanized_population, " +
                "SUM(agency_mode.%s) / agency.urbanized_population AS rate, " +
                "ROW_NUMBER() OVER (ORDER BY SUM(agency_mode.%s) DESC) total_rank, " +
                "ROW_NUMBER() OVER (ORDER BY agency.urbanized_population DESC) pop_rank " +
                "FROM agency INNER JOIN agency_mode " +
                "WHERE agency_mode.ntd_id = agency.ntd_id " +
                 " %s " +
                "GROUP BY agency.metro ORDER BY agency.urbanized_population DESC) AS sub " +
                "WHERE urbanized_population >= %d) AS sub2;",
                        statistic.getColumnName(), statistic.getColumnName(), statistic.getColumnName(),
                        MetroRankDaoImpl.createOrStatement(type),
                        populationLimit
                        );
        List<ScatterplotEntity> list = template.query(sql, new ScatterplotItemInfoMapper());
        for (ScatterplotEntity entity : list) {
            entity.setStatisticName(statistic.getDisplayName());
            entity.setGroupType(type.getTransitTypeName());
        }
        return list;
    }


    @Override
    public List<ScatterplotEntity> getEntitiesForPopulation(int populationLimit) {
        String sql = String.format("SELECT agency.metro, agency.urbanized_population AS rate, " +
                "ROW_NUMBER() OVER (ORDER BY agency.urbanized_population DESC) rate_rank " +
                "FROM agency WHERE urbanized_population >= %d " +
                "GROUP BY agency.metro " +
                "ORDER BY agency.urbanized_population DESC;",
                populationLimit);
        List<ScatterplotEntity> list = template.query(sql, new ScatterplotItemInfoMapper());
        for (ScatterplotEntity entity : list) {
            entity.setStatisticName(AggregateStatistic.POPULATION.getDisplayName());
            entity.setGroupType(TransitAggregateType.ALL.getTransitTypeName());
        }
        return list;
    }
}
