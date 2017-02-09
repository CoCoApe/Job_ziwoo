package cn.com.zhiwoo.activity.home;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.com.zhiwoo.R;
import cn.com.zhiwoo.activity.base.BaseActivity;
import cn.com.zhiwoo.adapter.home.OrderAdapter;
import cn.com.zhiwoo.bean.home.Order;
import cn.com.zhiwoo.bean.main.Account;
import cn.com.zhiwoo.tool.AccountTool;
import cn.com.zhiwoo.utils.Api;
import cn.com.zhiwoo.utils.LogUtils;
import okhttp3.Call;
import okhttp3.Response;


public class OrdersActivity extends BaseActivity {

    private ListView listView;
    private SVProgressHUD progressHUD;
    private ArrayList<Order> orders;

    @Override
    public void initView() {
        super.initView();
        LinearLayout linearLayout = (LinearLayout) View.inflate(this, R.layout.home_order_activity,null);
        flContent.addView(linearLayout);
        listView = (ListView) linearLayout.findViewById(R.id.order_listview);
        progressHUD = new SVProgressHUD(this);
    }

    @Override
    public void initData() {
        titleView.setText("我的订单");
        progressHUD.showWithStatus("正在加载订单...");
        Map<String,String> params = new HashMap<>();
        Account account = AccountTool.getCurrentAccount(getBaseContext());
        params.put("access_token",account.getAccessToken());
        params.put("user_id", account.getId());
        OkGo.get(Api.ORDERS)
                .params(params)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        LogUtils.log("全部预约" + s);
                        Gson gson = new Gson();
                        orders = gson.fromJson(s, new TypeToken<ArrayList<Order>>() {
                        }.getType());
                        for (Order order : orders) {
                            order.loadMoreInfo();
                        }
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (orders.size() == 0) {
                                    progressHUD.showSuccessWithStatus("您当前没有订单");
                                } else {
                                    progressHUD.showSuccessWithStatus("订单加载成功");
                                    listView.setAdapter(new OrderAdapter(getBaseContext(), orders));
                                }
                            }
                        }, 500);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        progressHUD.showErrorWithStatus("订单加载失败");
                        super.onError(call, response, e);
                    }
                });
    }

    @Override
    public void initListener() {

    }

    @Override
    public void process(View v) {

    }
}
