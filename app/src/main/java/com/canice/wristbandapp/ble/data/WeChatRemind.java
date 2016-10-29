package com.canice.wristbandapp.ble.data;

public class WeChatRemind extends Data {

    public byte[] toValue() {
        byte[] value = new byte[20];
        value[0] = (byte) 0xAA;
        value[1] = (byte) 0xE1;
        value[2] = Byte.parseByte("00000010", 2);
        value[3] = 0x01;
        value[4] = 0x01;
        value[19] = sum(value);
        return value;
    }
}
