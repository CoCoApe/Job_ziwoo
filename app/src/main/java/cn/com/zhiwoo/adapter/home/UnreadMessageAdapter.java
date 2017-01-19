package cn.com.zhiwoo.adapter.home;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import cn.com.zhiwoo.R;
import cn.com.zhiwoo.bean.home.Message;
import cn.com.zhiwoo.tool.ChatTool;



public class UnreadMessageAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Message> unreadMessages = new ArrayList<>();
    public UnreadMessageAdapter(Context context) {
        this.context = context;
        unreadMessages = ChatTool.sharedTool().getTipMessages();
    }
    @Override
    public int getCount() {
        return unreadMessages.size();
    }

    @Override
    public Object getItem(int position) {
        return unreadMessages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.home_unreadmessage_item,null);
        }
        ViewHolder viewHolder = getViewHolder(convertView);
        if (position == (getCount() - 1)) {
            viewHolder.dividerView.setVisibility(View.GONE);
        } else {
            viewHolder.dividerView.setVisibility(View.VISIBLE);
        }
        Message message = unreadMessages.get(position);
        Glide.with(context)
                .load(message.getUserIcon())
                .into(viewHolder.iconImageView);
        viewHolder.nickNmaeTextView.setText(message.getUserName());
        viewHolder.unredCountTextView.setText(message.getUnreadCount() + "");
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
        private View dividerView;
        private TextView unredCountTextView;

        ViewHolder(View view) {
            iconImageView = (ImageView) view.findViewById(R.id.icon_imageview);
            nickNmaeTextView = (TextView) view.findViewById(R.id.nickname_textview);
            unredCountTextView = (TextView) view.findViewById(R.id.unredcount_textview);
            dividerView = view.findViewById(R.id.divider_view);
        }
    }

    public void setUnreadMessages(ArrayList<Message> unreadMessages) {
        this.unreadMessages = unreadMessages;
    }
}
