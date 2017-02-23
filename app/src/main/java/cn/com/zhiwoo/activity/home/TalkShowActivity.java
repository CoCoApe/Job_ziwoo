package cn.com.zhiwoo.activity.home;


import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import cn.com.zhiwoo.R;
import cn.com.zhiwoo.activity.base.BaseActivity;

public class TalkShowActivity extends BaseActivity {


    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FragmentManager fm;
    private List<Fragment> fragments = new ArrayList<>();
    private List<String> tabs = new ArrayList<>();
    private LessonPagerAdapter adapter;

    @Override
    public void initView() {
        super.initView();
        fm = getSupportFragmentManager();
        View view = View.inflate(this, R.layout.home_talk_show, null);
        tabLayout = (TabLayout) view.findViewById(R.id.lesson_tab);
        viewPager = (ViewPager) view.findViewById(R.id.lesson_pager);
        flContent.addView(view);
    }

    @Override
    public void initData() {
        fragments.add(new AllLessonsFragment());
        fragments.add(new MyLessonsFragment());
        tabs.add("所有课程");
        tabs.add("我的课程");
        adapter = new LessonPagerAdapter(fm);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void initListener() {

    }

    @Override
    public void process(View v) {

    }

    class LessonPagerAdapter extends FragmentPagerAdapter {

        LessonPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments == null ? null : fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabs.get(position);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}


//public class TalkShowActivity extends BaseWebViewActivity {
////    private boolean mark = true;
//    @Override
//    public void initData() {
//        super.initData();
//        titleView.setText("语音直播");
//        webView.loadUrl("http://www.dian.fm/147124");
//    }
//
//    @Override
//    public void webViewLoadSuccess() {
//       new Handler().postDelayed(new Runnable() {
//           @Override
//           public void run() {
//               webView.loadUrl("javascript:var download = document.getElementById('js-channel-download');download.remove();");
//           }
//       },1000);
//    }
//}
