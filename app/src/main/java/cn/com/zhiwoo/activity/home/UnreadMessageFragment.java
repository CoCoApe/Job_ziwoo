package cn.com.zhiwoo.activity.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import cn.com.zhiwoo.R;
import cn.com.zhiwoo.activity.consult.ConsultChatActivity;
import cn.com.zhiwoo.adapter.home.UnreadMessageAdapter;
import cn.com.zhiwoo.bean.home.Message;
import cn.com.zhiwoo.bean.tutor.Tour;
import cn.com.zhiwoo.bean.tutor.User;
import cn.com.zhiwoo.tool.ChatTool;


public class UnreadMessageFragment extends BaseMessageFragment {

    private ListView listView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_unreadmessage_fragment,null);
        listView = (ListView) view.findViewById(R.id.unreadmessage_listview);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UnreadMessageAdapter adapter = (UnreadMessageAdapter) listView.getAdapter();
                Message message = (Message) adapter.getItem(position);

                Intent intent = new Intent(getActivity(), ConsultChatActivity.class);
                Bundle bundle = new Bundle();

                User user = new Tour();
                user.setId(message.getUserId());
                user.setNickName(message.getUserName());
                user.setHeadImageUrl(message.getUserIcon());
                //[测试id]
//                user.setId("1652");
                bundle.putSerializable("user", user);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setAdapter(new UnreadMessageAdapter(getActivity()));
    }

    @Override
    void refreshData() {
        ArrayList<Message> unreadMessages = ChatTool.sharedTool().getTipMessages();
        UnreadMessageAdapter adapter = (UnreadMessageAdapter) listView.getAdapter();
        adapter.setUnreadMessages(unreadMessages);
        adapter.notifyDataSetChanged();
    }
}
