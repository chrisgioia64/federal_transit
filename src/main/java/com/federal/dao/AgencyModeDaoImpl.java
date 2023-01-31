package com.federal.dao;

import com.federal.model.AgencyMode;
import lombok.extern.log4j.Log4j2;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
public class AgencyModeDaoImpl implements AgencyModeDao {

    public static final String TABLE_NAME = "agency_mode";

    private static final String ID = "id";
    private static final String NTD_ID = "ntd_id";
    private static final String AGENCY_NAME = "agency_name";
    private static final String MODE = "mode";
    private static final String TYPE_OF_SERVICE = "type_of_service";
    private static final String REPORT_YEAR = "report_year";
    private static final String MONTHS = "number_of_months";
    private static final String PASSENGER_MILES = "passenger_miles";
    private static final String UPT = "upt";
    private static final String FARES = "fares";
    private static final String OPERATING_EXPENSES = "operating_expenses";

    private DataSource dataSource;
    private JdbcTemplate template;
    private SimpleJdbcInsert insert;

    public AgencyModeDaoImpl(DataSource dataSource) {
        log.info("Agency Mode Dao - Data source: " + dataSource.toString());

        this.dataSource = dataSource;
        this.template = new JdbcTemplate(dataSource);
        this.insert = new SimpleJdbcInsert(dataSource).withTableName(TABLE_NAME)
                .usingGeneratedKeyColumns(ID);
    }

    public static class AgencyModeRowMapper implements RowMapper<AgencyMode> {

        @Override
        public AgencyMode mapRow(ResultSet rs, int rowNum) throws SQLException {
            AgencyMode agency = new AgencyMode();
            int id = rs.getInt(ID);
            agency.setId(id);
            agency.setNtdId(rs.getInt(NTD_ID));
            agency.setMode(rs.getString(MODE));
            agency.setTypeOfService(rs.getString(TYPE_OF_SERVICE));
            agency.setReportYear(rs.getInt(REPORT_YEAR));
            agency.setNumberOfMonths(rs.getInt(MONTHS));
            agency.setPassengerMiles(rs.getInt(PASSENGER_MILES));
            agency.setUpt(rs.getInt(UPT));
            agency.setFares(rs.getInt(FARES));
            agency.setOperatingExpenses(rs.getInt(OPERATING_EXPENSES));
            return agency;
        }
    }

    @Override
    public int addAgencyMode(AgencyMode agencyMode) {
        log.info("Agency Mode: " + agencyMode.toString());
        Map<String, Object> params = new HashMap<>();
        params.put(NTD_ID, agencyMode.getNtdId());
        params.put(MODE, agencyMode.getMode());
        params.put(TYPE_OF_SERVICE, agencyMode.getTypeOfService());
        params.put(REPORT_YEAR, agencyMode.getReportYear());
        params.put(MONTHS, agencyMode.getNumberOfMonths());
        params.put(PASSENGER_MILES, agencyMode.getPassengerMiles());
        params.put(UPT, agencyMode.getUpt());
        params.put(FARES, agencyMode.getFares());
        params.put(OPERATING_EXPENSES, agencyMode.getOperatingExpenses());
        Number number = insert.executeAndReturnKey(params);
        agencyMode.setId(number.intValue());
        return number.intValue();
    }

    @Override
    public List<AgencyMode> getAgencyModesByAgency(String agencyName) {
        String sql = String.format("SELECT * FROM %s WHERE %s = ?",
                TABLE_NAME, AGENCY_NAME);
        return template.query(sql, new AgencyModeDaoImpl.AgencyModeRowMapper(), agencyName);
    }

    @Override
    public List<AgencyMode> getAgencyModesByNtdId(int ntd_id) {
        String sql = String.format("SELECT * FROM %s WHERE %s = ?",
                TABLE_NAME, NTD_ID);
        return template.query(sql, new AgencyModeDaoImpl.AgencyModeRowMapper(), ntd_id);
    }

    @Override
    public Integer getId(int ntdId, String mode, String typeOfService) {
        AgencyMode obj = getAgencyMode(ntdId, mode, typeOfService);
        if (obj == null) {
            return -1;
        }
        return obj.getId();
    }

    @Override
    public AgencyMode getAgencyMode(int ntdId, String mode, String typeOfService) {
        String sql = String.format(
                "SELECT * FROM %s WHERE %s = ? AND %s = ? AND %s = ?;",
                TABLE_NAME, NTD_ID, MODE, TYPE_OF_SERVICE);
        List<AgencyMode> list = template.query(sql,
                new AgencyModeDaoImpl.AgencyModeRowMapper(),
                ntdId, mode, typeOfService);
        if (list.size() == 1) {
            return list.get(0);
        } else {
            return null;
        }
    }

}
