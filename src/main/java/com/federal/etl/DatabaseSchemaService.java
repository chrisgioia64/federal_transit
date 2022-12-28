package com.federal.etl;

import com.federal.dao.AgencyDao;
import com.federal.dao.AgencyDaoImpl;
import com.federal.dao.AgencyModeDao;
import com.federal.dao.AgencyModeDaoImpl;
import lombok.Getter;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;

@Service
@Log4j2
public class DatabaseSchemaService {

    private DataSource dataSource;
    private Connection connection;

    public DatabaseSchemaService(DataSource dataSource) {
        this.dataSource = dataSource;
        try {
            this.connection = dataSource.getConnection();
        } catch (SQLException e) {
            log.error("Could not retrieve connection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean seedDatabase() throws FileNotFoundException {
        ScriptRunner sr = new ScriptRunner(connection);
        Reader reader = reader = new BufferedReader(new FileReader("src/main/resources/seed.sql"));
        sr.runScript(reader);
        return true;
    }

    /**
     * Creates the database schema
     */
    public boolean createDatabase() throws FileNotFoundException {
        ScriptRunner sr = new ScriptRunner(connection);
        Reader reader = reader = new BufferedReader(new FileReader("src/main/resources/schema.sql"));
        sr.runScript(reader);
        return true;
    }

    public boolean truncateDatabase() throws FileNotFoundException {
        ScriptRunner sr = new ScriptRunner(connection);
        Reader reader = new BufferedReader(
                    new FileReader("src/main/resources/truncate_tables.sql"));
        sr.runScript(reader);
        return true;
    }

    public boolean dropDatabase() throws FileNotFoundException {
        ScriptRunner sr = new ScriptRunner(connection);
        Reader reader = new BufferedReader(
                    new FileReader("src/main/resources/drop_tables.sql"));
        sr.runScript(reader);
        return true;
    }

}
