package com.federal.dao;

import com.federal.model.web.*;
import com.federal.model.AggregateStatistic;
import com.federal.model.TransitAggregateType;
import lombok.extern.log4j.Log4j2;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.math.BigDecimal;
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

    private final static String COL_OPERATION_EXPENSES = "operating_total";
    private final static String COL_COST_PER_PERSON = "cost_per_person";
    private final static String COL_TOTAL_FARES = "total_fares";
    private final static String COL_FAREBOX_RECOVERY = "farebox_recovery";
    private final static String COL_MILES_PER_TRIP = "miles_per_trip";
    private final static String COL_OPERATING_EXPENSE_PER_TRIP = "operating_expense_per_trip";
    private final static String COL_OPERATING_EXPENSE_PER_MILE = "operating_expense_per_mile";

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
        String columnName = statistic.getColumnName();
        String sql =
                "SELECT * FROM (SELECT metro, count, urbanized_population, total, rate, pop_rank, total_rank, " +
                "ROW_NUMBER() OVER (ORDER BY rate DESC) rate_rank " +
                " FROM (SELECT COUNT(*) AS count, agency.urbanized_population, agency.metro, SUM(agency_mode." + columnName + ") AS total, " +
                "SUM(agency_mode." + columnName + ") / agency.urbanized_population AS rate, " +
                "ROW_NUMBER() OVER (ORDER BY SUM(agency_mode." + columnName + ") DESC) total_rank, " +
                "ROW_NUMBER() OVER (ORDER BY agency.urbanized_population DESC) pop_rank " +
                "FROM agency INNER JOIN agency_mode " +
                "WHERE agency_mode.ntd_id = agency.ntd_id " +
                "GROUP BY agency.metro ORDER BY agency.urbanized_population DESC) AS sub " +
                "WHERE urbanized_population >= 500000) AS sub2 " +
                "WHERE metro = ? " +
                "ORDER BY rate DESC;";
        log.debug("Executing getRankInfo query for metro: {} with statistic: {}", metroName, statistic.getDisplayName());
        List<MetroRankInfo> results = template.query(sql,
                new MetroRankInfoRowMapper(), metroName);
        if (results.isEmpty()) {
            log.warn("No rank info found for metro: {} with statistic: {}", metroName, statistic.getDisplayName());
            return null;
        }
        MetroRankInfo info = results.get(0);
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
        String columnName = statistic.getColumnName();
        String transitFilter = createOrStatement(transitType);
        String sql = "SELECT * FROM (SELECT metro, count, urbanized_population, total, rate, total_rank, pop_rank, " +
                "ROW_NUMBER() OVER (ORDER BY rate DESC) rate_rank " +
                " FROM (SELECT COUNT(*) AS count, agency.urbanized_population, agency.metro, SUM(agency_mode." + columnName + ") AS total, " +
                "SUM(agency_mode." + columnName + ") / agency.urbanized_population AS rate, " +
                "ROW_NUMBER() OVER (ORDER BY SUM(agency_mode." + columnName + ") DESC) total_rank, " +
                "ROW_NUMBER() OVER (ORDER BY agency.urbanized_population DESC) pop_rank " +
                "FROM agency INNER JOIN agency_mode " +
                "WHERE agency_mode.ntd_id = agency.ntd_id " +
                transitFilter +
                "GROUP BY agency.metro ORDER BY agency.urbanized_population DESC) AS sub " +
                "WHERE urbanized_population >= 500000) AS sub2 " +
                "WHERE metro = ? " +
                "ORDER BY rate DESC;";
        log.debug("Executing getTransitInfo query for metro: {} with statistic: {} and type: {}", metroName, statistic.getDisplayName(), transitType.getTransitTypeName());
        List<MetroRankInfo> results = template.query(sql,
                new MetroRankInfoRowMapper(), metroName);
        if (results.isEmpty()) {
            log.warn("No transit info found for metro: {} with statistic: {} and type: {}", metroName, statistic.getDisplayName(), transitType.getTransitTypeName());
            return null;
        }
        MetroRankInfo info = results.get(0);
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
        String columnName = statistic.getColumnName();
        String transitFilter = createOrStatement(type);
        String sql = "SELECT metro, SUM(agency_mode." + columnName + ") AS total " +
                        "FROM agency LEFT JOIN agency_mode " +
                        "ON agency_mode.ntd_id = agency.ntd_id " +
                        transitFilter +
                        "WHERE agency.metro = ? " +
                        "GROUP BY agency.metro " +
                        "ORDER BY SUM(agency_mode.upt) DESC;";
        log.debug("Executing getAggregateAmount query for metro: {} with statistic: {} and type: {}", metropolitanArea, statistic.getDisplayName(), type.getTransitTypeName());
        List<Double> list = template.query(sql, new ScatterplotItemDaoImpl.AggregateStatisticDoubleMapper(), metropolitanArea);
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
        String columnName = statistic.getColumnName();
        String sql = "SELECT agency.metro, agency.agency_name, agency_mode.mode, SUM(agency_mode." + columnName + ") AS amount " +
                "FROM agency INNER JOIN agency_mode " +
                "ON agency.ntd_id = agency_mode.ntd_id " +
                "WHERE metro = ? " +
                "GROUP BY agency.agency_name, agency_mode.mode " +
                "ORDER BY metro;";
        log.debug("Executing getTravelModeStatisticDatums query for metro: {} with statistic: {}", metropolitanArea, statistic.getDisplayName());
        List<TravelModeStatisticDatum> list
                = template.query(sql, new TravelModeMapper(), metropolitanArea);
        return list;
    }

    @Override
    public List<TravelModeStatisticDatum> getTravelModeStatisticDatumsByYear(String metropolitanArea,
                                                                             int year, String ridershipDataType) {
        String sql = "Select agency_name, mode, type_of_service, agency_mode_id, year, month, SUM(data) AS amount " +
                "FROM agency INNER JOIN agency_mode " +
                "ON agency.ntd_id = agency_mode.ntd_id " +
                "INNER JOIN ridership_data " +
                "ON agency_mode.id = ridership_data.agency_mode_id " +
                "WHERE agency.metro = ? AND year = ? " +
                "AND type = ? " +
                "GROUP BY agency_mode_id, mode, type_of_service, year ";
        log.debug("Executing getTravelModeStatisticDatumsByYear query for metro: {}, year: {}, type: {}", metropolitanArea, year, ridershipDataType);
        List<TravelModeStatisticDatum> list
                = template.query(sql, new TravelModeMapper(), metropolitanArea, year, ridershipDataType);
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
        String sql = "Select year FROM agency INNER JOIN agency_mode " +
                "ON agency.ntd_id = agency_mode.ntd_id " +
                "INNER JOIN ridership_data " +
                "ON agency_mode.id = ridership_data.agency_mode_id " +
                "WHERE agency.metro = ? AND type = ? " +
                "GROUP BY year " +
                "ORDER BY year desc;";
        log.debug("Executing getAvailableYears query for metro: {} with type: {}", metropolitanArea, ridershipDataType);
        return template.query(sql, new YearMapper(), metropolitanArea, ridershipDataType);
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
        // Fixed: Added agency.ntd_id to GROUP BY to avoid SQL ambiguity
        // Also removed DISTINCT since GROUP BY already ensures uniqueness
        String sql = "SELECT agency_name, SUM(agency_mode.upt) AS total_upt, agency.ntd_id " +
                        "FROM agency " +
                "INNER JOIN agency_mode ON agency.ntd_id = agency_mode.ntd_id " +
                "WHERE agency.metro = ? " +
                "GROUP BY agency_name, agency.ntd_id " +
                "ORDER BY SUM(agency_mode.upt) DESC; ";
        log.debug("Executing getAgenciesForMetropolitanArea query for metro: {}", metropolitanArea);
        List<AgencyDatum> results = template.query(sql, new AgencyDatumMapper(), metropolitanArea);
        log.debug("Found {} agencies for metro: {}", results.size(), metropolitanArea);
        if (results.isEmpty()) {
            log.warn("No agencies found for metro: {}", metropolitanArea);
        }
        return results;
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

    public static class AgencyDataDatumMapper implements RowMapper<AgencyData> {

        @Override
        public AgencyData mapRow(ResultSet rs, int rowNum) throws SQLException {
            AgencyData data = new AgencyData();
            data.setAgencyName(rs.getString(COL_AGENCY_NAME));
            data.setTotalOperationCost(rs.getInt(COL_OPERATION_EXPENSES));
            data.setOperationCostPerPerson(rs.getDouble(COL_COST_PER_PERSON));
            data.setTotalFares(rs.getInt(COL_TOTAL_FARES));
            data.setFareboxRecovery(rs.getDouble(COL_FAREBOX_RECOVERY));
            data.setMilesPerTrip(rs.getDouble(COL_MILES_PER_TRIP));
            data.setOperatingExpensePerTrip(rs.getDouble(COL_OPERATING_EXPENSE_PER_TRIP));
            data.setOperatingExpensePerMile(rs.getDouble(COL_OPERATING_EXPENSE_PER_MILE));
            return data;
        }
    }

    @Override
    public List<AgencyModeDatum> getAgencyModes(String agencyName) {
        String sql = "SELECT agency_name, mode, type_of_service FROM agency " +
                "INNER JOIN agency_mode ON agency.ntd_id = agency_mode.ntd_id " +
                "WHERE agency_name = ?";
        log.debug("Executing getAgencyModes query for agency: {}", agencyName);
        return template.query(sql, new AgencyModeDatumMapper(), agencyName);
    }

    @Override
    public List<AgencyModeDatum> getAgencyModes(int ntdId) {
        String sql = "SELECT agency_name, mode, type_of_service FROM agency " +
                "INNER JOIN agency_mode ON agency.ntd_id = agency_mode.ntd_id " +
                "WHERE agency.ntd_id = ?";
        log.debug("Executing getAgencyModes query for ntdId: {}", ntdId);
        return template.query(sql, new AgencyModeDatumMapper(), ntdId);
    }

    @Override
    public List<AgencyData> getAgencyDatums(String metropolitanArea) {
        String sql = """
                        SELECT * FROM (SELECT metro, agency_name, urbanized_population,
                        SUM(agency_mode.operating_expenses) AS operating_total,
                        SUM(agency_mode.operating_expenses) / agency.urbanized_population AS cost_per_person,
                        SUM(agency_mode.fares) AS total_fares,
                        SUM(agency_mode.fares) / SUM(agency_mode.operating_expenses) AS farebox_recovery,
                        SUM(agency_mode.passenger_miles) / SUM(agency_mode.upt) AS miles_per_trip,
                        SUM(agency_mode.operating_expenses) / SUM(agency_mode.UPT) AS operating_expense_per_trip,
                        SUM(agency_mode.operating_expenses) / (SUM(agency_mode.passenger_miles)) AS operating_expense_per_mile
                        FROM agency
                        INNER JOIN agency_mode ON agency_mode.ntd_id = agency.ntd_id
                        GROUP BY agency.metro, agency.agency_name
                        ORDER BY agency.urbanized_population DESC)
                        AS sub2 WHERE metro = ? ORDER BY cost_per_person DESC;
                 """;
        log.debug("Executing getAgencyDatums query for metro: {}", metropolitanArea);
        return template.query(sql, new AgencyDataDatumMapper(), metropolitanArea);
    }

    public class MetroWithCoordinatesMapper implements RowMapper<MetroWithCoordinatesDTO> {
        @Override
        public MetroWithCoordinatesDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            MetroWithCoordinatesDTO dto = new MetroWithCoordinatesDTO();
            dto.setName(rs.getString(AgencyDaoImpl.METRO));
            dto.setState(rs.getString(AgencyDaoImpl.STATE));
            
            BigDecimal lat = rs.getBigDecimal("latitude");
            BigDecimal lon = rs.getBigDecimal("longitude");
            
            // Only set if not null
            if (lat != null) {
                dto.setLatitude(lat);
            }
            if (lon != null) {
                dto.setLongitude(lon);
            }
            
            // Get population (use MAX since we're grouping by metro)
            Long population = rs.getLong("population");
            if (!rs.wasNull()) {
                dto.setPopulation(population);
            }
            
            return dto;
        }
    }

    @Override
    public List<MetroWithCoordinatesDTO> getMetropolitanAreasWithCoordinates() {
        String sql = "SELECT DISTINCT agency.metro, MAX(agency.state) as state, " +
                     "MAX(agency.latitude) as latitude, MAX(agency.longitude) as longitude, " +
                     "MAX(agency.urbanized_population) as population " +
                     "FROM agency " +
                     "WHERE agency.urbanized_population >= 500000 " +
                     "AND agency.latitude IS NOT NULL " +
                     "AND agency.longitude IS NOT NULL " +
                     "GROUP BY agency.metro " +
                     "ORDER BY agency.metro";
        
        return template.query(sql, new MetroWithCoordinatesMapper());
    }

}
