package cn.com.zhiwoo.activity.home;

import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.exception.HttpException;

import java.util.ArrayList;
import java.util.HashMap;

import cn.com.zhiwoo.R;
import cn.com.zhiwoo.activity.base.BaseActivity;
import cn.com.zhiwoo.adapter.home.OrderAdapter;
import cn.com.zhiwoo.bean.home.Order;
import cn.com.zhiwoo.bean.main.Account;
import cn.com.zhiwoo.tool.AccountTool;
import cn.com.zhiwoo.tool.NetworkTool;
import cn.com.zhiwoo.tool.OnNetworkResponser;
import cn.com.zhiwoo.utils.LogUtils;


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
        HashMap<String,String> params = new HashMap<>();
        Account account = AccountTool.getCurrentAccount(getBaseContext());
        params.put("access_token",account.getAccessToken());
        params.put("user_id", account.getId());
        //[{"id":59,"user_id":1040,"tutor_id":9,"name":"test","gender":0,"age":null,"contact":"1234567890","problem":null,"prepaid":0,"status":0,"created_at":"2016-05-26T07:44:34.911Z","updated_at":"2016-05-26T07:44:34.911Z"}]
        NetworkTool.GET("http://121.201.7.33/zero/api/v1/consults", params, new OnNetworkResponser() {
            @Override
            public void onSuccess(String result) {
                LogUtils.log("全部预约" + result);
                Gson gson = new Gson();
                orders = gson.fromJson(result, new TypeToken<ArrayList<Order>>() {
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
            public void onFailure(HttpException e, String s) {
                progressHUD.showErrorWithStatus("订单加载失败");
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
