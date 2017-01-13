package cn.com.zhiwoo.activity.home;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import cn.com.zhiwoo.R;
import cn.com.zhiwoo.activity.base.BaseActivity;
import cn.com.zhiwoo.utils.LogUtils;


public class ModifyInfoActivity extends BaseActivity {

    private EditText infoEditText;
    private String name;

    @Override
    public void initView() {
        super.initView();
        LinearLayout linearLayout = (LinearLayout) View.inflate(this, R.layout.home_modify_activity,null);
        flContent.addView(linearLayout);
        infoEditText = (EditText) linearLayout.findViewById(R.id.info_edittext);
        linearLayout.findViewById(R.id.save_button).setOnClickListener(this);
    }

    @Override
    public void initData() {
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        if (name == null) {
            return;
        }
        this.name = name;
        titleView.setText(name);
        infoEditText.setHint("请输入" + name);
        String defaultValue = intent.getStringExtra("defaultValue");
        if (!TextUtils.isEmpty(defaultValue)) {
            infoEditText.setText(defaultValue);
        }
    }


    @Override
    public void initListener() {

    }

    @Override
    public void process(View v) {
        switch (v.getId()) {
            case R.id.save_button : {
                String value = infoEditText.getText().toString();
                if (TextUtils.isEmpty(value)) {
                    Toast.makeText(getBaseContext(),name + "不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                LogUtils.log("保存信息 " + value);
                Intent intent = new Intent();
                intent.putExtra("value",value);
                setResult(RESULT_OK,intent);
                finish();
            }
            break;
        }
    }
}
