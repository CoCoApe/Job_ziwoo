package cn.com.zhiwoo.activity.home;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.com.zhiwoo.R;
import cn.com.zhiwoo.activity.base.BaseActivity;
import cn.com.zhiwoo.bean.react.LessonEvent;

/**
 * Created by 25820 on 2017/1/12.
 */

public class MediaActivity extends BaseActivity implements MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener {
    private TextView current_time;
    private TextView end_time;
    private SeekBar seekBar;
    private Button start_btn;
    private Button last_btn;
    private Button next_btn;
    private MediaPlayer mediaPlayer;
    private Timer mTimer = new Timer(); // 计时器
    private int current_position;
    private List<LessonEvent.DataBean> mList;

    // 计时器
    private TimerTask timerTask = new TimerTask() {

        @Override
        public void run() {
            if (mediaPlayer == null)
                return;
            if (mediaPlayer.isPlaying() && !seekBar.isPressed()) {
                handler.sendEmptyMessage(0); // 发送消息
            }
        }
    };


    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            int position = mediaPlayer.getCurrentPosition();
            int duration = mediaPlayer.getDuration();
            if (duration > 0) {
                // 计算进度（获取进度条最大刻度*当前音乐播放位置 / 当前音乐时长）
                long pos = seekBar.getMax() * position / duration;
                seekBar.setProgress((int) pos);
                current_time.setText(formatTime(position));
            }
        }
    };



    @Override
    public void initView() {
        super.initView();
        View view = View.inflate(this, R.layout.activity_media,null);
        current_time = (TextView) view.findViewById(R.id.media_current_time);
        end_time = (TextView) view.findViewById(R.id.media_last_time);
        seekBar = (SeekBar) view.findViewById(R.id.media_seekBar);
        start_btn = (Button) view.findViewById(R.id.media_start_button);
        last_btn = (Button) view.findViewById(R.id.media_last_button);
        next_btn = (Button) view.findViewById(R.id.media_next_button);
        flContent.addView(view);
    }

    @Override
    public void initData() {
        Intent intent = getIntent();
        current_position = intent.getIntExtra("currentPosition",0);
        mList = (List<LessonEvent.DataBean>) intent.getSerializableExtra("toPlayList");
        LessonEvent.DataBean currentBean = mList.get(current_position);
        initPlayer();
        Log.i("aaaaa", "initData: 11111"+ currentBean.getCourse_href());
        setUrl(currentBean);
    }

    private void initPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);// 设置媒体流类型
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnPreparedListener(this);
    }

    @Override
    public void initListener() {
        start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mediaPlayer && !mediaPlayer.isPlaying()){
                    mediaPlayer.start();
                    start_btn.setBackgroundResource(R.drawable.pause_icon);
                }else if (null != mediaPlayer && mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    start_btn.setBackgroundResource(R.drawable.play_icon);
                }

            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (null != mediaPlayer && mediaPlayer.isPlaying()){
                    mediaPlayer.seekTo(seekBar.getProgress()*1000);
                    current_time.setText(formatTime(seekBar.getProgress()*1000));
                }
            }
        });
        last_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (current_position>0 && null != mediaPlayer){
                    current_position -= 1;
                    LessonEvent.DataBean currentBean = mList.get(current_position);
                    mediaPlayer.release();
                    mediaPlayer.reset();
                    setUrl(currentBean);
                    mediaPlayer.start();
                    if (current_position == 0){
                        last_btn.setEnabled(false);
                    }else {
                        last_btn.setEnabled(true);
                    }
                }
            }
        });
        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (current_position < mList.size() && null != mediaPlayer){
                    current_position += 1;
                    LessonEvent.DataBean currentBean = mList.get(current_position);
                    mediaPlayer.release();
                    mediaPlayer.reset();
                    setUrl(currentBean);
                    mediaPlayer.start();
                    if (current_position == mList.size()-1){
                        next_btn.setEnabled(false);
                    }else {
                        next_btn.setEnabled(true);
                    }
                }
            }
        });
    }

    @Override
    public void process(View v) {

    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        seekBar.setSecondaryProgress(percent);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        end_time.setText(formatTime(mediaPlayer.getDuration()));
        seekBar.setMax(mediaPlayer.getDuration()/1000);
    }

    public void setUrl(LessonEvent.DataBean bean) {
        Log.i("aaaaa", "initData: 22222"+ bean.getCourse_href());
        try {
            mediaPlayer.setDataSource(bean.getCourse_href()); // 设置数据源
            Log.i("aaaaa", "initData: 33333"+ bean.getCourse_href());
            mediaPlayer.prepare(); // prepare自动播放
        } catch (IOException e) {
            e.printStackTrace();
        }
        mTimer.schedule(timerTask, 0, 1000);// 每一秒触发一次
    }

    /**
     * 格式化时间，将毫秒转换为分:秒格式
     */
    public static String formatTime(long time) {
        String min = time / (1000 * 60) + "";
        String sec = time % (1000 * 60) + "";
        if (min.length() < 2) {
            min = "0" + time / (1000 * 60) + "";
        } else {
            min = time / (1000 * 60) + "";
        }
        if (sec.length() == 4) {
            sec = "0" + (time % (1000 * 60)) + "";
        } else if (sec.length() == 3) {
            sec = "00" + (time % (1000 * 60)) + "";
        } else if (sec.length() == 2) {
            sec = "000" + (time % (1000 * 60)) + "";
        } else if (sec.length() == 1) {
            sec = "0000" + (time % (1000 * 60)) + "";
        }
        return min + ":" + sec.trim().substring(0, 2);
    }



}
