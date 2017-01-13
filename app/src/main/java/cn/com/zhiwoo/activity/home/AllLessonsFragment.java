package cn.com.zhiwoo.activity.home;

import android.app.Activity;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.com.zhiwoo.R;
import cn.com.zhiwoo.tool.AccountTool;
import cn.com.zhiwoo.tool.PayTool;
import de.greenrobot.event.EventBus;

/**
 * Created by 25820 on 2017/1/10.
 */

public class AllLessonsFragment extends Fragment {
    private ListView mListView;
    private Activity mActivity;
    private List<String> mList = new ArrayList<>();
    private AllLessonsAdapter allLessonsAdapter;

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

    }

    private void initData() {
        mList.add("课程=====1");
        mList.add("课程=====2");
        mList.add("课程=====3");
        mList.add("课程=====4");
        Collections.reverse(mList);
        allLessonsAdapter.notifyDataSetChanged();
    }

    private void initListener() {

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
            textView.setText(mList.get(position));
            buyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (AccountTool.isLogined(mActivity)){
                        PayTool payTool = new PayTool(mActivity, new PayTool.OnPayResultListener() {
                            @Override
                            public void paySuccess() {
                                mList.remove(position);
                                allLessonsAdapter.notifyDataSetChanged();
                                EventBus.getDefault().post(new LessonEvent("课程1",0.01f,"http://yqall02.baidupcs.com/file/754e3c6b9d7f55541587208fcf755b6b?bkt=p3-0000bf57292ac4ace2cd8259eeae9d0c9e11&fid=3859092981-250528-878528322779116&time=1484119511&sign=FDTAXGERLBH-DCb740ccc5511e5e8fedcff06b081203-hjp9DBWpipYuLbYvFMuZY541sxY%3D&to=yqhb&fm=Yan,B,T,t&sta_dx=8325058&sta_cs=1&sta_ft=mp3&sta_ct=0&sta_mt=0&fm2=Yangquan,B,T,t&newver=1&newfm=1&secfm=1&flow_ver=3&pkey=0000bf57292ac4ace2cd8259eeae9d0c9e11&sl=76480590&expires=8h&rt=sh&r=570511750&mlogid=246829529371701007&vuk=3859092981&vbdid=2480269028&fin=crash.mp3&fn=crash.mp3&slt=pm&uta=0&rtype=1&iv=0&isw=0&dp-logid=246829529371701007&dp-callid=0.1.1&csl=80&csign=D%2BbeukNG0g1BaX8zKFT4%2Bo1rdxk%3D"));
                            }
                            @Override
                            public void payFailure() {

                            }
                        });
                        PayTool.Product product = new PayTool.Product("咨我课程购买",0.01f);
                        payTool.pay(product);
                    }
                }
            });
            return convertView;
        }
    }
}
