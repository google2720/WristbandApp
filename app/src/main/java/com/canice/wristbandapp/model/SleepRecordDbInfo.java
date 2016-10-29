/*
 * 文件名：SleepRecordDbInfo.java
 * 创建人：fei
 * 创建时间：2016-3-14
 * 版     权：Copyright Easier Digital Tech. Co. Ltd. All Rights Reserved.
 */
package com.canice.wristbandapp.model;

import java.io.Serializable;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * @author fei
 * @version [WristbandApp, 2016-3-14] 
 */
public class SleepRecordDbInfo implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = -3677963799275852059L;
    private String address;
    private int  step;
    private int  cal;
    private int sleep;
    private int battery;
    private int time;
    /**
     * 获取address
     * @return address address
     */
    public String getAddress() {
        return address;
    }
    /**
     * 设置address
     * @param address address
     */
    public void setAddress(String address) {
        this.address = address;
    }
    /**
     * 获取step
     * @return step step
     */
    public int getStep() {
        return step;
    }
    /**
     * 设置step
     * @param step step
     */
    public void setStep(int step) {
        this.step = step;
    }
    /**
     * 获取cal
     * @return cal cal
     */
    public int getCal() {
        return cal;
    }
    /**
     * 设置cal
     * @param cal cal
     */
    public void setCal(int cal) {
        this.cal = cal;
    }
    /**
     * 获取sleep
     * @return sleep sleep
     */
    public int getSleep() {
        return sleep;
    }
    /**
     * 设置sleep
     * @param sleep sleep
     */
    public void setSleep(int sleep) {
        this.sleep = sleep;
    }
    /**
     * 获取battery
     * @return battery battery
     */
    public int getBattery() {
        return battery;
    }
    /**
     * 设置battery
     * @param battery battery
     */
    public void setBattery(int battery) {
        this.battery = battery;
    }
    /**
     * 获取time
     * @return time time
     */
    public int getTime() {
        return time;
    }
    /**
     * 设置time
     * @param time time
     */
    public void setTime(int time) {
        this.time = time;
    }
    
    @Override
    public String toString() {
        return "SleepRecordDbInfo [getStep()=" + getStep() + ", getCal()=" + getCal() + ", getSleep()=" + getSleep()
                + ", getBattery()=" + getBattery() + ", getTime()=" + getTime() + "]"+"\n";
    }

}
