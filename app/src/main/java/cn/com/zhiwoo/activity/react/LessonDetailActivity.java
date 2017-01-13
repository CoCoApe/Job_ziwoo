package cn.com.zhiwoo.activity.react;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import cn.com.zhiwoo.R;
import cn.com.zhiwoo.activity.base.BaseActivity;
import cn.com.zhiwoo.activity.home.APPServiceActivity;
import cn.com.zhiwoo.activity.main.LoginActivity;
import cn.com.zhiwoo.bean.react.Lesson;
import cn.com.zhiwoo.bean.tutor.User;
import cn.com.zhiwoo.tool.AccountTool;
import cn.com.zhiwoo.tool.ChatTool;
import cn.com.zhiwoo.utils.LogUtils;
import io.rong.imlib.model.UserInfo;


public class LessonDetailActivity extends BaseActivity {
    private Lesson lesson;
    private WebView webView;

    private Button buyButton;

    private Button customerButton;
    private ProgressBar progressBar;
    private LinearLayout bottomLl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView() {
        super.initView();
        LinearLayout linearLayout = (LinearLayout) View.inflate(this, R.layout.react_lesson_detail, null);
        flContent.addView(linearLayout);
        bottomLl = (LinearLayout) linearLayout.findViewById(R.id.bottom_ll);
        webView = (WebView) linearLayout.findViewById(R.id.desc_webview);
        buyButton = (Button) linearLayout.findViewById(R.id.buy_button);
        customerButton = (Button) linearLayout.findViewById(R.id.customer_button);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
    }

    @Override
    public void initData() {
        lesson = (Lesson)getIntent().getSerializableExtra("lesson");
        LogUtils.log(this, lesson.getIntroUrl());
        titleView.setText(lesson.getTitle());
        if (lesson.getTourId()==null && lesson.getPrePrice() == 0.0f) {
            bottomLl.setVisibility(View.GONE);
        }
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progressBar.setProgress(newProgress);
                progressBar.setVisibility(newProgress >= 100 ? View.GONE : View.VISIBLE);
            }
        });
        webView.loadUrl(lesson.getIntroUrl());
        webView.getSettings().setJavaScriptEnabled(true);
    }

    @Override
    public void initListener() {
        buyButton.setOnClickListener(this);
        customerButton.setOnClickListener(this);
    }

    @Override
    public void process(View v) {
        switch (v.getId()) {
            case R.id.buy_button : {
                Intent intent = new Intent(this,BuyLessonActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("lesson",lesson);
                intent.putExtras(bundle);
                startActivity(intent);
            }
            break;
            case R.id.customer_button : {
                connect();
            }
            break;
        }
    }
    private void connect() {
        if (!AccountTool.isLogined(getBaseContext())) {
            Toast.makeText(getBaseContext(), "您还没有登录", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getBaseContext(), LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.main_login_show, 0);
            return;
        }
        ChatTool.sharedTool().getUserInfo(lesson.getTourId(), new ChatTool.loadUserInfoCallBack() {
            @Override
            public void onsuccess(UserInfo userInfo) {
                Intent intent = new Intent(getBaseContext(), APPServiceActivity.class);
                Bundle bundle = new Bundle();
                User user = new User();
                user.setId(userInfo.getUserId());
                user.setHeadImageUrl(userInfo.getPortraitUri().toString());
                user.setNickName(userInfo.getName());
                //[测试id]
//                user.setId("1652");
                bundle.putSerializable("user",user);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
}
