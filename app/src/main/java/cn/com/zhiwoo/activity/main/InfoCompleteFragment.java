package cn.com.zhiwoo.activity.main;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import cn.com.zhiwoo.R;
import cn.com.zhiwoo.activity.home.ProvincesPickerDialog;
import cn.com.zhiwoo.tool.AccountTool;
import cn.com.zhiwoo.tool.ChatTool;
import cn.com.zhiwoo.utils.LogUtils;

/**
 * Created by 25820 on 2016/12/12.
 */

public class InfoCompleteFragment extends Fragment implements View.OnClickListener{
    private TextView ageTv;
    private TextView areaTv;
    private TextView expectationTv;
    private LinearLayout ageLl;
    private LinearLayout areaLl;
    private LinearLayout expectationLl;
    private Button nextBtn;
    private Context mContext;
    private int ageInt;
    private String phone;
    private String birth;
    private static final String[] expectations= new String[]{
            "挽回真命","自我提升","提升情商","长期关系"
    };

    public static InfoCompleteFragment newInstance(String phone){
        InfoCompleteFragment fragment = new InfoCompleteFragment();
        Bundle bundle = new Bundle();
        bundle.putCharSequence("phone",phone);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        this.phone = (String) getArguments().get("phone");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_info_fragment,container,false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        ageTv = (TextView) view.findViewById(R.id.info_complete_age);
        areaTv = (TextView) view.findViewById(R.id.info_complete_area);
        expectationTv = (TextView) view.findViewById(R.id.info_complete_expectation);
        nextBtn = (Button) view.findViewById(R.id.main_bind_next);
        ageLl = (LinearLayout) view.findViewById(R.id.info_complete_age_ll);
        areaLl = (LinearLayout) view.findViewById(R.id.info_complete_area_ll);
        expectationLl = (LinearLayout) view.findViewById(R.id.info_complete_expectation_ll);
        ageLl.setOnClickListener(this);
        areaLl.setOnClickListener(this);
        expectationLl.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        final Calendar calendar = Calendar.getInstance();
        switch (v.getId()){
            case R.id.info_complete_age_ll:
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("请选择生日日期：");
                View view = View.inflate(mContext,R.layout.date_picker_layout,null);
                final DatePicker datePicker = (DatePicker) view.findViewById(R.id.date_picker);
                builder.setView(view);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int year = datePicker.getYear();
                        int month = datePicker.getMonth();
                        int dayOfMonth = datePicker.getDayOfMonth();
                        ageInt = calendar.get(Calendar.YEAR) - year;
                        ageTv.setText(ageInt+"岁");
                        birth = year+"-"+month+"-"+dayOfMonth;
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.show();
                break;
            case R.id.info_complete_area_ll:
                ProvincesPickerDialog provincesPickerDialog = new ProvincesPickerDialog(mContext, new ProvincesPickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(final String provinceStr) {
                        areaTv.setText(provinceStr);
                    }
                }, "选择城市", "广东 广州");
                provincesPickerDialog.show();
                break;
            case R.id.info_complete_expectation_ll:
                new AlertDialog.Builder(mContext).setTitle("请选择期望服务：")
                        .setItems(expectations, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        expectationTv.setText(expectations[which]);
                    }
                }).show();
                break;
            case R.id.main_bind_next:
                String age = ageTv.getText().toString();
                String area = areaTv.getText().toString();
                String expectation = expectationTv.getText().toString();
                if (TextUtils.isEmpty(age)||TextUtils.isEmpty(area)){
                    Toast.makeText(mContext, "请选择年龄和地区", Toast.LENGTH_SHORT).show();
                }else {
                    AccountTool.updateAllInfo(phone, birth, area, expectation, mContext, new AccountTool.UpdateResultListener() {
                        @Override
                        public void updateSuccess() {
                            if (AccountTool.isLogined(mContext)) {
                                //新账号登录成功,配置好聊天工具
                                LogUtils.log("新账号登录,配置聊天工具");
                                APP app = (APP) getActivity().getApplication();
                                ChatTool.config(app.getMainActivity());
                            }
                            ChatTool.sharedTool().login();
                            Intent intent1 = new Intent("userinfo_change");
                            mContext.sendBroadcast(intent1);
                            getActivity().onBackPressed();
                        }
                        @Override
                        public void updateFailure() {
                            Toast.makeText(mContext, "更新失败！", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                break;
        }
    }
}
