package com.boosj.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.boosj.babyapp.R;

/**
 * png序列图动画类(每帧一张图的资源形式)
 * Created by QZD on 2015/3/17.
 */
public class pngAnimView2 extends FrameLayout{
    private ImageView animImage;
    //实现动画的类
    private AnimationDrawable animDrawable;
    public pngAnimView2(Context context) {
        super(context);
        init(context);
    }

    public pngAnimView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        LayoutInflater.from(context).inflate(R.layout.pnganimview_l, this);
        animImage=(ImageView) findViewById(R.id.animView);
        animDrawable=new AnimationDrawable();
    }

    /**
     *
     * @param _f 帧频
     * @param _res 资源图数组
     */
    public void setInfo(int[] _res) {//参数为帧数和资源图编号
        try {
            for (int frame = 0; frame < _res.length; frame++) {
                Resources res = getResources();
                //获取资源图的bitmap
                BitmapDrawable bmpDraw = (BitmapDrawable) res.getDrawable(_res[frame]);
                Bitmap bmp = bmpDraw.getBitmap();
                //填充每帧图片
                animDrawable.addFrame(new BitmapDrawable(null, bmp), 100);
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
