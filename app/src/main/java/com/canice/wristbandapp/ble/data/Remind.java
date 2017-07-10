package com.canice.wristbandapp.ble.data;

public class Remind extends Data {

    public byte[] toValue(byte[] phone) {
        byte[] value = new byte[20];
        value[0] = (byte) 0xAA;
        value[1] = (byte) 0xE1;
        value[2] = Byte.parseByte("00001000", 2);
        value[3] = Byte.parseByte("00001000", 2);
        value[4] = 0x01;
        System.arraycopy(phone, 0, value, 5, Math.min(phone.length, 14));
        value[19] = sum(value);
        return value;
    }
}
