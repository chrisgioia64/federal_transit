package com.federal.dao;

import com.federal.model.Agency;

import java.util.List;

public interface AgencyDao {

    public int addAgency(Agency agency);

    public Agency getAgencyByAgencyName(String agencyName);

    public Agency getAgencyByNtdId(int id);

    public List<Agency> getAgenciesByCityAndState(String city, String state);

    public int getNumAgencies();

}
