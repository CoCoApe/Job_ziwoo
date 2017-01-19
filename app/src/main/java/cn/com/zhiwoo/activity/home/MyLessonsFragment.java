package cn.com.zhiwoo.activity.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;


import org.greenrobot.eventbus.Subscribe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.zhiwoo.R;
import cn.com.zhiwoo.bean.main.Account;
import cn.com.zhiwoo.bean.react.LessonEvent;
import cn.com.zhiwoo.tool.AccountTool;
import cn.com.zhiwoo.utils.Api;
import de.greenrobot.event.EventBus;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by 25820 on 2017/1/10.
 */

public class MyLessonsFragment extends Fragment {
    private ListView mListView;
    private Activity mActivity;
    private List<LessonEvent.DataBean> mList = new ArrayList<>();
    private MyLessonsAdapter adapter;
    private Account account;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
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
        adapter = new MyLessonsAdapter();
        mListView.setAdapter(adapter);
        account = AccountTool.getCurrentAccount(mActivity);
    }

    private void initData() {
        Map<String,String> params = new HashMap<>();
        params.put("userId",account.getId());
        params.put("isCost","2");
        OkGo.post(Api.LESSONS)
            .params(params)
            .execute(new StringCallback() {
                @Override
                public void onSuccess(String s, Call call, Response response) {
                    LessonEvent lessonEvent = new Gson().fromJson(s,LessonEvent.class);
                    mList.clear();
                    mList.addAll(lessonEvent.getData());
                    adapter.notifyDataSetChanged();
                }
            });
    }

    private void initListener() {

    }

    class MyLessonsAdapter extends BaseAdapter{

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
            Button playBtn = (Button) convertView.findViewById(R.id.all_lesson_item_buy);
            TextView textView = (TextView) convertView.findViewById(R.id.all_lesson_item_content);
            playBtn.setText("播放");
            final LessonEvent.DataBean bean = mList.get(position);
            textView.setText(bean.getCourse_name());
            playBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mActivity,MediaActivity.class);
                    Bundle bundle = new Bundle();
                    intent.putExtra("currentPosition",position);
                    bundle.putSerializable("toPlayList", (Serializable) mList);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
            return convertView;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(LessonEvent.DataBean bean){
        mList.add(bean);
        adapter.notifyDataSetChanged();
    }
}
