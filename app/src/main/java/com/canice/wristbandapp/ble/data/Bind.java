package com.canice.wristbandapp.ble.data;

public class Bind extends Data {

    public byte[] toValue(byte[] data, byte[] ids) {
        byte[] value = new byte[20];
        value[0] = (byte) 0xAA;
        value[1] = (byte) 0xF2;
        value[2] = data[2];
        value[3] = data[3];
        value[4] = data[4];
        value[5] = data[5];
        value[6] = ids[0];
        value[7] = ids[1];
        value[8] = ids[2];
        value[9] = ids[3];
        value[10] = ids[4];
        value[11] = ids[5];
        value[12] = ids[6];
        value[19] = sum(value);
        return value;
    }
}
