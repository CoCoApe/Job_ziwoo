package cn.com.zhiwoo.tool;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import cn.com.zhiwoo.utils.Api;
import cn.com.zhiwoo.utils.Global;
import cn.com.zhiwoo.utils.LogUtils;
import cn.com.zhiwoo.utils.aliUtils.PayResult;
import cn.com.zhiwoo.utils.aliUtils.SignUtils;
import cn.com.zhiwoo.utils.wxUtils.MD5;
import cn.com.zhiwoo.utils.wxUtils.Util;
import cn.com.zhiwoo.view.main.PayStyleChoseDialog;
import okhttp3.Call;
import okhttp3.Response;


public class PayTool {

    //从官方网站申请到的合法appId
//    private static String WX_APP_ID = "wx74b2d981d893a0d7";
    private static String WX_APP_ID = "wx74b2d981d893a0d7";

    //商家向财付通申请的商家id
//    private static String WX_PARTNER_ID = "1283740601";
    private static String WX_PARTNER_ID = "2183772341";

    //微信公众平台商户模块和商户约定的密钥
//    private static String WX_PARTNER_KEY = "womendezhengtushixingcengdahai11";
    private static String WX_PARTNER_KEY = "qazwsxedcrfvtgbyhnujmikolp654321";

    // 商户PID
//    private static String ALi_PARTNER = "2088021022878420";
    private static String ALi_PARTNER = "9765347532973629";

    // 商户收款账号
//    private static String ALi_SELLER = "635686991@qq.com";
    private static String ALi_SELLER = "997634523@qq.com";

    // 商户私钥，pkcs8格式
//    private static String ALi_RSA_PRIVATE = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAOptS01V+5jP25xivunc4BIWls9OEngTuiEKADHooAVTXK/D8lY3MYzxecJ5OPmDy9E3sAmVbJJEv+m1BS6djOTP6E4kF6UhZV8P2xbbFNL7zD/2URHzD9PbNPJIErcel1011OJVf3TiRi+jNbnt1Bai5VsDI44lrUlcnMb4pzhpAgMBAAECgYAC4vulZSSm+hBDObgOGykrL9oFjXmOvvdwxrW/55Ro3GDszSXjx+Q1TxfnvCRmdigBWIUzaQKgPnZ6gz8gFtKQTLKHsKabDsAcVwRrNASXtIR+ASs5TXg0EfKS1b8ZjxXZTnXlLinPO/glGRgMIGFJOF1xTUBVljFfqN5N2i9ZAQJBAPdKVhsTDy9r2GeVBKJaAeuznLqjz/RNc02lS/tua65yQwEcdfNnHUK1Evd0pUPzMSpd0T3UQ+9OVxYhIB08W4kCQQDyrvnhlpn0xr3QUg5GwJ1SBCtuCwGyJdB8OhFrbud0I7WMNddE6ebC+cZPZVU95DIIVsqU481gQc78lM0WCl3hAkAIZjv6e3E+mRkmm4cmxIvgJ5+hL0M29xJ9hqnIBn4d1L+13/OZqtzxkRjt0sZyQmZfHASpZvZPwIdwtvtSQuSRAkBlD/EHvMvIX6tUQZeoZzYcnZfob7T5Fz5HPXdbogfJGcXU6ecHz9BFifbHwY9KAunDB0G911ADseQc02rErvSBAkEAjTGWDgN8KMWyZgP4n8yOMfUrIzqxfEIS+DSQiXGG6EilU+B5izu/V0D4lZQrzfGquVfNfQIYz6G0NW4tNjjxUw==";
    private static String ALi_RSA_PRIVATE = "MIIDFKIBADANBgkqhkiG9w0BAQEFAASCBBBwggJcAgEAAoGBAOptS01V+5jP25xivunc4iowls9OEngTuiEKADHooAVTXK/D8lY3MYzxecJ5OPmDy9E3sAmVbJJEv+m1BS6djOTP6E4kF6jhDV8P2xbbFNL7zD/2URHzD9PbNPJIErcel1011OJVf3TiRi+NNBnt1Bai5VsDI44lrUlcnMb4pzhpAgMBAAECgYAC4vulZSSm+hBDObgOGykrL9oFjXmOvvdwxrW/55Ro3GDszSXjx+Q1TxfnvCRmdigqwerzaQKgPnZ6gz8gFtKQTLKHsKabDsAcVwRrNASXtIR+ASs5TXg0EfKS1b8ZjxokmnXlLinPO/glGRgMIGFJOF1xTUBVljFfqN5N2i9ZAQJBAPdKVhsTDy9r2GeVBKJaAeuznLqjz/RNc99lS/tua65yQwEcdfNnHUK1Evd0pUPzMSpd0T3UQ+9OVxYhIB08W4kCQQDyrvnhlpn0xr3QUg5GwJ1SBCtuCwGyJdB8OhFrbud0I7WMNddE6ebC+cZPZVU95DIIVsqU481gQc78lM0WCl3hAkAIZjv6e3E+mRkmm4cmxIvgJ5+hL0M29xJ9hqnIBn4d1L+13/OZqtzxkRjt99ZyQmZfHASpZvZPwIdwtvtSQuSRAkBlD/EHvMvIX6tUQZeoZzYcnZfob7T5Fz5HPXdbogfJGcXU6ecHz9BFifbHwY9KAunDB0G911ADADQc02rErvSBAkEAjTGWDgN8KMWyZgP4n8yOMfUrIzqxfEIS+DSQiXGG6EilU+B5izu/V0D4lZQrzfGquVfNfQIYz6G0NW4tNjjxMT==";


