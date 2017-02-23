package cn.com.zhiwoo.activity.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import cn.com.zhiwoo.R;
import cn.com.zhiwoo.tool.AccountTool;
import cn.com.zhiwoo.tool.ChatTool;
import cn.com.zhiwoo.utils.LogUtils;
import cn.com.zhiwoo.view.main.MessageTipPopup;


public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    public TextView titleView;
    public ImageView titleImage;
    public ImageView leftImageView;
    public ImageView rightImageView;
    public FrameLayout flContent;
    private MyBroadCastReceiver broadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initListener();
    }
    public void initView() {
        setContentView(R.layout.base_activity);
        titleView = (TextView) findViewById(R.id.tv_title);
        titleImage = (ImageView) findViewById(R.id.topBar_middle_image);
        leftImageView = (ImageView) findViewById(R.id.topBar_left_image);
        rightImageView = (ImageView) findViewById(R.id.topBar_right_image);
        leftImageView.setOnClickListener(this);
        rightImageView.setOnClickListener(this);
        flContent = (FrameLayout) findViewById(R.id.base_activity_content_fl);
        leftImageView.setImageResource(R.drawable.nav_back2);
        if (AccountTool.isLogined(this)) {
            rightImageView.setSelected(ChatTool.sharedTool().getTipMessages().size() > 0);
        } else {
            rightImageView.setSelected(false);
        }

        // 动态注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction("message_change");
        broadcastReceiver = new MyBroadCastReceiver();
        registerReceiver(broadcastReceiver, filter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.topBar_left_image : {
                titleBarLeftBtnClick();
            }
            break;
            case R.id.topBar_right_image : {
                titleBarRightBtnClick();
            }
            break;
            default: {
                process(v);
            }
            break;
        }
    }
    public void titleBarLeftBtnClick() {
        finish();
    }
    public void titleBarRightBtnClick() {
        MessageTipPopup.showMessageTipPopup(getApplicationContext(),rightImageView);
        BaseAdapter adapter = (BaseAdapter) MessageTipPopup.newInstance(this).mListView.getAdapter();
        adapter.notifyDataSetChanged();
    }

    public class MyBroadCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int count = ChatTool.sharedTool().getTipMessages().size();
            LogUtils.log("进入新控制器-查询到的未读消息数 : " + count);
            rightImageView.setSelected(count>0);

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    public abstract void initData();
    public abstract void initListener();
    public abstract void process(View v);

}
