package util;

import android.app.Service;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MusicService extends Service {
    private static final String TAG = "MediaService LOGCAT";
    private MyBinder mBinder = new MyBinder();
    //当前歌曲的序号
    private int i = 0;
    //歌曲路径
    private List<String> musicPath = new ArrayList<>();

    public MediaPlayer mMediaPlayer;

    public MusicService() {
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
            }
        }

        /**
         * 暂停播放
         */
        public void pauseMusic() {
            if (mMediaPlayer.isPlaying()) {
                //如果还没开始播放，就开始
                mMediaPlayer.pause();
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
    }

    @Override
    public void unbindService(ServiceConnection conn) {
        Log.d(TAG,"unbindService");
        super.unbindService(conn);
    }
}
