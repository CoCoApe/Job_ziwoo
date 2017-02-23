package cn.com.zhiwoo.activity.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

import cn.com.zhiwoo.R;
import cn.com.zhiwoo.activity.react.ArticleDetailActivity;
import cn.com.zhiwoo.bean.react.ReactArticle;
import cn.com.zhiwoo.pager.ConsultPager;
import cn.com.zhiwoo.pager.CoursePager;
import cn.com.zhiwoo.pager.HomePager;
import cn.com.zhiwoo.pager.ReactPager;
import cn.com.zhiwoo.pager.TourPager;
import cn.com.zhiwoo.pager.base.BasePager;
import cn.com.zhiwoo.tool.AccountTool;
import cn.com.zhiwoo.tool.ChatTool;
import cn.com.zhiwoo.tool.PushTool;
import cn.com.zhiwoo.utils.LogUtils;
import cn.jpush.android.api.JPushInterface;
import io.rong.imkit.RongIM;


/**
 * 在主要的activity中配置好chattool,accounttool.在重新创建mainactivity的时候,重新配置
 */


public class MainActivity extends AppCompatActivity{
    public static boolean isAlive = false;
    private long exitTime = 0;
    private MyBroadCastReceiver broadcastReceiver;
    private BasePager selectedPager;
    private ViewPager viewPager;
    private ArrayList<BasePager> mPagers = new ArrayList<>();
    private RadioGroup radioGroup;
    private ReactPager reactPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        APP app = (APP) getApplication();
        app.setMainActivity(this);
        //如果当前有保存的账号,就配置聊天工具
        LogUtils.log("创建mainactivity -----");
        if (AccountTool.isLogined(this)) {
            LogUtils.log("根据当前账号,配置好聊天工具");
            ChatTool.config(this);
        }
        initView();
        initData();
        isAlive = true;
    }

    public void initView() {
        setContentView(R.layout.main_acvity_main);
        viewPager = (ViewPager) findViewById(R.id.viewpager_main);
        radioGroup = (RadioGroup) findViewById(R.id.tabbar_main);
    }
    public void initData() {
        if (AccountTool.isLogined(this)) {
            LogUtils.log(this,"有账号登录过");
            //登录融云
            ChatTool.sharedTool().login();
        } else {
            LogUtils.log(this,"没有账号登录过");
        }
        PushTool.config(this);
        ConsultPager consultPager = new ConsultPager(this);
        mPagers.add(consultPager);
        reactPager = new ReactPager(this);
        mPagers.add(reactPager);
        //add 2.13 新加入节目模块
        CoursePager coursePager = new CoursePager(this);
        mPagers.add(coursePager);

        TourPager tourPager = new TourPager(this);
        mPagers.add(tourPager);
        HomePager homePager = new HomePager(this);
        mPagers.add(homePager);

        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return mPagers.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return object == view;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                BasePager pager = mPagers.get(position);
                container.addView(pager.mRootView);// 将页面布局添加到容器中
                return pager.mRootView;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (MainActivity.this.selectedPager != null) {
                    MainActivity.this.selectedPager.onDeselected();
                }
                switch (checkedId) {
                    case R.id.tabbar_item_consult: {
                        setPager(0);
                    }
                    break;

                    case R.id.tabbar_item_react: {
                        setPager(1);
                        reactPager.reactImage.setVisibility(View.VISIBLE);
                    }
                    break;

                    case R.id.tabbar_item_course: {
                        setPager(2);
                    }
                    break;

                    case R.id.tabbar_item_tour: {
                        setPager(3);
                    }
                    break;

                    case R.id.tabbar_item_home: {
                        setPager(4);
                    }
                    break;
                    default:
                        break;
                }
            }
        });

        //初始化默认选中的pager的内容
        mPagers.get(0).onSelected();
        setSelectedPager(mPagers.get(0));
        // 动态注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction("message_change");
        broadcastReceiver = new MyBroadCastReceiver();
        registerReceiver(broadcastReceiver, filter);

        Bundle bundle = getIntent().getBundleExtra("launchBundle");
        if (bundle != null) {
            ReactArticle article = (ReactArticle) bundle.getSerializable("article");
            if (article == null) return;
            LogUtils.log("展示文章, 链接 : " + article.getQ().get(0).getContent_url());
            Intent intent = new Intent(getBaseContext(), ArticleDetailActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtils.log("onNewIntent ==");
        finish();
        Intent intent1 = new Intent(getBaseContext(),LaunchActivity.class);
        startActivity(intent1);
        overridePendingTransition(0,0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
        LogUtils.log("主界面 销毁!!!!!");
        RongIM.getInstance().disconnect();
        isAlive = false;
        for (BasePager pager : mPagers) {
            pager.onDestroy();
        }
    }



    private class MyBroadCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int count = ChatTool.sharedTool().getTipMessages().size();
            LogUtils.log("收到了广播 : "+count);
            for (BasePager pager : mPagers) {
                pager.receiverMessageBroadcast(count);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - exitTime > 2000) {
            Toast.makeText(this,"再按一次 退出咨我",Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            if (null != RongIM.getInstance() && null != RongIM.getInstance().getRongIMClient()){
                RongIM.getInstance().getRongIMClient().disconnect();
            }
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        BasePager basePager = mPagers.get(viewPager.getCurrentItem());
        basePager.onResume();
        JPushInterface.onResume(this);
        MobclickAgent.onResume(getBaseContext());
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
        MobclickAgent.onPause(getBaseContext());
    }


    public void setSelectedPager(BasePager selectedPager) {
        this.selectedPager = selectedPager;
    }

    private void setPager(int position){
        viewPager.setCurrentItem(position, false);
        setSelectedPager(mPagers.get(position));
        mPagers.get(position).onSelected();
    }
}
