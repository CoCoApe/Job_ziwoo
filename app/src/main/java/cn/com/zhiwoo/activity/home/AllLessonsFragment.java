package cn.com.zhiwoo.activity.home;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.zhiwoo.R;
import cn.com.zhiwoo.bean.main.Account;
import cn.com.zhiwoo.bean.react.LessonEvent;
import cn.com.zhiwoo.tool.AccountTool;
import cn.com.zhiwoo.tool.PayTool;
import cn.com.zhiwoo.utils.Api;
import de.greenrobot.event.EventBus;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by 25820 on 2017/1/10.
 */

public class AllLessonsFragment extends Fragment {
    private ListView mListView;
    private Activity mActivity;
    private List<LessonEvent.DataBean> mList = new ArrayList<>();
    private AllLessonsAdapter allLessonsAdapter;
    private Account account;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.all_lesson_fragment,container,false);
        mListView = (ListView) view.findViewById(R.id.all_lesson_list);
        initView();
        initData();
        initListener();
        return view;
    }

    private void initView() {
        allLessonsAdapter = new AllLessonsAdapter();
        mListView.setAdapter(allLessonsAdapter);
        account = AccountTool.getCurrentAccount(mActivity);
    }

    private void initData() {
        Map<String,String> params = new HashMap<>();
        params.put("userId",account.getId());
        params.put("isCost","1");
        OkGo.post(Api.LESSONS)
                .cacheKey("AllLessons")
                .params(params)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        LessonEvent lessonEvent = new Gson().fromJson(s,LessonEvent.class);
                        mList.clear();
                        mList.addAll(lessonEvent.getData());
                        allLessonsAdapter.notifyDataSetChanged();
                        Log.i("OkGo_post", "onSuccess: "+s);
                    }
                });
    }

    private void initListener() {

    }

    private void showDialog(final LessonEvent.DataBean bean, final int position) {
        View view = View.inflate(getContext(),R.layout.lesson_buy_dialog_layout,null);
        final Dialog lesson_buy_dialog = new AlertDialog.Builder(getContext(),R.style.lesson_buy_dialog)
                .setView(view).create();
        view.findViewById(R.id.dialog_lesson_name).setSelected(true);
        view.findViewById(R.id.dialog_lesson_buy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lesson_buy_dialog.dismiss();
                PayTool payTool = new PayTool(mActivity, new PayTool.OnPayResultListener() {
                    @Override
                    public void paySuccess() {
                        isPay(bean);
                        mList.remove(position);
                        allLessonsAdapter.notifyDataSetChanged();
                        ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.lesson_pager);
                        viewPager.setCurrentItem(1);
                        EventBus.getDefault().post(bean);
                    }
                    @Override
                    public void payFailure() {

                    }
                });
                PayTool.Product product = new PayTool.Product(bean.getCourse_name(),Float.valueOf(bean.getPay()));
                payTool.pay(product);
            }
        });

        lesson_buy_dialog.setCanceledOnTouchOutside(true);
        Window dialogWindow = lesson_buy_dialog.getWindow();
        if (dialogWindow != null) {
            dialogWindow.setGravity(Gravity.BOTTOM);
            dialogWindow.setWindowAnimations(R.style.lesson_buy_dialog_animation); // 添加动画
            WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
            lp.x = 0; // 新位置X坐标
            lp.y = 0; // 新位置Y坐标
            view.measure(0, 0);
            lp.width = getResources().getDisplayMetrics().widthPixels; // 宽度
            lp.height = (int) (getResources().getDisplayMetrics().heightPixels * 0.6); // 高度
//            lp.height = view.getMeasuredHeight();
            lp.alpha = 1f; // 透明度
            dialogWindow.setAttributes(lp);
            lesson_buy_dialog.show();
        }
    }


    class AllLessonsAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return mList == null ? 0 : mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null){
                convertView = View.inflate(mActivity,R.layout.all_lesson_item,null);
            }
            Button buyBtn = (Button) convertView.findViewById(R.id.all_lesson_item_buy);
            TextView textView = (TextView) convertView.findViewById(R.id.all_lesson_item_content);
            final LessonEvent.DataBean bean = mList.get(position);
            Log.i("getView", "getView: "+bean.getCourse_name());
            textView.setText(bean.getCourse_name());
            buyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (AccountTool.isLogined(mActivity)){
                        showDialog(bean,position);
                    }
                }
            });
            return convertView;
        }
    }

    private void isPay(LessonEvent.DataBean bean){
        Map<String,String> params = new HashMap<>();
        params.put("courseId",bean.getCourse_id());
        params.put("userId",account.getId());
        params.put("pay",bean.getPay());
        params.put("modify","1");
        OkGo.post(Api.IS_COST_LESSON)
                .params(params)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        initData();
                    }
                });
    }
}
