package cn.com.zhiwoo.activity.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import cn.com.zhiwoo.R;
import cn.com.zhiwoo.bean.main.Account;
import cn.com.zhiwoo.utils.Global;
import cn.com.zhiwoo.view.main.NoScrollViewPager;
import cn.smssdk.SMSSDK;
import de.greenrobot.event.EventBus;

/**
 * Created by 25820 on 2017/2/16.
 */

public class InfoPerfectActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView back_image;
    private TextView title_tv;
    private NoScrollViewPager viewPager;
    private Button skip_btn;
    private List<Fragment> fragments = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_perfect);
        EventBus.getDefault().register(this);
        SMSSDK.initSDK(getApplicationContext(), Global.SMS_APPKEY, Global.SMS_SECRET);
        initView();
        initData();
        initListener();
    }

    private void initView() {
        back_image = (ImageView) findViewById(R.id.info_perfect_back);
        title_tv = (TextView) findViewById(R.id.info_perfect_title);
        viewPager = (NoScrollViewPager) findViewById(R.id.info_perfect_viewPager);
        skip_btn = (Button) findViewById(R.id.info_perfect_skip);
    }
    private void initData() {
        Account account = (Account) getIntent().getSerializableExtra("account");
        FragmentManager fm = getSupportFragmentManager();
        fragments.add(PhoneBindFragment.newInstance(account));
        fragments.add(InfoCompleteFragment.newInstance());
        viewPager.setAdapter(new InfoPerfectAdapter(fm));
    }
    private void initListener() {
        back_image.setOnClickListener(this);
        skip_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        finish();
    }

    public class InfoPerfectAdapter extends FragmentPagerAdapter {
        InfoPerfectAdapter(FragmentManager fm) {
            super(fm);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(String str){
        if ("info_next".equals(str)){
            viewPager.setCurrentItem(1);
            title_tv.setText("2/2 完善个人信息");
        }else if ("info_done".equals(str)){
            finish();
        }
    }
}
