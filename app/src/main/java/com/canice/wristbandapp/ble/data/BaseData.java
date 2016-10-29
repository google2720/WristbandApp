package com.canice.wristbandapp.ble.data;

public class BaseData extends Data {

    public int year;
    public int month;
    public int day;
    public int hour;
    public int minute;
    public int height;
    public int weight;
    public int step;
    public int goal;
    public byte clock;
    public int clockTimeHour;
    public int clockTimeMinute;
    public int clockIntervalTime;
    public byte remind;
    public int remindStartTimeHour;
    public int remindEndTimeHour;
    public int remindIntervalTime;

    public byte[] toValue(boolean clear) {
        byte[] value = new byte[20];
        value[0] = (byte) 0xAA;
        value[1] = clear ? (byte) 0x01 : (byte) 0x00;
        value[2] = (byte) year;
        value[3] = (byte) month;
        value[4] = (byte) day;
        value[5] = (byte) hour;
        value[6] = (byte) minute;
        value[7] = (byte) height;
        value[8] = (byte) weight;
        value[9] = (byte) step;
        value[10] = clock;
        value[11] = (byte) clockTimeHour;
        value[12] = (byte) clockTimeMinute;
        value[13] = (byte) clockIntervalTime;
        value[14] = remind;
        value[15] = (byte) remindStartTimeHour;
        value[16] = (byte) remindEndTimeHour;
        value[17] = (byte) remindIntervalTime;
        value[18] = (byte) goal;
        value[19] = sum(value);
        return value;
    }
}
