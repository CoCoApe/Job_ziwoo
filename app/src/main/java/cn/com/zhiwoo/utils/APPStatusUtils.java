package cn.com.zhiwoo.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.text.TextUtils;

import java.util.List;


public class APPStatusUtils {
    /**
     * 判断程序是否在前台运行（当前运行的程序）
     */
    public static boolean isAppRunningOnTop(Context context, String appPackageName) {

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningTaskInfo> runningTaskInfo = activityManager.getRunningTasks(1);

        String topAppPackageName = runningTaskInfo.get(0).topActivity.getPackageName();

        return TextUtils.equals(appPackageName, topAppPackageName);
    }
}
