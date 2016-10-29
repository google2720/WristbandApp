package com.canice.wristbandapp.remind;

import java.io.Serializable;

public class Remind implements Serializable {
    private static final long serialVersionUID = 5895946808607633727L;
    public boolean enabled;
    public int startTimeHour = 7;
    public int startTimeMinute;
    public int endTimeHour = 9;
    public int endTimeMinute;
    public int intervalTime = 15;
    public boolean mondayEnabled;
    public boolean tuesdayEnabled;
    public boolean wednesdayEnabled;
    public boolean thursdayEnabled;
    public boolean fridayEnabled;
    public boolean saturdayEnabled;
    public boolean sundayEnabled;
}