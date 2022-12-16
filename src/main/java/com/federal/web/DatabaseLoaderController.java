package com.federal.web;

import com.federal.etl.DatabaseSchemaService;
import com.federal.etl.TransitRidershipExcelReaderService;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @PostMapping("/database/load_master/{id}")
    public ResponseEntity loadMasterDatabase(@PathVariable int id) {
        service.loadMasterData(id);
        return ResponseEntity.ok("Data loaded successfully");
    }

    @PostMapping("/database/load_upt/{id}")
    public ResponseEntity loadUpt(@PathVariable int id) {
        service.loadUptData(id);
        return ResponseEntity.ok("Data loaded successfully into upt");
    }

    @PostMapping("/database/load_vrm/{id}")
    public ResponseEntity loadVrm(@PathVariable int id) {
        service.loadVrmData(id);
        return ResponseEntity.ok("Data loaded successfully into vrm");
    }

    @PostMapping("/database/load_vrh/{id}")
    public ResponseEntity loadVrh(@PathVariable int id) {
        service.loadVrhData(id);
        return ResponseEntity.ok("Data loaded successfully into vrh");
    }

    @PostMapping("/database/load_voms/{id}")
    public ResponseEntity loadDatabase(@PathVariable int id) {
        service.loadVomsData(id);
        return ResponseEntity.ok("Data loaded successfuly into voms");
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
