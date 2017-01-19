package cn.com.zhiwoo.activity.main;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.support.multidex.MultiDex;

import com.igexin.sdk.PushManager;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.tendcloud.tenddata.TCAgent;
import com.testin.agent.TestinAgent;

import cn.com.zhiwoo.service.GeIntentService;
import cn.com.zhiwoo.service.GePushService;
import cn.com.zhiwoo.tool.PayTool;
import cn.com.zhiwoo.utils.Global;
import cn.com.zhiwoo.utils.LogUtils;
import cn.jpush.android.api.JPushInterface;
import cn.smssdk.SMSSDK;
import io.rong.imkit.RongIM;

public class APP extends Application {
    private Activity mainActivity;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.log(this, "程序启动了");
        PayTool.config(getApplicationContext());
        TCAgent.init(this);
        TCAgent.setReportUncaughtExceptions(true);
        if ("cn.com.zhiwoo".equals(getCurProcessName()) || "io.rong.push".equals(getCurProcessName())) {
            LogUtils.log(this, "初始化融云");
            RongIM.init(this);
        }
        PushManager.getInstance().initialize(this.getApplicationContext(), GePushService.class);
        PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), GeIntentService.class);
        JPushInterface.setDebugMode(false);
        JPushInterface.init(this);
        //短信验证初始化
        SMSSDK.initSDK(this, Global.SMS_APPKEY, Global.SMS_SECRET);
        TestinAgent.init(this,"279c8c7a996a03f452e6804a1f23a5e5","");
        //OkGo初始化
        OkGo.init(this);
        OkGo.getInstance().setCacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST);
    }

    @Override
    public void onTerminate() {
        LogUtils.log(this, "程序关闭了");
        super.onTerminate();
    }

    public Activity getMainActivity() {
        return mainActivity;
    }

    public void setMainActivity(Activity mainActivity) {
        this.mainActivity = mainActivity;
    }
    String getCurProcessName () {
        int pid = android.os.Process.myPid ();
        ActivityManager mActivityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses())  {
            if(appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null ;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
