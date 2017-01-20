package cn.com.zhiwoo.activity.tutor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import cn.com.zhiwoo.R;
import cn.com.zhiwoo.activity.base.BaseActivity;
import cn.com.zhiwoo.activity.consult.ConsultChatActivity;
import cn.com.zhiwoo.activity.main.LoginActivity;
import cn.com.zhiwoo.bean.main.Account;
import cn.com.zhiwoo.bean.tutor.Comments;
import cn.com.zhiwoo.bean.tutor.Tour;
import cn.com.zhiwoo.tool.AccountTool;
import cn.com.zhiwoo.utils.Api;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Response;


public class TourDetailActivity extends BaseActivity {

    private ImageView iconImageView;
    private TextView nameTextView;
    private View dividerView;
    private ImageView relationImpelImageView;
    private ImageView matrimonyHoldImageView;
    private ImageView retrieveLoverImageView;
    private ImageView dispenseSingleImageView;
    private TextView likerateTextView;
    private Button consultButton;
    private Tour tour;
    private TextView shortintroTextView;
    private ListView listView;
    private CommentAdapter adapter;
    private Account account;
    private List<Comments.CodeBean> mList = new ArrayList<>();


    @Override
    public void initView() {
        super.initView();
        LinearLayout linearLayout = (LinearLayout) View.inflate(this, R.layout.tour_detail_activity,null);
        flContent.addView(linearLayout);
        iconImageView = (ImageView) linearLayout.findViewById(R.id.detail_icon_imageview);
        nameTextView = (TextView) linearLayout.findViewById(R.id.detail_name_textview);
        shortintroTextView = (TextView) linearLayout.findViewById(R.id.detail_shortintro_textview);
        relationImpelImageView = (ImageView) linearLayout.findViewById(R.id.detail_relationImpel_imageview);
        matrimonyHoldImageView = (ImageView) linearLayout.findViewById(R.id.detail_matrimonyHold_imageview);
        retrieveLoverImageView = (ImageView) linearLayout.findViewById(R.id.detail_retrieveLover_imageview);
        dispenseSingleImageView = (ImageView) linearLayout.findViewById(R.id.detail_dispenseSingle_imageview);
        likerateTextView = (TextView) linearLayout.findViewById(R.id.detail_likerate_textview);
        consultButton = (Button) linearLayout.findViewById(R.id.detail_consult_button);
        listView = (ListView) linearLayout.findViewById(R.id.comment_list);
    }

    @Override
    public void initData() {
        tour = (Tour) getIntent().getSerializableExtra("tour");
        Tour.Points points = tour.getPoints();
        titleView.setText("咨询师详情");
        adapter = new CommentAdapter();
        listView.setAdapter(adapter);
        nameTextView.setText(tour.getNickName());
        Glide.with(this).load(tour.getHeadImageUrl()).into(iconImageView);
        shortintroTextView.setText(tour.getShort_intro());
        dispenseSingleImageView.setImageResource(getRatingRes(points.getDispenseSingle()));
        matrimonyHoldImageView.setImageResource(getRatingRes(points.getMatrimonyHold()));
        relationImpelImageView.setImageResource(getRatingRes(points.getRelationImpel()));
        retrieveLoverImageView.setImageResource(getRatingRes(points.getRetrieveLover()));
        likerateTextView.setText((int) (Math.random() * 10 + 90) + "%");
        initCommentData(tour);
    }

