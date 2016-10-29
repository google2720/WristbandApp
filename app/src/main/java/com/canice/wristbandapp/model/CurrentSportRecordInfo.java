/*
 * 文件名：CurrentSportRecordInfo.java
 * 创建人：fei
 * 创建时间：2016-3-11
 * 版     权：Copyright Easier Digital Tech. Co. Ltd. All Rights Reserved.
 */
package com.canice.wristbandapp.model;

import java.io.Serializable;
import java.math.BigDecimal;

import com.canice.wristbandapp.ble.data.PedometerDataResult;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * 
 * @author fei
 * @version [WristbandApp, 2016-3-11]
 */
public class CurrentSportRecordInfo implements Serializable {

    private static final long serialVersionUID = -4590937052185158651L;
    public String address;
    public String date;
    public int stepNum;
    public double distance;
    public int cal;
    public int goal;
    public int battery;

    public static CurrentSportRecordInfo parser(PedometerDataResult d) {
        CurrentSportRecordInfo record = new CurrentSportRecordInfo();
        record.date = d.getDate();
        record.stepNum = d.getStep();
        double distance = d.getDistance() / 100.0;
        BigDecimal b = new BigDecimal(distance);
        distance = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        record.distance = distance;
        record.cal = d.getCal();
        record.goal = d.getGol();
        record.battery = d.getBattery();
        return record;
    }
}
