package cn.com.zhiwoo.tool;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.Map;

import cn.com.zhiwoo.utils.LogUtils;


public class NetworkTool {
        public static HttpUtils httpUtils = new HttpUtils(10000).configCurrentHttpCacheExpiry(5000);

        public static void GET(String url,Map<String,String> params,final OnNetworkResponser networkResponser) {
                RequestParams requestParams = null;
                if (params != null) {
                        requestParams = new RequestParams();
                        for (Map.Entry<String,String> entry : params.entrySet()) {
                                requestParams.addBodyParameter(entry.getKey(),entry.getValue());
                        }
                }
                httpUtils.send(HttpRequest.HttpMethod.GET, url, requestParams, new RequestCallBack<String>() {
                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {
                                if (responseInfo != null) {
                                        if (responseInfo.result.contains("error_code") || responseInfo.result.contains("error_message")) {
                                                if (networkResponser != null) {
                                                        networkResponser.onFailure(new HttpException(-1),responseInfo.result);
                                                }
                                        } else if (networkResponser != null) {
                                                networkResponser.onSuccess(responseInfo.result);
                                        }
                                }
                        }
                        @Override
                        public void onFailure(HttpException e, String s) {
                                if (networkResponser != null) {
                                        networkResponser.onFailure(e,s);
                                }
                        }
                });
        }
        public static void POST(String url,Map<String,String> params,final OnNetworkResponser networkResponser) {
                RequestParams requestParams = null;
                if (params != null) {
                        requestParams = new RequestParams();
                        for (Map.Entry<String,String> entry : params.entrySet()) {
                                requestParams.addBodyParameter(entry.getKey(),entry.getValue());
                        }
                }
                httpUtils.send(HttpRequest.HttpMethod.POST, url, requestParams, new RequestCallBack<String>() {
                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {
                                if (responseInfo != null) {
                                        if (responseInfo.result.contains("error_code") || responseInfo.result.contains("error_message")) {
                                                if (networkResponser != null) {
                                                        networkResponser.onFailure(new HttpException(-1),responseInfo.result);
                                                }
                                        } else if (networkResponser != null) {
                                                networkResponser.onSuccess(responseInfo.result);
                                        }
                                }
                        }
                        @Override
                        public void onFailure(HttpException e, String s) {
                                if (networkResponser != null) {
                                        networkResponser.onFailure(e,s);
                                }
                        }
                });
        }
        static void PUT(String url, Map<String, String> params, final OnNetworkResponser networkResponser) {
                RequestParams requestParams = null;
                if (params != null) {
                        requestParams = new RequestParams();
                        for (Map.Entry<String,String> entry : params.entrySet()) {
                                requestParams.addBodyParameter(entry.getKey(),entry.getValue());
                        }
                }
                httpUtils.send(HttpRequest.HttpMethod.PUT, url, requestParams, new RequestCallBack<String>() {
                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {
                                LogUtils.log("put 结果 : " + responseInfo.result);
                                if (responseInfo.result.contains("error_code") || responseInfo.result.contains("error_message")) {
                                        if (networkResponser != null) {
                                                LogUtils.log("put包含错误信息");
                                                networkResponser.onFailure(new HttpException(-1),responseInfo.result);
                                        }
                                } else if (networkResponser != null) {
                                        LogUtils.log("put成功");
                                        networkResponser.onSuccess(responseInfo.result);
                                }
                        }
                        @Override
                        public void onFailure(HttpException e, String s) {
                                if (networkResponser != null) {
                                        LogUtils.log("put直接报错");
                                        networkResponser.onFailure(e,s);
                                }
                        }
                });
        }

}
