package util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.qzd.utildemo.MusicActivity;
import com.example.qzd.utildemo.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MusicService extends Service {
    private Context mContext;
    private MyBinder mBinder = new MyBinder();
    private int i = 0;//当前歌曲的序号
    private List<String> musicPath = new ArrayList<>();//歌曲路径

    public MediaPlayer mMediaPlayer;

    private static final String TAG = "MediaService LOGCAT";

    //notification
    public Notification notification;
    private NotificationManager notificationManager;
    private MusicReceiver receive;
    private final String NotificationId="01";
    public final int DEFAULT_NOTIFICATION_ID = 1;
    public final static String PLAYER_TAG="musicPlay";//设置动作标记用以过滤广播
    public final static String INTENT_BUTTONID_TAG = "ButtonId";
    public final static int BUTTON_PREV_ID = 1;
    public final static int BUTTON_NEXT_ID = 2;
    public final static int BUTTON_PLAY_ID = 3;
    public final static int BUTTON_PAUSE_ID = 4;

    public MusicService() {
        mContext=this;
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //播放完毕
                stopSelf();
            }
        });
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.d(TAG, "music onPrepared");
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"startService");
        //动态注册广播（具体操作由通知栏触发---PendingIntent）
        receive = new MusicReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(PLAYER_TAG);
        registerReceiver(receive, filter);
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * 操作都定义在这里
     */
    public class MyBinder extends Binder {
        public void setUrl(String _url){
            musicPath.add(_url);
            iniMediaPlayerFile(i);
        }

        /**
         * 播放音乐
         */
        public void playMusic() {
            if (!mMediaPlayer.isPlaying()) {
                //如果还没开始播放，就开始
                mMediaPlayer.start();
                initNotification();
                updateNotification(1);
            }
        }

        /**
         * 暂停播放
         */
        public void pauseMusic() {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                updateNotification(2);
            }
        }

        /**
         * 下一首
         */
        public void nextMusic() {
            if (mMediaPlayer != null && i < 4 && i >= 0) {
                //切换歌曲reset()很重要很重要很重要，没有会报IllegalStateException
                mMediaPlayer.reset();
                iniMediaPlayerFile(i + 1);
                //这里的if是为了不让歌曲的序号越界，因为只有4首歌
                if (i == musicPath.size()-1) {
                } else {
                    i = i + 1;
                }
                playMusic();
            }
        }

        /**
         * 上一首
         */
        public void preciousMusic() {
            if (mMediaPlayer != null && i < 4 && i > 0) {
                mMediaPlayer.reset();
                iniMediaPlayerFile(i - 1);
                if (i == 1) {
                } else {
                    i = i - 1;
                }
                playMusic();
            }
        }

        /**
         * 关闭播放器
         */
        public void closeMedia() {
            if (mMediaPlayer != null) {
                mMediaPlayer.stop();
                mMediaPlayer.release();
            }
        }

        /**
         * 获取歌曲长度
         **/
        public int getProgress() {
            return mMediaPlayer.getDuration();
        }

        /**
         * 获取播放位置
         */
        public int getPlayPosition() {
            return mMediaPlayer.getCurrentPosition();
        }

        /**
         * 播放指定位置
         */
        public void seekToPositon(int msec) {
            mMediaPlayer.seekTo(msec);
        }
    }

    /**
     * 添加file文件到MediaPlayer对象并且准备播放音频
     */
    private void iniMediaPlayerFile(int _index) {
        try {
            //设置音频文件到MediaPlayer对象中
            mMediaPlayer.setDataSource(musicPath.get(_index));
            mMediaPlayer.prepare();
            Log.d(TAG, "musicPath："+musicPath.get(_index));
        } catch (IOException e) {
            Log.d(TAG, "设置资源，准备阶段出错");
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();//释放资源，不然音乐会继续播放而且无法操作MediaPlayer
        mMediaPlayer.stop();
        mMediaPlayer.release();
        mMediaPlayer=null;
        Log.d(TAG,"destoryed");
        if(notification!=null) {
            notificationManager.cancel(DEFAULT_NOTIFICATION_ID);
        }
        if(receive!=null){
            unregisterReceiver(receive);
            receive=null;
        }
    }

    @Override
    public void unbindService(ServiceConnection conn) {
        Log.d(TAG,"unbindService");
        super.unbindService(conn);
    }

    /**
     * ----------------------notification----------------------------
     */
    /**
     * 初始化通知栏
     */
    private void initNotification() {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,NotificationId);//自己定个id,这个id主要用于分类，暂时没什么用

        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification_control);

        contentView.setImageViewResource(R.id.custom_song_icon,R.drawable.logo);//图片展示
        contentView.setImageViewResource(R.id.btn_custom_play,R.drawable.pause);//button显示为正在播放

        Intent intentPause = new Intent(PLAYER_TAG);//设置通知栏内按钮点击事件
        intentPause.putExtra(INTENT_BUTTONID_TAG,BUTTON_PAUSE_ID);
        PendingIntent pIntentPause = PendingIntent.getBroadcast(this, 2, intentPause, PendingIntent.FLAG_UPDATE_CURRENT);
        contentView.setOnClickPendingIntent(R.id.btn_custom_play, pIntentPause);

        Intent notificationIntent=new Intent(this, MusicActivity.class);
        PendingIntent intent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        mBuilder.setContent(contentView).setSmallIcon(R.drawable.xin).setContentIntent(intent);//setContentIntent设置整体点击事件，点击后跳转到指定MusicActivity
        notification=mBuilder.build();
        notification.flags = notification.FLAG_NO_CLEAR;//设置通知点击或滑动时不被清除
        notificationManager.notify(DEFAULT_NOTIFICATION_ID, notification);//开启通知
    }

    /**
     * 更新状态栏
     * @param  type  图标样式：1是正在播放状态，2是停止状态； 3播放完成
     */
    public void updateNotification(int type){
        //更新操作
        switch (type){
            case 1:
                notification.contentView.setImageViewResource(R.id.btn_custom_play,R.drawable.pause);
                Intent intentPlay = new Intent(PLAYER_TAG);//下一次意图，并设置action标记为"play"，用于接收广播时过滤意图信息
                intentPlay.putExtra(INTENT_BUTTONID_TAG,BUTTON_PAUSE_ID);
                PendingIntent pIntentPlay = PendingIntent.getBroadcast(mContext, 2, intentPlay, PendingIntent.FLAG_UPDATE_CURRENT);
                notification.contentView.setOnClickPendingIntent(R.id.btn_custom_play, pIntentPlay);//为控件注册事件
                notification.contentView.setTextViewText(R.id.play_state, "播放中");
                break;
            case 2:
                notification.contentView.setImageViewResource(R.id.btn_custom_play,R.drawable.play);
                Intent intentPause = new Intent(PLAYER_TAG);
                notification.contentView.setTextViewText(R.id.play_state, "暂停");
                intentPause.putExtra(INTENT_BUTTONID_TAG,BUTTON_PLAY_ID);
                PendingIntent pIntentPause = PendingIntent.getBroadcast(mContext, 2, intentPause, PendingIntent.FLAG_UPDATE_CURRENT);
                notification.contentView.setOnClickPendingIntent(R.id.btn_custom_play, pIntentPause);
                break;
        }
        notificationManager.notify(DEFAULT_NOTIFICATION_ID, notification);//开启通知
    }

    /**
     * 音乐通知栏广播接收器
     */
    public class MusicReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(PLAYER_TAG)){
                //通过传递过来的ID判断按钮点击属性或者通过getResultCode()获得相应点击事件
                int buttonId = intent.getIntExtra(INTENT_BUTTONID_TAG, 0);
                switch (buttonId) {
                    case BUTTON_PREV_ID:
                        Log.d(TAG , "prev song");
                        break;
                    case BUTTON_PLAY_ID:
                        Log.d(TAG , "play");
                        mBinder.playMusic();
                        break;
                    case BUTTON_PAUSE_ID:
                        Log.d(TAG , "pause");
                        mBinder.pauseMusic();
                        break;
                    case BUTTON_NEXT_ID:
                        Log.d(TAG , "next song");
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
