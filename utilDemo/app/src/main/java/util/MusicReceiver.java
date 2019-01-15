package util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.qzd.utildemo.MusicActivity;

/**
 * 音乐页面接收通知栏广播
 */
public class MusicReceiver extends BroadcastReceiver {

    public final static String PLAYER_TAG="musicPlay";//设置动作标记用以过滤广播
    public final static String INTENT_BUTTONID_TAG = "ButtonId";
    public final static int BUTTON_PREV_ID = 1;
    public final static int BUTTON_NEXT_ID = 2;
    public final static int BUTTON_PLAY_ID = 3;
    public final static int BUTTON_PAUSE_ID = 4;
    private final static String TAG="MusicReceiver LOGCAT";

    @Override
    public void onReceive(Context context, Intent intent) {
        MusicActivity _mActivity=(MusicActivity) context;
        String action = intent.getAction();
        if(action.equals(PLAYER_TAG)){
            //通过传递过来的ID判断按钮点击属性或者通过getResultCode()获得相应点击事件
            int buttonId = intent.getIntExtra(INTENT_BUTTONID_TAG, 0);
            switch (buttonId) {
                case BUTTON_PREV_ID:
                    Log.d(TAG , "prev song");
                    break;
                case BUTTON_PLAY_ID:
                    Log.d(TAG , "play"+context.getPackageName());
                    _mActivity.playMusic();
                    break;
                case BUTTON_PAUSE_ID:
                    Log.d(TAG , "pause"+context.getPackageName());
                    _mActivity.pauseMusic();
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
