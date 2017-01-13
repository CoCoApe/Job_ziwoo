package cn.com.zhiwoo.tool;

import android.content.Context;

public class PushTool {
    private static PushTool instance;
    private Context context;

    public static void config(Context context) {
        if (instance == null) {
            instance = new PushTool(context);
            instance.registerMessageReceiver();
        }
    }

    private PushTool(Context context) {
        this.context = context;
    }

    private void registerMessageReceiver() {

    }
}
