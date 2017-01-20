package cn.com.zhiwoo.pager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import cn.com.zhiwoo.R;
import cn.com.zhiwoo.activity.consult.ConsultChatActivity;
import cn.com.zhiwoo.activity.main.LoginActivity;
import cn.com.zhiwoo.activity.react.LessonDetailActivity;
import cn.com.zhiwoo.bean.consult.Banner;
import cn.com.zhiwoo.bean.consult.ConsultTypeMode;
import cn.com.zhiwoo.bean.react.Lesson;
import cn.com.zhiwoo.bean.tutor.Tour;
import cn.com.zhiwoo.pager.base.BasePager;
import cn.com.zhiwoo.tool.AccountTool;
import cn.com.zhiwoo.utils.Api;
import cn.com.zhiwoo.utils.LogUtils;
import cn.com.zhiwoo.view.consult.SlideCutListView;
import okhttp3.Call;
import okhttp3.Response;

//import cn.com.zhiwoo.activity.react.LessonDetailActivity;


public class ConsultPager extends BasePager{

    private SlideCutListView listView;
    private ConvenientBanner banner;
//    private ViewPager viewPager;
    private ArrayList<ConsultTypeMode> modes;
    private SVProgressHUD mHud = new SVProgressHUD(mActivity);
    private List<Banner.DataBean> bannerList = new ArrayList<>();
//    private ArrayList<LessonImageView> lessonImageViews = new ArrayList<>();
//    private ArrayList<Lesson> lessons = Lesson.allLessons();
    private Lesson lesson;
    private static int Msg_Update_Count = 111;
    private ConsultTypesAdapter consultTypesAdapter;
    private Handler handler = new Handler(new Handler.Callback()
    {
        public boolean handleMessage(Message paramMessage)
        {
            if (paramMessage.what == Msg_Update_Count) {
                ConsultPager.this.listView.setAdapter(new ConsultTypesAdapter());
            }
            return false;
        }

    });


    public ConsultPager(Activity activity) {
        super(activity);
    }

    @Override
    public void initView() {
        super.initView();
        LinearLayout linearLayout = (LinearLayout) View.inflate(mActivity, R.layout.consult_content,null);
        flContent.addView(linearLayout);
        mDrawerLayout.removeView(drawerView);
        listView = (SlideCutListView) linearLayout.findViewById(R.id.listview);
        modes = ConsultTypeMode.allConsultTypeModes();
        lesson = new Lesson();
        View headerView =  View.inflate(mActivity,R.layout.consult_head_view,null);
        banner = (ConvenientBanner) headerView.findViewById(R.id.consult_header_banner);
        listView.addHeaderView(headerView);
        consultTypesAdapter = new ConsultTypesAdapter();
        listView.setAdapter(consultTypesAdapter);
        listView.setRemoveListener(new SlideCutListView.RemoveListener() {
            @Override
            public void removeItem(SlideCutListView.RemoveDirection direction, int position) {
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
                ConsultTypeMode mode = (ConsultTypeMode) consultTypesAdapter.getItem(position);
                if (mode == null) {
                    return;
                }
                int modeValue = mode.getMode();
                HashMap<String,String> params = new HashMap<>();
                params.put("access_token", AccountTool.getCurrentAccount(mActivity.getBaseContext()).getAccessToken());
                params.put("module",modeValue + "");
                mHud.showWithStatus("正在连接咨询师...", SVProgressHUD.SVProgressHUDMaskType.Clear);
                OkGo.get(Api.CONNECT_TUTOR)
                        .params(params)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(String s, Call call, Response response) {
                                Gson gson = new Gson();
                                Tour tour = gson.fromJson(s,Tour.class);
                                LogUtils.log(mActivity.getBaseContext(),"名称: " + tour.getNickName() + ", 头像 : " + tour.getHeadImageUrl());
                                Intent intent = new Intent(mActivity.getBaseContext(), ConsultChatActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("user",tour);
                                intent.putExtras(bundle);
                                mActivity.startActivity(intent);
                                mHud.dismiss();
                            }
                            @Override
                            public void onError(Call call, Response response, Exception e) {
                                mHud.showErrorWithStatus("网络不佳,稍后再试!");
                            }
                        });
            }
        });
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public void initData() {
        titleView.setText("咨询");
//        initImageLoader();
        loadLessonImageViews();
//        viewPager.setAdapter(new HeaderPagerAdapter());
//        CirclePageIndicator indicator = (CirclePageIndicator) listView.findViewById(R.id.header_indicator);
//        indicator.setViewPager(viewPager);

    }

//    private void initImageLoader() {
//        bitmapUtils = new BitmapUtils(mActivity);
//        bitmapUtils.configDefaultLoadingImage(R.drawable.react_article_bg_placeholer);
//    }

    private void loadLessonImageViews() {
        OkGo.post(Api.BANNER)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        Banner bean = new Gson().fromJson(s,Banner.class);
                        if (null != bean){
                            bannerList.clear();
                            bannerList.addAll(bean.getData());
                        }
                        banner.setPages(new CBViewHolderCreator<BannerImageHolderView>() {
                            @Override
                            public BannerImageHolderView createHolder() {
                                return new BannerImageHolderView();
                            }
                        },bannerList)
                            .setPageIndicator(new int[]{R.drawable.dot_unselect, R.drawable.dot_select})
                            .startTurning(5000);
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
        updateCounts();
    }


