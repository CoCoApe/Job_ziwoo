package cn.com.zhiwoo.utils;

import android.content.Context;
import android.util.Log;


public class LogUtils {
    public static void log(String message) {
        if (Global.DEBUG) {
            Log.i(Global.LOG_TAG, message);
        }
    }

    public static void log(Context context,String message) {
        log(context.getClass().getSimpleName()+" :  "+ message);
    }
}
