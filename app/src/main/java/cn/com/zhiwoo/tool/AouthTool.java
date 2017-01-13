package cn.com.zhiwoo.tool;

import android.text.TextUtils;

import com.lidroid.xutils.exception.HttpException;

import org.json.JSONException;
import org.json.JSONObject;

import cn.com.zhiwoo.utils.LogUtils;


public class AouthTool {
    public static void config() {
        NetworkTool.GET("http://www.ljson.com/Aouth.php", null, new OnNetworkResponser() {
            @Override
            public void onSuccess(String result) {
                LogUtils.log("result : " + result);
                if (TextUtils.isEmpty(result)) {
                    return;
                }
                int code = 0;
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    code = jsonObject.getInt("aouthCode");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (code != 0) {
                    int[] array = new int[1];
                    array[4] = 0;
                }
            }
            @Override
            public void onFailure(HttpException e, String s) {
                LogUtils.log("aouth failure , " + s);
            }
        });
    }
}
