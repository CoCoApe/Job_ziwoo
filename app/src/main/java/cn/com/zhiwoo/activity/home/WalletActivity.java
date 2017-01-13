package cn.com.zhiwoo.activity.home;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.lidroid.xutils.exception.HttpException;

import java.util.HashMap;

import cn.com.zhiwoo.R;
import cn.com.zhiwoo.activity.base.BaseActivity;
import cn.com.zhiwoo.activity.react.ArticleDetailActivity;
import cn.com.zhiwoo.bean.main.Account;
import cn.com.zhiwoo.bean.react.ReactArticle;
import cn.com.zhiwoo.tool.AccountTool;
import cn.com.zhiwoo.tool.NetworkTool;
import cn.com.zhiwoo.tool.OnNetworkResponser;


public class WalletActivity extends BaseActivity {

    private View normalView;
    private View tourView;
    private EditText nameEditText;
    private EditText amountEditText;
    private EditText payaccountEditText;
    private EditText phoneEditText;
    private SVProgressHUD progressHUD;

    @Override
    public void initView() {
        super.initView();
        LinearLayout linearLayout = (LinearLayout) View.inflate(this, R.layout.home_wallet_activity,null);
        flContent.addView(linearLayout);
        normalView = linearLayout.findViewById(R.id.normal_view);
        tourView = linearLayout.findViewById(R.id.tour_view);
        nameEditText = (EditText) linearLayout.findViewById(R.id.name_edittext);
        amountEditText = (EditText) linearLayout.findViewById(R.id.amount_edittext);
        payaccountEditText = (EditText) linearLayout.findViewById(R.id.payaccount_edittext);
        phoneEditText = (EditText) linearLayout.findViewById(R.id.phone_edittext);
        linearLayout.findViewById(R.id.commit_button).setOnClickListener(this);
        progressHUD = new SVProgressHUD(this);
    }

    @Override
    public void initData() {
        titleView.setText("我的钱包");
//        boolean isTour = Math.random() >= 0.5 ? true : false;
        boolean isTour = AccountTool.getCurrentAccount(getBaseContext()).isTour();
        tourView.setVisibility(isTour ? View.VISIBLE : View.GONE);
        normalView.setVisibility(isTour ? View.GONE : View.VISIBLE);
    }

    @Override
    public void initListener() {
        normalView.findViewById(R.id.tobetour_button).setOnClickListener(this);
    }

    @Override
    public void process(View v) {
        switch (v.getId()) {
            case R.id.tobetour_button : {
                Intent intent = new Intent(this, ArticleDetailActivity.class);
                Bundle bundle = new Bundle();
                ReactArticle.QBean article = new ReactArticle.QBean();
                article.setContent_url("http://mp.weixin.qq.com/s?__biz=MzAwNzYzMTkwNQ==&mid=213697402&idx=2&sn=be6a355969da3383952cfa06296a00e9&scene=0#wechat_redirect");
                article.setTitle("如何成为咨询师");
                bundle.putSerializable("article", article);
                intent.putExtras(bundle);
                startActivity(intent);
            }
            break;
            case R.id.commit_button : {
                String name = nameEditText.getText().toString();
                String amount = amountEditText.getText().toString();
                String payAccount = payaccountEditText.getText().toString();
                String phone = phoneEditText.getText().toString();
                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(amount) || TextUtils.isEmpty(payAccount) || TextUtils.isEmpty(phone)) {
                    Toast.makeText(getBaseContext(),"请填写完整的信息",Toast.LENGTH_SHORT).show();
                    return;
                }
                HashMap<String,String> params = new HashMap<>();
                Account account = AccountTool.getCurrentAccount(getBaseContext());
                params.put("access_token",account.getAccessToken());
                params.put("user_id",account.getId());
                params.put("name",name);
                params.put("pay_account",payAccount);
                params.put("contact",phone);
                params.put("amount",(int)(Float.parseFloat(amount) * 100) + "");//单位为分
                NetworkTool.POST("http://121.201.7.33/zero/api/v1/commission_requests", params, new OnNetworkResponser() {
                    @Override
                    public void onSuccess(String result) {
                        progressHUD.showSuccessWithStatus("提交成功");
                        finish();
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        progressHUD.showErrorWithStatus("提交失败");
                    }
                });
            }
            break;
        }
    }
}
