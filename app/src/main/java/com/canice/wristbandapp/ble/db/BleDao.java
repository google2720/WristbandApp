package com.canice.wristbandapp.ble.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.canice.wristbandapp.BuildConfig;
import com.canice.wristbandapp.ble.data.HistoryResult;
import com.canice.wristbandapp.ble.data.PedometerDataResult;
import com.canice.wristbandapp.ble.data.VersionInfoResult;
import com.canice.wristbandapp.model.CurrentSportRecordInfo;
import com.canice.wristbandapp.model.SleepInfo;
import com.canice.wristbandapp.model.SleepRecordDbInfo;
import com.canice.wristbandapp.util.Tools;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BleDao {

    public static void saveVersionInfo(Context context, String address, VersionInfoResult result) {
        ContentValues values = new ContentValues();
        values.put("_address", address);
        values.put("_main_version", result.getMainVersion());
        values.put("_minor_version", result.getMinorVersion());
        SQLiteDatabase db = DBHelper.getInstance(context).getWritableDatabase();
        db.replace(DBHelper.TABLE_DEVICE_INFO, null, values);
    }

    public static void saveHistory(Context context, String address, HistoryResult result) {
        SQLiteDatabase db = DBHelper.getInstance(context).getWritableDatabase();
        try {
            db.beginTransaction();
            ContentValues values = new ContentValues();
            values.put("_address", address);
            values.put("_step", result.getStep());
            values.put("_cal", result.getCal());
            values.put("_sleep", result.getSleep());
            values.put("_battery", result.getBattery());
            values.put("_time", result.getTime());
            db.replace(DBHelper.TABLE_HISTORY, null, values);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public static long getMaxHistotyTime(Context context, String address) {
        Cursor cursor = null;
        try {
            String[] columns = new String[]{"max(_time)"};
            SQLiteDatabase db = DBHelper.getInstance(context).getReadableDatabase();
            cursor = db.query(DBHelper.TABLE_HISTORY, columns, null, null, null, null, null);
            if (cursor != null && cursor.moveToNext()) {
                return cursor.getLong(0);
            }
        } finally {
            close(cursor);
        }
        return 0;
    }

    public static List<SleepRecordDbInfo> getHistory(Context context, String address) {
        List<SleepRecordDbInfo> list = new ArrayList<SleepRecordDbInfo>();
        Cursor cursor = null;
        String selection = "_address=?";
        String[] selectionArgs = new String[]{address};
        try {
            SQLiteDatabase db = DBHelper.getInstance(context).getReadableDatabase();
            cursor = db.query(DBHelper.TABLE_HISTORY, null, selection, selectionArgs, null, null, null);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
            while (cursor != null && cursor.moveToNext()) {
                SleepRecordDbInfo info = new SleepRecordDbInfo();
                info.setAddress(cursor.getString(cursor.getColumnIndexOrThrow("_address")));
                info.setBattery(cursor.getInt(cursor.getColumnIndex("_battery")));
                info.setCal(cursor.getInt(cursor.getColumnIndex("_cal")));
                info.setSleep(cursor.getInt(cursor.getColumnIndex("_sleep")));
                info.setTime(cursor.getInt(cursor.getColumnIndex("_time")));
                info.setStep(cursor.getInt(cursor.getColumnIndex("_step")));
                list.add(info);
                Log.i("info", info.toString());
            }
        } finally {
            close(cursor);
        }
        return list;
    }

    public static SleepInfo getSleepTime(Context context, String address) {
        SleepInfo sleepInfo = new SleepInfo();
        if (address == null) {
            return sleepInfo;
        }
        int sleep;
        Date date;
        if (BuildConfig.newFit) {
            date = Tools.getZoreTime();
        } else {
            date = Tools.getPreDay();
        }
        long time = Tools.getDeviceTime(date);
        Cursor cursor = null;
        String selection = "_address=? AND _time>=?";
        String[] selectionArgs = new String[]{address, String.valueOf(time)};
        try {
            SQLiteDatabase db = DBHelper.getInstance(context).getReadableDatabase();
            cursor = db.query(DBHelper.TABLE_HISTORY, null, selection, selectionArgs, null, null, null);
            while (cursor != null && cursor.moveToNext()) {
                sleep = cursor.getInt(cursor.getColumnIndex("_sleep"));
                if (sleep != -1 && sleep != 255) {
                    if (sleep < 10) {
                        sleepInfo.ssmTime += 1;
                    } else {
                        sleepInfo.qsmTime += 1;
                    }
                }
            }
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
            sleepInfo.sleepDate = df.format(date);
        } finally {
            close(cursor);
        }
        return sleepInfo;
    }

    public static void saveCurrentData(Context context, String address, PedometerDataResult result) {
        ContentValues values = new ContentValues();
        values.put("_address", address);
        values.put("_step", result.getStep());
        values.put("_cal", result.getCal());
        values.put("_distance", result.getDistance());
        values.put("_battery", result.getBattery());
        values.put("_gol", result.getGol());
        values.put("_date", result.getDate());
        SQLiteDatabase db = DBHelper.getInstance(context).getWritableDatabase();
        db.replace(DBHelper.TABLE_CURRENT_DATA, null, values);
    }

    public static CurrentSportRecordInfo getCurrentData(Context context, String address) {
        Cursor cursor = null;
        String selection = "_address=?";
        String[] selectionArgs = new String[]{address};
        CurrentSportRecordInfo info = new CurrentSportRecordInfo();
        if (address == null) {
            return info;
        }
        try {
            SQLiteDatabase db = DBHelper.getInstance(context).getReadableDatabase();
            cursor = db.query(DBHelper.TABLE_CURRENT_DATA, null, selection, selectionArgs, null, null, null);
            if (cursor != null && cursor.moveToNext()) {
                info.address = cursor.getString(cursor.getColumnIndex("_address"));
                info.stepNum = cursor.getInt(cursor.getColumnIndex("_step"));
                info.cal = cursor.getInt(cursor.getColumnIndex("_cal"));
                info.distance = cursor.getInt(cursor.getColumnIndex("_distance"));
                info.battery = cursor.getInt(cursor.getColumnIndex("_battery"));
                info.goal = cursor.getInt(cursor.getColumnIndex("_gol"));
                info.date = cursor.getString(cursor.getColumnIndex("_date"));
            }
        } finally {
            close(cursor);
        }
        return info;
    }

    public static void close(Cursor c) {
        if (c != null) {
            c.close();
        }
    }

    public static class StepData {
        public String recordDate;
        public long stepNum;
    }

    public static List<StepData> getHistoryStep(Context context, String address) {
        List<StepData> datas = new ArrayList<BleDao.StepData>();
        Map<String, StepData> maps = new HashMap<String, StepData>();
        Cursor cursor = null;
        try {
            String key = null;
            StepData data = null;
            int time = 0;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
            String today = sdf.format(new Date());
            String selection = "_address=?";
            String[] selectionArgs = new String[]{address};
            SQLiteDatabase db = DBHelper.getInstance(context).getReadableDatabase();
            cursor = db.query(DBHelper.TABLE_HISTORY, null, selection, selectionArgs, null, null, null);
            while (cursor != null && cursor.moveToNext()) {
                time = cursor.getInt(cursor.getColumnIndex("_time"));
                key = sdf.format(Tools.changeDeviceTime2PhoneTime(time));
//                if (key.equals(today)) {
//                    continue;
//                }
                data = maps.get(key);
                if (data == null) {
                    data = new StepData();
                    data.recordDate = key;
                    maps.put(key, data);
                    datas.add(data);
                }
                data.stepNum += cursor.getInt(cursor.getColumnIndex("_step"));
            }
        } finally {
            close(cursor);
        }
        return datas;
    }

    public static List<StepData> getHistoryStep1(Context context, String address) {
        List<StepData> datas = new ArrayList<BleDao.StepData>();
        Map<String, StepData> maps = new HashMap<String, StepData>();
        Cursor cursor = null;
        try {
            String key = null;
            StepData data = null;
            int time = 0;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
            String today = sdf.format(new Date());
            String selection = "_address=?";
            String[] selectionArgs = new String[]{address};
            SQLiteDatabase db = DBHelper.getInstance(context).getReadableDatabase();
            cursor = db.query(DBHelper.TABLE_HISTORY, null, selection, selectionArgs, null, null, null);
            while (cursor != null && cursor.moveToNext()) {
                time = cursor.getInt(cursor.getColumnIndex("_time"));
                key = String.valueOf(time);
                data = maps.get(key);
                if (data == null) {
                    data = new StepData();
                    maps.put(key, data);
                    datas.add(data);
                }
                data.recordDate = sdf.format(Tools.changeDeviceTime2PhoneTime(time));
                ;
                data.stepNum += cursor.getInt(cursor.getColumnIndex("_step"));
            }
        } finally {
            close(cursor);
        }
        return datas;
    }

    public static class SleepData {
        public String sleepDate;
        public long qsmTime;
        public long ssmTime;
    }

    public static class SleepData1 {
        public String sleepDate;
        public long qsmTime;
        public long ssmTime;
        public int actionNumber;
    }

    public static List<SleepData> getHistorySleep(Context context, String address) {
        List<SleepData> datas = new ArrayList<SleepData>();
        Map<String, SleepData> maps = new HashMap<String, SleepData>();
        Cursor cursor = null;
        try {
            String key = null;
            SleepData data = null;
            int time = 0;
            int stime = 0;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
            String today = sdf.format(new Date());
            String selection = "_address=? AND _sleep!=255";
            String[] selectionArgs = new String[]{address};
            SQLiteDatabase db = DBHelper.getInstance(context).getReadableDatabase();
            cursor = db.query(DBHelper.TABLE_HISTORY, null, selection, selectionArgs, null, null, null);
            while (cursor != null && cursor.moveToNext()) {
                time = cursor.getInt(cursor.getColumnIndex("_time"));
                key = sdf.format(Tools.changeDeviceTime2PhoneTime(time));
//                if (key.equals(today)) {
//                    continue;
//                }
                stime = cursor.getInt(cursor.getColumnIndex("_sleep"));
                data = maps.get(key);
                if (data == null) {
                    data = new SleepData();
                    data.sleepDate = key;
                    maps.put(key, data);
                    datas.add(data);
                }
                if (stime < 10) {
                    //data.ssmTime += 60;
                    data.ssmTime += 1;
                } else {
                    // data.qsmTime += 60;
                    data.qsmTime += 1;
                }
            }
        } finally {
            close(cursor);
        }
        return datas;
    }

    public static List<SleepData1> getHistorySleep1(Context context, String address) {
        List<SleepData1> datas = new ArrayList<SleepData1>();
        Map<Long, SleepData1> maps = new HashMap<Long, SleepData1>();
        Cursor cursor = null;
        try {
            Long key = null;
            SleepData1 data = null;
            long time = 0;
            int stime = 0;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
            String today = sdf.format(new Date());
            String selection = "_address=? AND _sleep!=255";
            String[] selectionArgs = new String[]{address};
            SQLiteDatabase db = DBHelper.getInstance(context).getReadableDatabase();
            cursor = db.query(DBHelper.TABLE_HISTORY, null, selection, selectionArgs, null, null, null);
            while (cursor != null && cursor.moveToNext()) {
                time = cursor.getInt(cursor.getColumnIndex("_time"));
                key = time;

                stime = cursor.getInt(cursor.getColumnIndex("_sleep"));
                data = maps.get(key);
                if (data == null) {
                    data = new SleepData1();
                    maps.put(key, data);
                    datas.add(data);
                }
                data.sleepDate = sdf.format(Tools.changeDeviceTime2PhoneTime(time));
                data.actionNumber = stime;
                if (stime < 10) {
                    //data.ssmTime += 60;
                    data.ssmTime += 1;
                } else {
                    //data.qsmTime += 60;
                    data.qsmTime += 1;
                }
            }
        } finally {
            close(cursor);
        }
        return datas;
    }

    public static void clearData(Context context) {
        SQLiteDatabase db = DBHelper.getInstance(context).getWritableDatabase();
        try {
            db.beginTransaction();
            db.delete(DBHelper.TABLE_CURRENT_DATA, null, null);
            db.delete(DBHelper.TABLE_HISTORY, null, null);
            db.delete(DBHelper.TABLE_DEVICE_INFO, null, null);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

}
