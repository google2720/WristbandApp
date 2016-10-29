package com.canice.wristbandapp.model;

import android.text.TextUtils;

import com.canice.wristbandapp.util.Constants;

/**
 * 用户信息
 *
 * @author canice_yuan
 */
public class UserInfo {

    private String high = "";
    private String password = "";
    private String nickName = "";
    private String sex = "";
    private String mobile = "";
    private String stepLong = "";
    private String weight = "";
    private String id = "";
    private String deviceId = "";
    private String age = "";
    private String token = "";
    private String exDeviceId = "";
    private String inviteCode = "";
    private String goal = "";
    private String recordDate = "";
    private String stepNum = "";

    public String getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(String recordDate) {
        this.recordDate = recordDate;
    }

    public String getStepNum() {
        return stepNum;
    }

    public void setStepNum(String stepNum) {
        this.stepNum = stepNum;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public String getHigh2() {
        if (TextUtils.isEmpty(high)) {
            return "100";
        } else {
            return high.replaceAll("[^0-9]", "");
        }
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getSex() {
        return sex;
    }

    public String getSex2() {
        if (!TextUtils.isEmpty(sex) && "M".equalsIgnoreCase(sex)) {
            return Constants.USER_SEX_MAN;
        } else {
            return sex;
        }
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getStepLong2() {
        if (TextUtils.isEmpty(stepLong)) {
            return "50";
        } else {
            return stepLong.replaceAll("[^0-9]", "");
        }
    }

    public void setStepLong(String stepLong) {
        this.stepLong = stepLong;
    }

    public String getWeight2() {
        if (TextUtils.isEmpty(weight)) {
            return "50";
        } else {
            return weight.replaceAll("[^0-9]", "");
        }
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getAge2() {
        try {
            int a = Integer.parseInt(age);
            return String.valueOf(a);
        } catch (Exception e) {
            return "18";
        }
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEx_deviceId() {
        return exDeviceId;
    }

    public void setEx_deviceId(String ex_deviceId) {
        this.exDeviceId = ex_deviceId;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

}
