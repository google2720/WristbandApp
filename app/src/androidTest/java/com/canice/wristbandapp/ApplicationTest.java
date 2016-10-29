package com.canice.wristbandapp;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.canice.wristbandapp.ble.BleController;

import org.junit.Test;

import java.util.Date;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {

    public ApplicationTest() {
        super(Application.class);
    }

    public void testAA() throws Exception {
        assertEquals("12321312312", BleController.getInstance().filterPhone("+8612321312312"));
        assertEquals("12321312312", BleController.getInstance().filterPhone("12321312312"));
        assertEquals("12321312312", BleController.getInstance().filterPhone("8612321312312"));
        assertEquals("12321312312", BleController.getInstance().filterPhone("008612321312312"));
        assertEquals("08612321312312", BleController.getInstance().filterPhone("08612321312312"));
    }

    @Test
    public void testTime(){
        long time= 1468521895;
        new Date(time * 1000);
        Log.e("time",new Date(time * 1000).toString());
    }

}