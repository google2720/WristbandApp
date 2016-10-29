package com.canice.wristbandapp.ble.data;

public class History extends Data {

    private int h;

    public History(int h) {
        this.h = h;
    }

    public byte[] toValue() {
        byte[] data = new byte[20];
        data[0] = (byte) 0xAA;
        data[1] = (byte) 0xA1;
        data[2] = (byte) h;
        data[19] = sum(data);
        return data;
    }
}
