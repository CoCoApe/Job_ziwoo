package cn.com.zhiwoo.pager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import cn.com.zhiwoo.R;
import cn.com.zhiwoo.activity.react.ArticleDetailActivity;
import cn.com.zhiwoo.bean.react.BigPic;
import cn.com.zhiwoo.bean.react.ReactArticle;
import cn.com.zhiwoo.pager.base.BasePager;
import cn.com.zhiwoo.utils.Api;
import okhttp3.Call;
import okhttp3.Response;


public class ReactPager extends BasePager {


    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private ArrayList<ReactArticle.QBean> articles = new ArrayList<>();
    private ArrayList<ReactArticle.QBean> getArticles = new ArrayList<>();
    private List<ReactArticle.QBean> articleList1 = new ArrayList<>();
    private List<ReactArticle.QBean> articleList2 = new ArrayList<>();
    private List<ReactArticle.QBean> articleList3 = new ArrayList<>();
    private List<ReactArticle.QBean> articleList4 = new ArrayList<>();
    public ImageView reactImage;
    private String bigUrl = null;
    private SVProgressHUD mHud = new SVProgressHUD(mActivity);
    private ImageView monomerIb;
    private ImageView saveIb;
    private ImageView eqIb;
    private ImageView relationshipIb;
    private RecyclerAdapter recyclerAdapter;

    public ReactPager(Activity activity) {
        super(activity);
    }

    @Override
    public void onDeselected() {
    }

    @Override
    public void initView() {
        super.initView();
        View view = LayoutInflater.from(mActivity).inflate(R.layout.react_content,null);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.react_articles_swipeRefresh);
        recyclerView = (RecyclerView) view.findViewById(R.id.react_articles_recycler);

        reactImage = (ImageView) view.findViewById(R.id.react_default_image);
        flContent.addView(view);
        monomerIb = (ImageView) mDrawerLayout.findViewById(R.id.drawer_monomer_imageButton);
        saveIb = (ImageView) mDrawerLayout.findViewById(R.id.drawer_save_imageButton);
        eqIb = (ImageView) mDrawerLayout.findViewById(R.id.drawer_eq_imageButton);
        relationshipIb = (ImageView) mDrawerLayout.findViewById(R.id.drawer_relationship_imageButton);
        recyclerAdapter = new RecyclerAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        recyclerView.setAdapter(recyclerAdapter);
        monomerIb.setOnClickListener(this);
        saveIb.setOnClickListener(this);
        eqIb.setOnClickListener(this);
        reactImage.setOnClickListener(this);
        relationshipIb.setOnClickListener(this);
        leftImageView.setOnClickListener(this);
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public void initTitleBar() {
        titleView.setText("干货");
        leftImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(drawerView);
            }
        });
    }

    @Override
    public void initData() {
        loadData();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });

    }
    private void loadData() {
        mHud.showWithStatus("正在加载...", SVProgressHUD.SVProgressHUDMaskType.Clear);
        OkGo.get(Api.BIG_PIC)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        if (null != s){
                            BigPic bigPic = new Gson().fromJson(s,BigPic.class);
                            if (null != bigPic){
                                Glide.with(mActivity)
                                        .load(bigPic.getQ_pic())
                                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                                        .thumbnail(0.1f)
                                        .into(reactImage);
                                bigUrl = bigPic.getQ_art();
                                mHud.dismiss();
                            }
                        }
                    }
                });
        OkGo.get(Api.ARTICLE)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        Gson gson = new Gson();
                        articles = (ArrayList<ReactArticle.QBean>) gson.fromJson(s,ReactArticle.class).getQ();
                        for (int i = 0; i < articles.size(); i++) {
                            ReactArticle.QBean bean = articles.get(i);
                            switch (bean.getApp_tag()){
                                case 1:
                                    articleList1.add(bean);
                                    break;
                                case 2:
                                    articleList2.add(bean);
                                    break;
                                case 3:
                                    articleList3.add(bean);
                                    break;
                                case 4:
                                    articleList4.add(bean);
                                    break;
                            }
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        swipeRefreshLayout.setRefreshing(false);
                        mHud.showErrorWithStatus("网络不佳,请稍后再试");
                    }
                });
    }


    private void updataList(List<ReactArticle.QBean> list){
        if (null != list){
            getArticles.clear();
            getArticles.addAll(list);
            recyclerAdapter.notifyDataSetChanged();
            recyclerAdapter.notifyItemRemoved(recyclerAdapter.getItemCount());
            if (reactImage.getVisibility() != View.GONE){
                reactImage.setVisibility(View.GONE);
            }
        }else {
            mHud.showErrorWithStatus("网络不佳,请稍后再试");
        }
        mDrawerLayout.closeDrawers();
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder{
        View view;
        ImageView itemImage;
        RecyclerViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            itemImage = (ImageView) itemView.findViewById(R.id.react_article_item_imageview);
        }
    }

    private class RecyclerAdapter extends RecyclerView.Adapter<RecyclerViewHolder>{

        @Override
        public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mActivity).inflate(R.layout.react_article_item2, parent, false);
            return new RecyclerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerViewHolder holder, int position) {
            final ReactArticle.QBean article = getArticles.get(position);
            Glide.with(mActivity).
                    load(article.getPic_url())
                    .placeholder(R.drawable.react_article_bg_placeholer)
                    .into(holder.itemImage);
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mActivity, ArticleDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("article", article);
                    intent.putExtras(bundle);
                    mActivity.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return getArticles == null ? 0 : getArticles.size();
        }
    }


    @Override
    public void process(View v) {
        switch (v.getId()){
            case R.id.drawer_monomer_imageButton:
                updataList(articleList1);
                break;
            case R.id.drawer_save_imageButton:
                updataList(articleList2);
                break;
            case R.id.drawer_eq_imageButton:
                updataList(articleList3);
                break;
            case R.id.drawer_relationship_imageButton :
                updataList(articleList4);
                break;
            case R.id.react_default_image:
                if (null != bigUrl){
                    ReactArticle.QBean article = new ReactArticle.QBean();
                    article.setContent_url(bigUrl);
                    Intent intent = new Intent(mActivity, ArticleDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("article", article);
                    intent.putExtras(bundle);
                    mActivity.startActivity(intent);
                }else {
                    mHud.showErrorWithStatus("网络不佳,请稍后再试");
                }
                break;
        }
    }

    @Override
    public void titleBarLeftBtnClick() {

    }

    @Override
    public void titleBarRightBtnClick() {
    }

    @Override
    public void onResume() {

    }
}
