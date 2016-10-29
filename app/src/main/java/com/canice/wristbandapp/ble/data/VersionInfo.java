package com.canice.wristbandapp.ble.data;

public class VersionInfo extends Data {
    
    public byte[] toValue() {
        byte[] value = new byte[20];
        value[0] = (byte) 0xAA;
        value[1] = (byte) 0xB2;
        value[19] = sum(value);
        return value;
    }
}
