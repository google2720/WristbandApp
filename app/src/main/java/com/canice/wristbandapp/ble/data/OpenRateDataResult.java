package com.canice.wristbandapp.ble.data;


import com.canice.wristbandapp.ble.BleController;
import com.github.yzeaho.log.Lg;

public class OpenRateDataResult extends Data {

    private byte[] data;

    private OpenRateDataResult(byte[] data) {
        this.data = data;
    }

    public static OpenRateDataResult parser(byte[] data) {
        if (data == null) {
            return null;
        }
        OpenRateDataResult r = new OpenRateDataResult(data);
        if (r.check()) {
            return r;
        }
        return null;
    }

    private boolean check() {
        return data.length == 20 && data[0] == (byte) 0xAA && data[1] == (byte) 0xE0 && data[2] == (byte) 0xEE && data[18] == (byte) 0x03;
    }
}
