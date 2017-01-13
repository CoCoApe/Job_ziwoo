package cn.com.zhiwoo.tool;

import com.lidroid.xutils.exception.HttpException;


public abstract class OnNetworkResponser {
    public abstract void onSuccess(String result);
    public abstract void onFailure(HttpException e, String s);
}
