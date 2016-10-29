package com.canice.wristbandapp;

import com.canice.wristbandapp.util.Tools;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class DateTest {

    @Test
    public void testParseInt() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2000);
        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        System.out.println(calendar.getTimeInMillis());
        System.out.println(calendar.getTime());
    }

    @Test
    public void testDate() throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        Date date = dateFormat.parse("1970-01-01 00:00:00:000");
        System.out.println(date);
        System.out.println(date.getTime());
        Date date2 = dateFormat.parse("2000-01-01 00:00:00:000");
        System.out.println(date2);
        System.out.println(date2.getTime());

        System.out.println(date2.getTime() - date.getTime());
    }

    @Test
    public void testDeviceTime() throws Exception {
        long deviceTime = getTime((byte) 0x28, (byte) 0xD3, (byte) 0x5D, (byte) 0x1F);
        System.out.println(deviceTime);
        System.out.println(Tools.changeDeviceTime2PhoneTime(deviceTime));

        deviceTime = getTime((byte) 0xB8, (byte) 0x70, (byte) 0x5D, (byte) 0x1F);
        System.out.println(deviceTime);
        System.out.println(Tools.changeDeviceTime2PhoneTime(deviceTime));
    }

    public long getTime(byte b1, byte b2, byte b3, byte b4) {
        return ((b4 & 0xff) << 24) + ((b3 & 0xff) << 16) + ((b2 & 0xff) << 8) + (b1 & 0xff);
    }
}