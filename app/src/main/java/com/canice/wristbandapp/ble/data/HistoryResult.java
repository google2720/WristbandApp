package com.canice.wristbandapp.ble.data;

public class HistoryResult extends Data {

    public byte[] data;

    private HistoryResult(byte[] data) {
        this.data = data;
    }

    public static HistoryResult parser(byte[] data) {
        if (data == null) {
            return null;
        }
        HistoryResult r = new HistoryResult(data);
        if (r.check()) {
            return r;
        }
        return null;
    }

    private boolean check() {
        return data.length == 20 && data[0] == (byte) 0xAA && data[18] == 0x03;
    }

    public int getStep() {
        return ((data[3] & 0xff) << 8) + (data[2] & 0xff);
    }

    public int getCal() {
        return ((data[5] & 0xff) << 8) + (data[4] & 0xff);
    }

    public int getSleep() {
        return data[6] & 0xff;
    }

    public int getBattery() {
        return data[7] & 0xff;
    }

    public long getTime() {
        return ((data[11] & 0xff) << 24) + ((data[10] & 0xff) << 16) + ((data[9] & 0xff) << 8) + (data[8] & 0xff);
    }

    @Override
    public String toString() {
        return "HistoryResult [getStep()=" + getStep() + ", getCal()=" + getCal() + ", getSleep()=" + getSleep()
                + ", getBattery()=" + getBattery() + ", getTime()=" + getTime() + "]";
    }

    public boolean isFinish() {
        return data[0] == (byte) 0xAA && data[1] == (byte) 0xA1 && data[2] == (byte) 0xEE;
    }
}
