package com.canice.wristbandapp.ble.data;

public class PedometerData extends Data {

    public byte[] toValue() {
        byte[] value = new byte[20];
        value[0] = (byte) 0xAA;
        value[1] = (byte) 0xB1;
        value[19] = sum(value);
        return value;
    }
}
