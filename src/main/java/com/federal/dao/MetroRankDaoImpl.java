package com.federal.dao;

import com.federal.model.web.AgencyDatum;
import com.federal.model.web.AgencyModeDatum;
import com.federal.model.web.MetroRankInfo;
import com.federal.model.AggregateStatistic;
import com.federal.model.TransitAggregateType;
import com.federal.model.web.TravelModeStatisticDatum;
import lombok.extern.log4j.Log4j2;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Log4j2
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

    private final static String COL_AGENCY_NAME = "agency_name";
    private final static String COL_MODE = "mode";
    private final static String COL_AMOUNT = "amount";
    private final static String COL_TYPE_OF_SERVICE = "type_of_service";
    private final static String COL_NTD_ID = "ntd_id";

    private final static String COL_YEAR = "year";

    public MetroRankDaoImpl(DataSource dataSource) {
        log.info("Metro Rank Data source: " + dataSource);
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

    @Override
    public List<String> getMetropolitanAreas() {
        String sql = "SELECT DISTINCT(agency.metro) FROM agency WHERE " +
                " agency.urbanized_population >= 500000 ORDER BY agency.metro;";
        return template.query(sql, new MetroMapper());
    }

    @Override
    public MetroRankInfo getTransitInfo(String metroName,
                                        AggregateStatistic statistic,
                                        TransitAggregateType transitType) {
        String sql = String.format("SELECT * FROM (SELECT metro, count, urbanized_population, total, rate, total_rank, pop_rank, " +
                "ROW_NUMBER() OVER (ORDER BY rate DESC) rate_rank " +
                " FROM (SELECT COUNT(*) AS count, agency.urbanized_population, agency.metro, SUM(agency_mode.%s) AS total, " +
                "SUM(agency_mode.%s) / agency.urbanized_population AS rate, " +
                "ROW_NUMBER() OVER (ORDER BY SUM(agency_mode.%s) DESC) total_rank, " +
                "ROW_NUMBER() OVER (ORDER BY agency.urbanized_population DESC) pop_rank " +
                "FROM agency INNER JOIN agency_mode " +
                "WHERE agency_mode.ntd_id = agency.ntd_id " +
                "%s " +
                "GROUP BY agency.metro ORDER BY agency.urbanized_population DESC) AS sub " +
                "WHERE urbanized_population >= 500000) AS sub2 " +
                "WHERE metro = '%s' " +
                "ORDER BY rate DESC;",
                statistic.getColumnName(), statistic.getColumnName(), statistic.getColumnName(),
                createOrStatement(transitType),
                metroName
                );
        MetroRankInfo info = template.queryForObject(sql,
                new MetroRankInfoRowMapper());
        info.setStatisticName(statistic.getDisplayName());
        info.setGroupType(transitType.getTransitTypeName());
        return info;
    }

    public static String createOrStatement(TransitAggregateType transitType) {
        StringBuilder b = new StringBuilder();
        if (transitType.equals(TransitAggregateType.ALL)) {
            return b.toString();
        }
        if (transitType.getTransitModes().size() == 0) {
            return b.toString();
        }
        b.append("AND ");
        b.append("(");
        for (String transitMode : transitType.getTransitModes()) {
            b.append("agency_mode.mode = '" + transitMode + "' OR ");
        }
        b.replace(b.length()-3, b.length(), "");
        b.append(") ");
        return b.toString();
    }

    @Override
    public double getAggregateAmount(String metropolitanArea,
                                     AggregateStatistic statistic, TransitAggregateType type) {
        String sql = String.format("SELECT metro, SUM(agency_mode.%s) AS total " +
                        "FROM agency LEFT JOIN agency_mode " +
                        "ON agency_mode.ntd_id = agency.ntd_id " +
                        "%s " +
                        "WHERE agency.metro = '%s' " +
                        "GROUP BY agency.metro " +
                        "ORDER BY SUM(agency_mode.upt) DESC;",
                statistic.getColumnName(),
                MetroRankDaoImpl.createOrStatement(type),
                metropolitanArea
        );
        List<Double> list = template.query(sql, new ScatterplotItemDaoImpl.AggregateStatisticDoubleMapper());
        if (list.size() == 0) {
            return 0.0;
        } else if (list.size() == 1){
            return list.get(0);
        } else {
            log.warn("There were " + list.size() + " elements returned by the query");
            return list.get(0);
        }
    }

    public static class TravelModeMapper implements RowMapper<TravelModeStatisticDatum> {

        @Override
        public TravelModeStatisticDatum mapRow(ResultSet rs, int rowNum) throws SQLException {
            TravelModeStatisticDatum datum = new TravelModeStatisticDatum();
            datum.setAgencyName(rs.getString(COL_AGENCY_NAME));
            datum.setTravelMode(rs.getString(COL_MODE));
            datum.setAmount(rs.getDouble(COL_AMOUNT));
            return datum;
        }
    }

    @Override
    public List<TravelModeStatisticDatum> getTravelModeStatisticDatums(
            String metropolitanArea, AggregateStatistic statistic) {
        String sql = String.format("SELECT agency.metro, agency.agency_name, agency_mode.mode, SUM(agency_mode.%s) AS amount " +
                "FROM agency INNER JOIN agency_mode " +
                "ON agency.ntd_id = agency_mode.ntd_id " +
                "WHERE metro = '%s' " +
                "GROUP BY agency.agency_name, agency_mode.mode " +
                "ORDER BY metro;",
                statistic.getColumnName(), metropolitanArea);
        List<TravelModeStatisticDatum> list
                = template.query(sql, new TravelModeMapper());
        return list;
    }

    @Override
    public List<TravelModeStatisticDatum> getTravelModeStatisticDatumsByYear(String metropolitanArea,
                                                                             int year, String ridershipDataType) {
        String sql = String.format("Select agency_name, mode, type_of_service, agency_mode_id, year, month, SUM(data) AS amount " +
                "FROM agency INNER JOIN agency_mode " +
                "ON agency.ntd_id = agency_mode.ntd_id " +
                "INNER JOIN ridership_data " +
                "ON agency_mode.id = ridership_data.agency_mode_id " +
                "WHERE agency.metro = '%s' AND year = %d " +
                "AND type = '%s' " +
                "GROUP BY agency_mode_id, mode, type_of_service, year ",
                metropolitanArea, year, ridershipDataType);
        List<TravelModeStatisticDatum> list
                = template.query(sql, new TravelModeMapper());
        return list;
    }

    public static class YearMapper implements RowMapper<Integer> {
        @Override
        public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getInt(COL_YEAR);
        }
    }

    @Override
    public List<Integer> getAvailableYears(String metropolitanArea, String ridershipDataType) {
        String sql = String.format("Select year FROM agency INNER JOIN agency_mode " +
                "ON agency.ntd_id = agency_mode.ntd_id " +
                "INNER JOIN ridership_data " +
                "ON agency_mode.id = ridership_data.agency_mode_id " +
                "WHERE agency.metro = '%s' AND type = '%s' " +
                "GROUP BY year " +
                "ORDER BY year desc;", metropolitanArea, ridershipDataType);
        return template.query(sql, new YearMapper());
    }

    public static class AgencyDatumMapper implements RowMapper<AgencyDatum> {

        @Override
        public AgencyDatum mapRow(ResultSet rs, int rowNum) throws SQLException {
            AgencyDatum datum = new AgencyDatum();
            datum.setAgencyName(rs.getString(COL_AGENCY_NAME));
            datum.setNtdId(rs.getInt(COL_NTD_ID));
            return datum;
        }
    }

    @Override
    public List<AgencyDatum> getAgenciesForMetropolitanArea(String metropolitanArea) {
        String sql = String.format("SELECT DISTINCT(agency_name), SUM(agency_mode.upt), agency.ntd_id " +
                        "FROM agency " +
                "INNER JOIN agency_mode ON agency.ntd_id = agency_mode.ntd_id " +
                "WHERE metro = '%s'" +
                "GROUP BY agency_name ORDER BY SUM(agency_mode.upt) DESC; ",
                metropolitanArea);
        return template.query(sql, new AgencyDatumMapper());
    }

    public static class AgencyModeDatumMapper implements RowMapper<AgencyModeDatum> {

        @Override
        public AgencyModeDatum mapRow(ResultSet rs, int rowNum) throws SQLException {
            AgencyModeDatum datum = new AgencyModeDatum();
            datum.setAgencyName(rs.getString(COL_AGENCY_NAME));
            datum.setMode(rs.getString(COL_MODE));
            datum.setTypeOfService(rs.getString(COL_TYPE_OF_SERVICE));
            return datum;
        }
    }

    @Override
    public List<AgencyModeDatum> getAgencyModes(String agencyName) {
        String sql = String.format("SELECT agency_name, mode, type_of_service FROM agency " +
                "INNER JOIN agency_mode ON agency.ntd_id = agency_mode.ntd_id " +
                "WHERE agency_name = '%s'", agencyName);
        return template.query(sql, new AgencyModeDatumMapper());
    }

    @Override
    public List<AgencyModeDatum> getAgencyModes(int ntdId) {
        String sql = String.format("SELECT agency_name, mode, type_of_service FROM agency " +
                "INNER JOIN agency_mode ON agency.ntd_id = agency_mode.ntd_id " +
                "WHERE agency.ntd_id = %d", ntdId);
        return template.query(sql, new AgencyModeDatumMapper());
    }



}
