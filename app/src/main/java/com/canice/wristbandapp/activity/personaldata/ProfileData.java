package com.canice.wristbandapp.activity.personaldata;

import android.content.Context;

import com.canice.wristbandapp.R;
import com.canice.wristbandapp.util.Constants;

public class ProfileData {

    private static final ProfileData sInstance = new ProfileData();

    public static final int SEX_MALE = 0;
    public static final int SEX_FEMALE = 1;
    private int sex;
    private String age;
    private String high;
    private String weight;
    private String steplong;
    private int goal = 10000;
    private String nickname;

    public static ProfileData getInstance() {
        return sInstance;
    }

    public String getSexString(Context context) {
        return sex == SEX_FEMALE ? Constants.USER_SEX_WOMAN : Constants.USER_SEX_MAN;
    }

    public int getSexText(Context context) {
        return sex == SEX_MALE ? R.string.male : R.string.female;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getSex() {
        return sex;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getAge() {
        return age;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public String getHigh() {
        return high;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getWeight() {
        return weight;
    }

    public void setStepLong(String steplong) {
        this.steplong = steplong;
    }

    public String getStepLong() {
        return steplong;
    }

    public void setGoal(int goal) {
        this.goal = goal;
    }

    public int getGoal() {
        return goal;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }
}