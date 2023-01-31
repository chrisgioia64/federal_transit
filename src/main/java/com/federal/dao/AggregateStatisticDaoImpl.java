package com.federal.dao;

import com.federal.model.AggregateEntity;
import com.federal.model.AggregateStatistic;
import com.federal.model.TravelMode;
import com.federal.services.AggregateResult;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class AggregateStatisticDaoImpl implements AggregateStatisticDao {

    public static final String TABLE_NAME = "agency";

    private static final String ID = "id";
    private static final String NTD_ID = "ntd_id";
    private static final String AGENCY_NAME = "agency_name";
    private static final String CITY = "city";
    private static final String STATE = "state";
    private static final String URBANIZED_AREA = "urbanized_area";
    private static final String URBANIZED_POPULATION = "urbanized_population";
    private static final String SERVICE_POPULATION = "service_population";

    private DataSource dataSource;
    private JdbcTemplate template;
    private SimpleJdbcInsert insert;

    public AggregateStatisticDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
        this.template = new JdbcTemplate(dataSource);
        this.insert = new SimpleJdbcInsert(dataSource).withTableName(TABLE_NAME)
                .usingGeneratedKeyColumns(ID);
    }

    public static class AggregateResultMapper implements RowMapper<AggregateResult> {

        @Override
        public AggregateResult mapRow(ResultSet rs, int rowNum) throws SQLException {
            AggregateResult result = new AggregateResult();
            result.setAggregateStatistic(rs.getLong(1));
            result.setEntityName(rs.getString(2));
            return result;
        }
    }

    @Override
    public List<AggregateResult> getResult(AggregateStatistic statistic,
                                           AggregateEntity entity,
                                           TravelMode mode) {
        String sql =
                String.format("SELECT SUM(agency_mode.%s), agency.%s " +
                        "FROM agency INNER JOIN agency_mode " +
                        "WHERE agency_mode.ntd_id = agency.ntd_id " +
                        "GROUP BY agency.%s ORDER BY SUM(agency_mode.%s) DESC",
                        statistic.getColumnName(), entity.getColumnName(),
                        entity.getColumnName(), statistic.getColumnName());
        return template.query(sql,
                new AggregateStatisticDaoImpl.AggregateResultMapper());
    }
}
