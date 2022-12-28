package com.federal.test.database;

import com.federal.dao.*;
import com.federal.etl.TransitRidershipExcelReaderService;
import com.federal.model.Agency;
import com.federal.model.AgencyMode;
import com.federal.model.RidershipData;
import com.federal.web.DatabaseLoaderController;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;

import java.util.List;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;

@SpringBootTest
@Log4j2
//@SpringBootTest(classes=FederalTransitApplication.class)
public class DatabaseSanityTest {

    private AgencyDao agencyDao;
    private AgencyModeDaoImpl agencyModeDao;
    private RidershipDataDao ridershipDataDao;

    @Autowired
    private DatabaseLoaderController controller;

    @Autowired
    private TransitRidershipExcelReaderService service;

    @Autowired
    private DataSource dataSource;

    @BeforeEach
    public void test() {
        agencyDao = new AgencyDaoImpl(dataSource);
        agencyModeDao = new AgencyModeDaoImpl(dataSource);
        ridershipDataDao = new RidershipDataDaoImpl(dataSource);
    }

    @Test
    public void testMasterDataAgency() {
        Agency agency = agencyDao.getAgencyByNtdId(1);
        assertEquals("King County Department of Metro Transit",
                agency.getAgencyName());
        assertEquals("seattle", agency.getCity().toLowerCase());
        assertEquals("wa", agency.getState().toLowerCase());
        assertEquals(2287050 , agency.getServicePopulation());
        assertEquals(1010, agency.getUrbanizedArea());
        assertEquals(3059393, agency.getUrbanizedPopulation());

        agency = agencyDao.getAgencyByNtdId(99425);
        assertEquals("Pomona Valley Transportation Authority",
                agency.getAgencyName());
        assertEquals("la verne", agency.getCity().toLowerCase());
        assertEquals("ca", agency.getState().toLowerCase());
        assertEquals(252880 , agency.getServicePopulation());
        assertEquals(1736, agency.getUrbanizedArea());
        assertEquals(12150996, agency.getUrbanizedPopulation());
    }

    @Test
    public void testMasterDataAgency1() {
        Agency agency = agencyDao.getAgencyByNtdId(1);
        assertEquals("King County Department of Metro Transit", agency.getAgencyName());
        assertEquals("seattle", agency.getCity().toLowerCase());
        assertEquals("wa", agency.getState().toLowerCase());
        assertEquals(3059393, agency.getUrbanizedPopulation());
    }

    @Test
    public void testMasterDataAgency2() {
        Agency agency = agencyDao.getAgencyByNtdId(99425);
        assertEquals("Pomona Valley Transportation Authority", agency.getAgencyName());
        assertEquals("la verne", agency.getCity().toLowerCase());
        assertEquals("ca", agency.getState().toLowerCase());
        assertEquals(12150996, agency.getUrbanizedPopulation());
    }

    @Test
    public void testMasterDataAgencyMode1() {
        List<AgencyMode> agencyModes = agencyModeDao.getAgencyModesByNtdId(1);
        assertEquals(9, agencyModes.size());

        AgencyMode agencyMode = agencyModeDao.getAgencyMode(1, "MB", "PT");
        log.info("Agency Mode: " + agencyMode.toString());
        assertEquals("MB", agencyMode.getMode());
        assertEquals(1631439, agencyMode.getPassengerMiles());
        assertEquals(424166, agencyMode.getUpt());
        assertEquals(258046, agencyMode.getFares());
        assertEquals(12178959, agencyMode.getOperatingExpenses());
    }

    @Test
    public void testMasterDataAgencyMode2() {
        List<AgencyMode> agencyModes = agencyModeDao.getAgencyModesByNtdId(99425);
        assertEquals(2, agencyModes.size());

        AgencyMode agencyMode = agencyModeDao.getAgencyMode(99425, "DR", "TX");
        assertEquals("DR", agencyMode.getMode());
        assertEquals(128170, agencyMode.getPassengerMiles());
        assertEquals(29556, agencyMode.getUpt());
        assertEquals(42744, agencyMode.getFares());
        assertEquals(875250, agencyMode.getOperatingExpenses());
    }

