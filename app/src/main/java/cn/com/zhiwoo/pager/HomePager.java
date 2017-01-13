package cn.com.zhiwoo.pager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.BitmapUtils;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import cn.com.zhiwoo.R;
import cn.com.zhiwoo.activity.home.CustomActivity;
import cn.com.zhiwoo.activity.home.MessageCenterActivity;
import cn.com.zhiwoo.activity.home.OrdersActivity;
import cn.com.zhiwoo.activity.home.ProfileActivity;
import cn.com.zhiwoo.activity.home.TalkShowActivity;
import cn.com.zhiwoo.activity.home.WalletActivity;
import cn.com.zhiwoo.activity.main.LoginActivity;
import cn.com.zhiwoo.pager.base.BasePager;
import cn.com.zhiwoo.tool.AccountTool;
import cn.com.zhiwoo.utils.LogUtils;
import cn.com.zhiwoo.utils.wxUtils.Constants;
import cn.com.zhiwoo.utils.wxUtils.Util;

public class HomePager extends BasePager {

    private TextView nameTextView;
    private ImageView iconImageView;
    private ImageView bgImageView;
    private ListView listView;
    private String[] icons;
    private String[] titles;
    private IWXAPI api;
    private MyBroadCastRecevier broadcastReceiver;

    {
        icons = new String[]{
                "home_cell_menu_messagecenter",
                "home_cell_menu_order",
                "home_cell_menu_talkshow",
                "home_cell_menu_wallet",
                "home_cell_menu_custom",
                "home_cell_menu_share"
        };
        titles = new String[]{
                "消息中心",
                "我的订单",
                "我的节目",
                "我的钱包",
                "通用",
                "分享"
        };
    }

    public HomePager(Activity activity) {
        super(activity);
    }

    @Override
    public void onDeselected() {

    }


    @Override
    public void initView() {
        super.initView();
        LinearLayout linearLayout = (LinearLayout) View.inflate(mActivity, R.layout.home_content, null);
        flContent.addView(linearLayout);
        mDrawerLayout.removeView(drawerView);
        listView = (ListView) linearLayout.findViewById(R.id.listview);
        LayoutInflater layoutInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        RelativeLayout relativeLayout = (RelativeLayout) layoutInflater.inflate(R.layout.home_list_header,listView,false);
        listView.addHeaderView(relativeLayout);
        relativeLayout.setOnClickListener(this);

        bgImageView = (ImageView) relativeLayout.findViewById(R.id.bg_iamgeview);
        iconImageView = (ImageView) relativeLayout.findViewById(R.id.icon_imageview);
        nameTextView = (TextView) relativeLayout.findViewById(R.id.name_textview);
    }

    @Override
    public void onDestroy() {
        if (broadcastReceiver != null) {
            mActivity.unregisterReceiver(broadcastReceiver);
        }
    }

