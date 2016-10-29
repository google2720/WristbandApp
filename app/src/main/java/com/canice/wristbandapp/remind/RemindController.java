package com.canice.wristbandapp.remind;

import com.canice.wristbandapp.LocalData;

import android.content.Context;

public class RemindController {

    public static Remind get(Context context, String email) {
        Remind c = LocalData.get(context, generateKey(email), Remind.class);
        return c == null ? new Remind() : c;
    }

    public static void save(Context context, String email, Remind c) {
        LocalData.save(context, generateKey(email), c);
    }

    private static String generateKey(String email) {
        return "remind_" + email.hashCode();
    }
}
