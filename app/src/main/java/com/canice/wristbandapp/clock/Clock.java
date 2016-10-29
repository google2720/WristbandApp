package com.canice.wristbandapp.clock;

import java.io.Serializable;

public class Clock implements Serializable {
    private static final long serialVersionUID = 2864852659722865123L;
    public boolean enabled;
    public int timeHour = 8;
    public int timeMinute;
    public int intervalTime = 30;
    public boolean mondayEnabled;
    public boolean tuesdayEnabled;
    public boolean wednesdayEnabled;
    public boolean thursdayEnabled;
    public boolean fridayEnabled;
    public boolean saturdayEnabled;
    public boolean sundayEnabled;
}