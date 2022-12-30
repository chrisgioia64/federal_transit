package com.federal.dao;

import com.federal.model.MetroRankInfo;
import com.federal.model.AggregateStatistic;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class MetroRankDaoImpl implements MetroRankDao {

    private DataSource dataSource;
    private JdbcTemplate template;

    private final static String COL_METRO = "metro";
    private final static String COL_COUNT = "count";
    private final static String COL_POPULATION = "urbanized_population";
    private final static String COL_POPULATION_RANK = "pop_rank";

    private final static String COL_TOTAL = "total";
    private final static String COL_RATE = "rate";
    private final static String COL_TOTAL_RANK = "total_rank";
    private final static String COL_RATE_RANK = "rate_rank";

    public MetroRankDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
        this.template = new JdbcTemplate(dataSource);
    }

    public class StateMapper implements RowMapper<String> {
        @Override
        public String mapRow(ResultSet rs, int rowNum) throws SQLException {
            String res = rs.getString(AgencyDaoImpl.STATE);
            return res;
        }
    }

    public class MetroMapper implements RowMapper<String> {
        @Override
        public String mapRow(ResultSet rs, int rowNum) throws SQLException {
            String res = rs.getString(AgencyDaoImpl.METRO);
            return res;
        }
    }

    public class MetroRankInfoRowMapper implements RowMapper<MetroRankInfo> {

        @Override
        public MetroRankInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
            MetroRankInfo info = new MetroRankInfo();
            info.setMetropolitanArea(rs.getString(COL_METRO));
            info.setNumEntities(rs.getInt(COL_COUNT));
            info.setPopulation(rs.getInt(COL_POPULATION));
            info.setPopulationRank(rs.getInt(COL_POPULATION_RANK));

            info.setTotalAmount(rs.getLong(COL_TOTAL));
            info.setTotalRank(rs.getInt(COL_TOTAL_RANK));
            info.setPerCapitaAmount(rs.getDouble(COL_RATE));
            info.setPerCapitaRank(rs.getInt(COL_RATE_RANK));
            return info;
        }
    }

    @Override
    public MetroRankInfo getRankInfo(String metroName, AggregateStatistic statistic) {
        String sql =
                String.format("SELECT * FROM (SELECT metro, count, urbanized_population, total, rate, pop_rank, total_rank, " +
                "ROW_NUMBER() OVER (ORDER BY rate DESC) rate_rank " +
                " FROM (SELECT COUNT(*) AS count, agency.urbanized_population, agency.metro, SUM(agency_mode.%s) AS total, " +
                "SUM(agency_mode.%s) / agency.urbanized_population AS rate, " +
                "ROW_NUMBER() OVER (ORDER BY SUM(agency_mode.%s) DESC) total_rank, " +
                "ROW_NUMBER() OVER (ORDER BY agency.urbanized_population DESC) pop_rank " +
                "FROM agency INNER JOIN agency_mode " +
                "WHERE agency_mode.ntd_id = agency.ntd_id " +
                "GROUP BY agency.metro ORDER BY agency.urbanized_population DESC) AS sub " +
                "WHERE urbanized_population >= 500000) AS sub2 " +
                "WHERE metro = '%s'" +
                "ORDER BY rate DESC;",
                        statistic.getColumnName(), statistic.getColumnName(), statistic.getColumnName(),
                        metroName
                );
        MetroRankInfo info = template.queryForObject(sql,
                new MetroRankInfoRowMapper());
        info.setStatisticName(statistic.getDisplayName());
        return info;
    }

    @Override
    public List<String> getStates() {
        String sql = "SELECT DISTINCT(state) FROM agency ORDER BY state";
        return template.query(sql, new StateMapper());
    }

    @Override
    public List<String> getLargeMetropolitanAreasByState(String state) {
        String sql = "SELECT DISTINCT(agency.metro) FROM agency WHERE state = ? " +
                "AND agency.urbanized_population >= 500000 ORDER BY agency.metro;";
        return template.query(sql, new MetroMapper(), state);
    }

}
