package cn.com.zhiwoo.activity.base;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.io.File;
import java.io.FileNotFoundException;

import cn.com.zhiwoo.R;
import cn.com.zhiwoo.utils.LogUtils;
import io.rong.imkit.tools.PhotoFragment;

public class PhotoActivity extends AppCompatActivity implements View.OnClickListener {
    PhotoFragment mPhotoFragment;
    Uri mDownloaded;
    private Uri photo;
    private boolean firstClick = false;
    private boolean secondClick = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.consult_photo_activity);
        ((ViewGroup)findViewById(android.R.id.content)).getChildAt(0).setOnClickListener(this);
        Button saveButton = (Button) findViewById(R.id.savephoto_button);
        saveButton.setOnClickListener(this);
        mPhotoFragment = (PhotoFragment) getSupportFragmentManager().findFragmentById(R.id.photo_fragment);
        Intent intent = getIntent();
        photo = intent.getParcelableExtra("photo");
        LogUtils.log("photo url : " + photo.toString());
        boolean isSend = intent.getBooleanExtra("isSend",false);
        saveButton.setVisibility(isSend ? View.GONE : View.VISIBLE);
        if (photo != null) {
            mPhotoFragment.initPhoto(photo, null, new PhotoFragment.PhotoDownloadListener() {
                @Override
                public void onDownloaded(Uri uri) {
                    mDownloaded = uri;
                }

                @Override
                public void onDownloadError() {
                    Toast.makeText(PhotoActivity.this, "下载失败", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            saveButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.savephoto_button) {
            if (mDownloaded == null) {
                Toast.makeText(this, "正在下载，请稍后保存！", Toast.LENGTH_SHORT).show();
                return ;
            }
            savePhoto();
        } else {//点击了其他区域
            if (!firstClick) {
                firstClick = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (firstClick && !secondClick) {
                            LogUtils.log("单击");
                        } else {
                            LogUtils.log("多击");
                            firstClick = false;
                            secondClick = false;
                        }
                    }
                },500);

            } else {
                secondClick = true;
            }

        }
    }
    private void savePhoto() {
        String url = null;
        if (photo != null) {
            url = photo.toString();
        }
        if (url == null) {
            Toast.makeText(getBaseContext(),"保存失败",Toast.LENGTH_SHORT).show();
            return;
        }
        File appDir = new File(Environment.getExternalStorageDirectory(), "Zhiwoo");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        final String fileName = System.currentTimeMillis() + ".jpg";
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.download(url, new File(appDir, fileName).getAbsolutePath(), new RequestCallBack<File>() {
            @Override
            public void onSuccess(ResponseInfo<File> responseInfo) {
                File file = responseInfo.result;
                // 其次把文件插入到系统图库
                try {
                    String url = MediaStore.Images.Media.insertImage(getContentResolver(),
                            file.getAbsolutePath(), fileName, null);
                    LogUtils.log("保存图片 : URL = " + url);
                    if (url != null) {
                        Toast.makeText(getBaseContext(),"图片保存成功",Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getBaseContext(),"图片保存失败",Toast.LENGTH_SHORT).show();
                    }
                } catch (FileNotFoundException e) {
                    LogUtils.log("保存图片出错 : " + e.toString());
                    Toast.makeText(getBaseContext(),"图片保存失败",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                // 最后通知图库更新
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(file.getAbsolutePath())));
            }

            @Override
            public void onFailure(HttpException e, String s) {
                LogUtils.log("保存失败 : " + s);
                Toast.makeText(getBaseContext(),"图片保存失败",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
