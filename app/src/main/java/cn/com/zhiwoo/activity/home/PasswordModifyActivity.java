package cn.com.zhiwoo.activity.home;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import cn.com.zhiwoo.R;
import cn.com.zhiwoo.activity.base.BaseActivity;
import cn.com.zhiwoo.utils.Global;
import cn.com.zhiwoo.utils.LogUtils;
import cn.com.zhiwoo.utils.MD5Utils;


public class PasswordModifyActivity extends BaseActivity {

    private EditText oldPasswordEditText;
    private EditText newPasswordEditText;

    @Override
    public void initView() {
        super.initView();
        LinearLayout linearLayout = (LinearLayout) View.inflate(this, R.layout.home_modifypassword_activity,null);
        flContent.addView(linearLayout);
        oldPasswordEditText = (EditText) linearLayout.findViewById(R.id.oldpassword_edittext);
        newPasswordEditText = (EditText) linearLayout.findViewById(R.id.newpassword_edittext);
        linearLayout.findViewById(R.id.save_button).setOnClickListener(this);
    }

    @Override
    public void initData() {
        titleView.setText("修改密码");
    }

    @Override
    public void initListener() {

    }

    @Override
    public void process(View v) {
        switch (v.getId()) {
            case R.id.save_button : {
                String newPassword = newPasswordEditText.getText().toString();
                String oldPassword = oldPasswordEditText.getText().toString();
                if (TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(oldPassword)) {
                    return;
                }
                if (newPassword.length() < 6 || oldPassword.length() < 6) {
                    Toast.makeText(getBaseContext(),"密码不要短于6位",Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra("oldPassword", MD5Utils.MD5(oldPassword, Global.ZW_SALT));
                intent.putExtra("newPassword",MD5Utils.MD5(newPassword, Global.ZW_SALT));
                setResult(RESULT_OK,intent);
                LogUtils.log("点击了保存按钮");
                finish();
            }
            break;
        }
    }
}
