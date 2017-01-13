package cn.com.zhiwoo.activity.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import cn.com.zhiwoo.R;
import cn.com.zhiwoo.bean.main.Account;
import cn.com.zhiwoo.utils.Global;
import cn.com.zhiwoo.utils.LogUtils;
import cn.com.zhiwoo.utils.MyUtils;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;




public class PhoneBindFragment extends Fragment implements View.OnClickListener{
    private final static int SUBMIT_CODE_SUCCESS = 1;
    private final static int SUBMIT_CODE_FAILURE = 2;
    private final static int GET_CODE_SUCCESS = 3;
    private final static int GET_CODE_FAILURE = 4;
    private static PhoneBindFragment phoneBindFragment;

    private EditText phoneEt;
    private EditText authCodeEt;
    private Button authCodeBtn;
    private Button nextBtn;
    private int time = 0;
    private String phone;
    private Account account;
    private EventHandler eh;
    private Context mContext;

    public static PhoneBindFragment newInstance(Account account) {
        phoneBindFragment = new PhoneBindFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("account",account);
        phoneBindFragment.setArguments(bundle);
        return phoneBindFragment;
    }

    private Handler timerHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            time --;
            if (time <= 0) {
                time = 0;
                timerHandler.removeCallbacksAndMessages(null);
                authCodeBtn.setText("获取验证码");
                authCodeBtn.setEnabled(true);
            } else {
                authCodeBtn.setText( time + "秒后可重获");
                authCodeBtn.setEnabled(false);
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
                    Toast.makeText(getActivity(), "验证码验证成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent("phone_bind");
                    intent.putExtra("phone",phone);
                    getActivity().sendBroadcast(intent);
                }
                break;

                case GET_CODE_SUCCESS : {
                    Toast.makeText(getActivity(),"验证码已发送",Toast.LENGTH_SHORT).show();
                }
                break;

                case SUBMIT_CODE_FAILURE : {
                    Toast.makeText(getActivity(),"验证码错误",Toast.LENGTH_SHORT).show();
                }
                break;

                case GET_CODE_FAILURE : {
                    Toast.makeText(getActivity(),"获取验证码失败",Toast.LENGTH_SHORT).show();
                }
                break;
            }
            return false;
        }
    });


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        account = (Account) getArguments().get("account");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_bind_fragment,container,false);
        initView(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SMSSDK.initSDK(getContext(), Global.SMS_APPKEY, Global.SMS_SECRET);
        eh = new EventHandler(){
            @Override
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE) {
                    //回调完成
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //提交验证码成功
                        mHandler.sendEmptyMessage(SUBMIT_CODE_SUCCESS);
                    }else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                        //获取验证码成功
                        mHandler.sendEmptyMessage(GET_CODE_SUCCESS);
                    }
                }else{
                    //回调完成
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //提交验证码成功
                        mHandler.sendEmptyMessage(SUBMIT_CODE_FAILURE);
                    }else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                        //获取验证码成功
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
                    LogUtils.log(getContext(), "错误代码:" + status);
                    if (status > 0 && !TextUtils.isEmpty(des)) {
                        LogUtils.log(getContext(), "失败原因:" + des);
                    }
                }
            }
        };
        SMSSDK.registerEventHandler(eh); //注册短信回调
    }

    private void initView(View view) {
        phoneEt = (EditText) view.findViewById(R.id.main_bind_phone);
        authCodeEt = (EditText) view.findViewById(R.id.main_bind_authCode);
        authCodeBtn = (Button) view.findViewById(R.id.main_bind_getAuthCode);
        nextBtn = (Button) view.findViewById(R.id.main_bind_next);
        authCodeBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.main_bind_getAuthCode:
                String phoneStr = phoneEt.getText().toString();
                if (!TextUtils.isEmpty(phoneStr) && MyUtils.isPhoneNumberValid(phoneStr)) {
                    time = 60;
                    timerHandler.sendEmptyMessage(0);
                    SMSSDK.getVerificationCode("86", phoneStr);
                    this.phone = phoneStr;
                    Toast.makeText(getContext(),"获取验证码",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(),"请输入正确的手机号码",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.main_bind_next:
                String code = authCodeEt.getText().toString();
                if (!TextUtils.isEmpty(phone)) {
                    if (!TextUtils.isEmpty(code)) {
                        SMSSDK.submitVerificationCode("86",phone,code);
                    } else {
                        Toast.makeText(getActivity(),"请输入验证码",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(),"请填写手机号和验证码",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


}
