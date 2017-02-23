package cn.com.zhiwoo.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import cn.com.zhiwoo.R;
import cn.com.zhiwoo.activity.course.CourseSeriesActivity;
import cn.com.zhiwoo.bean.react.LessonEvent;
import de.greenrobot.event.EventBus;

/**
 * Created by 25820 on 2017/2/17.
 */

public class MediaService extends Service implements MediaPlayer.OnBufferingUpdateListener,MediaPlayer.OnPreparedListener{
    private MediaPlayer mediaPlayer;
    private HashSet<OnMediaChangeListener> listeners;
    private MediaBinder mBinder;
    private LessonEvent.DataBean bean;
    private List<LessonEvent.DataBean> courseList = new ArrayList<>();
    private int current_position;
    private static final int REQUEST_CODE = 100;
    private static final String ACTION_PLAY = "play";
    private static final String ACTION_PAUSE = "pause";
    private static final String ACTION_NEXT = "next";
    private static final String ACTION_EXIT = "exit";
    private PlayerReceiver playerReceiver;

    MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            notifyAllCompletion();
        }
    };
    private RemoteViews remoteViews;
    private Notification notification;
    private NotificationManager notifyManager;


    @Override
    public void onCreate() {
        super.onCreate();
        listeners = new HashSet<>();
        mBinder = new MediaBinder();
        registerPlayerReceiver();
        setRemoteViews();
    }

    private void registerPlayerReceiver() {
        playerReceiver = new PlayerReceiver();
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ACTION_PLAY);
        mFilter.addAction(ACTION_PAUSE);
        mFilter.addAction(ACTION_NEXT);
        mFilter.addAction(ACTION_EXIT);
        registerReceiver(playerReceiver, mFilter);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null && intent.getAction() != null) {
            Intent intent1 = new Intent(intent.getAction());
            sendBroadcast(intent1);
        }
        mBinder.init();
        courseList.clear();
        List<LessonEvent.DataBean> list = (List<LessonEvent.DataBean>) intent.getSerializableExtra("course_list");
        courseList.addAll(list);
        current_position = intent.getIntExtra("current_position",0);
        mBinder.play(current_position);
        startForeground(666, notification);
        return Service.START_STICKY;
    }


    private void setRemoteViews() {
        notifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.icon)
                .setContentTitle("最简单的Notification")
                .setContentText("内容");
        remoteViews = new RemoteViews(this.getPackageName(), R.layout.notification_player_layout);
        PendingIntent pIntent_play = PendingIntent.getBroadcast(this.getApplicationContext(),
                REQUEST_CODE,new Intent(ACTION_PLAY),PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pIntent_next = PendingIntent.getBroadcast(this.getApplicationContext(),
                REQUEST_CODE,new Intent(ACTION_NEXT),PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pIntent_exit = PendingIntent.getBroadcast(this.getApplicationContext(),
                REQUEST_CODE,new Intent(ACTION_EXIT),PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.notification_player_start,pIntent_play);
        remoteViews.setOnClickPendingIntent(R.id.notification_player_next,pIntent_next);
        remoteViews.setOnClickPendingIntent(R.id.notification_player_exit,pIntent_exit);
        notification = builder.build();
        notification.contentView = remoteViews;
    }

    private void notifyAllPlay() {
        for (OnMediaChangeListener listener : listeners) {
            listener.onMediaPlay();
        }
    }

    private void notifyAllStart(){
        for (OnMediaChangeListener listener : listeners) {
            listener.onMediaPlay();
        }
    }

    private void notifyAllPause() {
        for (OnMediaChangeListener listener : listeners) {
            listener.onMediaPause();
        }
    }

    private void notifyAllStop() {
        for (OnMediaChangeListener listener : listeners) {
            listener.onMediaStop();
        }
    }

    private void notifyAllCompletion() {
        for (OnMediaChangeListener listener : listeners) {
            listener.onMediaCompletion();
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public void onPrepared(MediaPlayer mp) {

    }


    public class MediaBinder extends Binder {
        public void addOnMediaChangeListener(OnMediaChangeListener listener) {
            if(listener != null) {
                listeners.add(listener);
            }
        }

        public void removeOnMediaChangeListener (OnMediaChangeListener listener) {
            if(listener != null) {
                listeners.remove(listener);
            }
        }
        public void init(){
            if(mediaPlayer != null){
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        }
        public void play(int position) {
            if(mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(courseList.get(position).getCourse_href());
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mediaPlayer.setOnCompletionListener(mOnCompletionListener);
            }
            mediaPlayer.start();
            notifyAllPlay();
            changeNotificationButton();
        }
        public int getCurrentListPosition(){
            if(mediaPlayer != null) {
                return current_position;
            }
            return -1;
        }

        public void pause() {
            if(mediaPlayer != null) {
                mediaPlayer.pause();
            }
            notifyAllPause();
            changeNotificationButton();
        }

        public void stop() {
            if(mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer = null;
            }
            notifyAllStop();
        }

        public boolean isPlaying() {
            if(mediaPlayer != null) {
                return mediaPlayer.isPlaying();
            }
            return false;
        }

        public int getDuration() {
            if(mediaPlayer != null) {
                return mediaPlayer.getDuration();
            }
            return -1;
        }

        public int getCurrentPosition() {
            if(mediaPlayer != null) {
                return mediaPlayer.getCurrentPosition();
            }
            return -1;
        }

        public void seek(int sec) {
            if(mediaPlayer != null) {
                try {
                    mediaPlayer.seekTo(sec);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
        public void toNext(){
            if (current_position < courseList.size()-1){
                play(++current_position);
            }else {
                current_position = 0;
                play(current_position);
            }
        }
        public void toLast(){
            if (current_position > 0){
                play(--current_position);
            }else {
                current_position = courseList.size()-1;
                play(current_position);
            }
        }
        public void start(){
            if(mediaPlayer != null ) {
                if (mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                }else {
                    mediaPlayer.start();
                }
                notifyAllStart();
                changeNotificationButton();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        listeners.clear();
        if (playerReceiver != null){
            unregisterReceiver(playerReceiver);
        }
        try {
            mediaPlayer.stop();
            mediaPlayer.release();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public class PlayerReceiver extends BroadcastReceiver {
        private final String TAG = PlayerReceiver.class.getSimpleName();
        public PlayerReceiver() {}
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();// 获取对应Action
            Log.d(TAG, "action:" + action);
            if (action.equals(MediaService.ACTION_PLAY)) {
                    if (mBinder == null){
                        return;
                    }
                    if (mBinder.isPlaying()){
                        mBinder.pause();
                    }else {
                        mBinder.start();
                    }
                } else if (action.equals(MediaService.ACTION_NEXT)) {
                    if (mBinder != null){
                        mBinder.toNext();
                    }
                } else if (action.equals(MediaService.ACTION_EXIT)) {
                    if (mBinder != null){
                        mBinder.stop();
                    }
                    stopSelf();
                    notifyManager.cancel(666);
                }
            }
    }
    private void changeNotificationButton(){
        if (mBinder != null){
            if (mBinder.isPlaying()){
                remoteViews.setImageViewResource(R.id.notification_player_start,R.drawable.pause_icon);
            }else {
                remoteViews.setImageViewResource(R.id.notification_player_start,R.drawable.play_icon);
            }
        }
    }
}
