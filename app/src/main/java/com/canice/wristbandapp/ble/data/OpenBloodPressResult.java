package com.canice.wristbandapp.ble.data;


public class OpenBloodPressResult extends Data {

    private byte[] data;

    private OpenBloodPressResult(byte[] data) {
        this.data = data;
    }

    public static OpenBloodPressResult parser(byte[] data) {
        if (data == null) {
            return null;
        }
        OpenBloodPressResult r = new OpenBloodPressResult(data);
        if (r.check()) {
            return r;
        }
        return null;
    }

    private boolean check() {
        return data.length == 20 && data[0] == (byte) 0xAA && data[1] == (byte) 0x00 && data[2] == (byte) 0x00 && data[18] == (byte) 0x03;
    }
}
