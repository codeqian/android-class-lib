package util;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.IOException;

public class MusicService extends Service {
    // 定义需要显示的音乐的字段
    String[] mCursorCols = new String[] {
            "audio._id AS _id", // index must match IDCOLIDX below
            MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.MIME_TYPE, MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ARTIST_ID, MediaStore.Audio.Media.DURATION };
    private MediaPlayer mMediaPlayer; // 声明播放器
    private Cursor mCursor; // 声明游标
    private int mPlayPosition = 0; // 当前播放的歌曲

    // 注册意图，级播放器行为
    public static final String PLAY_ACTION = "com.wyl.music.PLAY_ACTION";
    public static final String PAUSE_ACTION = "com.wyl.music.PAUSE_ACTION";
    public static final String NEXT_ACTION = "com.wyl.music.NEXT_ACTION";
    public static final String PREVIOUS_ACTION = "com.wyl.music.PREVIOUS_ACTION";

    public MusicService() {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mMediaPlayer = new MediaPlayer();
        Uri MUSIC_URL = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        // 这里我过滤了一下，因为我机里有些音频文件是游戏音频，很短
        // 我这里作了处理，默认大于10秒的可以看作是系统音乐
        mCursor = getContentResolver().query(MUSIC_URL, mCursorCols,
                "duration > 10000", null, null);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        String action = intent.getAction();
        if (action.equals(PLAY_ACTION)) {//播放
            play();
        } else if (action.equals(PAUSE_ACTION)) {//暂停
            pause();
        } else if (action.equals(NEXT_ACTION)) {//下一首
            next();
        } else if (action.equals(PREVIOUS_ACTION)) {//前一首
            previous();
        }
    }

    /**
     * 播放
     */
    public void play() {
        //初始化音乐播放器
        init();
    }

    /**
     * 暂停，结束服务
     */
    public void pause() {
        //暂停音乐播放
        stopSelf();
    }

    /**
     * 上一首
     */
    public void previous() {
        //得到前一首的歌曲
        if (mPlayPosition == 0) {
            mPlayPosition = mCursor.getCount() - 1;
        } else {
            mPlayPosition--;
        }
        //开始播放
        init();
    }

    /**
     * 下一首
     */
    public void next() {
        //得到后一首歌曲
        if (mPlayPosition == mCursor.getCount() - 1) {
            mPlayPosition = 0;
        } else {
            mPlayPosition++;
        }
        //开始播放
        init();
    }

    /**
     * 初始化播放器
     */
    public void init() {
        //充值MediaPlayer
        mMediaPlayer.reset();
        // 获取歌曲位置
        String dataSource = getDateByPosition(mCursor, mPlayPosition);
        // 歌曲信息
        String info = getInfoByPosition(mCursor, mPlayPosition);
        // 用Toast显示歌曲信息
        Toast.makeText(getApplicationContext(), info, Toast.LENGTH_SHORT)
                .show();
        try {
            // 播放器绑定资源
            mMediaPlayer.setDataSource(dataSource);
            // 播放器准备
            mMediaPlayer.prepare();
            // 播放
            mMediaPlayer.start();
        } catch (IllegalArgumentException e1) {
            e1.printStackTrace();
        } catch (IllegalStateException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * 根据位置来获取歌曲位置
     * @param c
     * @param position
     * @return
     */
    public String getDateByPosition(Cursor c, int position) {
        c.moveToPosition(position);
        int dataColumn = c.getColumnIndex(MediaStore.Audio.Media.DATA);
        String data = c.getString(dataColumn);
        return data;
    }

    /**
     * 获取当前播放歌曲演唱者及歌名
     * @param c
     * @param position
     * @return
     */
    public String getInfoByPosition(Cursor c, int position) {
        c.moveToPosition(position);
        int titleColumn = c.getColumnIndex(MediaStore.Audio.Media.TITLE);
        int artistColumn = c.getColumnIndex(MediaStore.Audio.Media.ARTIST);
        String info = c.getString(artistColumn) + " " + c.getString(titleColumn);
        return info;

    }

    /**
     * 服务结束时要释放MediaPlayer
     */
    public void onDestroy() {
        super.onDestroy();
        mMediaPlayer.release();
    }
}
