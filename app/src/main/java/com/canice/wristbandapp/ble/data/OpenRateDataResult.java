package com.canice.wristbandapp.ble.data;


import com.canice.wristbandapp.ble.BleController;
import com.github.yzeaho.log.Lg;

public class OpenRateDataResult extends Data {

    private byte[] data;

    private OpenRateDataResult(byte[] data) {
        this.data = data;
    }

    public static OpenRateDataResult parser(byte[] aData) {
        if (aData == null) {
            return null;
        }
        OpenRateDataResult r = new OpenRateDataResult(aData);
        Lg.d("OpenRateDataResult-BleData", "read data [" +toHex(aData) + "]");
        if (r.check()) {
            return r;
        }
        return null;
    }

    public static String toHex(byte[] data) {
        if (data == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder(data.length);
        for (byte byteChar : data) {
            builder.append(String.format("%02X ", byteChar));
        }
        builder.delete(builder.length() - 1, builder.length());
        return builder.toString();
    }
    private boolean check() {
        return data.length == 20 && data[0] == (byte) 0xAA && data[1] == (byte)0xE0&& data[2] == (byte)0xEE&&data[18] == (byte)0x03;
    }

    public int getHeartRate() {
        return data[1] & 0xff;
    }
}