    @Test
    public void testUptData() {
        String type = "UPT";
        RidershipData data = ridershipDataDao.getRidershipData(
                2, "DR", "PT", 1, 2010, type);
        assertEquals(20651, data.getData());

        data = ridershipDataDao.getRidershipData(
                2, "DR", "PT", 5, 2010, type);
        assertEquals(22305, data.getData());

        data = ridershipDataDao.getRidershipData(
                2, "DR", "PT", 5, 2010, type);
        assertEquals(22305, data.getData());

        data = ridershipDataDao.getRidershipData(
                2, "DR", "PT", 10, 2022, type);
        assertEquals(14314, data.getData());

        data = ridershipDataDao.getRidershipData(
                2, "DR", "PT", 5, 2010, type);
        assertEquals(22305, data.getData());

        data = ridershipDataDao.getRidershipData(
                2, "DR", "PT", 10, 2022, type);
        assertEquals(14314, data.getData());

        data = ridershipDataDao.getRidershipData(
                1, "MB", "PT", 1, 2002, type);
        assertNull(data);

        data = ridershipDataDao.getRidershipData(
                90020, "CB", "DO", 1, 2002, type);
        assertNull(data);

        data = ridershipDataDao.getRidershipData(
                90189, "MB", "PT", 2, 2002, type);
        assertEquals(142053, data.getData());
    }

    @Test
    public void testVrmData() {
        String type = "VRM";
        RidershipData data = ridershipDataDao.getRidershipData(
                1, "DR", "PT", 1, 2002, type);
        assertEquals(746158, data.getData());

        data = ridershipDataDao.getRidershipData(
                1, "MB", "DO", 9, 2022, type);
        assertEquals(2387413, data.getData());

        data = ridershipDataDao.getRidershipData(
                1, "MB", "DO", 9, 2022, type);
        assertEquals(2387413, data.getData());

        data = ridershipDataDao.getRidershipData(
                10007, "DR", "PT", 1, 2002, type);
        assertNull(data);

        data = ridershipDataDao.getRidershipData(
                10008, "DR", "PT", 2, 2002, type);
        assertEquals(314503, data.getData());

        data = ridershipDataDao.getRidershipData(
                99425, "DR", "TX", 3, 2016, type);
        assertEquals(24835, data.getData());
    }

    @Test
    public void testVrhData() {
        String type = "VRH";
        RidershipData data = ridershipDataDao.getRidershipData(
                1, "DR", "PT", 1, 2002, type);
        assertEquals(53306, data.getData());

        data = ridershipDataDao.getRidershipData(
                1, "MB", "DO", 10, 2022, type);
        assertEquals(231132, data.getData());

        data = ridershipDataDao.getRidershipData(
                20009, "MB", "DO", 1, 2002, type);
        assertNull(data);

        data = ridershipDataDao.getRidershipData(
                20010, "DR", "DO", 1, 2002, type);
        assertEquals(1819, data.getData());

        data = ridershipDataDao.getRidershipData(
                30010, "DR", "PT", 1, 2002, type);
        assertEquals(17010, data.getData());

        data = ridershipDataDao.getRidershipData(
                99425, "DR", "TX", 6, 2022, type);
        assertEquals(439, data.getData());
    }

    @Test
    public void testVomsData() {
        String type = "VOMS";
        RidershipData data = ridershipDataDao.getRidershipData(
                1, "DR", "PT", 1, 2002, type);
        assertEquals(574, data.getData());

        data = ridershipDataDao.getRidershipData(
                1, "DR", "PT", 10, 2022, type);
        assertEquals(459, data.getData());

        data = ridershipDataDao.getRidershipData(
                8, "CR", "PT", 1, 2002, type);
        assertNull(data);

        data = ridershipDataDao.getRidershipData(
                60056, "CR", "PT", 1, 2002, type);
        assertEquals(9, data.getData());

        data = ridershipDataDao.getRidershipData(
                99424, "MB", "PT", 9, 2022, type);
        assertEquals(24, data.getData());
    }


}
