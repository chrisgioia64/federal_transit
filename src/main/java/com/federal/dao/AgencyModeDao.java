package com.federal.dao;

import com.federal.model.Agency;
import com.federal.model.AgencyMode;

import java.util.List;

public interface AgencyModeDao {

    public int addAgencyMode(AgencyMode agencyMode);

    public List<AgencyMode> getAgencyModesByAgency(String agencyName);

    public List<AgencyMode> getAgencyModesByNtdId(int ntd_id);

}
