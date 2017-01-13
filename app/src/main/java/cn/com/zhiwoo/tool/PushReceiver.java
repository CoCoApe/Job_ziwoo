package cn.com.zhiwoo.tool;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import cn.com.zhiwoo.activity.main.MainActivity;
import cn.com.zhiwoo.activity.react.ArticleDetailActivity;
import cn.com.zhiwoo.bean.react.ReactArticle;
import cn.com.zhiwoo.utils.APPStatusUtils;
import cn.com.zhiwoo.utils.LogUtils;
import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * 
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class PushReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
			LogUtils.log("[MyReceiver] 接收Registration Id : " + regId);
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
			LogUtils.log("[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
			LogUtils.log( "[MyReceiver] 接收到推送下来的通知");
			dealPush(context, bundle,false);
		} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
			LogUtils.log( "[MyReceiver] 打开了推送下来的通知");
			dealPush(context, bundle,true);
        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
			LogUtils.log( "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
        } else if(JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
        	boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
			LogUtils.log( "[MyReceiver]" + intent.getAction() +" connected state change to "+connected);
        } else {
			LogUtils.log( "[MyReceiver] Unhandled intent - " + intent.getAction());
        }
	}

	private void dealPush(Context context, Bundle bundle, boolean isTouch) {
		String content = bundle.getString("cn.jpush.android.ALERT");
		ReactArticle.QBean article = null;
		if (!TextUtils.isEmpty(content)) {
			int index = content.indexOf("链接:");
			if (index != -1) {
				String url = content.substring(index + "链接:".length());
				LogUtils.log("通知,拼接 - 链接为: " + url);
				article = new ReactArticle.QBean();
				article.setContent_url(url);
			}
		}
		if (article == null || article.getContent_url() == null) {
			//检测 携带的参数
			String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);
			if (!TextUtils.isEmpty(extra)) {
				try {
					JSONObject json = new JSONObject(extra);
					String url = json.getString("url");
					if (!TextUtils.isEmpty(url)) {
						LogUtils.log("通知,参数 - 链接为: " + url);
						article = new ReactArticle.QBean();
						article.setContent_url(url);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		if (article == null || article.getContent_url() == null) {
			return;
		}
		if (isTouch) { //用户点击了通知
			LogUtils.log("用户点击了通知");
			if (MainActivity.isAlive && !APPStatusUtils.isAppRunningOnTop(context,"cn.com.zhiwoo")) {
				//主界面在但不在前台的时候,点击了通知,唤醒APP并跳转到页面
				LogUtils.log("主界面在但不在前台的时候,点击了通知,唤醒APP并跳转到页面");
				Intent intent1 = new Intent(context, ArticleDetailActivity.class);
				Bundle bundle1 = new Bundle();
				bundle1.putSerializable("article",article);
				intent1.putExtras(bundle1);
				intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
				context.startActivity(intent1);

			} else if (!MainActivity.isAlive) {
				//主界面不在,点击了通知
				LogUtils.log("主界面不在,点击了通知,启动APP打开页面");
				Intent launchIntent = context.getPackageManager().
						getLaunchIntentForPackage("cn.com.zhiwoo");
				launchIntent.setFlags(
						Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
				Bundle args = new Bundle();
				args.putSerializable("article", article);
				launchIntent.putExtra("launchBundle", args);
				context.startActivity(launchIntent);
			}else {
				LogUtils.log("不属于任何范畴!!!");
			}
		} else { //只是接收到了通知
			LogUtils.log("用户只是收到了通知");
			if (MainActivity.isAlive && APPStatusUtils.isAppRunningOnTop(context,"cn.com.zhiwoo")) {
				//主界面在且在前台的时候,收到通知,直接弹出页面
				LogUtils.log("用户在前台收到了通知");
				Intent intent1 = new Intent(context, ArticleDetailActivity.class);
				Bundle bundle1 = new Bundle();
				bundle1.putSerializable("article",article);
				intent1.putExtras(bundle1);
				intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
				context.startActivity(intent1);
			}
		}
	}
}
