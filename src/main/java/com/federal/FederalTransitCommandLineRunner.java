package com.federal;

import com.federal.etl.TransitRidershipExcelReaderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

//@SpringBootApplication
public class FederalTransitCommandLineRunner
        implements CommandLineRunner {

    @Autowired
    private TransitRidershipExcelReaderService service;

    private static Logger LOG = LoggerFactory
            .getLogger(FederalTransitCommandLineRunner.class);

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(FederalTransitCommandLineRunner.class);
        application.setWebApplicationType(WebApplicationType.NONE);
        application.run(args);
    }

    @Override
    public void run(String... args) {
        List<String> agencies = service.loadData();
        LOG.info("Agencies: " + agencies);
    }
}
