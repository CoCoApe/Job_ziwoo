package cn.com.zhiwoo.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import cn.com.zhiwoo.R;
import cn.com.zhiwoo.utils.LogUtils;
import cn.com.zhiwoo.utils.wxUtils.Constants;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler{
    private IWXAPI api;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_payresult);
    	api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
        api.handleIntent(getIntent(), this);
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}
	/**
	 * 支付回调
	 */
	@Override
	public void onResp(BaseResp resp) {
		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			int result = resp.errCode;
			LogUtils.log(this,"支付结果 = " + result);
			Intent intent = new Intent("wx_pay");
			intent.putExtra("result",result);
			sendBroadcast(intent);
		}
		finish();
	}
}