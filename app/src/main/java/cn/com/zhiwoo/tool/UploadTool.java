package cn.com.zhiwoo.tool;

import android.graphics.Bitmap;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import cn.com.zhiwoo.utils.Api;
import cn.com.zhiwoo.utils.Global;
import cn.com.zhiwoo.utils.LogUtils;
import okhttp3.Call;
import okhttp3.Response;


public class UploadTool {
    private static UploadTool instance = null;
    public static UploadTool sharedTool() {
        if (instance == null) {
            instance = new UploadTool();
        }
        return instance;
    }

    private UploadManager uploadManager;
    private UploadTool() {
        uploadManager = new UploadManager();
    }
    public void uploadImage(final Bitmap image,String userToken, final OnUploadResultListener onUploadResultListener) {
        HashMap<String,String> params = new HashMap<>();
        params.put("access_token",userToken);
        OkGo.get(Api.UPLOAD_IMAGE)
                .params(params)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] data = baos.toByteArray();
                        String token = "";
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            token = jsonObject.getString("uptoken");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        uploadManager.put(data, null, token, new UpCompletionHandler() {
                            @Override
                            public void complete(String key, ResponseInfo info, JSONObject response) {
                                try {
                                    String pathKey = response.getString("key");
                                    String url = Global.QiNiu_Url + pathKey;
                                    LogUtils.log("上传成功 : " + url);
                                    if (onUploadResultListener != null) {
                                        onUploadResultListener.uploadSuccess(url);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, null);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        if (onUploadResultListener != null) {
                            onUploadResultListener.uploadFailure();
                        }
                    }
                });
    }
    public interface OnUploadResultListener {
        void uploadSuccess(String url);
        void uploadFailure();
    }
}