    private void initCommentData(Tour tour) {
        StringBuilder sb = new StringBuilder(Api.COMMENTS);
        if (AccountTool.isLogined(this)){
            account = AccountTool.getCurrentAccount(this);
            sb.append("?tutorId=")
                    .append(tour.getId())
                    .append("&usId=")
                    .append(AccountTool.getCurrentAccount(this).getId());
        }else {
            sb.append("?tutorId=")
                    .append(tour.getId());
        }
        String url = sb.toString();
        OkGo.get(url)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        if (!TextUtils.isEmpty(s)){
                            Comments comments = new Gson().fromJson(s,Comments.class);
                            mList.clear();
                            mList.addAll(comments.getCode());
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    @Override
    public void initListener() {
        consultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AccountTool.isLogined(getBaseContext())) {
                    Toast.makeText(getBaseContext(), "您还没有登录", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.main_login_show, 0);
                    return;
                }
                if (AccountTool.getCurrentAccount(getBaseContext()).isTour()) {
                    Toast.makeText(getBaseContext(),"您已经是导师了,请不要调戏同行!",Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(getBaseContext(), ConsultChatActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", tour);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    @Override
    public void process(View v) {

    }

    private int getRatingRes(int rating){
        switch (rating){
            case 1:
                return R.drawable.new_star_love_1;
            case 2:
                return R.drawable.new_star_love_2;
            case 3:
                return R.drawable.new_star_love_3;
            case 4:
                return R.drawable.new_star_love_4;
            case 5:
                return R.drawable.new_star_love_5;
            default:
                return R.drawable.new_star_love_5;
        }
    }

    class CommentAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return null != mList ? mList.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final Comments.CodeBean bean = mList.get(position);
            if (null == convertView){
                convertView = View.inflate(TourDetailActivity.this,R.layout.comment_item,null);
            }
            CircleImageView image_icon = (CircleImageView) convertView.findViewById(R.id.comment_item_icon);
            TextView tv_name = (TextView) convertView.findViewById(R.id.comment_item_name);
            TextView tv_content = (TextView) convertView.findViewById(R.id.comment_item_content);
            final CheckBox box_like = (CheckBox) convertView.findViewById(R.id.comment_item_likes);
            TextView tv_popularity = (TextView) convertView.findViewById(R.id.comment_item_popularity);
            if (!TextUtils.isEmpty(bean.getUser_headimg())){
                Glide.with(TourDetailActivity.this)
                        .load(bean.getUser_headimg())
                        .override(120,120)
                        .into(image_icon);
            }
            tv_name.setText(bean.getUser_nickname());
            tv_content.setText(bean.getUser_pl());
            tv_popularity.setText(String.valueOf(bean.getPl_hot()));
            box_like.setText(String.valueOf(bean.getDz_sum()));
            box_like.setChecked(AccountTool.isLogined(TourDetailActivity.this) && bean.isIs_dz());
            box_like.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (AccountTool.isLogined(TourDetailActivity.this)){
                        String likeUrl;
                        int likes = bean.getDz_sum();
                        if (bean.isIs_dz() && !isChecked){
                            likeUrl = getLikeUrl(account.getId(),bean.getId(),0);
                            box_like.setText(String.valueOf(likes-1));
                            sendLike(likeUrl);
                        }else if (!bean.isIs_dz() && isChecked){
                            likeUrl = getLikeUrl(account.getId(),bean.getId(),1);
                            box_like.setText(String.valueOf(likes+1));
                            sendLike(likeUrl);
                        }else {
                            box_like.setText(String.valueOf(likes));
                        }
                    }else {
                        box_like.setChecked(false);
                        IntentFilter likeIntentFilter = new IntentFilter();
                        likeIntentFilter.addAction("like_change");
                        //注册广播
                        registerReceiver(new LikeUpdateBroadCast(), likeIntentFilter);
                        Toast.makeText(TourDetailActivity.this,"您还没有登录",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(TourDetailActivity.this, LoginActivity.class);
                        TourDetailActivity.this.startActivity(intent);
                        TourDetailActivity.this.overridePendingTransition(R.anim.main_login_show, 0);
                    }
                }
            });
            return convertView;
        }
    }

    private void sendLike(String url){
        if (!TextUtils.isEmpty(url)){
            OkGo.get(url)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(String s, Call call, Response response) {

                        }
                    });
        }
    }
    private String getLikeUrl(String userId,int plId,int isLike){
        StringBuilder sb = new StringBuilder();
        sb.append(Api.COMMENT_LIKES)
                .append("?plId=")
                .append(plId)
                .append("&userId=")
                .append(userId)
                .append("&userDz=")
                .append(isLike);
        return sb.toString();
    }


    class LikeUpdateBroadCast extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            initCommentData(tour);
            unregisterReceiver(this);
        }
    }
}
