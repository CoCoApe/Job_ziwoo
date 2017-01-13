package cn.com.zhiwoo.activity.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;

import cn.com.zhiwoo.R;
import cn.com.zhiwoo.activity.base.BaseActivity;
import cn.com.zhiwoo.utils.LogUtils;


public class MessageCenterActivity extends BaseActivity {

    private ViewPager viewPager;
    private Button allmessageButton;
    private Button unreadmessageButton;
    private AllMessageFragment allmessageFragment;
    private UnreadMessageFragment unreadmessageFragment;
    private MyBroadCastRecevier broadcastReceiver;

    @Override
    public void initView() {
        super.initView();
        LinearLayout linearLayout = (LinearLayout) View.inflate(this, R.layout.home_messagecenter_activity, null);
        flContent.addView(linearLayout);
        viewPager = (ViewPager) linearLayout.findViewById(R.id.viewpager);
        allmessageButton = (Button) linearLayout.findViewById(R.id.allmessage_button);
        unreadmessageButton = (Button) linearLayout.findViewById(R.id.unreadmessage_button);
    }

    @Override
    public void initData() {
        titleView.setText("消息中心");
        FragmentManager fm = getSupportFragmentManager();
        final ArrayList<Fragment> fragments = new ArrayList<>();
        allmessageFragment = new AllMessageFragment();
        unreadmessageFragment = new UnreadMessageFragment();
        fragments.add(allmessageFragment);
        fragments.add(unreadmessageFragment);
        viewPager.setAdapter(new MessageCenterFragmentAdapter(fm, fragments));
        //开始监听消息改变的通知
        IntentFilter filter = new IntentFilter();
        filter.addAction("message_change");
        broadcastReceiver = new MyBroadCastRecevier();
        registerReceiver(broadcastReceiver, filter);
    }

    @Override
    public void initListener() {
        allmessageButton.setOnClickListener(this);
        unreadmessageButton.setOnClickListener(this);
    }

    @Override
    public void process(View v) {
        switch (v.getId()) {
            case R.id.allmessage_button: {
                viewPager.setCurrentItem(0,false);//不要滚动效果
                allmessageButton.setBackgroundResource(R.color.messagecenterbuttonbg);
                allmessageButton.setTextColor(Color.WHITE);
                unreadmessageButton.setBackgroundColor(Color.WHITE);
                unreadmessageButton.setTextColor(Color.BLACK);
            }
            break;
            case R.id.unreadmessage_button: {
                viewPager.setCurrentItem(1,false);//不要滚动效果
                unreadmessageButton.setBackgroundResource(R.color.messagecenterbuttonbg);
                unreadmessageButton.setTextColor(Color.WHITE);
                allmessageButton.setBackgroundColor(Color.WHITE);
                allmessageButton.setTextColor(Color.BLACK);
            }
            break;
        }
    }

    public class MessageCenterFragmentAdapter extends FragmentPagerAdapter {
        private ArrayList<Fragment> fragments;

        MessageCenterFragmentAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }
    private class MyBroadCastRecevier extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtils.log("收到了新消息,消息中心要刷新数据");
            allmessageFragment.refreshData();
            unreadmessageFragment.refreshData();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
}
