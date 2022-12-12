package com.federal.web;

import com.federal.etl.TransitRidershipExcelReaderService;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Log4j2
public class DatabaseLoaderController {

    @Autowired
    private TransitRidershipExcelReaderService service;

    public DatabaseLoaderController(TransitRidershipExcelReaderService service) {
        this.service = service;
    }

    @GetMapping("/load_database")
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

}
