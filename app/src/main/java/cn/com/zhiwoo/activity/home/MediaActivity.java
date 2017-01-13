package cn.com.zhiwoo.activity.home;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import cn.com.zhiwoo.R;
import cn.com.zhiwoo.activity.base.BaseActivity;

/**
 * Created by 25820 on 2017/1/12.
 */

public class MediaActivity extends BaseActivity implements MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener {
    private TextView current_time;
    private TextView last_time;
    private SeekBar seekBar;
    private Button start_btn;
    private String url = "http://cctv3.qiniudn.com/zuixingfuderen.mp3";
    private MediaPlayer mediaPlayer;
    private Timer mTimer = new Timer(); // 计时器

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
        last_time = (TextView) view.findViewById(R.id.media_last_time);
        seekBar = (SeekBar) view.findViewById(R.id.media_seekBar);
        start_btn = (Button) view.findViewById(R.id.media_start_button);
        flContent.addView(view);
    }

    @Override
    public void initData() {
        initPlayer();

    }

    private void initPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);// 设置媒体流类型
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnPreparedListener(this);
        SetUrl(url);
        mTimer.schedule(timerTask, 0, 1000);// 每一秒触发一次
    }

    @Override
    public void initListener() {
        start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mediaPlayer && !mediaPlayer.isPlaying()){
                    mediaPlayer.start();
                    start_btn.setText("暂停");
                }else if (null != mediaPlayer && mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    start_btn.setText("播放");
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

    }

    public void SetUrl(String url) {
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(url); // 设置数据源
            mediaPlayer.prepare(); // prepare自动播放
            last_time.setText(formatTime(mediaPlayer.getDuration()));
            seekBar.setMax(mediaPlayer.getDuration()/1000);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * 格式化时间，将毫秒转换为分:秒格式
     * @param time
     * @return
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
