package com.canice.wristbandapp.ble.data;

public class BindQueryResult extends Data {

    public byte[] data;

    private BindQueryResult(byte[] data) {
        this.data = data;
    }

    public static BindQueryResult parser(byte[] data) {
        if (data == null) {
            return null;
        }
        BindQueryResult r = new BindQueryResult(data);
        if (r.check()) {
            return r;
        }
        return null;
    }

    private boolean check() {
        if (data.length != 20) {
            return false;
        }
        if (data[0] != (byte) 0xAA || data[1] != 0x0F || data[18] != 0x08) {
            return false;
        }
        return true;
    }

    public String getDeviceId() {
        StringBuilder sb = new StringBuilder();
        for (int i = 6; i < 13; i++) {
            sb.append(data[i]);
        }
        return sb.toString();
    }

    public boolean isBinded() {
        int count = 0;
        for (int i = 6; i <= 12; i++) {
            if (data[i] == 0) {
                count++;
            }
        }
        int count1 = 0;
        for (int i = 6; i <= 12; i++) {
            if (data[i] == (byte) 0xFF ) {
                count1++;
            }
        }
        return !(count == 7||count1==7);
    }
}
