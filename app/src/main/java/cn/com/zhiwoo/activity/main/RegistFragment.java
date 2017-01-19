package cn.com.zhiwoo.activity.main;

import android.annotation.SuppressLint;
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
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.com.zhiwoo.R;
import cn.com.zhiwoo.utils.Api;
import cn.com.zhiwoo.utils.Global;
import cn.com.zhiwoo.utils.LogUtils;
import cn.com.zhiwoo.utils.MD5Utils;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import okhttp3.Call;
import okhttp3.Response;


@SuppressLint("ValidFragment")
public class RegistFragment extends Fragment implements View.OnClickListener {
    private final static int SUBMIT_CODE_SUCCESS = 1;
    private final static int SUBMIT_CODE_FAILURE = 2;
    private final static int GET_CODE_SUCCESS = 3;
    private final static int GET_CODE_FAILURE = 4;
    private RegistFragmentRegistListener registListener;
    private EditText phoneEditText;
    private EditText passwordEditText;
    private EditText aouthCodeEditText;
    private TextView getCodeTextView;
    private Button agreeButton;
    private int time = 0;
    private String phone;
    private EventHandler eh;
    private Handler timerHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            time --;
            if (time <= 0) {
                time = 0;
                timerHandler.removeCallbacksAndMessages(null);
                getCodeTextView.setText("获取验证码");
                getCodeTextView.setEnabled(true);
            } else {
                getCodeTextView.setText( time + "秒后可重获");
                getCodeTextView.setEnabled(false);
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
                    Toast.makeText(getContext(), "验证码验证成功", Toast.LENGTH_SHORT).show();
                    regist();
                }
                break;

                case GET_CODE_SUCCESS : {
                    Toast.makeText(getContext(),"获取验证码成功",Toast.LENGTH_SHORT).show();
                }
                break;

                case SUBMIT_CODE_FAILURE : {
                    Toast.makeText(getContext(),"验证码验证失败",Toast.LENGTH_SHORT).show();
                }
                break;

                case GET_CODE_FAILURE : {
                    Toast.makeText(getContext(),"获取验证码失败",Toast.LENGTH_SHORT).show();
                }
                break;
            }
            return false;
        }
    });

    public RegistFragment() {
    }

    public RegistFragment(RegistFragmentRegistListener registListener) {
        this.registListener = registListener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_regist_fragment,null);
        phoneEditText = (EditText)view.findViewById(R.id.phone_edittext);
        passwordEditText = (EditText)view.findViewById(R.id.password_edittext);
        aouthCodeEditText = (EditText)view.findViewById(R.id.aouthcode_edittext);
        agreeButton = (Button)view.findViewById(R.id.agree_button);
        agreeButton.setSelected(true);
        agreeButton.setOnClickListener(this);
        view.findViewById(R.id.userprotocol_textview).setOnClickListener(this);
        view.findViewById(R.id.regist_button).setOnClickListener(this);
        getCodeTextView = (TextView)view.findViewById(R.id.getcode_textview);
        getCodeTextView.setOnClickListener(this);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SMSSDK.initSDK(getContext(), Global.SMS_APPKEY, Global.SMS_SECRET,false);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.agree_button : {
                agreeButton.setSelected(!agreeButton.isSelected());
            }
            break;
            case  R.id.userprotocol_textview : {
                Intent intent = new Intent(getContext(),UserProtocolActivity.class);
                startActivityForResult(intent,1000);
            }
            break;
            case R.id.regist_button : {
                if (!agreeButton.isSelected()) {
                    Toast.makeText(getContext(),"请同意咨我用户协议",Toast.LENGTH_SHORT).show();
                    return;
                }
                String code = aouthCodeEditText.getText().toString();
                if (!TextUtils.isEmpty(this.phone)) {
                    if (!TextUtils.isEmpty(code)) {
                        SMSSDK.submitVerificationCode("86",this.phone,code);
                        Toast.makeText(getContext(),"验证结果",Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(),"请输入验证码",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(),"您还没有获取验证码",Toast.LENGTH_SHORT).show();
                }
            }
            break;
            case R.id.getcode_textview : {
                String phone = phoneEditText.getText().toString();
                if (!TextUtils.isEmpty(phone) && isPhoneNumberValid(phone)) {
                    time = 60;
                    timerHandler.sendEmptyMessage(0);
                    SMSSDK.getVerificationCode("86", phone);
//                    SMSManager.getInstance().sendMessage(getContext(), "86",phone);
                    this.phone = phone;
                    Toast.makeText(getContext(),"获取验证码",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(),"请输入正确的手机号码",Toast.LENGTH_SHORT).show();
                }
            }
            break;
        }
    }
    public interface RegistFragmentRegistListener {
        void registSuccess();
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
    private void regist() {
        String password = passwordEditText.getText().toString();
        if (password.length() < 6) {
            Toast.makeText(getContext(),"密码不要小于6位",Toast.LENGTH_SHORT).show();
        } else if (!TextUtils.isEmpty(this.phone)){
            HashMap<String,String> param = new HashMap<>();
            param.put("mobile",this.phone);
            param.put("password", MD5Utils.MD5(password, Global.ZW_SALT));
            OkGo.post(Api.PHONE_REGIST)
                    .params(param)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            Toast.makeText(getContext(),"注册成功",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            Toast.makeText(getContext(),"注册失败",Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(getContext(),"请先获取验证码",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        boolean agree = data.getBooleanExtra("agree",true);
        LogUtils.log(getContext(),(agree ? "同意" : "不同意") + " 用户协议");
        agreeButton.setSelected(agree);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eh);
    }
}
