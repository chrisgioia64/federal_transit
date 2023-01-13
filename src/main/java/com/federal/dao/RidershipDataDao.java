package com.federal.dao;

import com.federal.model.RidershipData;

import java.util.List;

public interface RidershipDataDao {

    public int addRidershipData(RidershipData data);

    public void addRidershipDataBatch(List<RidershipData> list);

    public RidershipData getRidershipData(int ntdId, String mode, String typeOfService,
                                          int month, int year, String type);

    /**
     * Used for generating the ridership data for the time series plot
     */
    public List<RidershipData> getRidershipData(int ntdId,
                                                String mode, String typeOfService,
                                                String type);

    /**
     * Aggregate the ridership data by month
     */
    public List<RidershipData> getRidershipDataByMonth(int ntdId,
                                                       String mode, String typeOfService,
                                                       String type);

}
