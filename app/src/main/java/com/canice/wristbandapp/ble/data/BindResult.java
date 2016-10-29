package com.canice.wristbandapp.ble.data;

public class BindResult extends Data {

    private BindResult(byte[] data) {
    }

    public static BindResult parser(byte[] data) {
        if (data == null || data.length != 20) {
            return null;
        }
//        if (data[0] != (byte) 0xAA || data[1] != 0x0F) {
//            return null;
//        }
        if (data[0] != (byte) 0xAA) {
            return null;
        }
        return new BindResult(data);
    }
}