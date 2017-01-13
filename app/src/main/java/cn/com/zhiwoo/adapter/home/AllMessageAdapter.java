package cn.com.zhiwoo.adapter.home;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;

import java.util.ArrayList;
import java.util.Date;

import cn.com.zhiwoo.R;
import cn.com.zhiwoo.bean.home.Message;
import cn.com.zhiwoo.tool.ChatTool;
import cn.com.zhiwoo.utils.DateUtils;
import cn.com.zhiwoo.utils.LogUtils;


public class AllMessageAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Message> allMessages = new ArrayList<>();
    private BitmapUtils bitmapUtils;
    public AllMessageAdapter(Context context) {
        this.context = context;
        this.bitmapUtils = new BitmapUtils(context);
        this.bitmapUtils.configDefaultLoadingImage(R.drawable.default_user_icon);
        allMessages = ChatTool.sharedTool().getAllMessages();
        for (Message message :
                allMessages) {
            LogUtils.log("全部消息: id = " + message.getUserId());
        }
    }
    @Override
    public int getCount() {
        return allMessages.size();
    }

    @Override
    public Object getItem(int position) {
        return allMessages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.home_allmessage_item,null);
        }
        ViewHolder viewHolder = getViewHolder(convertView);
        if (position == (getCount() - 1)) {
            viewHolder.dividerView.setVisibility(View.GONE);
        } else {
            viewHolder.dividerView.setVisibility(View.VISIBLE);
        }
        Message message = allMessages.get(position);
        bitmapUtils.display(viewHolder.iconImageView, message.getUserIcon());
        LogUtils.log("usericon : " + message.getUserIcon());
        viewHolder.nickNmaeTextView.setText(message.getUserName());
        Date date = new Date(message.getLastTime());
        viewHolder.timeTextView.setText(DateUtils.relativeDate(date));
        return convertView;
    }
    private ViewHolder getViewHolder(View view) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        if (viewHolder == null){
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }
        return viewHolder;
    }
    private class ViewHolder {

        private ImageView iconImageView;
        private TextView nickNmaeTextView;
        private TextView timeTextView;
        private View dividerView;

        ViewHolder(View view) {
            iconImageView = (ImageView) view.findViewById(R.id.icon_imageview);
            nickNmaeTextView = (TextView) view.findViewById(R.id.nickname_textview);
            timeTextView = (TextView) view.findViewById(R.id.time_textview);
            dividerView = view.findViewById(R.id.divider_view);
        }
    }

    public void setAllMessages(ArrayList<Message> allMessages) {
        this.allMessages = allMessages;
    }
}