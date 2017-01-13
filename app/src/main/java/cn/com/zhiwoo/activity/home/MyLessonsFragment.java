package cn.com.zhiwoo.activity.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import cn.com.zhiwoo.R;
import de.greenrobot.event.EventBus;

/**
 * Created by 25820 on 2017/1/10.
 */

public class MyLessonsFragment extends Fragment {
    private ListView mListView;
    private Activity mActivity;
    private List<LessonEvent> mList = new ArrayList<>();
    private MyLessonsAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
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
    }

    private void initData() {

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
            textView.setText(mList.get(position).getName());
            playBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mActivity,MediaActivity.class);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(LessonEvent event) {
        mList.add(event);
        adapter.notifyDataSetChanged();
        ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.lesson_pager);
        viewPager.setCurrentItem(1);
    }

}
