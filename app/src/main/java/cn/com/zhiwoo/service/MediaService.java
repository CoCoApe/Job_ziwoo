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
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.RemoteViews;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import cn.com.zhiwoo.R;
import cn.com.zhiwoo.activity.course.MediaActivity;
import cn.com.zhiwoo.bean.react.LessonEvent;
import cn.com.zhiwoo.utils.APPStatusUtils;

/**
 * Created by 25820 on 2017/2/17.
 */

public class MediaService extends Service{
    private MediaPlayer mediaPlayer;
    private HashSet<OnMediaChangeListener> listeners;
    private MediaBinder mBinder;
    private LessonEvent.DataBean bean;
    private List<LessonEvent.DataBean> courseList = new ArrayList<>();
    private int current_position = -1;
    private static final int REQUEST_CODE = 100;
    private static final String ACTION_PLAY = "play";
    private static final String ACTION_PAUSE = "pause";
    private static final String ACTION_NEXT = "next";
    private static final String ACTION_EXIT = "exit";
    private static final String ACTION_START_ACTIVITY = "start_activity";

    private PlayerReceiver playerReceiver;

    MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            notifyAllCompletion();
            changeNotificationButton();
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
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        tm.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    private void registerPlayerReceiver() {
        playerReceiver = new PlayerReceiver();
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ACTION_PLAY);
        mFilter.addAction(ACTION_PAUSE);
        mFilter.addAction(ACTION_NEXT);
        mFilter.addAction(ACTION_EXIT);
        mFilter.addAction(ACTION_START_ACTIVITY);
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
                .setContentText("内容")
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_MAX);
        notification = builder.build();
        remoteViews = new RemoteViews(this.getPackageName(), R.layout.notification_player_layout);
        PendingIntent pIntent_start_activity = PendingIntent.getBroadcast(this.getApplicationContext(),
                REQUEST_CODE,new Intent(ACTION_START_ACTIVITY),PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pIntent_play = PendingIntent.getBroadcast(this.getApplicationContext(),
                REQUEST_CODE,new Intent(ACTION_PLAY),PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pIntent_next = PendingIntent.getBroadcast(this.getApplicationContext(),
                REQUEST_CODE,new Intent(ACTION_NEXT),PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pIntent_exit = PendingIntent.getBroadcast(this.getApplicationContext(),
                REQUEST_CODE,new Intent(ACTION_EXIT),PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.notification_player_icon,pIntent_start_activity);
        remoteViews.setOnClickPendingIntent(R.id.notification_player_start,pIntent_play);
        remoteViews.setOnClickPendingIntent(R.id.notification_player_next,pIntent_next);
        remoteViews.setOnClickPendingIntent(R.id.notification_player_exit,pIntent_exit);
        notification.contentView = remoteViews;
    }

    private void notifyAllPlay() {
        for (OnMediaChangeListener listener : listeners) {
            listener.onMediaPlay();
        }
    }

    private void notifyAllStart(){
        for (OnMediaChangeListener listener : listeners) {
            listener.onMediaStart();
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
                current_position = position;
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
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        tm.listen(mPhoneStateListener,0);
        stopForeground(true);
    }

    /**
     *  接收通知栏指令的广播接收器
     *
     */
    public class PlayerReceiver extends BroadcastReceiver {
        private final String TAG = PlayerReceiver.class.getSimpleName();
        public PlayerReceiver() {
        }
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();// 获取对应Action
            if (action.equals(MediaService.ACTION_PLAY)) {
                if (mBinder == null) {
                    return;
                }
                if (mBinder.isPlaying()) {
                    mBinder.pause();
                } else {
                    mBinder.start();
                }
            } else if (action.equals(MediaService.ACTION_NEXT)) {
                Log.i(TAG, "onReceive: " + "next");
                if (mBinder != null) {
                    mBinder.init();
                    mBinder.toNext();
                }
            } else if (action.equals(MediaService.ACTION_START_ACTIVITY)) {
                if (APPStatusUtils.isAppRunningOnTop(context, getPackageName())) {
                    directStartActivity(context);
                } else {
                    indirectStartActivity(context);
                }
            } else if (action.equals(MediaService.ACTION_EXIT)) {
                if (mBinder != null) {
                    mBinder.stop();
                }
                stopSelf();
            }
        }
    }

    /**
     *  app在前台时，直接到播放界面
     */
    private void directStartActivity(Context context) {
        Intent intent1 = new Intent(context, MediaActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent1);
    }
    /**
     *  app不在前台时，先启动程序，在跳转播放界面
     */
    private void indirectStartActivity(Context context) {
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage("cn.com.zhiwoo");
        launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        Intent playerIntent = new Intent(getApplicationContext(),MediaActivity.class);
        Intent[] intents = {launchIntent,playerIntent};
        context.startActivities(intents);
    }

    /**
     *  更新通知栏播放状态图标
     *
     */
    private void changeNotificationButton(){
        if (mBinder != null){
            if (mBinder.isPlaying()){
                remoteViews.setImageViewResource(R.id.notification_player_start,R.drawable.pause_icon);
            }else {
                remoteViews.setImageViewResource(R.id.notification_player_start,R.drawable.play_icon);
            }
            notifyManager.notify(666,notification);
        }
    }

    /**
     *  来电状态监听
     *  来电响铃，接起，播出时暂停
     *  空闲时再继续
     */
    private boolean mResumeAfterCall = false;
    private PhoneStateListener mPhoneStateListener = new PhoneStateListener(){
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
                switch (state){
                    case TelephonyManager.CALL_STATE_IDLE:
                        if (mResumeAfterCall){
                            mBinder.start();
                            mResumeAfterCall = false;
                        }
                        break;
                    case TelephonyManager.CALL_STATE_RINGING:
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        if (mBinder.isPlaying()){
                            mBinder.pause();
                            mResumeAfterCall = true;
                        }
                        break;
                }
            super.onCallStateChanged(state, incomingNumber);
        }
    };






}
