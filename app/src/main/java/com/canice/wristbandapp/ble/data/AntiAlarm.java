package com.canice.wristbandapp.ble.data;

/**
 * 报警
 * Created by y on 2016/6/29.
 */
public class AntiAlarm extends Data {

    public byte[] toValue() {
        byte[] value = new byte[20];
        value[0] = (byte) 0xAA;
        value[1] = (byte) 0x02;
        value[2] = (byte) 0x04;
        value[19] = sum(value);
        return value;
    }
}
