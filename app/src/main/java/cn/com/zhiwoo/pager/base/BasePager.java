package cn.com.zhiwoo.pager.base;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cn.com.zhiwoo.R;
import cn.com.zhiwoo.activity.main.LoginActivity;
import cn.com.zhiwoo.tool.AccountTool;
import cn.com.zhiwoo.tool.ChatTool;
import cn.com.zhiwoo.view.main.MessageTipPopup;


public abstract class BasePager implements View.OnClickListener {
    private long loginShowTime = 0;
    public Activity mActivity;
    public View mRootView;
    public TextView titleView;
    protected ImageView leftImageView;
    private ImageView rightImageView;
    public FrameLayout flContent;
    public boolean didInitData = false;
    public DrawerLayout mDrawerLayout;
    public View drawerView;

    public BasePager(Activity activity) {
        mActivity = activity;
        initView();
    }
    public abstract void onDeselected();
    public void onSelected() {
        if (!didInitData) {
            initTitleBar();
            initData();
            didInitData = true;
        }
    }
    /**
     * 初始化布局
     */
    public void initView() {
        mRootView = View.inflate(mActivity, R.layout.base_pager, null);
        flContent = (FrameLayout) mRootView.findViewById(R.id.fl_content);
        mDrawerLayout = (DrawerLayout) mRootView.findViewById(R.id.main_drawer);
        drawerView = mRootView.findViewById(R.id.main_drawerLinearLayout);
        titleView = (TextView) mRootView.findViewById(R.id.tv_title);
        leftImageView = (ImageView) mRootView.findViewById(R.id.topBar_left_image);
        rightImageView = (ImageView) mRootView.findViewById(R.id.topBar_right_image);
        leftImageView.setOnClickListener(this);
        rightImageView.setOnClickListener(this);
        if (AccountTool.isLogined(mActivity)) {
            rightImageView.setSelected(ChatTool.sharedTool().getTipMessages().size() > 0);
        } else {
            rightImageView.setSelected(false);
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /**
             * 严格控制登录成功后才能使用的功能,避免错误
             */
            case R.id.topBar_left_image : {
                if (AccountTool.isLogined(mActivity)) {
                    Toast.makeText(mActivity,"您已经登录成功",Toast.LENGTH_SHORT).show();
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
            break;
            case R.id.topBar_right_image : {
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
                // 设置回调监听，获取点击事件
                titleBarRightBtnClick();
            }
            break;
            default: {
                process(v);
            }
            break;
        }
    }
    public void receiverMessageBroadcast(int count) {
        rightImageView.setSelected(count>0);
    }

    public abstract void onDestroy();
    public abstract void initTitleBar();
    public abstract void initData();
    public abstract void process(View v);
    public abstract void titleBarLeftBtnClick();
    public abstract void titleBarRightBtnClick();
    public abstract void onResume();
}
