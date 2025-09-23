package com.federal.dao;

public interface MetroSummaryDao {

    String getSummary(String metroName);

    String getSummary();

    String getUptUsageSummary(String metroName);

    String getUptUsageSummary();

    String getAgencyDataAsString();
}