    @Override
    public void initData() {
        titleView.setText("我");
        api = WXAPIFactory.createWXAPI(mActivity.getBaseContext(), Constants.APP_ID);
        api.registerApp(Constants.APP_ID);
        listView.setAdapter(new HomeListAdapter());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = null;
                switch ((int)id) {
                    case 0 : {
                        if (!AccountTool.isLogined(mActivity)) {
                            Toast.makeText(mActivity, "您还没有登录", Toast.LENGTH_SHORT).show();
                            Intent intent1 = new Intent(mActivity, LoginActivity.class);
                            mActivity.startActivity(intent1);
                            mActivity.overridePendingTransition(R.anim.main_login_show, 0);
                            return;
                        }
                        intent = new Intent(mActivity, MessageCenterActivity.class);
                    }
                    break;
                    case 1 : {
                        if (!AccountTool.isLogined(mActivity)) {
                            Toast.makeText(mActivity, "您还没有登录", Toast.LENGTH_SHORT).show();
                            Intent intent1 = new Intent(mActivity, LoginActivity.class);
                            mActivity.startActivity(intent1);
                            mActivity.overridePendingTransition(R.anim.main_login_show, 0);
                            return;
                        }
                        intent = new Intent(mActivity, OrdersActivity.class);
                    }
                    break;
                    case 2 : {
                        intent = new Intent(mActivity, TalkShowActivity.class);
//                        Toast.makeText(mActivity, "该功能暂未开放，敬请期待下版本更新。", Toast.LENGTH_SHORT).show();
                    }
                    break;
                    case 3 : {
                        if (!AccountTool.isLogined(mActivity)) {
                            Toast.makeText(mActivity, "您还没有登录", Toast.LENGTH_SHORT).show();
                            Intent intent1 = new Intent(mActivity, LoginActivity.class);
                            mActivity.startActivity(intent1);
                            mActivity.overridePendingTransition(R.anim.main_login_show, 0);
                            return;
                        }
                        intent = new Intent(mActivity, WalletActivity.class);
                    }
                    break;
                    case 4 : {
                        intent = new Intent(mActivity, CustomActivity.class);
                    }
                    break;
                    case 5 : {
                        Toast.makeText(mActivity,"正在启用微信分享...",Toast.LENGTH_SHORT).show();
                        WXWebpageObject webpage = new WXWebpageObject();
                        webpage.webpageUrl = "http://www.zhiwoo.com.cn";
                        WXMediaMessage msg = new WXMediaMessage(webpage);
                        msg.title = "咨我--你的专属恋情顾问";
                        msg.description = "咨我在手,天下我有!";
                        //图片不能太大,否则发不了
                        Bitmap thumb = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.icon_share);
                        msg.thumbData = Util.bmpToByteArray(thumb, true);
                        SendMessageToWX.Req req = new SendMessageToWX.Req();
                        req.transaction = System.currentTimeMillis() + "shareAPP";
                        req.message = msg;
                        req.scene = SendMessageToWX.Req.WXSceneTimeline;
                        api.sendReq(req);
                    }
                    break;
                }
                if (intent != null) {
                    mActivity.startActivity(intent);
                }
            }
        });

        IntentFilter filter = new IntentFilter();
        filter.addAction("userinfo_change");
        broadcastReceiver = new MyBroadCastRecevier();
        mActivity.registerReceiver(broadcastReceiver, filter);
    }
    @Override
    public void initTitleBar() {
        leftImageView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void process(View v) {
        switch (v.getId()) {
            case R.id.home_header_rl : {
                if (!AccountTool.isLogined(mActivity)) {
                    Toast.makeText(mActivity,"您还没有登录",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(mActivity, LoginActivity.class);
                    mActivity.startActivity(intent);
                    mActivity.overridePendingTransition(R.anim.main_login_show, 0);
                    return;
                }
                Intent intent = new Intent(mActivity.getBaseContext(), ProfileActivity.class);
                mActivity.startActivity(intent);
            }
            break;
        }
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

    private class HomeListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(mActivity,R.layout.home_list_item,null);
            }
            ImageView iconImageView = (ImageView) convertView.findViewById(R.id.icon_imageview);
            TextView titleTextView = (TextView) convertView.findViewById(R.id.title_textview);
            iconImageView.setImageResource(mActivity.getResources().getIdentifier(icons[position], "drawable", mActivity.getPackageName()));
            titleTextView.setText(titles[position]);
            return convertView;
        }
    }

    @Override
    public void onSelected() {
        super.onSelected();
        BitmapUtils bitmapUtils = new BitmapUtils(mActivity);
        if (AccountTool.isLogined(mActivity)) {
            bitmapUtils.display(iconImageView,AccountTool.getCurrentAccount(mActivity).getHeadImageUrl());
            bitmapUtils.display(bgImageView, AccountTool.getCurrentAccount(mActivity).getHeadImageUrl());
            nameTextView.setText(AccountTool.getCurrentAccount(mActivity).getNickName());
        }

    }
    private class MyBroadCastRecevier extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtils.log("home -> 收到 个人信息改变的通知");
            BitmapUtils bitmapUtils = new BitmapUtils(mActivity.getBaseContext());
            bitmapUtils.display(bgImageView,AccountTool.getCurrentAccount(mActivity.getBaseContext()).getHeadImageUrl());
            bitmapUtils.display(iconImageView,AccountTool.getCurrentAccount(mActivity.getBaseContext()).getHeadImageUrl());
            nameTextView.setText(AccountTool.getCurrentAccount(mActivity.getBaseContext()).getNickName());
        }
    }
