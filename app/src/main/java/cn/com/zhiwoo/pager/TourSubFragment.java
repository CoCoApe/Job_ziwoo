package cn.com.zhiwoo.pager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;

import cn.com.zhiwoo.R;
import cn.com.zhiwoo.activity.consult.ConsultChatActivity;
import cn.com.zhiwoo.activity.main.LoginActivity;
import cn.com.zhiwoo.activity.tutor.ReservationActivity;
import cn.com.zhiwoo.activity.tutor.TourDetailActivity;
import cn.com.zhiwoo.bean.tutor.Tour;
import cn.com.zhiwoo.tool.AccountTool;
import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;


@SuppressLint("ValidFragment")
public class TourSubFragment extends Fragment {
    private Tour tour;
    private ImageView icon;
    private TextView name;
    private TextView content;
    private ImageView ratingBar1;
    private ImageView ratingBar2;
    private ImageView ratingBar3;
    private ImageView ratingBar4;
    private Button consultBtn;
    private Button phoneBtn;
    private Activity mActivity;
    private Context mContext;
    private CircleImageView tutor_change;

    public static TourSubFragment newInstance(Tour tour,Activity mActivity) {
        TourSubFragment tourSubFragment = new TourSubFragment(mActivity);
        Bundle bundle=new Bundle();
        bundle.putSerializable("tour",tour);
        tourSubFragment.setArguments(bundle);
        return tourSubFragment;
    }

    public TourSubFragment(Activity mActivity) {
        this.mActivity = mActivity;
    }
    public TourSubFragment(){
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        Bundle arguments = getArguments();
        tour = (Tour) arguments.getSerializable("tour");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tour_sub_frg, container, false);
        initView(view);
        initListener();
        setData();
        return view;
    }

    private void initListener() {
        consultBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AccountTool.isLogined(mActivity)) {
                    Toast.makeText(mActivity,"您还没有登录",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(mActivity, LoginActivity.class);
                    mActivity.startActivity(intent);
                    mActivity.overridePendingTransition(R.anim.main_login_show, 0);
                    return;
                }
                if (AccountTool.getCurrentAccount(getActivity().getBaseContext()).isTour()) {
                    Toast.makeText(mActivity,"您已经是导师了,请不要调戏同行!",Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(mActivity, ConsultChatActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", tour);
                intent.putExtras(bundle);
                mActivity.startActivity(intent);
            }
        });
        phoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AccountTool.isLogined(getActivity())) {
                    Toast.makeText(getActivity(),"您还没有登录",Toast.LENGTH_SHORT).show();
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
                intent.putExtra("tourId",tour.getId());
                mActivity.startActivity(intent);
            }
        });
        tutor_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post("change");
            }
        });

        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity,TourDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("tour",tour);
                intent.putExtras(bundle);
                mActivity.startActivity(intent);
            }
        });

        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity,TourDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("tour",tour);
                intent.putExtras(bundle);
                mActivity.startActivity(intent);
            }
        });
    }

    private void setData() {
        name.setText(TextUtils.isEmpty(tour.getEnglishname())?tour.getNickName():tour.getNickName()+"("+tour.getEnglishname()+")");
        Tour.Points points = tour.getPoints();
        Glide.with(this)
                .load(tour.getHeadImageUrl())
                .thumbnail(0.2f)
                .override(200,200)
                .into(icon);
        content.setText(tour.getShort_intro());
        if (null != points){
            ratingBar1.setImageResource(getRatingRes(points.getRelationImpel()));
            ratingBar2.setImageResource(getRatingRes(points.getMatrimonyHold()));
            ratingBar3.setImageResource(getRatingRes(points.getDispenseSingle()));
            ratingBar4.setImageResource(getRatingRes(points.getRetrieveLover()));
        }
    }

    private void initView(View view) {
        icon = (ImageView) view.findViewById(R.id.tour_sub_icon);
        name = (TextView) view.findViewById(R.id.tour_sub_name);
        content = (TextView) view.findViewById(R.id.tour_sub_content);
        ratingBar1 = (ImageView) view.findViewById(R.id.ratingBar_1);
        ratingBar2 = (ImageView) view.findViewById(R.id.ratingBar_2);
        ratingBar3 = (ImageView) view.findViewById(R.id.ratingBar_3);
        ratingBar4 = (ImageView) view.findViewById(R.id.ratingBar_4);
        consultBtn = (Button) view.findViewById(R.id.tour_sub_consult_btn);
        phoneBtn = (Button) view.findViewById(R.id.tour_sub_phone_btn);
        tutor_change = (CircleImageView) view.findViewById(R.id.tutor_change);
    }

    private int getRatingRes(int rating){
        switch (rating){
            case 1:
                return R.drawable.new_star_1;
            case 2:
                return R.drawable.new_star_2;
            case 3:
                return R.drawable.new_star_3;
            case 4:
                return R.drawable.new_star_4;
            case 5:
                return R.drawable.new_star_5;
            default:
                return R.drawable.new_star_5;
        }
    }

}
