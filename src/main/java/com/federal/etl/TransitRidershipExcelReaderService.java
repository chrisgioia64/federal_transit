package com.federal.etl;

import com.federal.dao.AgencyDao;
import com.federal.dao.AgencyDaoImpl;
import com.federal.dao.AgencyModeDao;
import com.federal.dao.AgencyModeDaoImpl;
import com.federal.model.Agency;
import com.federal.model.AgencyMode;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Log4j2
@Service
public class TransitRidershipExcelReaderService {

    private final static String ADJUST_DATABASE_DOC = "docs/October 2022 Adjusted Database.xlsx";

    private DataSource dataSource;

    @Getter
    private AgencyDao agencyDao;
    @Getter
    private AgencyModeDao agencyModeDao;

    public TransitRidershipExcelReaderService(DataSource dataSource) {
        this.dataSource = dataSource;
        this.agencyDao = new AgencyDaoImpl(dataSource);
        this.agencyModeDao = new AgencyModeDaoImpl(dataSource);
    }

    private final static int BOUND = 20;

    /**
     * Does the database contain entries?
     */
    public boolean databaseContainsEntries() {
        return false;
    }

    private List<String> loadMasterData(XSSFSheet sheet) {
        String filename = ADJUST_DATABASE_DOC;
        XSSFWorkbook workbook = null;
        List<String> result = new LinkedList<>();

            int rowNumber = 1;
            Map<Integer, Agency> idToAgencyMap = new HashMap<>();
            while (rowNumber < BOUND) {
                XSSFRow row = sheet.getRow(rowNumber);
                XSSFCell cell = row.getCell(2);
                if (cell == null || cell.getStringCellValue() == null || cell.getStringCellValue().equals("")) {
                    break;
                }
                int ntdId = getInteger(row.getCell(0));
                String agencyName = row.getCell(2).getStringCellValue();
                String mode = row.getCell(3).getStringCellValue();
                String tos = row.getCell(4).getStringCellValue();
                String city = row.getCell(8).getStringCellValue();
                String state = row.getCell(9).getStringCellValue();
                int uzaSquareMiles = getInteger(row.getCell(12));
                int uzaPopulation = getInteger(row.getCell(13));
                int servicePopulation = getInteger(row.getCell(15));

                int mostRecentYear = getInteger(row.getCell(16));
                int numberMonths = getInteger(row.getCell(17));
                int passengerMiles = getInteger(row.getCell(18));
                int upt = getInteger(row.getCell(19));
                int fares = getInteger(row.getCell(21));
                int operatingExpenses = getInteger(row.getCell(22));

                if (!idToAgencyMap.containsKey(ntdId)) {
                    Agency agency = new Agency();
                    agency.setNtdId(ntdId);
                    agency.setAgencyName(agencyName);
                    agency.setCity(city);
                    agency.setState(state);
                    agency.setUrbanizedArea(uzaSquareMiles);
                    agency.setUrbanizedPopulation(uzaPopulation);
                    agency.setServicePopulation(servicePopulation);

                    idToAgencyMap.put(ntdId, agency);

                    agencyDao.addAgency(agency);
                    log.info("Added agency with name " + agencyName);

                    result.add(agencyName);
                }

                AgencyMode agencyMode = new AgencyMode();
                agencyMode.setNtdId(ntdId);
                agencyMode.setAgencyName(agencyName);
                agencyMode.setMode(mode);
                agencyMode.setTypeOfService(tos);
                agencyMode.setReportYear(mostRecentYear);
                agencyMode.setNumberOfMonths(numberMonths);
                agencyMode.setPassengerMiles(passengerMiles);
                agencyMode.setUpt(upt);
                agencyMode.setFares(fares);
                agencyMode.setOperatingExpenses(operatingExpenses);
                agencyModeDao.addAgencyMode(agencyMode);

                log.info("Reading row number " + rowNumber);
                rowNumber++;
            }
        return result;
    }

    public List<String> loadData() {
        String filename = ADJUST_DATABASE_DOC;
        XSSFWorkbook workbook = null;
        XSSFSheet userSheet = workbook.getSheetAt(1);
        List<String> result = loadMasterData(userSheet);
        return result;
    }

    public Integer getInteger(XSSFCell cell) {
        String number = "";
        try {
            number = cell.getStringCellValue();
            return Integer.parseInt(number);
        } catch (NumberFormatException ex) {
            log.warn("Could not translate the " + number + " into a number");
            return 0;
        }
    }

}
