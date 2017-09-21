package com.example.youjiannuo.listviewfloattitle;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.Service;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Camera;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.util.List;

import static android.content.Context.KEYGUARD_SERVICE;


public class SystemUtil {
    private static String deviceId = null;

    public static String getDeviceId(Context context) {
        if (deviceId != null) {
            return deviceId;
        }

        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }

    public static boolean isPhone(Context context) {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (manager.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE) {
            return false;
        } else {
            return true;
        }
    }

    public static String getProjectVersion() {
        PackageManager manager;
        PackageInfo info;
        manager = ContextManager.getContext().getPackageManager();
        try {
            info = manager.getPackageInfo(ContextManager.getContext().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
        return info.versionName;
    }


    /**
     * 检查SDCard是否存在
     *
     * @return
     */
    public static boolean checkSDCardAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }


    public static boolean isGPSOpen() {
        LocationManager locationManager = (LocationManager) ContextManager.getContext()
                .getSystemService(Context.LOCATION_SERVICE);
        // 判断GPS模块是否开启，如果没有则开启
        if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return false;
        }
        return true;
    }

    public static boolean isReadContactPermission() {
        // 读取联系人
        Cursor cursor = ContextManager.getContext().getContentResolver().query(ContactsContract.Data.CONTENT_URI, null, null, null, null);
        boolean is = false;
        if (cursor != null && cursor.moveToFirst()) {
            is = true;
            cursor.close();
        }
        return is;
    }

    /**
     * 获取SDCard的地址
     *
     * @return
     */
    public static String getSDCardPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public static int[] getPhoneScreenWH(Context context) {
        if (context == null) {
            return new int[]{1, 1};
        }
        int wh[] = new int[2];
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        wh[0] = dm.widthPixels;
        wh[1] = dm.heightPixels;
        return wh;
    }

    public static int[] getPhoneScreenWHForPX(Context context) {
        int wh[] = new int[2];
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        wh[0] = dipTOpx(dm.widthPixels);
        wh[1] = dipTOpx(dm.heightPixels);
        return wh;
    }


