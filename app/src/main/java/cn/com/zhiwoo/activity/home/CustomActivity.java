package cn.com.zhiwoo.activity.home;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.lidroid.xutils.BitmapUtils;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Calendar;

import cn.com.zhiwoo.R;
import cn.com.zhiwoo.activity.base.BaseActivity;
import cn.com.zhiwoo.activity.main.LoginActivity;
import cn.com.zhiwoo.activity.main.MainActivity;
import cn.com.zhiwoo.bean.tutor.User;
import cn.com.zhiwoo.tool.AccountTool;
import cn.com.zhiwoo.tool.ChatTool;
import cn.com.zhiwoo.utils.LogUtils;
import io.rong.imlib.model.UserInfo;


public class CustomActivity extends BaseActivity {
    private SVProgressHUD mHud;
    private TextView cacheSizeTextView;
    private Switch notifySwitch;
    private Button unregistButton;
    private TextView copyright_textview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHud = new SVProgressHUD(this);
    }

    @Override
    public void initView() {
        super.initView();
        LinearLayout linearLayout = (LinearLayout) View.inflate(this, R.layout.home_custom_activity, null);
        flContent.addView(linearLayout);

        notifySwitch = (Switch) linearLayout.findViewById(R.id.notify_switch);
        notifySwitch.setOnClickListener(this);
        notifySwitch.setClickable(false);
        linearLayout.findViewById(R.id.phone_layout).setOnClickListener(this);
        linearLayout.findViewById(R.id.mail_layout).setOnClickListener(this);
        linearLayout.findViewById(R.id.customer_layout).setOnClickListener(this);
        linearLayout.findViewById(R.id.cache_layout).setOnClickListener(this);
        unregistButton = (Button) linearLayout.findViewById(R.id.unregister_button);
        cacheSizeTextView = (TextView) findViewById(R.id.cachesize_textview);
        unregistButton.setOnClickListener(this);
        copyright_textview = (TextView) linearLayout.findViewById(R.id.copyright_textview);
    }

    @Override
    public void initData() {
        titleView.setText("通用");
        loadCacheFilesize();
        notifySwitch.setChecked(isNotificationAllow());
        String version = "最新版本";
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            version = info.versionName;
            } catch (Exception ignored) {
        }

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR); //获取当前年份

        String copy = "V " + version + "\n咨我 版权所有\nCopyright © " + year +" zhiwoo inc.";
        copyright_textview.setText(copy);
    }

    private void loadCacheFilesize() {
        File file = new File(getExternalCacheDir().getAbsolutePath() + "/xBitmapCache");
        if (!file.exists()) {
            file = new File(getCacheDir().getAbsolutePath() + "/xBitmapCache");
        }
        if (!file.exists()) {
            LogUtils.log("没找到缓存目录");
            return;
        }
        LogUtils.log("缓存路径: " + file.getAbsolutePath());
        long size = 0;
        try {
            size = getFileSize(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        cacheSizeTextView.setText("当前缓存:" + formetFileSize(size));
    }

    @Override
    public void initListener() {

    }

    @Override
    public void process(View v) {
        switch (v.getId()) {
            case R.id.notify_switch: {
                notifySwitch.setClickable(false);
                LogUtils.log("点击了通知按钮");
            }
            break;
            case R.id.phone_layout: {
                LogUtils.log("拨打客服电话");
                call();
            }
            break;
            case R.id.mail_layout: {
                LogUtils.log("发送官方邮件");
                sendMail();
            }
            break;
            case R.id.customer_layout: {
                LogUtils.log("联系在线客服");
                connect();
            }
            break;
            case R.id.cache_layout: {
                LogUtils.log("清除缓存");
                clearCache();
            }
            break;
            case R.id.unregister_button: {
                LogUtils.log("注销登录");
                unregist();
            }
            break;
        }
    }

    private void call() {
        //用intent启动拨打电话
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "02029172574"));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(intent);
    }
    private void sendMail() {
        //用intent发邮件
        Intent data = new Intent(Intent.ACTION_SENDTO);
        data.setData(Uri.parse("mailto:fuente@zhiwoo.com.cn"));
        if (data.resolveActivity(getPackageManager()) != null) {
            startActivity(data);
        } else {
            Toast.makeText(getBaseContext(),"您的设备不支持发送邮件",Toast.LENGTH_LONG).show();
        }
    }
    private void connect() {
        if (!AccountTool.isLogined(getBaseContext())) {
            Toast.makeText(getBaseContext(), "您还没有登录", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getBaseContext(), LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.main_login_show, 0);
            return;
        }
        ChatTool.sharedTool().getUserInfo("1468", new ChatTool.loadUserInfoCallBack() {
            @Override
            public void onsuccess(UserInfo userInfo) {
                Intent intent = new Intent(getBaseContext(), APPServiceActivity.class);
                Bundle bundle = new Bundle();
                User user = new User();
                user.setId(userInfo.getUserId());
                user.setHeadImageUrl(userInfo.getPortraitUri().toString());
                user.setNickName(userInfo.getName());
                //[测试id]
//                user.setId("1652");
                bundle.putSerializable("user",user);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
    private void clearCache() {
        final ActionSheetDialog actionSheetDialog = new ActionSheetDialog(this,new String[]{"确定"},null);
        actionSheetDialog.title("确定要清除缓存?")
                .titleTextColor(Color.RED)
                .show();
        actionSheetDialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                BitmapUtils bitmapUtils = new BitmapUtils(getBaseContext());
                bitmapUtils.clearCache();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadCacheFilesize();
                    }
                },500);
                actionSheetDialog.dismiss();
            }
        });
    }

    private void unregist() {
        final ActionSheetDialog actionSheetDialog = new ActionSheetDialog(this,new String[]{"退出"},null);
        actionSheetDialog.title("确定要退出当前账号?")
                .titleTextColor(Color.RED)
                .show();
        actionSheetDialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                boolean result = AccountTool.unregistCurrentAccount(getBaseContext());
                if (result) {
                    Toast.makeText(getBaseContext(), "注销成功", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent i = new Intent(getBaseContext(), MainActivity.class);
                            startActivity(i);
                            overridePendingTransition(0, 0);
                        }
                    }, 500);
                    actionSheetDialog.dismiss();
                } else {
                    mHud.showErrorWithStatus("注销失败");
                }
            }
        });
    }

    /*** 获取文件夹大小 ***/
    private long getFileSize(File f) throws Exception {
        long size = 0;
        File flist[] = f.listFiles();
        for (File aFlist : flist) {
            if (aFlist.isDirectory()) {
                size = size + getFileSize(aFlist);
            } else {
                size = size + aFlist.length();
            }
        }
        return size;
    }
    /*** 转换文件大小单位(b/kb/mb/gb) ***/
    private String formetFileSize(long fileS) {// 转换文件大小
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString;
        if (fileS < 1024) {
            fileSizeString = "0";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }
    private boolean isNotificationAllow() {
        try {
            PackageManager pm = getPackageManager();
            PackageInfo pi = pm.getPackageInfo(getPackageName(), 0);
            // 得到自己的包名
            String pkgName = pi.packageName;
            PackageInfo pkgInfo = pm.getPackageInfo(pkgName,
                    PackageManager.GET_PERMISSIONS);//通过包名，返回包信息
            String sharedPkgList[] = pkgInfo.requestedPermissions;//得到权限列表
            boolean result = false;
            for (String permName : sharedPkgList) {
                if (permName.equals("android.permission.RECEIVE_BOOT_COMPLETED")) {
                    if (PackageManager.PERMISSION_GRANTED == pm.checkPermission(permName, pkgName)) {
                        result = true;
                    }
                    LogUtils.log("权限: " + permName + (PackageManager.PERMISSION_GRANTED == pm.checkPermission(permName, pkgName) ? " 通过" : " 未通过"));
                    return result;
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        unregistButton.setEnabled(AccountTool.isLogined(this));
    }
}
