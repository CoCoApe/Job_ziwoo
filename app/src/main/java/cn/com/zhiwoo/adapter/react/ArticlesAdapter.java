package cn.com.zhiwoo.adapter.react;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.lidroid.xutils.BitmapUtils;

import java.util.ArrayList;
import java.util.List;

import cn.com.zhiwoo.R;
import cn.com.zhiwoo.bean.react.ReactArticle;


public class ArticlesAdapter extends BaseAdapter {

    private List<ReactArticle.QBean> articles = new ArrayList<>();
    private Context context;
    private final BitmapUtils bitmapUtils;

    public ArticlesAdapter(Context context, ArrayList<ReactArticle.QBean> articles) {
        super();
        this.context = context;
        this.articles.clear();
        this.articles.addAll(articles);
        bitmapUtils = new BitmapUtils(context);
        bitmapUtils.configDefaultLoadingImage(R.drawable.react_article_bg_placeholer);
    }


    @Override
    public int getCount() {
        int size = articles==null ? 0 : articles.size();
        Log.i("aaaa", "getCount: getCount()执行"+size);
        return articles==null ? 0 : articles.size();
    }

    @Override
    public Object getItem(int position) {
        return articles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.i("aaaa", "getView: 执行了getView");
        if (convertView == null) {
            convertView = View.inflate(context,R.layout.react_article_item2,null);
//            ImageView imageView = (ImageView) convertView.findViewById(R.id.react_article_item_imageview);
//            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//            Point size = new Point();
//            wm.getDefaultDisplay().getSize(size);
//            int width = size.x;
//            int height = (int)(width * 700.0f / 1242.0f) + 1;
////            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
////            layoutParams.height = height;
//
//            imageView.setLayoutParams(width,height);
        }
        ViewHolder viewHolder = getViewHolder(convertView);
        bitmapUtils.display(viewHolder.imageView,articles.get(position).getPic_url());
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
        final ImageView imageView;
        ViewHolder(View view) {
            imageView = (ImageView) view.findViewById(R.id.react_article_item_imageview);
        }
    }

}
