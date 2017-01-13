package cn.com.zhiwoo.activity.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.com.zhiwoo.R;
import cn.com.zhiwoo.activity.base.BaseActivity;
import cn.com.zhiwoo.utils.Global;
import cn.com.zhiwoo.utils.LogUtils;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;


public class PhoneBindActivity extends BaseActivity {
    private final static int SUBMIT_CODE_SUCCESS = 1;
    private final static int SUBMIT_CODE_FAILURE = 2;
    private final static int GET_CODE_SUCCESS = 3;
    private final static int GET_CODE_FAILURE = 4;

    private EditText phoneEditText;
    private EditText codeEdittext;
    private String phone;

    private int time = 60;
    private Handler timerHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            time --;
            if (time <= 0) {
                time = 0;
                timerHandler.removeCallbacksAndMessages(null);
                fetchCodeButton.setText("获取验证码");
                fetchCodeButton.setEnabled(true);
            } else {
                fetchCodeButton.setText( time +"秒后可重获");
                fetchCodeButton.setEnabled(false);
                timerHandler.sendEmptyMessageDelayed(0,1000);
            }
            return false;
        }
    });
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case SUBMIT_CODE_SUCCESS : {
                    Toast.makeText(getBaseContext(), "验证码验证成功", Toast.LENGTH_SHORT).show();
                    //返回新的手机号码
                    Intent intent = new Intent();
                    intent.putExtra("phone",phone);
                    setResult(RESULT_OK,intent);
                    finish();
                }
                break;

                case GET_CODE_SUCCESS : {
                    Toast.makeText(getBaseContext(),"验证码已发送",Toast.LENGTH_SHORT).show();
                }
                break;

                case SUBMIT_CODE_FAILURE : {
                    Toast.makeText(getBaseContext(),"验证码错误",Toast.LENGTH_SHORT).show();
                }
                break;

                case GET_CODE_FAILURE : {
                    Toast.makeText(getBaseContext(),"获取验证码失败",Toast.LENGTH_SHORT).show();
                }
                break;
            }
            return false;
        }
    });
    private Button fetchCodeButton;
    private EventHandler eh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SMSSDK.initSDK(this,Global.SMS_APPKEY,Global.SMS_SECRET);
        eh = new EventHandler(){
            @Override
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE) {//成功
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //提交验证码成功
                        mHandler.sendEmptyMessage(SUBMIT_CODE_SUCCESS);
                    }else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                        //获取验证码成功
                        mHandler.sendEmptyMessage(GET_CODE_SUCCESS);
                    }
                }else{
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {//失败
                        //提交验证码失败
                        mHandler.sendEmptyMessage(SUBMIT_CODE_FAILURE);
                    }else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                        //获取验证码失败
                        mHandler.sendEmptyMessage(GET_CODE_FAILURE);
                    }
                    Throwable throwable = (Throwable) data;
                    throwable.printStackTrace();
                    JSONObject object = null;
                    try {
                        object = new JSONObject(throwable.getMessage());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String des = object != null ? object.optString("detail") : null;//错误描述
                    int status = object != null ? object.optInt("status") : 0;//错误代码
                    LogUtils.log(getBaseContext(), "错误代码:" + status);
                    if (status > 0 && !TextUtils.isEmpty(des)) {
                        LogUtils.log(getBaseContext(), "失败原因:" + des);
                    }
                }
            }
        };
        SMSSDK.registerEventHandler(eh); //注册短信回调
    }

    @Override
    public void initView() {
        super.initView();
        LinearLayout linearLayout = (LinearLayout) View.inflate(this, R.layout.home_bind_phone_activity, null);
        flContent.addView(linearLayout);
        phoneEditText = (EditText) linearLayout.findViewById(R.id.phone_edittext);
        codeEdittext = (EditText) linearLayout.findViewById(R.id.aouthcode_edittext);
        fetchCodeButton = (Button) linearLayout.findViewById(R.id.fetchcode_button);
        fetchCodeButton.setOnClickListener(this);
        linearLayout.findViewById(R.id.save_button).setOnClickListener(this);
    }

    @Override
    public void initData() {
        titleView.setText("手机");
    }

    @Override
    public void initListener() {

    }

    @Override
    public void process(View v) {
        switch (v.getId()) {
            case R.id.fetchcode_button : {
                LogUtils.log("获取验证码");
                String phone = phoneEditText.getText().toString();
                if (!TextUtils.isEmpty(phone) && isPhoneNumberValid(phone)) {
                    time = 60;
                    timerHandler.sendEmptyMessage(0);
                    SMSSDK.getVerificationCode("86", phone);
                    this.phone = phone;
                    Toast.makeText(getBaseContext(),"获取验证码",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getBaseContext(),"请输入正确的手机号码",Toast.LENGTH_SHORT).show();
                }
            }
            break;
            case R.id.save_button : {
                String code = codeEdittext.getText().toString();
                if (!TextUtils.isEmpty(this.phone)) {
                    if (!TextUtils.isEmpty(code)) {
                        SMSSDK.submitVerificationCode("86",this.phone,code);
                    } else {
                        Toast.makeText(getBaseContext(),"请输入验证码",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getBaseContext(),"您还没有获取验证码",Toast.LENGTH_SHORT).show();
                }
                LogUtils.log("保存信息");
            }
            break;
        }
    }
    private boolean isPhoneNumberValid(String phoneNumber) {
        boolean isValid = false;
        String expression = "((^(13|15|18)[0-9]{9}$)|(^0[1,2]\\d-?\\d{8}$)|(^0[3-9] \\d{2}-?\\d{7,8}$)|(^0[1,2]\\d-?\\d{8}-(\\d{1,4})$)|(^0[3-9]\\d{2}-? \\d{7,8}-(\\d{1,4})$))";
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(phoneNumber);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eh);
    }
}