    @Override
    public void initTitleBar() {
        leftImageView.setVisibility(View.INVISIBLE);
    }
    private void updateCounts()
    {
        new Thread(new Runnable()
        {
            public void run()
            {
                if (ConsultPager.this.modes != null)
                {
                    Date localDate1 = new GregorianCalendar(2016, 4, 1).getTime();
                    Date localDate2 = new Date();
                    float f = (float)(localDate2.getTime() - localDate1.getTime()) / 1000.0F / 24.0F / 60.0F / 60.0F;
                    LogUtils.log("start : " + localDate1 + " now : " + localDate2 + " days : " + f);
                    int[] arrayOfInt = { 56642 + (int)(196.0F * f), 26889 + (int)(93.0F * f), 52521 + (int)(181.0F * f), 36965 + (int)(127.0F * f) };
                    for (int i = 0; i < ConsultPager.this.modes.size(); i++)
                        ConsultPager.this.modes.get(i).setNum(arrayOfInt[i]);
                    ConsultPager.this.handler.sendEmptyMessage(ConsultPager.Msg_Update_Count);
                }
            }
        }).run();
    }



    private class ConsultTypesAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return modes != null ? modes.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return modes.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(mActivity,R.layout.consult_type_item,null);
            }
            ImageView imageView = (ImageView) convertView.findViewById(R.id.mode_image);
            ConsultTypeMode mode = modes.get(position);
            imageView.setImageResource(mode.getImageName());

            //屏蔽icon+数字+dot，改为全图
//            ImageView imageView = (ImageView) convertView.findViewById(R.id.mode_imageview);
//            TextView titleView = (TextView) convertView.findViewById(R.id.mode_title_textview);
//            TextView numView = (TextView) convertView.findViewById(R.id.mode_num_textview);
//            ConsultTypeMode mode = modes.get(position);
//            imageView.setImageResource(mode.getImageName());
//            titleView.setText(mode.getTitle());
//            SpannableString spanString = new SpannableString("已有 " + mode.getNum()+ " 人提问");
//            AbsoluteSizeSpan sizeSpan = new AbsoluteSizeSpan(55);
//            spanString.setSpan(sizeSpan, 3, (mode.getNum() + "").length() + 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.BLACK);
//            spanString.setSpan(colorSpan, 3, (mode.getNum() + "").length() + 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            StyleSpan styleSpan = new StyleSpan(Typeface.BOLD_ITALIC);
//            spanString.setSpan(styleSpan, 3, (mode.getNum() + "").length() + 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            numView.setText(spanString);
            return convertView;
        }
    }

    @Override
    public void onSelected() {
        super.onSelected();
//        if (this.handler != null) {
//            this.handler.sendEmptyMessage(Msg_Update_Image);
//            LogUtils.log("选中咨询");
//        }
    }
    @Override
    public void onDeselected() {
        super.onSelected();
//        if (this.handler != null) {
//            this.handler.removeCallbacksAndMessages(null);
//            LogUtils.log("取消选中咨询");
//        }
    }
//    private class HeaderPagerAdapter extends PagerAdapter {
//        HeaderPagerAdapter() {
//            super();
//        }
//        @Override
//        public int getCount() {
//            return lessons.size();
//        }
//
//        @Override
//        public boolean isViewFromObject(View view, Object object) {
//            return view == object;
//        }
//        @Override
//        public Object instantiateItem(ViewGroup container, int position) {
//            LessonImageView imageView = lessonImageViews.get(position);
//            container.addView(imageView);
//            imageView.displayImage();
//            imageView.setOnClickListener(new LessonOnClickListener(position));
//            return imageView;
//        }
//        @Override
//        public void destroyItem(ViewGroup container, int position, Object object) {
//            container.removeView((View) object);
//        }
//    }
//    private class LessonOnClickListener implements View.OnClickListener {
//        private int position;
//        LessonOnClickListener(int position) {
//            this.position = position;
//        }
//        @Override
//        public void onClick(View v) {
//            LogUtils.log(mActivity,"onClick");
//            Lesson lesson = lessons.get(position);
//            Intent intent = new Intent(mActivity, LessonDetailActivity.class);
//            Bundle bundle = new Bundle();
//            bundle.putSerializable("lesson",lesson);
//            intent.putExtras(bundle);
//            mActivity.startActivity(intent);
//        }
//    }

    private class BannerImageHolderView implements Holder<Banner.DataBean> {
        private ImageView imageView;
        @Override
        public View createView(Context context) {
            imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            return imageView;
        }

        @Override
        public void UpdateUI(Context context, final int position, Banner.DataBean data) {
            Glide.with(context)
                    .load(data.getApp_pic())
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .placeholder(R.drawable.react_article_bg_placeholer)
                    .into(imageView);
//            bitmapUtils.display(imageView,data.getApp_pic());
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lesson.setIntroUrl(bannerList.get(position).getApp_url());
                    Intent intent = new Intent(mActivity, LessonDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("lesson",lesson);
                    intent.putExtras(bundle);
                    mActivity.startActivity(intent);
                }
            });

        }
    }

}
