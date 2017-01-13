package cn.com.zhiwoo.activity.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.exception.HttpException;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import cn.com.zhiwoo.R;
import cn.com.zhiwoo.bean.main.Account;
import cn.com.zhiwoo.tool.AccountTool;
import cn.com.zhiwoo.tool.ChatTool;
import cn.com.zhiwoo.tool.NetworkTool;
import cn.com.zhiwoo.tool.OnNetworkResponser;
import cn.com.zhiwoo.utils.Global;
import cn.com.zhiwoo.utils.LogUtils;
import cn.com.zhiwoo.utils.wxUtils.Constants;
import cn.com.zhiwoo.view.main.NoScrollViewPager;
import cn.smssdk.SMSSDK;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView tipTextView;
    private NoScrollViewPager viewPager;
    private ImageView closeImage;
    private ImageView weiLogin;
    private Bitmap bitmap;
    private IWXAPI iwxapi;
    private MyBroadCastRecevier broadcastReceiver;
    private PhoneBindCastRecevier phoneBindCastRecevier;
    private ImageView bgImageView;
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private LoginFragmentAdapter loginPagerAdapter;
    private InfoCompleteFragment infoCompleteFragment;
    private PhoneBindFragment phoneBindFragment;
    public String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_login_activity);
        iwxapi = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
        iwxapi.registerApp(Constants.APP_ID);
        SMSSDK.initSDK(getApplicationContext(), Global.SMS_APPKEY, Global.SMS_SECRET);
        IntentFilter filter1 = new IntentFilter();
        IntentFilter filter2 = new IntentFilter();
        filter1.addAction("wx_code");
        filter2.addAction("phone_bind");
        broadcastReceiver = new MyBroadCastRecevier();
        phoneBindCastRecevier = new PhoneBindCastRecevier();
        registerReceiver(broadcastReceiver, filter1);
        registerReceiver(phoneBindCastRecevier, filter2);
        initView();
        initData();
    }

    private void initView() {
        bgImageView = (ImageView) findViewById(R.id.bg_imageview);
        tipTextView = (TextView) findViewById(R.id.tip_textview);
        closeImage = (ImageView) findViewById(R.id.close_imageview);
        weiLogin = (ImageView) findViewById(R.id.weixin_login_iamgeview);
        viewPager = (NoScrollViewPager) findViewById(R.id.scroll_viewpager);
        InputStream is = null;
        try {
            is = this.getAssets().open("login_bg.png");
        } catch (IOException e) {
            e.printStackTrace();
        }
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeStream(is, null, options);
        bgImageView.setImageBitmap(bitmap);
        tipTextView.setOnClickListener(this);
        closeImage.setOnClickListener(this);
        weiLogin.setOnClickListener(this);
    }
    private void initData() {
        LoginFragment loginFragment = new LoginFragment(new LoginFragment.LoginFragmentLoginListener() {
            @Override
            public void loginSuccess() {
                if (AccountTool.isLogined(getBaseContext())) {
                    //新账号登录成功,配置好聊天工具
                    LogUtils.log("新账号登录,配置聊天工具");
                    APP app = (APP) getApplication();
                    ChatTool.config(app.getMainActivity());
                    isLoginFromLike();
                }
            }
        });
        RegistFragment registFragment = new RegistFragment(new RegistFragment.RegistFragmentRegistListener() {
            @Override
            public void registSuccess() {

            }
        });
        FragmentManager fm = getSupportFragmentManager();
        fragments.add(loginFragment);
        fragments.add(registFragment);
        loginPagerAdapter = new LoginFragmentAdapter(fm);
        viewPager.setAdapter(loginPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tipTextView.setText(position == 0 ? "去注册?" : "去登录?");
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0,R.anim.main_login_dismiss);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close_imageview : {
                finish();
                overridePendingTransition(0,R.anim.main_login_dismiss);
            }
            break;
            case R.id.weixin_login_iamgeview : {
                loginByWeixin();
            }
            break;
            case R.id.tip_textview : {
                viewPager.setCurrentItem(viewPager.getCurrentItem() == 0 ? 1 : 0);
            }
            break;
        }
    }



    public class LoginFragmentAdapter extends FragmentPagerAdapter {
        LoginFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

    }

    @Override
    protected void onDestroy() {
        if(!bitmap.isRecycled()){
            bitmap.recycle(); //回收图片所占的内存
            System.gc(); //提醒系统及时回收
        }
        iwxapi.unregisterApp();
        unregisterReceiver(broadcastReceiver);
        unregisterReceiver(phoneBindCastRecevier);
        super.onDestroy();
    }
    private void loginByWeixin() {
        Toast.makeText(getBaseContext(),"正在启用微信登录...",Toast.LENGTH_SHORT).show();
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wechat_sdk_demo_test";
        iwxapi.sendReq(req);
    }
    private class MyBroadCastRecevier extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            String code = intent.getExtras().getString("code");
            HashMap<String,String> params = new HashMap<>();
            params.put("code",code);
            params.put("platform","1");
            NetworkTool.POST("http://121.201.7.33/zero/api/v1/user/auth/weixin", params, new OnNetworkResponser() {
                @Override
                public void onSuccess(String result) {
                    Gson gson = new Gson();
                    Account account = gson.fromJson(result, Account.class);
                    if (account != null && account.getAccessToken() != null) {
                        if (!TextUtils.isEmpty(account.getMobile())){
                            AccountTool.saveAsCurrentAccount(getBaseContext(), account);
                            if (AccountTool.isLogined(getBaseContext())) {
                                //新账号登录成功,配置好聊天工具
                                LogUtils.log("新账号登录,配置聊天工具");
                                APP app = (APP) getApplication();
                                ChatTool.config(app.getMainActivity());
                            }
                            ChatTool.sharedTool().login();
                            isLoginFromLike();
                            Intent intent1 = new Intent("userinfo_change");
                            sendBroadcast(intent1);
                            onBackPressed();
                        } else {
                            AccountTool.saveAsCurrentAccount(getBaseContext(), account);
                            phoneBindFragment = PhoneBindFragment.newInstance(account);
                            fragments.add(phoneBindFragment);
                            loginPagerAdapter.notifyDataSetChanged();
                            viewPager.setCurrentItem(2);
                        }
                    }else {
                        Toast.makeText(getBaseContext(),"登录失败1",Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(HttpException e, String s) {
                    Toast.makeText(getBaseContext(),"登录失败2",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    private class PhoneBindCastRecevier extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String phone = intent.getStringExtra("phone");
            infoCompleteFragment = InfoCompleteFragment.newInstance(phone);
            fragments.add(infoCompleteFragment);
            loginPagerAdapter.notifyDataSetChanged();
            viewPager.setCurrentItem(3);
        }
    }


    private void isLoginFromLike(){
        sendBroadcast(new Intent("like_change"));
    }
}