package cn.com.zhiwoo.activity.tutor;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.lidroid.xutils.exception.HttpException;

import java.util.HashMap;

import cn.com.zhiwoo.R;
import cn.com.zhiwoo.activity.base.BaseActivity;
import cn.com.zhiwoo.bean.main.Account;
import cn.com.zhiwoo.tool.AccountTool;
import cn.com.zhiwoo.tool.NetworkTool;
import cn.com.zhiwoo.tool.OnNetworkResponser;
import cn.com.zhiwoo.tool.PayTool;
import cn.com.zhiwoo.utils.LogUtils;


public class ReservationActivity extends BaseActivity {

    private String tourId;
    private EditText nameEditText;
    private RadioGroup radioGroup;
    private EditText ageEditText;
    private EditText phoneEditText;
    private EditText problemEditText;
    private ScrollView scrollView;
    private SVProgressHUD progressHUD;

    @Override
    public void initView() {
        super.initView();
        scrollView = (ScrollView) View.inflate(this, R.layout.tour_reservation_activity, null);
        flContent.addView(scrollView);

        nameEditText = (EditText) scrollView.findViewById(R.id.name_edittext);
        radioGroup = (RadioGroup) scrollView.findViewById(R.id.male_radiogroup);
        ageEditText = (EditText) scrollView.findViewById(R.id.age_edittext);
        phoneEditText = (EditText) scrollView.findViewById(R.id.phone_edittext);
        problemEditText = (EditText) scrollView.findViewById(R.id.problem_edittext);
        problemEditText.setSingleLine(false);
        problemEditText.setMaxLines(5);
        progressHUD = new SVProgressHUD(this);
    }

    @Override
    public void initData() {
        titleView.setText("预约导师");
        tourId = getIntent().getStringExtra("tourId");
    }

    @Override
    public void titleBarLeftBtnClick() {
        finish();
    }

    @Override
    public void initListener() {
        scrollView.findViewById(R.id.commit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String phone = phoneEditText.getText().toString();
                if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(getBaseContext(),"请输入联系方式",Toast.LENGTH_SHORT).show();
                    return;
                }
                PayTool payTool = new PayTool(ReservationActivity.this, new PayTool.OnPayResultListener() {
                    @Override
                    public void paySuccess() {
                        HashMap<String,String> params = new HashMap<>();
                        Account account = AccountTool.getCurrentAccount(getBaseContext());
                        params.put("access_token",account.getAccessToken());
                        params.put("user_id",account.getId());
                        params.put("tutor_id",tourId);

                        String name = nameEditText.getText().toString();
                        if (!TextUtils.isEmpty(name)) {
                            params.put("name",name);
                        }

                        String gender = radioGroup.getCheckedRadioButtonId() == R.id.male ? "男" : "女";
                        params.put("gender",gender);

                        String age = ageEditText.getText().toString();
                        if (!TextUtils.isEmpty(age)) {
                            params.put("age",age);
                        }

                        params.put("contact",phone);

                        String problem = problemEditText.getText().toString();
                        if (!TextUtils.isEmpty(problem)) {
                            params.put("problem",problem);
                        }

                        params.put("prepaid","100");

                        NetworkTool.POST("http://121.201.7.33/zero/api/v1/consults", params, new OnNetworkResponser() {
                            @Override
                            public void onSuccess(String result) {
//                                progressHUD.showSuccessWithStatus("预约成功");
                                Toast.makeText(getBaseContext(),"预约成功",Toast.LENGTH_SHORT).show();
                                finish();
                            }

                            @Override
                            public void onFailure(HttpException e, String s) {
//                                progressHUD.showErrorWithStatus("预约失败");
                                Toast.makeText(getBaseContext(),"提交预约失败,请主动联系客服",Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }

                    @Override
                    public void payFailure() {
                        HashMap<String,String> params = new HashMap<>();
                        Account account = AccountTool.getCurrentAccount(getBaseContext());
                        params.put("access_token",account.getAccessToken());
                        params.put("user_id",account.getId());
                        params.put("tutor_id",tourId);

                        String name = nameEditText.getText().toString();
                        if (!TextUtils.isEmpty(name)) {
                            params.put("name",name);
                        }

                        String gender = radioGroup.getCheckedRadioButtonId() == R.id.male ? "男" : "女";
                        params.put("gender",gender);

                        String age = ageEditText.getText().toString();
                        if (!TextUtils.isEmpty(age)) {
                            params.put("age",age);
                        }

                        params.put("contact",phone);

                        String problem = problemEditText.getText().toString();
                        if (!TextUtils.isEmpty(problem)) {
                            params.put("problem",problem);
                        }

                        NetworkTool.POST("http://121.201.7.33/zero/api/v1/consults", params, new OnNetworkResponser() {
                            @Override
                            public void onSuccess(String result) {
                                LogUtils.log("未支付定金,提交订单成功,result: " + result);
                                finish();
                            }

                            @Override
                            public void onFailure(HttpException e, String s) {
                                LogUtils.log("未支付定金,提交订单失败");
                                finish();
                            }
                        });
                    }
                });
                // [支付]
                PayTool.Product product = new PayTool.Product("预约咨询-定金",100.0f);
//                PayTool.Product product = new PayTool.Product("预约咨询-定金",0.01f);
                payTool.pay(product);
            }
        });
    }

    @Override
    public void process(View v) {

    }
}
