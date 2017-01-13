package cn.com.zhiwoo.activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import cn.com.zhiwoo.R;
import cn.com.zhiwoo.utils.LogUtils;

public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_launch);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                    Intent intent = new Intent(LaunchActivity.this,MainActivity.class);
                    Bundle bundle = getIntent().getBundleExtra("launchBundle");
                    if (bundle != null && bundle.getSerializable("article") != null) {
                        intent.putExtra("launchBundle",bundle);
                        LogUtils.log("启动界面,有数据!");
                    }
                    LogUtils.log("加载主界面");
                    startActivity(intent);
                    overridePendingTransition(0,0);
                LaunchActivity.this.finish();//这里将其finish,以免按返回的时候,回到这个界面
            }
        },1000);
    }
}
