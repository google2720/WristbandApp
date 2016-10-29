package com.canice.wristbandapp.ble.data;

public class VersionInfoResult extends Data {

    private byte[] data;

    private VersionInfoResult(byte[] data) {
        this.data = data;
    }

    public static VersionInfoResult parser(byte[] data) {
        if (data == null) {
            return null;
        }
        VersionInfoResult r = new VersionInfoResult(data);
        if (r.check()) {
            return r;
        }
        return null;
    }

    public String getMainVersion() {
        return (data[1] & 0xff) + "." + (data[2] & 0xff) + "." + (data[3] & 0xff);
    }

    public String getMinorVersion() {
        return (data[4] & 0xff) + "." + (data[5] & 0xff) + "." + (data[6] & 0xff);
    }

    private boolean check() {
        return data != null && data.length == 20 && data[0] != (byte) 0xAA && data[18] != 0x05;
    }
}