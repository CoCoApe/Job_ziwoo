package cn.com.zhiwoo.service;

/**
 * Created by 25820 on 2017/2/20.
 */

public interface OnMediaChangeListener {
    public void onMediaPlay();
    public void onMediaStart();
    public void onMediaPause();
    public void onMediaStop();
    public void onMediaCompletion();
}
