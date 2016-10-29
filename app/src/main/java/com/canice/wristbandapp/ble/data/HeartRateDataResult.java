package com.canice.wristbandapp.ble.data;


public class HeartRateDataResult extends Data {

    private byte[] data;

    private HeartRateDataResult(byte[] data) {
        this.data = data;
    }

    public static HeartRateDataResult parser(byte[] data) {
        if (data == null) {
            return null;
        }
        HeartRateDataResult r = new HeartRateDataResult(data);
        if (r.check()) {
            return r;
        }
        return null;
    }

    private boolean check() {
        return data.length == 20 && data[0] == (byte) 0xAA && data[18] == 0x0F;
    }

    public int getHeartRate() {
        return data[1] & 0xff;
    }
}
