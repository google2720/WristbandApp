package com.canice.wristbandapp.ble.data;

public class RemindResult extends Data {

    public byte[] data;

    private RemindResult(byte[] data) {
        this.data = data;
    }

    public static RemindResult parser(byte[] data) {
        if (data == null) {
            return null;
        }
        RemindResult r = new RemindResult(data);
        if (r.check()) {
            return r;
        }
        return null;
    }

    private boolean check() {
        return data.length == 20 && data[0] == (byte) 0xAA && data[18] != 0x03;
    }


    public boolean isSuccess() {
        return data[0] == (byte) 0xAA && data[1] == (byte) 0xE1 && data[2] == (byte) 0xEE;
    }
}
