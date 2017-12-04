package util;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.qzd.utildemo.R;

/**
 * Created by QZD on 2017/11/30.
 */

public class ScaleTextView extends TextView {
    private int baseScreenHeight = 720;
    public ScaleTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray type = context.obtainStyledAttributes(attrs,R.styleable.ScaleTextView);//获得属性值
        int i = type.getInteger(R.styleable.ScaleTextView_textSizePx, 25);
        baseScreenHeight = type.getInteger(R.styleable.ScaleTextView_baseScreenHeight, 720);
        setTextSize(TypedValue.COMPLEX_UNIT_PX, getFontSize(i));
    }

    /**
     * 获取当前手机屏幕分辨率，然后根据和设计图的比例对照换算实际字体大小
     * @param textSize
     * @return
     */
    private int getFontSize(int textSize) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        int screenHeight = dm.heightPixels;
        Log.d("LOGCAT","baseScreenHeight"+baseScreenHeight);
        int rate = (int) (textSize * (float) screenHeight / baseScreenHeight);
        return rate;
    }
}