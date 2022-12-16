package com.federal.etl;

import com.federal.dao.*;
import com.federal.model.Agency;
import com.federal.model.AgencyMode;
import com.federal.model.RidershipData;
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
import java.sql.SQLIntegrityConstraintViolationException;
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
    @Getter
    private RidershipDataDao ridershipDataDao;

    public TransitRidershipExcelReaderService(DataSource dataSource) {
        this.dataSource = dataSource;
        this.agencyDao = new AgencyDaoImpl(dataSource);
        this.agencyModeDao = new AgencyModeDaoImpl(dataSource);
        this.ridershipDataDao = new RidershipDataDaoImpl(dataSource);
    }

    private final static int BOUND = 1000000;

    /**
     * Does the database contain entries?
     */
    public boolean databaseContainsEntries() {
        return false;
    }

    private List<String> loadMasterData(XSSFSheet sheet, int startRow) {
        String filename = ADJUST_DATABASE_DOC;
        XSSFWorkbook workbook = null;
        List<String> result = new LinkedList<>();

            int rowNumber = startRow;
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
                int passengerMiles = getInteger(row.getCell(19));
                int upt = getInteger(row.getCell(20));
                int fares = getInteger(row.getCell(22));
                int operatingExpenses = getInteger(row.getCell(23));

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

    public void loadRidershipData(int startRow, int spreadsheetTabNumber,
                                  String type) {
        String filename = ADJUST_DATABASE_DOC;
        XSSFWorkbook workbook = null;
        try {
            workbook = new XSSFWorkbook(new File(filename));
            XSSFSheet userSheet = workbook.getSheetAt(spreadsheetTabNumber);
            loadRidershipData(startRow, userSheet, type);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }
    }

    public static class MonthYear {
        private int year;
        private int month;

        public MonthYear(int year, int month) {
            this.year = year;
            this.month = month;
        }
    }

    private static MonthYear getMonthYear(int col) {
        int yearIndex = 2002 + ((col - 9) / 12);
        int monthIndex = ((col - 9) % 12) + 1;
        return new MonthYear(yearIndex, monthIndex);
    }

    public void loadRidershipData(int startRow, XSSFSheet sheet, String type) {
        String filename = ADJUST_DATABASE_DOC;
        XSSFWorkbook workbook = null;
        List<String> result = new LinkedList<>();

        int rowNumber = startRow;
        long getIdTime = 0;
        long insertEntryTime = 0;
        long startTime = System.currentTimeMillis();

        while (rowNumber < BOUND) {
            XSSFRow row = sheet.getRow(rowNumber);
            XSSFCell cell = row.getCell(2);
            if (cell == null || cell.getStringCellValue() == null || cell.getStringCellValue().equals("")) {
                break;
            }

            if (rowNumber % 100 == 0) {
                log.info("Grabbing the id time: " + getIdTime);
                log.info("Inserting the entry time: " + insertEntryTime);
            }

            int ntdId = getInteger(row.getCell(0));
            String agencyName = row.getCell(2).getStringCellValue();
            String mode = row.getCell(7).getStringCellValue();
            String tos = row.getCell(8).getStringCellValue();
            long time = System.currentTimeMillis();
            Integer modeId = agencyModeDao.getId(ntdId, mode, tos);
            getIdTime += (System.currentTimeMillis() - time);

            int startCol = 9;
            int endCol = 259;
            List<RidershipData> list = new LinkedList<>();
            for (int i = startCol; i <= endCol; i++) {
                MonthYear monthYear = getMonthYear(i);
                cell = row.getCell(i);
                if (cell == null) {
                    continue;
                } else {
                    Double cellValue = cell.getNumericCellValue();
                    int value = cellValue.intValue();
                    if (value != 0) {
                        RidershipData data = new RidershipData();
                        data.setMonth(monthYear.month);
                        data.setYear(monthYear.year);
                        data.setAgencyModeId(modeId);
                        data.setType(type);
                        data.setData(value);
                        list.add(data);
                    }
//                    ridershipDataDao.addRidershipData(data);
                }
            }
            time = System.currentTimeMillis();
            ridershipDataDao.addRidershipDataBatch(list);
            insertEntryTime += (System.currentTimeMillis() - time);
            rowNumber++;
        }
        long currentTime = System.currentTimeMillis();
        long totalTime = (currentTime - startTime);
        log.info("Total time: " + totalTime);
    }

    public void loadUptData(int startRow) {
        loadRidershipData(startRow, 2, "UPT");
    }

    public void loadVrmData(int startRow) {
        loadRidershipData(startRow, 3, "VRM");
    }

    public void loadVrhData(int startRow) {
        loadRidershipData(startRow, 4, "VRH");
    }

    public void loadVomsData(int startRow) {
        loadRidershipData(startRow, 5, "VOMS");
    }

    public List<String> loadMasterData(int startRow) {
        String filename = ADJUST_DATABASE_DOC;
        XSSFWorkbook workbook = null;
        try {
            workbook = new XSSFWorkbook(new File(filename));
            XSSFSheet userSheet = workbook.getSheetAt(1);
            List<String> result = loadMasterData(userSheet, startRow);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Integer getInteger(XSSFCell cell) {
        Double number = 0.0;
        if (cell == null) {
            return 0;
        }
        try {
            number = cell.getNumericCellValue();
            return number.intValue();
        } catch (NumberFormatException ex) {
            log.warn("Could not translate the " + number + " into a number");
            return 0;
        }
    }

}
