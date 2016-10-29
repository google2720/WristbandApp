package com.canice.wristbandapp.clock;

import com.canice.wristbandapp.LocalData;

import android.content.Context;

public class ClockController {

    public static final Clock get(Context context, String email) {
        Clock c = LocalData.get(context, generateKey(email), Clock.class);
        return c == null ? new Clock() : c;
    }

    public static final void save(Context context, String email, Clock c) {
        LocalData.save(context, generateKey(email), c);
    }

    private static String generateKey(String email) {
        return "clock_" + email.hashCode();
    }
}
