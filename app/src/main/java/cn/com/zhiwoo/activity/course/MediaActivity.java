package cn.com.zhiwoo.activity.course;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.List;

import cn.com.zhiwoo.R;
import cn.com.zhiwoo.activity.base.BaseActivity;
import cn.com.zhiwoo.bean.react.LessonEvent;
import cn.com.zhiwoo.service.MediaService;
import cn.com.zhiwoo.service.OnMediaChangeListener;

/**
 * Created by 25820 on 2017/1/12.
 */
public class MediaActivity extends BaseActivity implements SeekBar.OnSeekBarChangeListener, OnMediaChangeListener, Handler.Callback{
    private TextView current_time;
    private TextView end_time;
    private SeekBar seekBar;
    private Button start_btn;
    private Button last_btn;
    private Button next_btn;
    private int current_position;
    private List<LessonEvent.DataBean> mList;
    private MediaService.MediaBinder mMediaBinder;
    private Handler mHandler;
    private boolean isTrackingTouch = false;


    @Override
    public void initView() {
        super.initView();
        mHandler = new Handler(this);
        View view = View.inflate(this, R.layout.activity_media,null);
        current_time = (TextView) view.findViewById(R.id.media_current_time);
        end_time = (TextView) view.findViewById(R.id.media_last_time);
        seekBar = (SeekBar) view.findViewById(R.id.media_seekBar);
        start_btn = (Button) view.findViewById(R.id.media_start_button);
        last_btn = (Button) view.findViewById(R.id.media_last_button);
        next_btn = (Button) view.findViewById(R.id.media_next_button);
        flContent.addView(view);
        connect();
    }

    @Override
    public void initData() {

    }

    @Override
    public void initListener() {
        seekBar.setOnSeekBarChangeListener(this);
        start_btn.setOnClickListener(this);
        last_btn.setOnClickListener(this);
        next_btn.setOnClickListener(this);
    }

    @Override
    public void process(View v) {
        switch (v.getId()){
            case R.id.media_start_button :
                if(mMediaBinder != null) {
                    if(mMediaBinder.isPlaying()) {
                        mMediaBinder.pause();
                    }else {
                        mMediaBinder.start();
                    }
                }
                break;
            case R.id.media_last_button:
                mMediaBinder.init();
                mMediaBinder.toLast();
                break;
            case R.id.media_next_button:
                mMediaBinder.init();
                mMediaBinder.toNext();
                break;
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        changeProgress();
        //1000毫秒后更新
        mHandler.sendEmptyMessageDelayed(0, 1000);
        return true;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(fromUser) {
            //拖动过程中，改进度时间
            current_time.setText(getStrTime(seekBar.getProgress()));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        isTrackingTouch = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        isTrackingTouch = false;
        mMediaBinder.seek(seekBar.getProgress());
    }

    @Override
    public void onMediaPlay() {
        changeInfo();
        startHandlerProgress();
    }

    @Override
    public void onMediaStart() {
        changeInfo();
        startHandlerProgress();
    }

    @Override
    public void onMediaPause() {
        changeInfo();
        stopHandlerProgress();
    }

    @Override
    public void onMediaStop() {
        changeInfo();
        stopHandlerProgress();
    }

    @Override
    public void onMediaCompletion() {
        stopHandlerProgress();
        //更改按钮的状态
        changeButton();
        //将进度条移到初始位置
        changeCurrent(0);
        Toast.makeText(this, "播放完毕", Toast.LENGTH_LONG).show();
    }



    /** 绑定服务*/
    private void connect() {
        Intent intent = new Intent(getApplicationContext(), MediaService.class);
        intent.putExtra("courseList", (Serializable) mList);
        intent.putExtra("current_position",current_position);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    /** 断开服务*/
    private void disconnect() {
        unbindService(mConnection);
    }

    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMediaBinder = (MediaService.MediaBinder) service;
            mMediaBinder.addOnMediaChangeListener(MediaActivity.this);
            //更新状态
            changeInfo();
            startHandlerProgress();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("Demo", "<-----onServiceDisconnected---->");
        }
    };

    /** 更新播放信息*/
    private void changeInfo() {
        changeButton();
        changeProgress();
    }

    private void changeButton() {
        if(mMediaBinder == null) {
            return;
        }
        if(mMediaBinder.isPlaying()) {
            start_btn.setBackgroundResource(R.drawable.pause_icon);
        } else {
            start_btn.setBackgroundResource(R.drawable.play_icon);
        }
    }

    /** 更新播放进度*/
    private void changeProgress() {
        if(mMediaBinder == null) {
            return;
        }
        changeDuration(mMediaBinder.getDuration());

        //当seekbar没被拖动时
        if(!isTrackingTouch){
            changeCurrent(mMediaBinder.getCurrentPosition());
        }
    }

    private void changeDuration(int duration) {
        end_time.setText(getStrTime(duration));
        seekBar.setMax(duration);
    }

    private void changeCurrent(int current) {
        current_time.setText(getStrTime(current));
        seekBar.setProgress(current);
    }
    private void startHandlerProgress() {
        if(mMediaBinder != null && mMediaBinder.isPlaying()) {
            //1秒更新一次进度
            mHandler.sendEmptyMessage(0);
        }
    }
    private void stopHandlerProgress() {
        mHandler.removeMessages(0);
    }

    /** 获取'时间'字符串 **/
    public String getStrTime(int time){
        int min;
        int sec;
        String strTime = "";
        time /= 1000;
        if(time < 60){
            min = 0;
            sec = time;
        }else{
            min = time / 60;
            sec = time % 60;
        }
        if(min < 10){
            strTime += "0" + min + ":" ;
        }else{
            strTime += min + ":" ;
        }
        if(sec<10){
            strTime += "0" + sec ;
        }else{
            strTime += sec ;
        }
        return strTime;
    }

    @Override
    protected void onResume() {
        super.onResume();
        startHandlerProgress();//更新进度
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopHandlerProgress();//停止更新进度,避免资源浪费
    }

    @Override
    protected void onStop() {
        boolean isplaying = false;
        if(mMediaBinder != null) {
            isplaying = mMediaBinder.isPlaying();
        }
        disconnect();
        //如果没播放了，就将服务停止了
        if(!isplaying) {
            Intent intent = new Intent(this, MediaService.class);
            stopService(intent);
        } else {
            //如果还在播放，则显示在通知栏显示,这里就不写了
            // TODO: 2017/2/20
        }
        super.onStop();
    }
}
