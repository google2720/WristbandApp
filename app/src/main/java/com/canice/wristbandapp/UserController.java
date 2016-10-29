package com.canice.wristbandapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.canice.wristbandapp.activity.personaldata.ProfileData;
import com.canice.wristbandapp.ble.BleController;
import com.canice.wristbandapp.model.UserInfo;
import com.canice.wristbandapp.util.Constants;

public class UserController {

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(Constants.SHARED_NAME, Context.MODE_PRIVATE);
    }

    public static void saveLoginState(Context context, String email, String pw, boolean login) {
        Editor editor = getSharedPreferences(context).edit();
        editor.putString(Constants.EMAIL, email);
        editor.putString(Constants.PASSWORD, pw);
        editor.putBoolean(Constants.LOGIN_STATE, login);
        editor.apply();
    }

    public static void saveAllUserInfo(Context context, UserInfo info) {
        Editor editor = getSharedPreferences(context).edit();
        editor.putString(Constants.NICKNAME, info.getNickName());
        editor.putString(Constants.SEX, info.getSex2());
        editor.putString(Constants.AGE, info.getAge2());
        editor.putString(Constants.HIGH, info.getHigh2());
        editor.putString(Constants.WEIGHT, info.getWeight2());
        editor.putString(Constants.STEPLONG, info.getStepLong2());
        editor.putString(Constants.ID, info.getId());
        editor.putString(Constants.DEVICEID, info.getDeviceId());
        editor.putString(Constants.TOKEN, info.getToken());
        editor.putString(Constants.EXDEVICEID, info.getEx_deviceId());
        editor.putString(Constants.INVITE_CODE, info.getInviteCode());
        editor.apply();
    }

    public static void saveUserInfo(Context c, String n, String s, String a, String h, String w, String step, int g) {
        SharedPreferences p = getSharedPreferences(c);
        Editor editor = p.edit();
        editor.putString(Constants.NICKNAME, n);
        editor.putString(Constants.SEX, s);
        editor.putString(Constants.AGE, a);
        editor.putString(Constants.HIGH, h);
        editor.putString(Constants.WEIGHT, w);
        editor.putString(Constants.STEPLONG, step);
        editor.putInt(Constants.GOAL, g);
        editor.apply();
    }

    public static void saveUserInfo(Context c, String n, String s, String a, String h, String w, String step) {
        SharedPreferences p = getSharedPreferences(c);
        Editor editor = p.edit();
        editor.putString(Constants.NICKNAME, n);
        editor.putString(Constants.SEX, s);
        editor.putString(Constants.AGE, a);
        editor.putString(Constants.HIGH, h);
        editor.putString(Constants.WEIGHT, w);
        editor.putString(Constants.STEPLONG, step);
        editor.apply();
    }

    public static void saveUserInfo(Context c, ProfileData d) {
        saveUserInfo(c, d.getNickname(), d.getSexString(c), d.getAge(), d.getHigh(), d.getWeight(), d.getStepLong(),
                d.getGoal());
    }

    public static int getGoal(Context context) {
        return getSharedPreferences(context).getInt(Constants.GOAL, 10000);
    }

    public static void saveGoal(Context context, int goal) {
        SharedPreferences p = getSharedPreferences(context);
        Editor editor = p.edit();
        editor.putInt(Constants.GOAL, goal);
        editor.apply();
    }

    public static String getUserId(Context context) {
        return getSharedPreferences(context).getString(Constants.ID, null);
    }

    public static String getSex(Context context) {
        return getSharedPreferences(context).getString(Constants.SEX, Constants.USER_SEX_MAN);
    }

    public static boolean isMan(Context context) {
        return !Constants.USER_SEX_WOMAN.equals(getSex(context));
    }

    public static String getNickname(Context context) {
        return getSharedPreferences(context).getString(Constants.NICKNAME, null);
    }

    public static String getAge(Context context) {
        return getSharedPreferences(context).getString(Constants.AGE, "18");
    }

    public static String getHeight(Context context) {
        return getSharedPreferences(context).getString(Constants.HIGH, "0");
    }

    public static String getWeight(Context context) {
        return getSharedPreferences(context).getString(Constants.WEIGHT, "0");
    }

    public static String getStepLong(Context context) {
        return getSharedPreferences(context).getString(Constants.STEPLONG, "0");
    }

    public static String getInviteCode(Context context) {
        return getSharedPreferences(context).getString(Constants.INVITE_CODE, null);
    }

    public static String getExDeviceId(Context context) {
        return getSharedPreferences(context).getString(Constants.EXDEVICEID, null);
    }

    public static String getEmail(Context context) {
        return getSharedPreferences(context).getString(Constants.EMAIL, null);
    }

    public static String getPassword(Context context) {
        return getSharedPreferences(context).getString(Constants.PASSWORD, null);
    }

    public static void saveCallRemind(Context context, boolean remind) {
        Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(Constants.CALL_REMIND, remind);
        editor.apply();
    }

    public static boolean isCallRemind(Context context) {
        return getSharedPreferences(context).getBoolean(Constants.CALL_REMIND, false);
    }

    public static void saveSmsRemind(Context context, boolean remind) {
        Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(Constants.SMS_REMIND, remind);
        editor.apply();
    }

    public static boolean isSmsRemind(Context context) {
        return getSharedPreferences(context).getBoolean(Constants.SMS_REMIND, false);
    }

    public static void saveWeChatRemind(Context context, boolean remind) {
        Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(Constants.WE_CHAT_REMIND, remind);
        editor.apply();
    }

    public static boolean isWeChatRemind(Context context) {
        return getSharedPreferences(context).getBoolean(Constants.WE_CHAT_REMIND, false);
    }

    public static void saveQQRemind(Context context, boolean remind) {
        Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(Constants.QQ_REMIND, remind);
        editor.apply();
    }

    public static boolean isQQRemind(Context context) {
        return getSharedPreferences(context).getBoolean(Constants.QQ_REMIND, false);
    }

    public static void logout(Context context) {
        Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(Constants.LOGIN_STATE, false);
        editor.apply();
        BleController.getInstance().saveBindedDevice(null, null);
        BleController.getInstance().disconnect();
    }

    /**
     * 激活sos求救功能
     *
     * @param phone 紧急联系人
     */
    public static void setSosEnable(Context context, boolean enable, String phone) {
        Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(Constants.SOS, enable);
        editor.putString(Constants.SOS_PHONE, phone);
        editor.apply();
    }

    public static String getSosPhone(Context context) {
        return getSharedPreferences(context).getString(Constants.SOS_PHONE, "");
    }

    public static boolean isSosEnable(Context context) {
        return getSharedPreferences(context).getBoolean(Constants.SOS, false);
    }

    public static void setSosBySms(Context context, boolean isSms) {
        Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(Constants.SOS_SMS, isSms);
        editor.apply();
    }

    public static boolean isSosBySms(Context context) {
        return getSharedPreferences(context).getBoolean(Constants.SOS_SMS, false);
    }

    /**
     * 激活防丢失功能
     */
    public static void setAntiLostEnable(Context context, boolean enable) {
        Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(Constants.ANTI_LOST, enable);
        editor.apply();
    }

    /**
     * 保存防丢失距离
     */
    public static void setAntiLostValue(Context context, int value) {
        Editor editor = getSharedPreferences(context).edit();
        editor.putInt(Constants.ANTI_LOST_VALUE, value);
        editor.apply();
    }

    public static int getAntiLostValue(Context context) {
        return getSharedPreferences(context).getInt(Constants.ANTI_LOST_VALUE, -90);
    }

    public static boolean isAntiLostEnable(Context context) {
        return getSharedPreferences(context).getBoolean(Constants.ANTI_LOST, false);
    }

    public static int getLanguage(Context context) {
        return getSharedPreferences(context).getInt(Constants.LANGUAGE, 0);
    }

    public static void setLanguage(Context context, int value) {
        Editor editor = getSharedPreferences(context).edit();
        editor.putInt(Constants.LANGUAGE, value);
        editor.apply();
    }
}
