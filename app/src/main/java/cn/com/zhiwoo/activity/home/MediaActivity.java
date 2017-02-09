package cn.com.zhiwoo.activity.home;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.com.zhiwoo.R;
import cn.com.zhiwoo.activity.base.BaseActivity;
import cn.com.zhiwoo.bean.react.LessonEvent;

/**
 * Created by 25820 on 2017/1/12.
 */

public class MediaActivity extends BaseActivity{
    private TextView current_time;
    private TextView end_time;
    private SeekBar seekBar;
    private Button start_btn;
    private Button last_btn;
    private Button next_btn;
    private MediaPlayer player;
    private SimpleDateFormat format = new SimpleDateFormat("mm:ss");
    private int current_position;
    private List<LessonEvent.DataBean> mList;
    public static final int MUSIC_DURATION = 0X324;
    public static final int MUSIC_POSITION = 0X325;
    private int totalNum;


/*
public void handleMessage(android.os.Message msg) {
            int position = player.getCurrentPosition();
            int duration = player.getDuration();
            if (duration > 0) {
                // 计算进度（获取进度条最大刻度*当前音乐播放位置 / 当前音乐时长）
                int pos = seekBar.getMax() * position / duration;
                seekBar.setProgress(pos);
                current_time.setText(formatTime(position));
            }
        }
 */
    private final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MUSIC_DURATION:
                    if (player != null) {
                        seekBar.setMax(player.getDuration() / 1000);
                        Log.i("asdasd", "handleMessage: "+player.getDuration());
                        end_time.setText(format.format(new Date(player.getDuration())));
                    }
                    break;
                case MUSIC_POSITION:
                    if (player != null) {
                        try {
                            seekBar.setProgress(player.getCurrentPosition()/1000);
                            current_time.setText(format.format(new Date(player.getCurrentPosition())));
                            handler.sendEmptyMessageDelayed(MUSIC_POSITION, 1000);
                        } catch (Exception e) {
                            e.getStackTrace();
                        }
                    }
                    break;
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
        startPlay(currentBean);
    }

    private void initPlayer() {
        player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);// 设置媒体流类型
    }

    @Override
    public void initListener() {
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
//                    mp.start();//播放
                handler.sendEmptyMessage(MUSIC_DURATION);
                handler.sendEmptyMessage(MUSIC_POSITION);
            }
        });

        player.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                seekBar.setSecondaryProgress(percent);
            }
        });

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                int num = mList.size();//获得音乐的数目
                if (current_position == num - 1) {
                    current_position = 0;//如果已经是最后一首，则播放第一首
                } else {
                    current_position += 1;//否则播放下一首
                }
                startPlay( mList.get(current_position));//开始播放
                player.start();
            }
        });

        start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != player && !player.isPlaying()){
                    player.start();
                    start_btn.setBackgroundResource(R.drawable.pause_icon);
                }else if (null != player && player.isPlaying()){
                    player.pause();
                    start_btn.setBackgroundResource(R.drawable.play_icon);
                }

            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    player.seekTo(progress * 1000);
                    current_time.setText(format.format(new Date(seekBar.getProgress() * 1000)));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        last_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                totalNum = mList.size();
                if (current_position == 0) {
                    current_position = totalNum - 1;//如果已经时第一首则播放最后一首
                } else {
                    current_position -= 1;//否则播放上一首
                }
                startPlay(mList.get(current_position));
                player.start();
            }
        });
        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                totalNum = mList.size();//获得音乐的数目
                if (current_position == totalNum - 1) {
                    current_position = 0;//如果已经是最后一首，则播放第一首
                } else {
                    current_position += 1;//否则播放下一首
                }
                startPlay(mList.get(current_position));
                player.start();
            }
        });
    }

    @Override
    public void process(View v) {

    }


    private void startPlay(LessonEvent.DataBean currentBean) {
        String url = currentBean.getCourse_href();
        player.reset();//重置,第一次不需要重置
        if (player.isPlaying()) {
            player.release();
        }
        try {
            player.setDataSource(this,Uri.parse(url));//设置播放的文件的路径
            player.prepare();//加载准备
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player.isPlaying()) {
            player.release();
        }
        handler.removeCallbacksAndMessages(null);
    }
}
