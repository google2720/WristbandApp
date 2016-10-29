package com.canice.wristbandapp.ble.data;

/**
 * 同步系统时间
 * Created by y on 2016/6/28.
 */
public class SystemDate extends Data {

    public int year;
    public int month;
    public int day;
    public int hour;
    public int minute;
    public int second;

    public byte[] toValue(long time) {
        byte[] value = new byte[20];
        value[0] = (byte) 0xAA;
        value[1] = (byte) 0xDA;
        value[2] = (byte) (time & 0xff);
        value[3] = (byte) (time >> 8 & 0xff);
        value[4] = (byte) (time >> 16 & 0xff);
        value[5] = (byte) (time >> 24 & 0xff);
        value[6] = (byte) year;
        value[7] = (byte) month;
        value[8] = (byte) day;
        value[9] = (byte) hour;
        value[10] = (byte) minute;
        value[11] = (byte) second;
        value[19] = sum(value);
        return value;
    }
}