    private Activity mActivity;
    private Product product;
    private OnPayResultListener onPayResultListener;
    private MyBroadCastRecevier broadcastReceiver;
    public PayTool(Activity mActivity,OnPayResultListener onPayResultListener) {
        this.mActivity = mActivity;
        this.onPayResultListener = onPayResultListener;
        iwxapi = WXAPIFactory.createWXAPI(mActivity.getBaseContext(), WX_APP_ID);
        iwxapi.registerApp(WX_APP_ID);

        // 动态注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction("wx_pay");
        broadcastReceiver = new MyBroadCastRecevier();
        mActivity.registerReceiver(broadcastReceiver, filter);
    }
    public void pay(Product product) {
        this.product = product;
        boolean isWXPaySupported = iwxapi.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;
        if (isWXPaySupported) {
            PayStyleChoseDialog.showDialog(mActivity, new PayStyleChoseDialog.OnChosenPayStyle() {
                @Override
                public void didChosenPayStyle(int payStyle) {
                    if (payStyle == PayStyleChoseDialog.PayStyleWXPay) {
                        payByWeixin();
                    } else {
                        payByALi();
                    }
                }
            });
        } else {
            payByALi();
        }
    }

    private void payByWeixin() {
        Toast.makeText(mActivity,"正在启用微信支付...",Toast.LENGTH_SHORT).show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                startWXPay();
            }
        }).start();

    }
    private void payByALi() {
        Toast.makeText(mActivity,"正在启用支付宝...",Toast.LENGTH_SHORT).show();
        String orderInfo = getOrderInfo(product);
        /**
         * 特别注意，这里的签名逻辑需要放在服务端，切勿将私钥泄露在代码中！
         */
        String sign = sign(orderInfo);
        try {
            /**
             * 仅需对sign 做URL编码
             */
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        /**
         * 完整的符合支付宝参数规范的订单信息
         */
        final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + getSignType();

        //异步调起支付
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(mActivity);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(payInfo, true);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                aliHandler.sendMessage(msg);
            }
        }).start();
    }

    /**
     * 微信支付相关代码
     */
    private IWXAPI iwxapi;
    private String nonceStr;//随机串
    static private final int FetchPrepayIdSuccess = 0;
    static private final int FetchPrepayIdFailure = 1;
    static private final int StartSendPayReq = 2;

    private Handler wxPayHandeler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case PayTool.FetchPrepayIdSuccess : {
                }
                break;
                case PayTool.FetchPrepayIdFailure : {
                }
                break;
                case PayTool.StartSendPayReq : {
                }
                break;
                default:
                    break;
            }
            return false;
        }
    });


    /**
     * 点击支付按钮,开始支付流程
     */
    private void startWXPay() {
        //获取 prepayid
        String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";
        //获取商品Prepayid的基本参数
//        ContentValues contentValues = new ContentValues();
//        contentValues.put("appid",WX_APP_ID);
//        contentValues.put("body", product.body);
//        contentValues.put("mch_id", WX_PARTNER_ID);
//        nonceStr = genNonceStr();
//        contentValues.put("nonce_str", nonceStr);
//        contentValues.put("notify_url", "http://weixin.qq.com");
//        contentValues.put("out_trade_no", genOutTradNo());
//        contentValues.put("spbill_create_ip", "196.168.1.1");
//        contentValues.put("total_fee", (int)(product.price * 100) + "");
//        contentValues.put("trade_type", "APP");
        List<NameValuePair> packageParams = new LinkedList<>();
        packageParams.add(new BasicNameValuePair("appid",WX_APP_ID));
        packageParams.add(new BasicNameValuePair("body", product.body));
        packageParams.add(new BasicNameValuePair("mch_id", WX_PARTNER_ID));
        nonceStr = genNonceStr();
        packageParams.add(new BasicNameValuePair("nonce_str", nonceStr));
        packageParams.add(new BasicNameValuePair("notify_url", "http://weixin.qq.com"));
        packageParams.add(new BasicNameValuePair("out_trade_no", genOutTradNo()));
        packageParams.add(new BasicNameValuePair("spbill_create_ip", "196.168.1.1"));
        packageParams.add(new BasicNameValuePair("total_fee", (int)(product.price * 100) + ""));
        packageParams.add(new BasicNameValuePair("trade_type", "APP"));
        //根据参数生成对应的XML文件(参看 文档 预支付订单 )
        String xmlString = createPrepayidParamXml(packageParams);
//        String xmlString = createPrepayidParamXml(contentValues);
        String xml = null;
        try {
            xml = new String(xmlString.getBytes(), "ISO8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //发送网络请求,获取 prepayid
        byte[] buf = Util.httpPost(url, xml);

        LogUtils.log(mActivity.getBaseContext(), "startPay: url = " + url + "xml = " + xml);
        if (buf == null || buf.length == 0) {
            //没有加载到数据
            LogUtils.log(mActivity.getBaseContext(), "startPay: 没有加载到prepayid");
            wxPayHandeler.sendEmptyMessage(PayTool.FetchPrepayIdFailure);
        } else {
            String content = new String(buf);
            LogUtils.log(mActivity.getBaseContext(), "startPay: content = " + content);
            String prepayid = parsePrepayid(content);
            if (prepayid == null) {
                return;
            }
            wxPayHandeler.sendEmptyMessage(PayTool.FetchPrepayIdSuccess);
            //发起支付
            sendPayReq(prepayid);
        }

    }
    /**
     * 根据参数生成 符合微信要求的XML
     */
    private String createPrepayidParamXml(List<NameValuePair> params) {
        String packageSign = getSign(params);
        params.add(new BasicNameValuePair("sign", packageSign));
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("xml");
        for (int i = 0; i < params.size(); i++) {
            root.addElement(params.get(i).getName()).addText(params.get(i).getValue());
        }
        return document.asXML();
    }
//    private String createPrepayidParamXml(ContentValues content){
//        String packageSign = getSign(content);
//        content.put("sign", packageSign);
//        Document document = DocumentHelper.createDocument();
//        Element root = document.addElement("xml");
//        for (Map.Entry<String, Object> entry : content.valueSet()){
//            root.addElement(entry.getKey()).addText((String) entry.getValue());
//        }
//        return document.asXML();
//    }
    /**
     * 按照微信的要求获取 参数的签名
     */
    private String getSign(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
//        sb.append(Constants.PARTNER_KEY); // 注意：不能hardcode在客户端，建议genPackage这个过程都由服务器端完成
        sb.append(WX_PARTNER_KEY); // 注意：不能hardcode在客户端，建议genPackage这个过程都由服务器端完成
        // 进行md5摘要前，params内容为原始内容，未经过url encode处理
        return MD5.getMD5(sb.toString()).toUpperCase();
    }
//    private String getSign(ContentValues content){
//        StringBuilder sb = new StringBuilder();
//        for (Map.Entry<String, Object> entry : content.valueSet()){
//            sb.append(entry.getKey())
//              .append('=')
//              .append(entry.getValue())
//              .append('&');
//        }
//        sb.append("key=");
//        sb.append(WX_PARTNER_KEY);
//        return MD5.getMD5(sb.toString()).toUpperCase();
//    }
    /**
     * 生成随机串
     */
    private String genNonceStr() {
        Random random = new Random();
        return MD5.getMD5(String.valueOf(random.nextInt(10000)));
    }

    /**
     * 生成 商户订单号
     */
    private String genOutTradNo() {
        Random random = new Random();
        return MD5.getMD5(String.valueOf(random.nextInt(10000)));
    }

    /**
     * 解析 请求prepayId返回的XML数据,获取到prepayid
     */
    private String parsePrepayid(String content) {
        if (content == null || content.length() <= 0) {
            return null;
        }
        Document document;
        JSONObject obj = new JSONObject();
        try {
            document = DocumentHelper.parseText(content);
            // 得到xml根元素
            Element root = document.getRootElement();
            // 得到根元素的所有子节点
            List<Element> elementList = root.elements();
            // 遍历所有子节点
            for (Element e : elementList) {
                obj.put(e.getName(),e.getText());
            }
        } catch (DocumentException | JSONException e) {
            e.printStackTrace();
        }
        String prepayId = null;
        if (obj.has("prepay_id")) {
            try {
                prepayId = obj.getString("prepay_id");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return prepayId;
    }

    /**
     * 根据prepayid,利用微信SDK发起支付
     */
    private void sendPayReq(String prepayId) {
        PayReq request = new PayReq();
        request.appId = WX_APP_ID;
        request.nonceStr = nonceStr;
        request.packageValue = "Sign=WXPay";
        request.partnerId = WX_PARTNER_ID;
        request.prepayId= prepayId;
        request.timeStamp = System.currentTimeMillis() / 1000 + "";
        //获取参数的签名
//        ContentValues contentValues = new ContentValues();
//        contentValues.put("appid", request.appId);
//        contentValues.put("noncestr", request.nonceStr);
//        contentValues.put("package", request.packageValue);
//        contentValues.put("partnerid", request.partnerId);
//        contentValues.put("prepayid", request.prepayId);
//        contentValues.put("timestamp", request.timeStamp);
//        request.sign= getSign(contentValues);
        List<NameValuePair> signParams = new LinkedList<>();
        signParams.add(new BasicNameValuePair("appid", request.appId));
        signParams.add(new BasicNameValuePair("noncestr", request.nonceStr));
        signParams.add(new BasicNameValuePair("package", request.packageValue));
        signParams.add(new BasicNameValuePair("partnerid", request.partnerId));
        signParams.add(new BasicNameValuePair("prepayid", request.prepayId));
        signParams.add(new BasicNameValuePair("timestamp", request.timeStamp));
        request.sign= getSign(signParams);
        String sendPayReqParamStr = "appId = " + request.appId + " partnerId = " + request.partnerId + " packageValue = " + request.packageValue + " prepayId = " + request.prepayId + " nonceStr = " + request.nonceStr + " timeStamp = " + request.timeStamp + " sign = " + request.sign;
        wxPayHandeler.sendEmptyMessage(PayTool.StartSendPayReq);
        // 调起支付
        iwxapi.sendReq(request);
    }


    /**
     * 支付宝支付相关代码
     */

    private static final int SDK_PAY_FLAG = 1;

    @SuppressLint("HandlerLeak")
    private Handler aliHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);
                    /**
                     * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
                     * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
                     * docType=1) 建议商户依赖异步通知
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息

                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
//                        Toast.makeText(mActivity.getBaseContext(), "支付宝支付成功", Toast.LENGTH_SHORT).show();
                        if (onPayResultListener != null) {
                            onPayResultListener.paySuccess();
                        }
                    } else {
                        // 判断resultStatus 为非"9000"则代表可能支付失败
                        // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (!TextUtils.equals(resultStatus, "8000")) {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
//                            Toast.makeText(mActivity.getBaseContext(), "支付宝支付失败", Toast.LENGTH_SHORT).show();
                            if (onPayResultListener != null) {
                                onPayResultListener.payFailure();
                            }
                        } else {
//                            Toast.makeText(mActivity.getBaseContext(), "支付结果确认中", Toast.LENGTH_SHORT).show();

                        }
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };

    /**
     * get the sdk version. 获取SDK版本号
     *
     */
    public void getSDKVersion() {
        PayTask payTask = new PayTask(mActivity);
        String version = payTask.getVersion();
        Toast.makeText(mActivity.getBaseContext(), version, Toast.LENGTH_SHORT).show();
    }

    /**
     * create the order info. 创建订单信息
     *
     */
    private String getOrderInfo(Product product) {

        // 签约合作者身份ID
//        String orderInfo = "partner=" + "\"" + cn.com.zhiwoo.Utils.aliUtils.Constants.PARTNER + "\"";
        String orderInfo = "partner=" + "\"" + ALi_PARTNER + "\"";


        // 签约卖家支付宝账号
//        orderInfo += "&seller_id=" + "\"" + cn.com.zhiwoo.Utils.aliUtils.Constants.SELLER + "\"";
        orderInfo += "&seller_id=" + "\"" + ALi_SELLER + "\"";

        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + getOutTradeNo() + "\"";

        // 商品名称
        orderInfo += "&subject=" + "\"" + product.body + "\"";

        // 商品详情
//        orderInfo += "&body=" + "\"" + body + "\"";

        // 商品金额
        orderInfo += "&total_fee=" + "\"" + product.price + "\"";

        // 服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\"" + "http://notify.msp.hk/notify.htm" + "\"";

        // 服务接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";

        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\"";

        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"30m\"";

        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        orderInfo += "&return_url=\"m.alipay.com\"";

        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
        // orderInfo += "&paymethod=\"expressGateway\"";

        return orderInfo;
    }

    /**
     * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
     *
     */
    private String getOutTradeNo() {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
        Date date = new Date();
        String key = format.format(date);

        Random r = new Random();
        key = key + r.nextInt();
        key = key.substring(0, 15);
        return key;
    }

    /**
     * sign the order info. 对订单信息进行签名
     *
     * @param content
     *            待签名订单信息
     */
    private String sign(String content) {
        return SignUtils.sign(content, ALi_RSA_PRIVATE);
    }

    public static void config(final Context context) {
        String jsonStr = null;
        try {
            FileInputStream inStream = context.openFileInput(Global.INFO_DATA_FILE_NAME);
            byte[] buffer = new byte[1024];
            int hasRead;
            StringBuilder sb = new StringBuilder();
            while ((hasRead = inStream.read(buffer)) != -1) {
                sb.append(new String(buffer, 0, hasRead));
            }
            inStream.close();
            jsonStr = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (jsonStr != null) {
            //设置好参数
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(jsonStr);
                WX_APP_ID = jsonObject.getString("WX_APP_ID");
                WX_PARTNER_ID = jsonObject.getString("WX_PARTNER_ID");
                WX_PARTNER_KEY = jsonObject.getString("WX_PARTNER_KEY");
                ALi_PARTNER = jsonObject.getString("ALi_PARTNER");
                ALi_RSA_PRIVATE = jsonObject.getString("ALi_RSA_PRIVATE");
                ALi_SELLER = jsonObject.getString("ALi_SELLER");
            } catch (JSONException e) {
                LogUtils.log("支付工具配置失败");
                e.printStackTrace();
            }
        } else {
            OkGo.get(Api.PAY_TOOL)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            try {
                                JSONObject jsonObject = new JSONObject(s);
                                WX_APP_ID = jsonObject.getString("WX_APP_ID");
                                WX_PARTNER_ID = jsonObject.getString("WX_PARTNER_ID");
                                WX_PARTNER_KEY = jsonObject.getString("WX_PARTNER_KEY");
                                ALi_PARTNER = jsonObject.getString("ALi_PARTNER");
                                ALi_RSA_PRIVATE = jsonObject.getString("ALi_RSA_PRIVATE");
                                ALi_SELLER = jsonObject.getString("ALi_SELLER");
                                FileOutputStream fos = context.openFileOutput(Global.INFO_DATA_FILE_NAME,
                                        Context.MODE_PRIVATE);
                                fos.write(s.getBytes());
                                fos.close();
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                                LogUtils.log("支付工具配置失败");
                            }
                        }
                    });
        }
    }

    /**
     * get the sign type we use. 获取签名方式
     *
     */
    private String getSignType() {
        return "sign_type=\"RSA\"";
    }

    /**
     * 商品模型
     */
    public static class Product {
        String body;
        float price;
        public Product(String body,float price) {
            this.body = body;
            this.price = price;
        }
    }
    public interface OnPayResultListener {
        void paySuccess();
        void payFailure();
    }
    private class MyBroadCastRecevier extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int result = intent.getExtras().getInt("result");
            if (result == 0) {
                if (onPayResultListener != null) {
                    onPayResultListener.paySuccess();
                }
            } else if (result == -1) {
                if (onPayResultListener != null) {
                    onPayResultListener.payFailure();
                }
            } else if (result == -2) {
                if (onPayResultListener != null) {
                    onPayResultListener.payFailure();
                }
            }
            mActivity.unregisterReceiver(broadcastReceiver);
            onPayResultListener = null;
        }
    }

}
