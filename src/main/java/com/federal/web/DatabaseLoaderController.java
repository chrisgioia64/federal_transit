package com.federal.web;

import com.federal.etl.DatabaseSchemaService;
import com.federal.etl.TransitRidershipExcelReaderService;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.FileNotFoundException;

@Controller
@Log4j2
public class DatabaseLoaderController {

    @Autowired
    private TransitRidershipExcelReaderService service;

    @Autowired
    private DatabaseSchemaService databaseSchemaService;

    public DatabaseLoaderController(TransitRidershipExcelReaderService service,
                                    DatabaseSchemaService databaseSchemaService) {
        this.service = service;
        this.databaseSchemaService = databaseSchemaService;
    }

    @PostMapping("/database/load")
    public ResponseEntity loadDatabase() {
        service.loadData();
        return ResponseEntity.ok("Data loaded successfully");
    }

    @GetMapping("/database/num_agencies")
    public ResponseEntity getNumAgencies() {
        int numAgencies = service.getAgencyDao().getNumAgencies();
        log.info("The number of agencies is: " + numAgencies);
        return ResponseEntity.ok("Number of agencies: " + numAgencies);
    }

    @PostMapping("/database/create")
    public ResponseEntity createDatabase() throws FileNotFoundException  {
        boolean ok = databaseSchemaService.createDatabase();
        return ResponseEntity.ok("Created database");
    }

    @PostMapping("/database/truncate")
    public ResponseEntity truncateDatabase() throws FileNotFoundException  {
        boolean ok = databaseSchemaService.truncateDatabase();
        return ResponseEntity.ok("Truncated database");
    }

    @PostMapping("/database/drop")
    public ResponseEntity dropDatabase() throws FileNotFoundException  {
        boolean ok = databaseSchemaService.dropDatabase();
        return ResponseEntity.ok("Dropped database");
    }

}
