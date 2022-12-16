package com.federal.dao;

import com.federal.model.AgencyMode;
import com.federal.model.RidershipData;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
public class RidershipDataDaoImpl implements RidershipDataDao {

    public static final String TABLE_NAME = "Ridership_Data";

    private static final String ID = "id";
    private static final String AGENCY_MODE_ID = "agency_mode_id";
    private static final String TYPE = "type";
    private static final String YEAR = "year";
    private static final String MONTH = "month";
    private static final String DATA = "data";

    private DataSource dataSource;
    private JdbcTemplate template;
    private SimpleJdbcInsert insert;

    public static class RidershipDataDaoMapper implements RowMapper<RidershipData> {

        @Override
        public RidershipData mapRow(ResultSet rs, int rowNum) throws SQLException {
            RidershipData data = new RidershipData();
            int id = rs.getInt(ID);
            data.setAgencyModeId(rs.getInt(AGENCY_MODE_ID));
            data.setType(rs.getString(TYPE));
            data.setYear(rs.getInt(YEAR));
            data.setMonth(rs.getInt(MONTH));
            data.setData(rs.getInt(DATA));
            return data;
        }
    }

    public RidershipDataDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
        this.template = new JdbcTemplate(dataSource);
        this.insert = new SimpleJdbcInsert(dataSource).withTableName(TABLE_NAME)
                .usingGeneratedKeyColumns(ID);
    }

    @Override
    public int addRidershipData(RidershipData data) {
        Map<String, Object> params = new HashMap<>();
        params.put(AGENCY_MODE_ID, data.getAgencyModeId());
        params.put(TYPE, data.getType());
        params.put(YEAR, data.getYear());
        params.put(MONTH, data.getMonth());
        params.put(DATA, data.getData());
        Number number = insert.executeAndReturnKey(params);
        data.setId(number.intValue());
        return number.intValue();
    }

//    @Override
//    public void addRidershipDataBatch(List<RidershipData> list) {
//        try {
//            Connection conn = DataSourceUtils.getConnection(dataSource);
//            String sql = String.format("INSERT INTO %s (%s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?)",
//                    TABLE_NAME, AGENCY_MODE_ID, TYPE, YEAR, MONTH, DATA);
//            PreparedStatement ps = conn.prepareStatement(sql);
//
//            for (RidershipData data : list) {
//                ps.setInt(1, data.getAgencyModeId());
//                ps.setString(2, data.getType());
//                ps.setInt(3, data.getYear());
//                ps.setInt(4, data.getMonth());
//                ps.setInt(5, data.getData());
//                ps.addBatch();
//                ps.clearParameters();
//            }
//            int[] results = ps.executeBatch();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//    }

    @Override
    public void addRidershipDataBatch(List<RidershipData> list) {
        try {
            Connection conn = DataSourceUtils.getConnection(dataSource);
            String sql = String.format("INSERT INTO %s (%s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?)",
                    TABLE_NAME, AGENCY_MODE_ID, TYPE, YEAR, MONTH, DATA);
            PreparedStatement ps = conn.prepareStatement(sql);

            for (RidershipData data : list) {
                ps.setInt(1, data.getAgencyModeId());
                ps.setString(2, data.getType());
                ps.setInt(3, data.getYear());
                ps.setInt(4, data.getMonth());
                ps.setInt(5, data.getData());
                ps.addBatch();
                ps.clearParameters();
            }
            int[] results = ps.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

//    @Override
//    public void addRidershipDataBatch(List<RidershipData> list) {
//        String sql = String.format("INSERT INTO %s (%s, %s, %s, %s, %s) " +
//                " VALUES (?, ?, ?, ?, ?)",
//                TABLE_NAME, AGENCY_MODE_ID, TYPE, YEAR, MONTH, DATA);
//        template.batchUpdate(sql,
//                list,
//                100,
//                (PreparedStatement ps, RidershipData ridershipData) -> {
//                    ps.setInt(1, ridershipData.getAgencyModeId());
//                    ps.setString(2, ridershipData.getType());
//                    ps.setInt(3, ridershipData.getYear());
//                    ps.setInt(4, ridershipData.getMonth());
//                    ps.setInt(5, ridershipData.getData());
//                });
//    }

    @Override
    public RidershipData getRidershipData(int ntdId, String mode, String typeOfService,
                                          int month, int year, String type) {
        String sql =
                "SELECT * FROM agency_mode " +
                "INNER JOIN ridership_data WHERE agency_mode.id = ridership_data.agency_mode_id " +
                "AND agency_mode.ntd_id = ? AND agency_mode.mode = ? AND agency_mode.type_of_service = ? " +
                        "AND ridership_data.month = ? AND ridership_data.year = ? AND ridership_data.type = ?" ;
        try {
            log.info("Sql: " + sql);
            RidershipData obj = template.queryForObject(sql,
                    new RidershipDataDaoImpl.RidershipDataDaoMapper(),
                    ntdId, mode, typeOfService, month, year, type);
            return obj;
        } catch (DataAccessException ex) {
            log.info(
                    String.format("Could not retrieve ridership data for %s, %s, %s -- %s %s - %s",
                            ntdId, mode, typeOfService, month, year, type));
            return null;
        }
    }

    private int addRidershipDataUsingSimpleInsert(RidershipData data) {
        Map<String, Object> params = new HashMap<>();
        params.put(AGENCY_MODE_ID, data.getAgencyModeId());
        params.put(TYPE, data.getType());
        params.put(YEAR, data.getYear());
        params.put(MONTH, data.getMonth());
        Number number = insert.executeAndReturnKey(params);
        data.setId(number.intValue());
        return number.intValue();
    }

}
