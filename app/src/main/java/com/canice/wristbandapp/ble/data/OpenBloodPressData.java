package com.canice.wristbandapp.ble.data;

public class OpenBloodPressData extends Data {

    public byte[] toValue() {
        byte[] value = new byte[20];
        value[0] = (byte) 0xAA;
        value[1] = (byte) 0xE0;
        value[2] = (byte) 0x00;
        value[3] = (byte) 0x00;
        value[4] = (byte) 0x01;
        value[19] = sum(value);
        return value;
    }
}
