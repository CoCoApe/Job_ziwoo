package cn.com.zhiwoo.pager;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.demievil.library.RefreshLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.exception.HttpException;

import java.util.ArrayList;
import java.util.List;

import cn.com.zhiwoo.R;
import cn.com.zhiwoo.activity.consult.ConsultChatActivity;
import cn.com.zhiwoo.activity.main.LoginActivity;
import cn.com.zhiwoo.activity.tutor.ReservationActivity;
import cn.com.zhiwoo.activity.tutor.TourDetailActivity;
import cn.com.zhiwoo.adapter.tutor.TourAdapter;
import cn.com.zhiwoo.bean.tutor.Tour;
import cn.com.zhiwoo.pager.base.BasePager;
import cn.com.zhiwoo.tool.AccountTool;
import cn.com.zhiwoo.tool.NetworkTool;
import cn.com.zhiwoo.tool.OnNetworkResponser;
import cn.com.zhiwoo.utils.LogUtils;


public class TourPager extends BasePager {

    private ListView listView;
    private ArrayList<Tour> tours;
    private RefreshLayout refreshLayout;
    private SVProgressHUD mHud = new SVProgressHUD(mActivity);
    private ViewPager mViewPager;
    private List<TourSubFragment> fragments = new ArrayList<>();
    private FragmentManager fm ;
    private ImageView pageLeft;
    private ImageView pageRight;


    public TourPager(Activity activity) {
        super(activity);
        fm = activity.getFragmentManager();
    }

    @Override
    public void onDeselected() {

    }

    @Override
    public void initView() {
        super.initView();
        RelativeLayout relativeLayout = (RelativeLayout) View.inflate(mActivity, R.layout.tour_content,null);
        flContent.addView(relativeLayout);
        mDrawerLayout.removeView(drawerView);
        mViewPager = (ViewPager) relativeLayout.findViewById(R.id.tour_sub_viewPager);
        refreshLayout = (RefreshLayout) relativeLayout.findViewById(R.id.refresh_layout);
        listView = (ListView) relativeLayout.findViewById(R.id.listview);
        pageLeft = (ImageView) relativeLayout.findViewById(R.id.page_left);
        pageRight = (ImageView) relativeLayout.findViewById(R.id.page_right);
        refreshLayout.setChildView(listView);
        refreshLayout.setColorSchemeResources(R.color.globalBgColor,
                R.color.globalBgColor,
                R.color.red,
                R.color.green,
                R.color.blue,
                R.color.yellow,
                R.color.fuchsia,
                R.color.navy);
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void initData() {
        titleView.setText("导师");
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });
        listView.setOnItemClickListener(new OnTourItemClickListener());
        loadData();
    }
    private void loadData() {
        mHud.showWithStatus("正在叫导师过来...", SVProgressHUD.SVProgressHUDMaskType.Clear);
        NetworkTool.GET("http://121.201.7.33/zero/api/tutors", null, new OnNetworkResponser() {
            @Override
            public void onSuccess(String result) {
                result = result.replace("关系推进", "relationImpel");
                result = result.replace("失恋挽回", "retrieveLover");
                result = result.replace("婚姻维系", "matrimonyHold");
                result = result.replace("摆脱单身", "dispenseSingle");
                Gson gson = new Gson();
                tours = gson.fromJson(result, (new TypeToken<List<Tour>>() {
                }).getType());
                initSubPagers(tours);
                refreshLayout.setRefreshing(false);
                listView.setAdapter(new TourAdapter(mActivity, tours, new OnTourConsultButtonClickListener()));
                mViewPager.setAdapter(new TourSubPagerAdapter(fm));
                mHud.dismiss();
            }

            @Override
            public void onFailure(HttpException e, String s) {
                refreshLayout.setRefreshing(false);
                mHud.showErrorWithStatus("网络不佳,请稍后再试");
            }
        });
    }

    private void initSubPagers(ArrayList<Tour> list) {
        if (null != list){
            for (int i = 0;i<list.size();i++){
                fragments.add(TourSubFragment.newInstance(list.get(i),mActivity));
            }
        }
    }

    @Override
    public void initTitleBar() {
        leftImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mViewPager.getVisibility()) {
                    case View.VISIBLE:
                        mViewPager.setVisibility(View.GONE);
                        pageLeft.setVisibility(View.GONE);
                        pageRight.setVisibility(View.GONE);
                        break;
                    case View.GONE:
                        mViewPager.setVisibility(View.VISIBLE);
                        pageLeft.setVisibility(View.VISIBLE);
                        pageRight.setVisibility(View.VISIBLE);
                        break;
                    case View.INVISIBLE:
                        break;
                }
            }
        });
    }

    @Override
    public void process(View v) {

    }

    @Override
    public void titleBarLeftBtnClick() {

    }

    @Override
    public void titleBarRightBtnClick() {

    }

    @Override
    public void onResume() {

    }

    private class OnTourConsultButtonClickListener implements TourAdapter.OnConsultButtonClickListener {

        @Override
        public void onQuickConsultButtonClick(int position) {
            if (!AccountTool.isLogined(mActivity)) {
                Toast.makeText(mActivity,"您还没有登录",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mActivity, LoginActivity.class);
                mActivity.startActivity(intent);
                mActivity.overridePendingTransition(R.anim.main_login_show, 0);
                return;
            }
            if (AccountTool.getCurrentAccount(mActivity.getBaseContext()).isTour()) {
                Toast.makeText(mActivity,"您已经是导师了,请不要调戏同行!",Toast.LENGTH_SHORT).show();
                return;
            }
            Tour tour = tours.get(position);
            //[测试id]
//            tour.setId("1652");
            Intent intent = new Intent(mActivity, ConsultChatActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("user", tour);
            intent.putExtras(bundle);
            mActivity.startActivity(intent);
        }

        @Override
        public void onPhoneConsultButtonClick(int position) {
            if (!AccountTool.isLogined(mActivity)) {
                Toast.makeText(mActivity,"您还没有登录",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mActivity, LoginActivity.class);
                mActivity.startActivity(intent);
                mActivity.overridePendingTransition(R.anim.main_login_show, 0);
                return;
            }
            if (AccountTool.getCurrentAccount(mActivity.getBaseContext()).isTour()) {
                Toast.makeText(mActivity,"您已经是导师了,请不要调戏同行!",Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(mActivity, ReservationActivity.class);
            Tour tour = tours.get(position);
            intent.putExtra("tourId",tour.getId());
            mActivity.startActivity(intent);
        }
    }
    private class OnTourItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            LogUtils.log(mActivity, "点击了导师: " + tours.get(position).getNickName());
            Intent intent = new Intent(mActivity,TourDetailActivity.class);
            Bundle bundle = new Bundle();
            Tour tour = tours.get(position);
            bundle.putSerializable("tour",tour);
            intent.putExtras(bundle);
            mActivity.startActivity(intent);
        }
    }

    private class TourSubPagerAdapter extends FragmentPagerAdapter {

        TourSubPagerAdapter(FragmentManager fm) {
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
    }
}