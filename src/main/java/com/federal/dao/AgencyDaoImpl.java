package com.federal.dao;

import com.federal.model.Agency;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataAccessException;
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
public class AgencyDaoImpl implements AgencyDao {

    public static final String TABLE_NAME = "Agency";

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

    public AgencyDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
        this.template = new JdbcTemplate(dataSource);
        this.insert = new SimpleJdbcInsert(dataSource).withTableName(TABLE_NAME)
                .usingGeneratedKeyColumns(ID);
    }

    public static class AgencyRowMapper implements RowMapper<Agency> {

        @Override
        public Agency mapRow(ResultSet rs, int rowNum) throws SQLException {
            Agency agency = new Agency();
            int id = rs.getInt(ID);
            agency.setId(id);
            agency.setAgencyName(rs.getString(AGENCY_NAME));
            agency.setNtdId(rs.getInt(NTD_ID));
            agency.setCity(rs.getString(CITY));
            agency.setState(rs.getString(STATE));
            agency.setUrbanizedArea(rs.getInt(URBANIZED_AREA));
            agency.setUrbanizedPopulation(rs.getInt(URBANIZED_POPULATION));
            agency.setServicePopulation(rs.getInt(SERVICE_POPULATION));
            return agency;
        }
    }

    @Override
    public int addAgency(Agency agency) {
        Map<String, Object> params = new HashMap<>();
        log.info("Agency: " + agency.toString());
        params.put(NTD_ID, agency.getNtdId());
        params.put(AGENCY_NAME, agency.getAgencyName());
        params.put(CITY, agency.getCity());
        params.put(STATE, agency.getState());
        params.put(URBANIZED_AREA, agency.getUrbanizedArea());
        params.put(URBANIZED_POPULATION, agency.getUrbanizedPopulation());
        params.put(SERVICE_POPULATION, agency.getServicePopulation());
        Number number = insert.executeAndReturnKey(params);
        agency.setId(number.intValue());
        return number.intValue();
    }

    @Override
    public Agency getAgencyByAgencyName(String agencyName) {
        String sql = String.format("SELECT * FROM %s WHERE %s = ?", TABLE_NAME, AGENCY_NAME);
        try {
            Agency agency = template.queryForObject(sql, new AgencyRowMapper(), agencyName);
            return agency;
        } catch (DataAccessException ex) {
            log.warn("No agency found with name " + agencyName);
            return null;
        }
    }

    @Override
    public Agency getAgencyByNtdId(int ntdId) {
        String sql = String.format("SELECT * FROM %s WHERE %s = ?", TABLE_NAME, NTD_ID);
        try {
            Agency agency = template.queryForObject(sql, new AgencyRowMapper(), ntdId);
            return agency;
        } catch (DataAccessException ex) {
            log.warn("No agency found with id " + ntdId);
            return null;
        }
    }

    @Override
    public List<Agency> getAgenciesByCityAndState(String city, String state) {
        String sql = String.format("SELECT * FROM %s WHERE %s = ? AND %s = ?",
                TABLE_NAME, CITY, STATE);
        return template.query(sql, new AgencyRowMapper(), city, state);
    }

    @Override
    public int getNumAgencies() {
        String sql = String.format("SELECT COUNT(*) FROM %s", TABLE_NAME);
        int count = template.queryForObject(sql, Integer.class);
        return count;
    }

}
