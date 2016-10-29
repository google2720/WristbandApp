package com.canice.wristbandapp.ble.data;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PedometerDataResult extends Data {

    private byte[] data;

    private PedometerDataResult(byte[] data) {
        this.data = data;
    }

    public static PedometerDataResult parser(byte[] data) {
        if (data == null) {
            return null;
        }
        PedometerDataResult r = new PedometerDataResult(data);
        if (r.check()) {
            return r;
        }
        return null;
    }

    private boolean check() {
        return data.length == 20 && data[0] == (byte) 0xAA && data[18] == 0x04;
    }

    public String getDate() {
        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        return dateFormater.format(new Date());
    }

    public int getStep() {
        return ((data[2] & 0xff) << 8) + (data[1] & 0xff);
    }

    public int getDistance() {
        return ((data[4] & 0xff) << 8) + (data[3] & 0xff);
    }

    public int getCal() {
        return ((data[6] & 0xff) << 8) + (data[5] & 0xff);
    }

    public int getGol() {
        return ((data[8] & 0xff) << 8) + (data[7] & 0xff);
    }

    public int getBattery() {
        return data[9] & 0xff;
    }

    @Override
    public String toString() {
        return "PedometerDataResult [getStep()=" + getStep() + ", getDistance()=" + getDistance() + ", getCal()="
                + getCal() + ", getGol()=" + getGol() + ", getBattery()=" + getBattery() + "]";
    }
}
