package cn.com.zhiwoo.activity.course;


import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.com.zhiwoo.R;
import cn.com.zhiwoo.activity.base.BaseActivity;
import cn.com.zhiwoo.service.MediaService;
import cn.com.zhiwoo.service.OnMediaChangeListener;
import cn.com.zhiwoo.view.custom.RoundRectImageView;

/**
 * Created by 25820 on 2017/2/13.
 */

public class CourseSeriesActivity extends BaseActivity implements OnMediaChangeListener {
    private TextView series_name;
    private TextView tutor_name;
    private TextView played_counts;
    private TextView update_time;
    private TextView price_number;
    private RoundRectImageView series_image;
    private Button buy_btn;
    private LinearLayout bottom_buy;
    private TextView bottom_price;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private List<String> tabs = new ArrayList<>();
    private List<Fragment> fragments = new ArrayList<>();
    private CourseSeriesAdapter adapter;
    private MediaService.MediaBinder mMediaBinder;
    private final String MEDIA_BROADCAST_ACTION = "cn.com.zhiwoo.activity.course";

    @Override
    public void initView() {
        super.initView();
        View view = View.inflate(this, R.layout.activity_course_series,null);
        series_name = (TextView) view.findViewById(R.id.course_series_name);
        tutor_name = (TextView) view.findViewById(R.id.course_series_tutor_name);
        played_counts = (TextView) view.findViewById(R.id.course_series_played_counts);
        update_time = (TextView) view.findViewById(R.id.course_series_update_time);
        price_number = (TextView) view.findViewById(R.id.course_series_price_number);
        bottom_price = (TextView) view.findViewById(R.id.course_series_bottom_price);
        series_image = (RoundRectImageView) view.findViewById(R.id.course_series_image);
        buy_btn = (Button) view.findViewById(R.id.course_series_buy_now);
        bottom_buy = (LinearLayout) view.findViewById(R.id.course_series_bottom_buy);
        tabLayout = (TabLayout) view.findViewById(R.id.course_series_tabLayout);
        viewPager = (ViewPager) view.findViewById(R.id.course_series_viewPager);
        flContent.addView(view);
        connect();
        initAdapter();
    }

    private void initAdapter() {
        FragmentManager fm = getSupportFragmentManager();
        tabs.add("详情");
        tabs.add("节目");
        fragments.add(new CourseDetailsFragment());
        fragments.add(new CourseListFragment());
        adapter = new CourseSeriesAdapter(fm);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void initData() {

    }

    @Override
    public void initListener() {

    }

    @Override
    public void process(View v) {

    }

    @Override
    public void onMediaPlay() {
        rightImageView.setImageResource(R.drawable.nav_back2);
        rightImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CourseSeriesActivity.this,MediaActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onMediaStart() {

    }

    @Override
    public void onMediaPause() {
        rightImageView.setImageResource(R.drawable.nav_back2);
    }

    @Override
    public void onMediaStop() {
        rightImageView.setImageResource(R.drawable.navbar_message_selector);
        rightImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titleBarRightBtnClick();
            }
        });
    }

    @Override
    public void onMediaCompletion() {
        rightImageView.setImageResource(R.drawable.navbar_message_selector);
        rightImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titleBarRightBtnClick();
            }
        });
    }

    private class CourseSeriesAdapter extends FragmentPagerAdapter {

        public CourseSeriesAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments == null ? 0 : fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabs.get(position);
        }
    }

    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMediaBinder = (MediaService.MediaBinder) service;
            mMediaBinder.addOnMediaChangeListener(CourseSeriesActivity.this);
            changeButton();
        }
    };



    /** 绑定服务*/
    private void connect() {
        Intent intent = new Intent(this, MediaService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }
    /** 断开服务*/
    private void disconnect() {
        unbindService(mConnection);
    }


    private void changeButton() {
        if(mMediaBinder == null) {
            return;
        }
        if(mMediaBinder.isPlaying()) {
            onMediaPlay();
        } else {
            onMediaStop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disconnect();
    }
}
