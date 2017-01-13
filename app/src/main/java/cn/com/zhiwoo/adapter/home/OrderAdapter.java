package cn.com.zhiwoo.adapter.home;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import cn.com.zhiwoo.R;
import cn.com.zhiwoo.bean.home.Order;


public class OrderAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Order> orders;

    public OrderAdapter(Context context,ArrayList<Order> orders) {
        this.context = context;
        this.orders = orders;
    }
    @Override
    public int getCount() {
        return orders.size();
    }

    @Override
    public Object getItem(int position) {
        return orders.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.home_order_item,null);
        }
        ViewHolder viewHolder = getViewHolder(convertView);
        Order order = orders.get(position);
        viewHolder.tourTextView.setText(order.getTourName());
        viewHolder.userTextView.setText(order.getUserName());
        viewHolder.reservationtimeTextView.setText("待定");
        viewHolder.dateTextView.setText(order.getDate());
        viewHolder.descTextView.setText(order.getProblem() == null ? "没有写咨询描述" : order.getProblem());


        return convertView;
    }
    private ViewHolder getViewHolder(View view) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        if (viewHolder == null) {
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }
        return viewHolder;
    }

    private class ViewHolder {
        private TextView descTextView;
        private TextView dateTextView;
        private TextView tourTextView;
        private TextView userTextView;
        private TextView reservationtimeTextView;

        ViewHolder(View view) {
            tourTextView = (TextView) view.findViewById(R.id.tour_textview);
            userTextView = (TextView) view.findViewById(R.id.user_textviw);
            dateTextView = (TextView) view.findViewById(R.id.time_textveiw);
            descTextView = (TextView) view.findViewById(R.id.desc_textviw);
            reservationtimeTextView = (TextView) view.findViewById(R.id.reservationtime_textview);

        }
    }
}
