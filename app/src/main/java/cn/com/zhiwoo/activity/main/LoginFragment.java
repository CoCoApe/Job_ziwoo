package cn.com.zhiwoo.activity.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import java.util.HashMap;

import cn.com.zhiwoo.R;
import cn.com.zhiwoo.bean.main.Account;
import cn.com.zhiwoo.tool.AccountTool;
import cn.com.zhiwoo.tool.ChatTool;
import cn.com.zhiwoo.utils.Api;
import cn.com.zhiwoo.utils.Global;
import cn.com.zhiwoo.utils.MD5Utils;
import okhttp3.Call;
import okhttp3.Response;


@SuppressLint("ValidFragment")
public class LoginFragment extends Fragment implements View.OnClickListener {
    private LoginFragmentLoginListener loginListener;
    private EditText phoneEditText;
    private EditText passwordEditText;
    public LoginFragment() {

    }
    public LoginFragment(LoginFragmentLoginListener loginListener) {
        this.loginListener = loginListener;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_login_fragment,null);
        phoneEditText = (EditText)view.findViewById(R.id.phone_edittext);
        passwordEditText = (EditText)view.findViewById(R.id.password_edittext);
        view.findViewById(R.id.login_button).setOnClickListener(this);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button : {
                loginByPhone();
            }
            break;
        }
    }

    private void loginByPhone() {
        String phone = phoneEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(password)) {
            HashMap<String, String> param = new HashMap<>();
            param.put("password", MD5Utils.MD5(password, Global.ZW_SALT));
            param.put("platform", "1");
            param.put("mobile", phone);
            OkGo.post(Api.PHONE_LOGIN)
                    .params(param)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            Gson gson = new Gson();
                            Account account = gson.fromJson(s, Account.class);
                            if (account != null && account.getAccessToken() != null) {
                                AccountTool.saveAsCurrentAccount(getContext(), account);
                                if (loginListener != null) {
                                    loginListener.loginSuccess();
                                }
                                ChatTool.sharedTool().login();
                                Intent intent1 = new Intent("userinfo_change");
                                getContext().sendBroadcast(intent1);
                                getActivity().onBackPressed();
                            } else {
                                Toast.makeText(getContext(), "账号或密码错误", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            Toast.makeText(getContext(), "账号或密码错误", Toast.LENGTH_SHORT).show();
                        }
                    });
        }else {
            Toast.makeText(getContext(),"请输入完整的信息",Toast.LENGTH_SHORT).show();
        }
    }

    public interface LoginFragmentLoginListener {
        void loginSuccess();
    }

}
