package com.canice.wristbandapp.ble.data;

import android.text.TextUtils;
import android.util.Log;

public class Data {

    public byte sum(byte[] value) {
        int total = 0;
        int size = value.length - 1;
        for (int i = 0; i < size; i++) {
            total += value[i];
        }
        return (byte) (total % 256);
    }

    public int change2int(String v) {
        if (TextUtils.isEmpty(v)) {
            return 0;
        }
        String t = v;
        int index = v.indexOf(".");
        if (index != -1) {
            t = v.substring(0, index);
        }
        try {
            return Integer.parseInt(t);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public int change2goal(double f) {
        int a = (int) Math.round(f * 10);
        Log.i("yy", "change2goal " + f + " " + a);
        return a;
    }
}
