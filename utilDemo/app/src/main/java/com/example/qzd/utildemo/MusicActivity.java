package com.example.qzd.utildemo;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.TextView;

import util.FileUtil;
import util.MusicReceiver;
import util.MusicService;

import static util.MusicReceiver.BUTTON_PAUSE_ID;
import static util.MusicReceiver.BUTTON_PLAY_ID;
import static util.MusicReceiver.INTENT_BUTTONID_TAG;
import static util.MusicReceiver.PLAYER_TAG;

public class MusicActivity extends AppCompatActivity {
    private static Context mContext;

    private MusicService.MyBinder mMyBinder;
    private Intent MediaServiceIntent;

    private Button selectBtn,playBtn,pauseBtn,stopBtn;
    private TextView info_t;

    private String filePath;

    //通知栏相关
    public static Notification notification;
    private static NotificationManager notificationManager;
    private MusicReceiver receive;
    private static final String NotificationId="01";
    public static final int DEFAULT_NOTIFICATION_ID = 1;


    private final String TAG="MUSIC PAGE LOGCAT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        mContext=this;

//        绑定service
        MediaServiceIntent = new Intent(this, MusicService.class);
        startService(MediaServiceIntent);//先start的话Activity销毁了service还在
        bindService(MediaServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        info_t=findViewById(R.id.info_t);
        selectBtn=findViewById(R.id.selectBtn);
        playBtn=findViewById(R.id.playBtn);
        pauseBtn=findViewById(R.id.pauseBtn);
        stopBtn=findViewById(R.id.stopBtn);
        selectBtn=findViewById(R.id.selectBtn);
        selectBtn.setOnClickListener(btnClick);
        playBtn.setOnClickListener(btnClick);
        pauseBtn.setOnClickListener(btnClick);
        stopBtn.setOnClickListener(btnClick);

        //动态注册广播（具体操作由通知栏触发---PendingIntent）
        receive = new MusicReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(PLAYER_TAG);
        registerReceiver(receive, filter);
    }

    private View.OnClickListener btnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent;
            switch (view.getId()){
                case R.id.selectBtn://选择本地文件
                    intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("audio/*");
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    startActivityForResult(Intent.createChooser(intent, "Select a File"), 0x1);
                    break;
                case R.id.playBtn:
                    mMyBinder.playMusic();
//                    initNotification();
//                    updateNotification(1);//通知改由service创建
                    break;
                case R.id.pauseBtn:
                    mMyBinder.pauseMusic();
//                    updateNotification(2);
                    break;
                case R.id.stopBtn:
                    unbindService(mServiceConnection);
                    stopService(MediaServiceIntent);//不stop的话service不会销毁
                    Log.d(TAG,"unbindService!");
                    if(notification!=null) {
                        notificationManager.cancel(DEFAULT_NOTIFICATION_ID);
                    }
                    if(receive!=null){
                        unregisterReceiver(receive);
                        receive=null;
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public void playMusic(){
        Log.d(TAG,"ui to play!");
    }

    public void pauseMusic(){
        Log.d(TAG,"ui to pause!");
    }

    /***
     * 绑定service事件
     */
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMyBinder = (MusicService.MyBinder) service;
            Log.d(TAG, "Service与Activity已连接");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "Service与Activity已断开");
        }
    };

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

        Intent notificationIntent=new Intent(this,MusicActivity.class);
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
    public static void updateNotification(int type){
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
     * 监听文件选择
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "requestCode:" + requestCode+"-"+"resultCode:"+resultCode);
        if (requestCode == 0x1 && resultCode == Activity.RESULT_OK && data!=null) {
            try{
                Uri audioUri = data.getData();
                filePath= FileUtil.getPath(this,audioUri);
                Log.d(TAG, "path:" + filePath);
                mMyBinder.setUrl(filePath);
                if(!filePath.equals("")) {
                    info_t.setText(filePath);
                }
            }catch (Exception e){
                Log.d(TAG,e.toString());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        unbindService(mServiceConnection);
//        stopService(MediaServiceIntent);//不stop的话service不会销毁
//        Log.d(TAG,"unbindService!");
//        if(notification!=null) {
//            notificationManager.cancel(DEFAULT_NOTIFICATION_ID);
//            unregisterReceiver(receive);
//        }
    }
}