    public static int dipTOpx(float dpValue) {
        final float scale = ContextManager.getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int pxTodip(float pxValue) {
        final float scale = ContextManager.getContext().getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int spTopx(float spValue) {
        final float fontScale = ContextManager.getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static int spTopx(float spValue, float fontScale) {
        return (int) (spValue * fontScale + 0.5f);
    }

    public static int pxTosp(float pxValue, float fontScale) {
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 延迟显示
     *
     * @param v
     */
    public static void showInputMethodManagerDelay(View v) {
        showInputMethodManager(v, 200, null);
    }


    /**
     * 马上显示
     *
     * @param v
     */
    public static void showInputMethodManagerNow(View v, onInputMethodListener l) {
        showInputMethodManager(v, 0, l);
    }


    public static void showInputMethodManager(final View v, long time, final onInputMethodListener l) {
        if (v == null) return;
        v.setFocusable(true);
        v.setFocusableInTouchMode(true);
        v.requestFocus();
        try {
            if (SystemUtil.getAndroidApi() <= 18) {
                getInputMethodManager(v.getContext()).showSoftInput(v, 0);
                if (l != null) l.onInputMethodShow();
            } else {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new InputRunnable(v, l), time);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class InputRunnable implements Runnable {

        private onInputMethodListener l;
        private View v;

        InputRunnable(View v, onInputMethodListener l) {
            this.l = l;
            this.v = v;
        }

        @Override
        public void run() {
            try {
                getInputMethodManager(v.getContext()).showSoftInput(v, 0);
                if (l != null) l.onInputMethodShow();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //判断软键盘是都出现
    public static boolean isInputMethodShow(View v) {

//        return  context.getWindow().peekDecorView() != null;
        InputMethodManager imm = getInputMethodManager(v.getContext());
        return imm != null && imm.isActive(v);
//        return context.getWindow().getAttributes().softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED;
    }

    /**
     * 关闭输入软键盘
     *
     * @param v
     */
    public static boolean closeInputMethodManager(View v) {
        if (v == null) return false;
        return getInputMethodManager(v.getContext()).hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }


    public static InputMethodManager getInputMethodManager(Context context) {
        return (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    }


    public interface onInputMethodListener {

        void onInputMethodShow();

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void copy(String text) {
        ClipboardManager cmb = (ClipboardManager) ContextManager.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(text);
    }

    public static String getTextFromClip() {
        ClipboardManager cmb = (ClipboardManager) ContextManager.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        if (cmb != null && cmb.getText() != null) {
            return cmb.getText().toString();
        }
        return "";
    }


    /**
     * 获取Android的版本号
     *
     * @return
     */
    public static String getAndroidId() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 获取手机型号
     */
    public static String getPhone() {
        return Build.MODEL;
    }

    /**
     * 获取Android系统的api
     *
     * @return
     */
    public static int getAndroidApi() {
        return Build.VERSION.SDK_INT;
    }






    /**
     * 获取一个随机数
     *
     * @param startNum 从那个位置开始
     * @param endNum   从那个位置结束
     * @return
     */
    public static int getRandom(int startNum, int endNum) {
        return startNum + (int) (Math.random() * endNum);
    }

    public static int getRandom(int endNum) {
        return getRandom(0, endNum);
    }

    public static boolean isMobileNetwork() {
        NetworkInfo networkInfo = getActiveNetworkType();
        return networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
    }

    public static NetworkInfo getActiveNetworkType() {
        ConnectivityManager connectivity = (ConnectivityManager) ContextManager.getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            Log.w("TAG", "couldn't get connectivity manager");
            return null;
        }

        NetworkInfo activeInfo = connectivity.getActiveNetworkInfo();
        if (activeInfo == null) {
            return null;
        }
        return activeInfo;
    }

    public static boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) ContextManager.getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            //如果仅仅是用来判断网络连接
            //则可以使用 cm.getActiveNetworkInfo().isAvailable();
            NetworkInfo info = cm.getActiveNetworkInfo();
            if (info != null) {
                return info.getState() == NetworkInfo.State.CONNECTED;
            }
        }
        return false;
    }

    //启动硬件加速
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void startLayerTypeHardWare(View view) {
        if (view != null)
            view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
    }

    //获取项目的版本
    public static int getAppVersion() {
        Context context = ContextManager.getContext();
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return Integer.MAX_VALUE;
    }


    /**
     * 获取application中指定的meta-data
     *
     * @return 如果没有获取成功(没有对应值，或者异常)，则返回值为空
     */
    public static String getAppMetaData(Context ctx, String key) {
        if (ctx == null || TextUtils.isEmpty(key)) {
            return null;
        }
        String resultData = null;
        try {
            PackageManager packageManager = ctx.getPackageManager();
            if (packageManager != null) {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        resultData = applicationInfo.metaData.getString(key);
                    }
                }

            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return resultData;
    }

    //跳转到主界面
    public static void startHome(Activity activity) {
        Intent mHomeIntent = new Intent(Intent.ACTION_MAIN);

        mHomeIntent.addCategory(Intent.CATEGORY_HOME);
        mHomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        activity.startActivity(mHomeIntent);

    }

    //获取相机的拍摄角度
    public static float getCameraViewAngle() {
        Camera camera = Camera.open();
        Camera.Parameters parameters = camera.getParameters();
        float angle = parameters.getHorizontalViewAngle();
        camera.release();
        return angle;
    }


    //获取项目包名
    public static String getPackageName(Context context) {
        if (context == null) return "you";
        try {
            return context.getPackageName();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "you";
    }


    public static void printlnInfo(String text) {
            Log.i("youjiannuo", text);
    }

    public static void startNewTaskActivity(Context context, Class cls, String keys[], Object[] objs) {
        if (context == null) return;
        Intent intent = new Intent();
        if (keys != null && objs != null) {
            for (int i = 0; i < keys.length; i++) {
                if (objs[i] instanceof String) {
                    intent.putExtra(keys[i], (String) objs[i]);
                } else if (objs[i] instanceof Integer) {
                    intent.putExtra(keys[i], (Integer) objs[i]);
                } else if (objs[i] instanceof Boolean) {
                    intent.putExtra(keys[i], (Boolean) objs[i]);
                }
            }
        }

        intent.setClass(context, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//它可以关掉所要到的界面中间的activity
        context.startActivity(intent);
//        android.os.Process.killProcess(android.os.Process.myPid());
//        context.startActivity(intent);
        //    System.exit(0);
        //保存umeng数据
        //  MobclickAgent.onKillProcess(context);
    }


    public static String getVersionCode() {
        PackageManager manager;
        PackageInfo info = null;
        manager = ContextManager.getContext().getPackageManager();
        try {
            info = manager.getPackageInfo(ContextManager.getContext().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return info.versionName;
    }


    public static boolean checkApkExist(Context context, String packageName) {
//        if (packageName == null || "".equals(packageName))
//            return false;
//        try {
//            ApplicationInfo info = context.getPackageManager()
//                    .getApplicationInfo(packageName,
//                            PackageManager.GET_UNINSTALLED_PACKAGES);
//            return true;
//        } catch (PackageManager.NameNotFoundException e) {
//            return false;
//        }

        List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);

        for (int i = 0; i < packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);
            if (packageName.equals(packageInfo.packageName)) {
                return true;
            }
        }
        return false;
    }





    //短信跳转
    public static void startSms(Context context, String msg, String tel) {
        Uri uri = Uri.parse("smsto:" + tel);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra("sms_body", msg);
        context.startActivity(intent);
    }

    //发送 邮件
    public static void startEmail(Context context, String title, String msg, String... toEmails) {
        toEmails = toEmails == null ? new String[0] : toEmails;
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822"); // 设置邮件格式
        intent.putExtra(Intent.EXTRA_EMAIL, toEmails);
        intent.putExtra(Intent.EXTRA_SUBJECT, title);
        intent.putExtra(Intent.EXTRA_TEXT, msg);
        context.startActivity(Intent.createChooser(intent, "请选择邮件类型"));
    }

    public static void startVibrator(long time) {
        Vibrator vibrator = (Vibrator) ContextManager.getContext().getSystemService(Service.VIBRATOR_SERVICE);
        vibrator.vibrate(time);
    }

    /**
     * 查询是否具备某一个权限
     *
     * @param permission 权限的名称，例如
     * @return
     */
    public static boolean checkPermission(String permission) {
        PackageManager pm = ContextManager.getContext().getPackageManager();

        return PackageManager.PERMISSION_GRANTED == pm.checkPermission(permission, getPackageName(ContextManager.getContext()));
    }

    public static String getMetaData(String key) {
        Context context = ContextManager.getContext();
        try {
            ApplicationInfo appInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            return appInfo.metaData.getString(key, "jw");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();

        }
        return "jw";
    }

    public static Class getNameClass(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @return true 锁屏了，false不锁屏
     */
    public static boolean isLockScreen() {
        KeyguardManager mKeyguardManager = (KeyguardManager) ContextManager.getSystemService(KEYGUARD_SERVICE);
        return mKeyguardManager.inKeyguardRestrictedInputMode();
    }

    @TargetApi(19)
    public static void setTranslucentStatus(Activity activity, boolean on) {
        if (Build.VERSION.SDK_INT >= 11) {
            Window win = activity.getWindow();
            WindowManager.LayoutParams winParams = win.getAttributes();
            final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            if (on) {
                winParams.flags |= bits;
            } else {
                winParams.flags &= ~bits;
            }

            win.setAttributes(winParams);
        }
    }


    //是否有这些应用
    public static boolean isPackage(String packageName) {
        final PackageManager packageManager = ContextManager.getContext().getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals(packageName)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 判断当前是否为Debug模式
     */
    public static boolean isApkDebugable() {
        try {
            ApplicationInfo info = ContextManager.getContext().getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {

        }
        return false;

    }


    /**
     * 当前的app是否退出后台
     */
    public static boolean isBackground() {
        Context context = ContextManager.getContext();
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null) return true;
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {

                if (appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    public static String currentRunningActivity() {
        Context context = ContextManager.getContext();
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.RunningTaskInfo info = manager.getRunningTasks(1).get(0);
        return info.topActivity.getShortClassName();    //类名
    }


    /**
     * 将得到的int类型的IP转换为String类型
     *
     * @param ip
     * @return
     */
    public static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }


}
