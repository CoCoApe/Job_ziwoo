package cn.com.zhiwoo.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import cn.com.zhiwoo.R;
import cn.com.zhiwoo.utils.LogUtils;
import cn.com.zhiwoo.utils.wxUtils.Constants;


public class WXEntryActivity extends Activity implements IWXAPIEventHandler{
	// IWXAPI 是第三方app和微信通信的openapi接口
	private IWXAPI api;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_payresult);
		// 通过WXAPIFactory工厂，获取IWXAPI的实例
		api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, false);
		api.registerApp(Constants.APP_ID);
		api.handleIntent(getIntent(), this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		api.handleIntent(intent, this);
	}

	// 微信发送请求到第三方应用时，会回调到该方法
	@Override
	public void onReq(BaseReq req) {
		Toast.makeText(this, "发送请求 openid = " + req.openId, Toast.LENGTH_SHORT).show();
	}

	// 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
	@Override
	public void onResp(BaseResp resp) {
		LogUtils.log(getBaseContext(),"resp.getType() = " + resp.getType());
		//授权 回调
		if (resp.getType() == ConstantsAPI.COMMAND_SENDAUTH) {
			switch (resp.errCode) {
				case BaseResp.ErrCode.ERR_OK :
					String code = ((SendAuth.Resp) resp).code;
					Log.i("aaaaa", "onResp: 微信请求响应OK"+code);
					Intent intent = new Intent("wx_code");
					intent.putExtra("code",code);
					sendBroadcast(intent);
					break;
				case BaseResp.ErrCode.ERR_USER_CANCEL :
					Toast.makeText(this, "微信登录失败", Toast.LENGTH_SHORT).show();
					break;
				case BaseResp.ErrCode.ERR_AUTH_DENIED :
					Toast.makeText(this, "微信登录失败", Toast.LENGTH_SHORT).show();
					break;
				default:
					break;
			}
		} else if (resp.getType() == ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX) {
			switch (resp.errCode) {
				case BaseResp.ErrCode.ERR_OK:
					Toast.makeText(this, "分享成功", Toast.LENGTH_SHORT).show();
					break;
				case BaseResp.ErrCode.ERR_USER_CANCEL :
					Toast.makeText(this, "分享失败", Toast.LENGTH_SHORT).show();
					break;
				case BaseResp.ErrCode.ERR_AUTH_DENIED :
					Toast.makeText(this, "分享失败", Toast.LENGTH_SHORT).show();
					break;
				default:
					break;
			}
		}
		finish();
	}
}