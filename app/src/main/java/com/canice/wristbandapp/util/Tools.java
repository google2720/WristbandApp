package com.canice.wristbandapp.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Patterns;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 工具类
 *
 * @author canice_yuan
 */
public class Tools {

    public static boolean isEmpty(String str) {
        if (str == null || str.equals("")) {
            return true;
        }
        return false;
    }

    /**
     * 验证手机格式
     */
    public static boolean isMobileNO(String mobiles) {
        /*
         * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
         * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
         * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
         */
        String telRegex = "[1][358]\\d{9}";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobiles))
            return false;
        else
            return mobiles.matches(telRegex);
    }

    /**
     * 验证邮箱格式
     */
    public static boolean isValidEmail(String mail) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        Matcher mc = pattern.matcher(mail);
        return mc.matches();
    }

    /**
     * 密码长度校验
     */
    public static boolean isQualifiedPwd(String pwd) {
        if (TextUtils.isEmpty(pwd) || pwd.length() < 6) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 验证码长度校验
     */
    public static boolean isQualifiedIdentifyingCode(String identifyingCode) {
        if (TextUtils.isEmpty(identifyingCode) || identifyingCode.length() == 4) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 重复密码确认校验
     */
    public static boolean isQualifieldPwdConfirm(String pwd, String pwdConfirm) {
        if (TextUtils.isEmpty(pwdConfirm) || !pwd.equals(pwdConfirm)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 检查指定应用是否安装
     *
     * @param packageName
     */
    public boolean checkInstallation(Context context, String packageName) {
        try {
            context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }

    }

    public static boolean checkInternet(Context context) {
        ConnectivityManager con = (ConnectivityManager) context.getSystemService(Activity.CONNECTIVITY_SERVICE);
        boolean wifi = con.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
        boolean internet = con.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
        if (wifi || internet) {
            return true;
        }
        return false;
    }

    /**
     * 调用图片剪辑程序
     */
    public static Intent getCropImageIntent(Uri uri) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        intent.setType("image/*");
        setIntentParams(intent, uri);
        return intent;
    }

    /**
     * 设置公用参数
     */
    private static void setIntentParams(Intent intent, Uri uri) {
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 600);
        intent.putExtra("outputY", 600);
        intent.putExtra("noFaceDetection", true); // no face detection
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
    }

    public static Bitmap decodeUriAsBitmap(Context context, Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

    /**
     * 默认以jpg格式保存图片到指定路径
     *
     * @param bm
     * @param path
     * @return
     */
    public static boolean saveFile(Bitmap bm, String path) {
        if (bm == null || path == null)
            return false;
        File myCaptureFile = new File(path);
        if (myCaptureFile.exists()) {
            myCaptureFile.delete();
        }
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
            bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
            bos.flush();
            bos.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 以指定格式保存图片到指定路径
     *
     * @param bm
     * @param path
     * @param format
     * @return
     */
    public static boolean saveFile(Bitmap bm, String path, Bitmap.CompressFormat format) {
        if (bm == null || path == null)
            return false;
        File myCaptureFile = new File(path);
        if (myCaptureFile.exists()) {
            myCaptureFile.delete();
        }
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
            bm.compress(format, 80, bos);
            bos.flush();
            bos.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Bitmap转byte[]
    public static byte[] Bitmap2Bytes(Bitmap bm, Bitmap.CompressFormat format) {
        if (bm == null) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(format, 100, baos);
        return baos.toByteArray();
    }

    // byte[]转Bitmap
    public static Bitmap Bytes2Bimap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }

    /**
     * bitmap转为base64
     *
     * @param bitmap
     * @return
     */
    public static String bitmapToBase64(Bitmap bitmap) {

        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * base64转为bitmap
     *
     * @param base64Data
     * @return
     */
    public static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public static long getDeviceTime(Date date) {
        return (date.getTime() - getDeviceTimeOffset()) / 1000;
    }

    private static long getDeviceTimeOffset() {
        return get2000Time();
        // GMT+8
        // return BuildConfig.newFit ? 0 : 946656000000L;
        // utc
        // return BuildConfig.newFit ? 0 : 946684800000L;
    }

    public static int getDistanceTime(long time) {
        long a = System.currentTimeMillis() - time;
        return (int) (a / 1000 / 60 / 60);
    }

    public static Date getPreDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        calendar.set(Calendar.HOUR_OF_DAY, 22);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date getZoreTime() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 1);
        return cal.getTime();
    }

    public static long get2000Time() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2000);
        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static Date changeDeviceTime2PhoneTime(long time) {
        return new Date(changeDeviceTime2Phone(time));
    }

    public static long changeDeviceTime2Phone(long time) {
        return time * 1000 + getDeviceTimeOffset();
    }

    public static String format(double d) {
        BigDecimal b = new BigDecimal(d);
        d = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        DecimalFormat df = new DecimalFormat("#0.00");
        String v = df.format(d);
        if ("0.00".equals(v)) {
            v = "0";
        }
        return v;
    }

    public static String getDistanceStr(long goal, double step) {
        double d = getDistance(goal, step);
        return format(d);
    }

    /**
     * 计算卡路里
     *
     * @return 千卡
     */
    public static String getFireStr(long goal, double step, double weight) {
        double d = getFire(goal, step, weight);
        d = d * 0.001;
        return format(d);
    }

    /**
     * 计算距离
     *
     * @param step 步长 单位cm
     * @return 千米
     */
    public static double getDistance(long goal, double step) {
        return step * 0.01 * 0.001 * goal;
    }

    /**
     * 计算卡路里<br>
     * 消耗热量＝体重（kg）×距离（公里）×1.036
     *
     * @return 卡
     */
    public static double getFire(long goal, double step, double weight) {
        return step * 0.01 * 0.001 * goal * weight * 1.036;
    }

    public static void callPhone(Context context, String phoneNo) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setData(Uri.parse("tel:" + phoneNo));
        context.startActivity(intent);
    }

    public static void sendSMS(Context context, String phoneNo, String smsBody) {
        SmsManager smsMgr = SmsManager.getDefault();
        smsMgr.sendTextMessage(phoneNo, null, smsBody, null, null);
    }

    public static void sendSMS2(Context context, String phoneNo, String smsBody) {
        Uri smsToUri = Uri.parse("smsto:" + phoneNo);
        Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("sms_body", smsBody);
        context.startActivity(intent);
    }

//    public static void sendSMS2(Context context, String phoneNo, String smsBody) {
//        try {
//
//            Class<?> smsManagerClass = null;
//            Class[] divideMessagePamas = { String.class };
//            Class[] sendMultipartTextMessagePamas = { String.class,String.class, ArrayList.class, ArrayList.class,ArrayList.class, int.class };
//            Method divideMessage = null;
//            Method sendMultipartTextMessage = null;
//            smsManagerClass = Class.forName("android.telephony.SmsManager");
//            Method method = smsManagerClass.getMethod("getDefault", new Class[]{});
//            Object smsManager = method.invoke(smsManagerClass, new Object[]{});
//            divideMessage = smsManagerClass.getMethod("divideMessage",divideMessagePamas);
//            sendMultipartTextMessage = smsManagerClass.getMethod("sendMultipartTextMessage", sendMultipartTextMessagePamas);
//            ArrayList<String> magArray = (ArrayList<String>) divideMessage.invoke(smsManager, smsBody);
//            ITelephony iTelephony= (ITelephony)ITelephony.Stub.asInterface(ServiceManager.getService("phone"));
//            int phoneType=iTelephony.getActivePhoneType();
//            ITelephony.
//            sendMultipartTextMessage.invoke(smsManager,phoneNo, "", magArray, null, null,phoneType);
//        } catch (IllegalArgumentException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (SecurityException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (NoSuchMethodException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//    }


}
