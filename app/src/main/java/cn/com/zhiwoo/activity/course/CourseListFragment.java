package cn.com.zhiwoo.activity.course;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.zhiwoo.R;
import cn.com.zhiwoo.bean.main.Account;
import cn.com.zhiwoo.bean.react.LessonEvent;
import cn.com.zhiwoo.service.MediaService;
import cn.com.zhiwoo.service.OnMediaChangeListener;
import cn.com.zhiwoo.tool.AccountTool;
import cn.com.zhiwoo.utils.Api;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by 25820 on 2017/2/15.
 */

public class CourseListFragment extends Fragment implements OnMediaChangeListener{
    private Context mContext;
    private ListView courseListView;
    private List<LessonEvent.DataBean> mList = new ArrayList<>();
    private CourseAdapter courseAdapter;
    private Account account;
    private int current_position = -1;
    private final String MEDIA_BROADCAST_ACTION = "cn.com.zhiwoo.activity.course";
    private MediaService.MediaBinder mMediaBinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext =getContext();
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.course_list_frg,container,false);
        courseListView = (ListView) view.findViewById(R.id.course_list_frg_listView);
        connect();
        initView();
        initData();
        initListener();
        return view;
    }

    private void initListener() {

    }

    private void initView() {
        courseAdapter = new CourseAdapter();
        courseListView.setAdapter(courseAdapter);
        account = AccountTool.getCurrentAccount(mContext);
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
                        courseAdapter.notifyDataSetChanged();
                    }
                });
    }



    class CourseAdapter extends BaseAdapter {

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
                convertView = View.inflate(mContext,R.layout.all_lesson_item,null);
            }
            Button buyBtn = (Button) convertView.findViewById(R.id.all_lesson_item_buy);
            buyBtn.setText("播放");
            final TextView textView = (TextView) convertView.findViewById(R.id.all_lesson_item_content);
            final LessonEvent.DataBean bean = mList.get(position);
            textView.setText(bean.getCourse_name());
            buyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startService(position);
                }
            });
            if (current_position == position){
                buyBtn.setText("暂停");
                buyBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMediaBinder.pause();
                    }
                });
            }
            return convertView;
        }
    }

    private void startService(int position){
//        先启动服务
        Intent intent = new Intent(mContext, MediaService.class);
        intent.setAction(MEDIA_BROADCAST_ACTION);
        intent.putExtra("current_position",position);
        intent.putExtra("course_list", (Serializable) mList);
        mContext.startService(intent);
    }



    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMediaBinder = (MediaService.MediaBinder) service;
            mMediaBinder.addOnMediaChangeListener(CourseListFragment.this);
            courseAdapter.notifyDataSetChanged();
        }
    };

    /** 绑定服务*/
    private void connect() {
        Intent intent = new Intent(mContext, MediaService.class);
        mContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }
    /** 断开服务*/
    private void disconnect() {
        mContext.unbindService(mConnection);
    }

    @Override
    public void onMediaPlay() {
        courseAdapter.notifyDataSetChanged();
    }

    @Override
    public void onMediaStart() {

    }

    @Override
    public void onMediaPause() {
        courseAdapter.notifyDataSetChanged();
    }

    @Override
    public void onMediaStop() {

    }

    @Override
    public void onMediaCompletion() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disconnect();
    }
}
