package cn.com.zhiwoo.adapter.tutor;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;

import java.util.ArrayList;

import cn.com.zhiwoo.R;
import cn.com.zhiwoo.bean.tutor.Tour;

public class TourAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Tour> tours;
    private OnConsultButtonClickListener onConsultButtonClickListener;
    public TourAdapter(Context context, ArrayList<Tour> tours, OnConsultButtonClickListener onConsultButtonClickListener) {
        super();
        this.context = context;
        this.tours = tours;
        this.onConsultButtonClickListener = onConsultButtonClickListener;
    }
    @Override
    public int getCount() {
        return tours.size();
    }

    @Override
    public Object getItem(int position) {
        return tours.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.tour_tour_item,null);
        }
        ViewHolder viewHolder = getViewHolder(convertView);
        //绑定数据
        Tour tour = tours.get(position);
        Tour.Points points = tour.getPoints();
        Glide.with(context)
                .load(tour.getHeadImageUrl())
                .into(viewHolder.iconImageView);
        viewHolder.nameTextView.setText(tour.getNickName());
        viewHolder.short_introTextView.setText(tour.getShort_intro());
        viewHolder.relationImpelImageView.setImageResource(getRatingRes(points.getRelationImpel()));
        viewHolder.matrimonyHoldImageView.setImageResource(getRatingRes(points.getMatrimonyHold()));
        viewHolder.dispenseSingleImageView.setImageResource(getRatingRes(points.getDispenseSingle()));
        viewHolder.retrieveLoverImageView.setImageResource(getRatingRes(points.getRetrieveLover()));
//        viewHolder.dispenseSingleImageView.setImageResource(context.getResources().getIdentifier("star_" + tour.getPoints().getDispenseSingle(), "drawable", context.getPackageName()));
//        viewHolder.matrimonyHoldImageView.setImageResource(context.getResources().getIdentifier("star_"+tour.getPoints().getMatrimonyHold(),"drawable",context.getPackageName()));
//        viewHolder.relationImpelImageView.setImageResource(context.getResources().getIdentifier("star_"+tour.getPoints().getRelationImpel(),"drawable",context.getPackageName()));
//        viewHolder.retrieveLoverImageView.setImageResource(context.getResources().getIdentifier("star_" + tour.getPoints().getRetrieveLover(), "drawable", context.getPackageName()));
        QuickConsultButtonOnClickListener quickConsultButtonOnClickListener = new QuickConsultButtonOnClickListener(position);
        viewHolder.quickconsultButton.setOnClickListener(quickConsultButtonOnClickListener);
        PhoneConsultButtonOnClickListener phoneConsultButtonOnClickListener = new PhoneConsultButtonOnClickListener(position);
        viewHolder.phoneconsultButton.setOnClickListener(phoneConsultButtonOnClickListener);
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

        ImageView iconImageView;
        TextView nameTextView;
        TextView short_introTextView;
        ImageView relationImpelImageView;
        ImageView retrieveLoverImageView;
        ImageView matrimonyHoldImageView;
        ImageView dispenseSingleImageView;
        Button quickconsultButton;
        Button phoneconsultButton;

        ViewHolder(View view) {
            iconImageView = (ImageView) view.findViewById(R.id.old_item_icon);
            nameTextView = (TextView) view.findViewById(R.id.old_item_name);
            short_introTextView = (TextView) view.findViewById(R.id.old_item_intro);
            relationImpelImageView = (ImageView) view.findViewById(R.id.ratingBar_1);
            retrieveLoverImageView = (ImageView) view.findViewById(R.id.ratingBar_2);
            matrimonyHoldImageView = (ImageView) view.findViewById(R.id.ratingBar_3);
            dispenseSingleImageView = (ImageView) view.findViewById(R.id.ratingBar_4);
            quickconsultButton = (Button) view.findViewById(R.id.tour_sub_consult_btn);
            phoneconsultButton = (Button) view.findViewById(R.id.tour_sub_phone_btn);
        }
    }
    private class QuickConsultButtonOnClickListener implements View.OnClickListener {
        private int position;
        QuickConsultButtonOnClickListener(int position) {
            this.position = position;
        }
        @Override
        public void onClick(View v) {
            if (onConsultButtonClickListener != null) {
                onConsultButtonClickListener.onQuickConsultButtonClick(position);
            }
        }
    }
    private class PhoneConsultButtonOnClickListener implements View.OnClickListener {
        private int position;
        PhoneConsultButtonOnClickListener(int position) {
            this.position = position;
        }
        @Override
        public void onClick(View v) {
            if (onConsultButtonClickListener != null) {
                onConsultButtonClickListener.onPhoneConsultButtonClick(position);
            }
        }
    }
    public interface OnConsultButtonClickListener {
        void onQuickConsultButtonClick(int position);
        void onPhoneConsultButtonClick(int position);
    }

    private int getRatingRes(int rating) {
        switch (rating) {
            case 1:
                return R.drawable.new_star_1;
            case 2:
                return R.drawable.new_star_2;
            case 3:
                return R.drawable.new_star_3;
            case 4:
                return R.drawable.new_star_4;
            case 5:
                return R.drawable.new_star_5;
            default:
                return R.drawable.new_star_5;
        }
    }

}
