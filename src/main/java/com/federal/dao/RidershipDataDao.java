package com.federal.dao;

import com.federal.model.RidershipData;

import java.util.List;

public interface RidershipDataDao {

    public int addRidershipData(RidershipData data);

    public void addRidershipDataBatch(List<RidershipData> list);

    public RidershipData getRidershipData(int ntdId, String mode, String typeOfService,
                                          int month, int year, String type);

}
