package com.qzd.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.qzd.qzdapp.R;

/**
 * png序列图动画类
 * Created by QZD on 2015/3/17.
 */
public class pngAnimView extends FrameLayout{
    private ImageView animImage;
    //实现动画的类
    private AnimationDrawable animDrawable;
    public pngAnimView(Context context) {
        super(context);
        init(context);
    }

    public pngAnimView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        LayoutInflater.from(context).inflate(R.layout.pnganimview_l, this);
        animImage=(ImageView) findViewById(R.id.animView);
        animDrawable=new AnimationDrawable();
    }

    public void setInfo(int _f,int _res) {//参数为帧数和资源图编号
        try {
            Resources res = getResources();
            //获取资源图的bitmap
            BitmapDrawable bmpDraw = (BitmapDrawable) res.getDrawable(R.drawable.loading_icon);
            Bitmap bmp = bmpDraw.getBitmap();
            int _w=bmp.getWidth();
            int _h=bmp.getHeight();
            int _ew = _w / _f;
            for (int frame = 0; frame < _f; frame++) {
                //分解资源图
                Bitmap bitmap = Bitmap.createBitmap(bmp, frame*_ew, 0, _ew, _h);
                //填充每帧图片
                animDrawable.addFrame(new BitmapDrawable(null, bitmap), 100);
            }
            //将动画设置给ImageView
            animImage.setImageDrawable(animDrawable);
            //设置循环播放
            animDrawable.setOneShot(false);
        }catch (Exception e){
        }
    }

    public void startAnim(){
        try {
            animDrawable.start();
        }catch (Exception e){
        }
    }

    public void stopAnim(){
        try {
            animDrawable.stop();
        }catch (Exception e){
        }
    }
}
