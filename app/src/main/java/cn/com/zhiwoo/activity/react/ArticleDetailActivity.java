package cn.com.zhiwoo.activity.react;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import cn.com.zhiwoo.R;
import cn.com.zhiwoo.activity.base.BaseWebViewActivity;
import cn.com.zhiwoo.bean.react.ReactArticle;
import cn.com.zhiwoo.utils.wxUtils.Constants;
import cn.com.zhiwoo.utils.wxUtils.Util;


public class ArticleDetailActivity extends BaseWebViewActivity {
    private ReactArticle.QBean article;
    private RelativeLayout shareView;
    private IWXAPI iwxapi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iwxapi = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
        iwxapi.registerApp(Constants.APP_ID);
    }

    @Override
    public void initView() {
        super.initView();
        shareView = (RelativeLayout) View.inflate(this, R.layout.react_share_imageview, null);
        shareView.setVisibility(View.GONE);
        flContent.addView(shareView);
        shareView.findViewById(R.id.share_imageview).setOnClickListener(this);
    }

    @Override
    public void initData() {
        super.initData();
        Intent intent = getIntent();
        ReactArticle.QBean article = (ReactArticle.QBean) intent.getSerializableExtra("article");
        this.article = article;
        String title = article.getTitle();
        titleView.setText(title != null ? title : "");
        webView.loadUrl(article.getContent_url());
    }

    @Override
    public void initListener() {
    }

    @Override
    public void process(View v) {
        switch (v.getId()) {
            case R.id.share_imageview : {
                Toast.makeText(getBaseContext(),"正在启用微信分享...",Toast.LENGTH_SHORT).show();
                WXWebpageObject webpage = new WXWebpageObject();
                webpage.webpageUrl = article.getContent_url();
                WXMediaMessage msg = new WXMediaMessage(webpage);
                msg.title = webView.getTitle();
                //图片不能太大,否则发不了
                Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.icon_share);
                msg.thumbData = Util.bmpToByteArray(thumb, true);

                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = System.currentTimeMillis() + "shareArticle";
                req.message = msg;
                req.scene = SendMessageToWX.Req.WXSceneTimeline;
                iwxapi.sendReq(req);
            }
            break;
        }
    }

    @Override
    public void webViewLoadSuccess() {
        shareView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        iwxapi.unregisterApp();
    }
}
