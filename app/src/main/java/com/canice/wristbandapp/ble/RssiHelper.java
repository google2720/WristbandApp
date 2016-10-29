package com.canice.wristbandapp.ble;

/**
 * rssi过滤
 * Created by y on 2016/6/29.
 */
public class RssiHelper {

    public static final int NO_RSSI = Integer.MAX_VALUE;
    private int total;
    private int count;

    public int add(int rssi) {
        if (rssi >= 0) {
            return NO_RSSI;
        }
        total += Math.abs(rssi);
        count++;
        if (count >= 5) {
            int r = -(total / 5);
            clear();
            return r;
        } else {
            return NO_RSSI;
        }
    }

    public void clear() {
        total = 0;
        count = 0;
    }
}
