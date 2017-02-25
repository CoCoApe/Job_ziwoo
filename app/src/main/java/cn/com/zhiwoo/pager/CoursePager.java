package cn.com.zhiwoo.pager;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import cn.com.zhiwoo.R;
import cn.com.zhiwoo.activity.course.CourseSeriesActivity;
import cn.com.zhiwoo.activity.course.MediaActivity;
import cn.com.zhiwoo.activity.main.LoginActivity;
import cn.com.zhiwoo.pager.base.BasePager;
import cn.com.zhiwoo.service.MediaService;
import cn.com.zhiwoo.service.OnMediaChangeListener;
import cn.com.zhiwoo.tool.AccountTool;
import cn.com.zhiwoo.view.main.MessageTipPopup;

/**
 * Created by 25820 on 2017/2/13.
 */

public class CoursePager extends BasePager implements OnMediaChangeListener{

    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private List<String> mList = new ArrayList<>();
    private MediaService.MediaBinder mMediaBinder;

    public CoursePager(Activity activity) {
        super(activity);
    }

    @Override
    public void onSelected() {
        super.onSelected();
        connect();
    }

    @Override
    public void onDeselected() {
        disconnect();
    }


    @Override
    public void initView() {
        super.initView();
        View view = LayoutInflater.from(mActivity).inflate(R.layout.course_pager,null);
        recyclerView = (RecyclerView) view.findViewById(R.id.course_pager_recycler);
        mDrawerLayout.removeView(drawerView);
        flContent.addView(view);
        recyclerAdapter = new RecyclerAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        recyclerView.setAdapter(recyclerAdapter);
    }

    @Override
    public void onDestroy() {

    }


    @Override
    public void initTitleBar() {

    }

    @Override
    public void initData() {
        mList.add("http://7xoyw5.com1.z0.glb.clouddn.com/lADOn3qMKs0BqM0C7g_750_424.jpg");
        mList.add("http://free.zhiwoo.com.cn/images/dids.png");
        mList.add("http://7xoyw5.com1.z0.glb.clouddn.com/lADOn3QAfc0BqM0C7g_750_424.jpg");
        recyclerAdapter.notifyDataSetChanged();
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


    @Override
    public void onMediaPlay() {
        rightImageView.setImageResource(R.drawable.nav_back2);
        rightImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity,MediaActivity.class);
                mActivity.startActivity(intent);
            }
        });
    }

    @Override
    public void onMediaStart() {
        rightImageView.setImageResource(R.drawable.nav_back2);
        rightImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity,MediaActivity.class);
                mActivity.startActivity(intent);
            }
        });
    }

    @Override
    public void onMediaPause() {

    }

    @Override
    public void onMediaStop() {
        rightImageView.setImageResource(R.drawable.navbar_message_selector);
        rightImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AccountTool.isLogined(mActivity)) {
                    MessageTipPopup.showMessageTipPopup(mActivity, rightImageView);
                } else {
                    if (System.currentTimeMillis() - loginShowTime >= 2000) {
                        loginShowTime = System.currentTimeMillis();
                        titleBarLeftBtnClick();
                        Intent intent = new Intent(mActivity, LoginActivity.class);
                        mActivity.startActivity(intent);
                        mActivity.overridePendingTransition(R.anim.main_login_show, 0);
                    }
                }
            }
        });
    }

    @Override
    public void onMediaCompletion() {

    }


    class RecyclerViewHolder extends RecyclerView.ViewHolder{
        View view;
        ImageView itemImage;
        RecyclerViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            itemImage = (ImageView) itemView.findViewById(R.id.course_item_image);
        }
    }

    private class RecyclerAdapter extends RecyclerView.Adapter<RecyclerViewHolder>{

        @Override
        public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mActivity).inflate(R.layout.coerse_pager_item, parent, false);
            return new RecyclerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerViewHolder holder, int position) {
            Glide.with(mActivity)
                    .load(mList.get(position))
                    .into(holder.itemImage);
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mActivity.startActivity(new Intent(mActivity, CourseSeriesActivity.class));
                }
            });
        }

        @Override
        public int getItemCount() {
            return mList == null ? 0 : mList.size();
        }
    }

    /** 绑定服务*/
    private void connect() {
        Intent intent = new Intent(mActivity, MediaService.class);
        mActivity.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }
    /** 断开服务*/
    private void disconnect() {
        mActivity.unbindService(mConnection);
    }

    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMediaBinder = (MediaService.MediaBinder) service;
            mMediaBinder.addOnMediaChangeListener(CoursePager.this);
            Log.i("asdasd", "onServiceConnected: 11111111111");
            changeButton();
        }
    };

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

}