//    private TextView nameTextView;
//    private ImageView iconImageView;
//    private ImageView bgImageView;
//    private ListView listView;
//    private IWXAPI api;
//    private MyBroadCastReceiver broadcastReceiver;
//    private String[] icons;
//    private String[] titles;
//    {
//        icons = new String[]{
//                "home_cell_menu_messagecenter",
//                "home_cell_menu_order",
//                "home_cell_menu_talkshow",
//                "home_cell_menu_wallet",
//                "home_cell_menu_custom",
//                "home_cell_menu_share"
//        };
//        titles = new String[]{
//                "消息中心",
//                "我的订单",
//                "我的节目",
//                "我的钱包",
//                "通用",
//                "分享"
//        };
//    }
//
//    public HomePager(Activity activity) {
//        super(activity);
//    }
//
//    @Override
//    public void onDeselected() {
//
//    }
//
//    @Override
//    public void initView() {
//        super.initView();
//        LinearLayout linearLayout = (LinearLayout) View.inflate(mActivity, R.layout.home_content, null);
//        flContent.addView(linearLayout);
//        mDrawerLayout.removeView(drawerView);
//        listView = (ListView) linearLayout.findViewById(R.id.listview);
//        LayoutInflater layoutInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//        RelativeLayout relativeLayout = (RelativeLayout) layoutInflater.inflate(R.layout.home_list_header,listView,false);
//        listView.addHeaderView(relativeLayout);
//        relativeLayout.setOnClickListener(this);
//
//        bgImageView = (ImageView) relativeLayout.findViewById(R.id.bg_iamgeview);
//        iconImageView = (ImageView) relativeLayout.findViewById(R.id.icon_imageview);
//        nameTextView = (TextView) relativeLayout.findViewById(R.id.name_textview);
//    }
//
//    @Override
//    public void onDestroy() {
//        if (broadcastReceiver != null) {
//            mActivity.unregisterReceiver(broadcastReceiver);
//        }
//    }
//
//    @Override
//    public void initData() {
//        titleView.setText("我");
//        api = WXAPIFactory.createWXAPI(mActivity.getBaseContext(), Constants.APP_ID);
//        api.registerApp(Constants.APP_ID);
//        if(null != AccountTool.getCurrentAccount(mActivity))
//        listView.setAdapter(new HomeListAdapter());
//        Log.i("aaaaa", "initData: 1111111");
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = null;
//                switch ((int)id) {
//                    case 0 : {
//                        if (!AccountTool.isLogined(mActivity)) {
//                            Toast.makeText(mActivity, "您还没有登录", Toast.LENGTH_SHORT).show();
//                            Intent intent1 = new Intent(mActivity, LoginActivity.class);
//                            mActivity.startActivity(intent1);
//                            mActivity.overridePendingTransition(R.anim.main_login_show, 0);
//                            return;
//                        }
//                        intent = new Intent(mActivity, MessageCenterActivity.class);
//                    }
//                    break;
//                    case 1 : {
//                        if (!AccountTool.isLogined(mActivity)) {
//                            Toast.makeText(mActivity, "您还没有登录", Toast.LENGTH_SHORT).show();
//                            Intent intent1 = new Intent(mActivity, LoginActivity.class);
//                            mActivity.startActivity(intent1);
//                            mActivity.overridePendingTransition(R.anim.main_login_show, 0);
//                            return;
//                        }
//                        intent = new Intent(mActivity, OrdersActivity.class);
//                    }
//                    break;
//                    case 2 : {
//                        intent = new Intent(mActivity, TalkShowActivity.class);
//                    }
//                    break;
//                    case 3 : {
//                        if (!AccountTool.isLogined(mActivity)) {
//                            Toast.makeText(mActivity, "您还没有登录", Toast.LENGTH_SHORT).show();
//                            Intent intent1 = new Intent(mActivity, LoginActivity.class);
//                            mActivity.startActivity(intent1);
//                            mActivity.overridePendingTransition(R.anim.main_login_show, 0);
//                            return;
//                        }
//                        intent = new Intent(mActivity, WalletActivity.class);
//                    }
//                    break;
//                    case 4 : {
//                        intent = new Intent(mActivity, CustomActivity.class);
//                    }
//                    break;
//                    case 5 : {
//                        Toast.makeText(mActivity,"正在启用微信分享...",Toast.LENGTH_SHORT).show();
//                        WXWebpageObject webpage = new WXWebpageObject();
//                        webpage.webpageUrl = "http://www.zhiwoo.com.cn";
//                        WXMediaMessage msg = new WXMediaMessage(webpage);
//                        msg.title = "咨我--你的专属恋情顾问";
//                        msg.description = "咨我在手,天下我有!";
//                        //图片不能太大,否则发不了
//                        Bitmap thumb = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.icon_share);
//                        msg.thumbData = Util.bmpToByteArray(thumb, true);
//                        SendMessageToWX.Req req = new SendMessageToWX.Req();
//                        req.transaction = System.currentTimeMillis() + "shareAPP";
//                        req.message = msg;
//                        req.scene = SendMessageToWX.Req.WXSceneTimeline;
//                        api.sendReq(req);
//                    }
//                    break;
//                }
//                if (intent != null) {
//                    mActivity.startActivity(intent);
//                }
//            }
//        });
//
//        IntentFilter filter = new IntentFilter();
//        filter.addAction("userinfo_change");
//        broadcastReceiver = new MyBroadCastReceiver();
//        mActivity.registerReceiver(broadcastReceiver, filter);
//    }
//    @Override
//    public void initTitleBar() {
//        leftImageView.setVisibility(View.INVISIBLE);
//    }
//
//    @Override
//    public void process(View v) {
//        switch (v.getId()) {
//            case R.id.home_header_rl : {
//                if (!AccountTool.isLogined(mActivity)) {
//                    Toast.makeText(mActivity,"您还没有登录",Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(mActivity, LoginActivity.class);
//                    mActivity.startActivity(intent);
//                    mActivity.overridePendingTransition(R.anim.main_login_show, 0);
//                    return;
//                }
//                Intent intent = new Intent(mActivity.getBaseContext(), ProfileActivity.class);
//                mActivity.startActivity(intent);
//            }
//            break;
//        }
//    }
//
//    @Override
//    public void titleBarLeftBtnClick() {
//
//    }
//
//    @Override
//    public void titleBarRightBtnClick() {
//
//    }
//
//    @Override
//    public void onResume() {
//    }
//
//    class HomeListAdapter extends BaseAdapter {
//
//        @Override
//        public int getCount() {
//            return titles.length;
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return null;
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            Log.i("aaaaa", "initData: getView22222222");
//            if (convertView == null) {
//                convertView = View.inflate(mActivity,R.layout.home_list_item,null);
//            }
//            ImageView iconImageView = (ImageView) convertView.findViewById(R.id.icon_imageview);
//            TextView titleTextView = (TextView) convertView.findViewById(R.id.title_textview);
//            iconImageView.setImageResource(mActivity.getResources().getIdentifier(icons[position], "drawable", mActivity.getPackageName()));
//            titleTextView.setText(titles[position]);
//            return convertView;
//        }
//    }
//
//    @Override
//    public void onSelected() {
//        super.onSelected();
//        BitmapUtils bitmapUtils = new BitmapUtils(mActivity);
//        if (AccountTool.isLogined(mActivity)) {
//            Account account = AccountTool.getCurrentAccount(mActivity);
////            Glide.with(mActivity).load(account.getHeadImageUrl()).placeholder(R.drawable.default_user_icon).into(iconImageView);
////            Glide.with(mActivity).load(account.getHeadImageUrl()).placeholder(R.drawable.default_user_icon).into(bgImageView);
//            bitmapUtils.display(iconImageView,account.getHeadImageUrl());
//            bitmapUtils.display(bgImageView, account.getHeadImageUrl());
//            nameTextView.setText(account.getNickName());
//        }
//
//    }
//    private class MyBroadCastReceiver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            LogUtils.log("home -> 收到 个人信息改变的通知");
//            BitmapUtils bitmapUtils = new BitmapUtils(mActivity.getBaseContext());
//            bitmapUtils.display(bgImageView,AccountTool.getCurrentAccount(mActivity.getBaseContext()).getHeadImageUrl());
//            bitmapUtils.display(iconImageView,AccountTool.getCurrentAccount(mActivity.getBaseContext()).getHeadImageUrl());
//            nameTextView.setText(AccountTool.getCurrentAccount(mActivity.getBaseContext()).getNickName());
//        }
//    }
}
