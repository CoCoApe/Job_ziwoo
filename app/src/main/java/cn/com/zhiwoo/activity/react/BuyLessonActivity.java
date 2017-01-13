package cn.com.zhiwoo.activity.react;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.lidroid.xutils.exception.HttpException;

import java.util.HashMap;

import cn.com.zhiwoo.R;
import cn.com.zhiwoo.activity.base.BaseActivity;
import cn.com.zhiwoo.bean.react.Lesson;
import cn.com.zhiwoo.tool.NetworkTool;
import cn.com.zhiwoo.tool.OnNetworkResponser;
import cn.com.zhiwoo.tool.PayTool;


public class BuyLessonActivity extends BaseActivity {

    private EditText nameEditText;
    private EditText phoneEditText;
    private Lesson lesson;
    private Button buyButton;

    @Override
    public void initView() {
        super.initView();
        LinearLayout linearLayout = (LinearLayout) View.inflate(this, R.layout.react_lesson_buy,null);
        flContent.addView(linearLayout);
        nameEditText = (EditText) linearLayout.findViewById(R.id.name_edittext);
        phoneEditText = (EditText) linearLayout.findViewById(R.id.phone_edittext);
        buyButton = (Button) findViewById(R.id.buy_button);
    }

    @Override
    public void initData() {
        titleView.setText("购买课程");
        lesson = (Lesson) getIntent().getSerializableExtra("lesson");
    }

    @Override
    public void initListener() {
        buyButton.setOnClickListener(this);
    }

    @Override
    public void process(View v) {
        switch (v.getId()) {
            case R.id.buy_button : {
                payLesson();
            }
            break;
        }
    }
    private void payLesson() {
        final String name = nameEditText.getText().toString().trim();
        final String phone = phoneEditText.getText().toString().trim();
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone)) {
            return;
        }
//        String result = "姓名: " + nameEditText.getText().toString() + "   电话: "+phoneEditText.getText().toString();
//        Toast.makeText(this,result,Toast.LENGTH_SHORT).show();
        PayTool payTool = new PayTool(this, new PayTool.OnPayResultListener() {
            @Override
            public void paySuccess() {
                HashMap<String,String> params = new HashMap<>();
                params.put("name",name);
                params.put("phone",phone);
                params.put("course_type","1");
                NetworkTool.POST("http://121.201.7.33/zero/api/v1/buy_course", params, new OnNetworkResponser() {
                    @Override
                    public void onSuccess(String result) {
                        Toast.makeText(getBaseContext(),"购买课程成功!",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        Toast.makeText(getBaseContext(),"购买出错,请主动联系客服!",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void payFailure() {
                Toast.makeText(getBaseContext(),"支付失败",Toast.LENGTH_SHORT).show();
            }
        });
        //[支付]
        String body = lesson.getPrePrice() <= 100.0f ? "咨我课程-定金" : "咨我课程";
        PayTool.Product product = new PayTool.Product(body,lesson.getPrePrice());
//        PayTool.Product product = new PayTool.Product(body,0.01f);
        payTool.pay(product);
    }

}
