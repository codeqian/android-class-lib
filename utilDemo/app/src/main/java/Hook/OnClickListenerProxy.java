package Hook;

import android.util.Log;
import android.view.View;

/**
 * 实现点击监听
 */
public class OnClickListenerProxy implements View.OnClickListener{
    private View.OnClickListener mOriginalListener;

    //直接在构造函数中传进来原来的OnClickListener
    public OnClickListenerProxy(View.OnClickListener originalListener) {
        mOriginalListener = originalListener;
    }

    @Override
    public void onClick(View v) {
        if (mOriginalListener != null) {
            mOriginalListener.onClick(v);
        }
//        Log.d("LOGCAT","hooked!"+v.getId());
        //拿到之前传递的参数
        Object obj = v.getTag(v.getId());
        Log.d("LOGCAT","hooked!"+v.getId()+"_"+obj.toString());
    }
}