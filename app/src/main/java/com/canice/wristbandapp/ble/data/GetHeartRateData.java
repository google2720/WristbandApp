package com.canice.wristbandapp.ble.data;

public class GetHeartRateData extends Data {

    public byte[] toValue() {
        byte[] value = new byte[20];
        value[0] = (byte) 0xAA;
        value[1] = (byte) 0xE0;
        value[2] = (byte) 0x30;
        value[19] = sum(value);
        return value;
    }
}
