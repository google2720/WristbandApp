package com.canice.wristbandapp.ble.data;

/**
 * 防止丢失
 * Created by y on 2016/6/28.
 */
public class AntiLost extends Data {

    public byte[] toValue(boolean enable) {
        byte[] value = new byte[20];
        value[0] = (byte) 0xAA;
        value[1] = enable ? (byte) 0x02 : (byte) 0x03;
        value[19] = sum(value);
        return value;
    }
}
