package cn.com.zhiwoo.activity.course;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

public class CourseListFragment extends Fragment implements OnMediaChangeListener,View.OnClickListener{
    private Context mContext;
    private ListView courseListView;
    private List<LessonEvent.DataBean> mList = new ArrayList<>();
    private CourseAdapter courseAdapter;
    private Account account;
    private int current_position = -1;
    private final String MEDIA_BROADCAST_ACTION = "cn.com.zhiwoo.activity.course";
    private MediaService.MediaBinder mMediaBinder;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.course_list_frg,container,false);
        courseListView = (ListView) view.findViewById(R.id.course_list_frg_listView);
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


    class CourseViewHolder{
        Button byButton;
        TextView textView;
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
            LessonEvent.DataBean bean = mList.get(position);
            final CourseViewHolder holder;
            if (convertView == null){
                holder = new CourseViewHolder();
                convertView = View.inflate(mContext,R.layout.all_lesson_item,null);
                holder.byButton = (Button) convertView.findViewById(R.id.all_lesson_item_buy);
                holder.textView = (TextView) convertView.findViewById(R.id.all_lesson_item_content);
                convertView.setTag(holder);
            }else {
                holder = (CourseViewHolder) convertView.getTag();
            }
            holder.byButton.setTag(position);
            holder.byButton.setText("播放");
            holder.textView.setText(bean.getCourse_name());
            holder.byButton.setOnClickListener(CourseListFragment.this);
            if (current_position == position){
                if (mMediaBinder.isPlaying()){
                    holder.byButton.setText("暂停");
                }else {
                    holder.byButton.setText("继续");
                }
            }
            return convertView;
        }
    }

    @Override
    public void onClick(View view) {
        Button button = (Button) view;
        int position = (int) view.getTag();
        if (position != mMediaBinder.getCurrentListPosition()){
            startService(position);
        }else {
            if (mMediaBinder.isPlaying()){
                mMediaBinder.pause();
                button.setText("继续");
            }else {
                mMediaBinder.start();
                button.setText("暂停");
            }
        }
    }


    private void startService(int position){
//        先启动服务
        Intent intent = new Intent(mContext, MediaService.class);
        intent.setAction(MEDIA_BROADCAST_ACTION);
        intent.putExtra("current_position",position);
        intent.putExtra("course_list", (Serializable) mList);
        mContext.startService(intent);
        current_position = mMediaBinder.getCurrentListPosition();
    }


    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMediaBinder = (MediaService.MediaBinder) service;
            mMediaBinder.addOnMediaChangeListener(CourseListFragment.this);
            current_position = mMediaBinder.getCurrentListPosition();

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
        int position = mMediaBinder.getCurrentListPosition();
        for (int i=0;i < courseListView.getChildCount();i++){
            CourseViewHolder holder = (CourseViewHolder) courseListView.getChildAt(i).getTag();
            if (position != i){
                holder.byButton.setText("播放");
            }else {
                holder.byButton.setText("暂停");
            }
        }
    }

    @Override
    public void onMediaStart() {
        ((CourseViewHolder) courseListView.getChildAt(mMediaBinder.getCurrentListPosition())
                .getTag()).byButton.setText("暂停");
    }

    @Override
    public void onMediaPause() {
        ((CourseViewHolder) courseListView.getChildAt(mMediaBinder.getCurrentListPosition())
                .getTag()).byButton.setText("继续");
    }

    @Override
    public void onMediaStop() {
        for (int i = 0; i < courseListView.getChildCount(); i++) {
            ((CourseViewHolder) courseListView.getChildAt(i).getTag()).byButton.setText("播放");
        }
        current_position = -1;
    }

    @Override
    public void onMediaCompletion() {
        ((CourseViewHolder) courseListView.getChildAt(mMediaBinder.getCurrentListPosition())
                .getTag()).byButton.setText("播放");
    }


    @Override
    public void onStart() {
        super.onStart();
        connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        disconnect();
    }
}
