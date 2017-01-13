package cn.com.zhiwoo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import cn.com.zhiwoo.tool.AccountTool;
import cn.com.zhiwoo.tool.ChatTool;
import cn.com.zhiwoo.utils.LogUtils;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;


public class NetworkStateChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isConnected = false;
        if (connectivity != null) {
            NetworkInfo activeNetworkInfo = connectivity.getActiveNetworkInfo();
            if (activeNetworkInfo != null) {
//                 || activeNetworkInfo.isConnected()
                isConnected = true;
            }
            if (isConnected) {
                if (RongIM.getInstance() != null && RongIM.getInstance().getRongIMClient() != null){
                    String stateStr = "";
                    switch (RongIM.getInstance().getRongIMClient().getCurrentConnectionStatus()) {
                        case NETWORK_UNAVAILABLE: {
                            stateStr = "无网络";
                        }
                        break;
                        case CONNECTED: {
                            stateStr = "已连接";
                        }
                        break;
                        case DISCONNECTED : {
                            stateStr = "未连接";
                        }
                        break;
                        case CONNECTING : {
                            stateStr = "正在连接";
                        }
                        break;
                        case KICKED_OFFLINE_BY_OTHER_CLIENT : {
                            stateStr = "被挤下线";
                        }
                        break;
                        case TOKEN_INCORRECT : {
                            stateStr = "token错误";
                        }
                        break;
                        case SERVER_INVALID : {
                            stateStr = "服务过期";
                        }
                        break;
                    }
                    LogUtils.log("网络恢复: " + stateStr);
                    if (AccountTool.isLogined(context) && RongIM.getInstance().getRongIMClient().getCurrentConnectionStatus() == RongIMClient.ConnectionStatusListener.ConnectionStatus.DISCONNECTED) {
                        //有网络了,有账号,但是融云没连接,融云发起连接.断网恢复之后,融云会自动进行重连的
                        ChatTool.sharedTool().login();
                        LogUtils.log("有网络了,开始重新连接");
                    }
                }

            } else {
                Toast.makeText(context,"网络已断开",Toast.LENGTH_LONG).show();
            }
        }
    }
}
