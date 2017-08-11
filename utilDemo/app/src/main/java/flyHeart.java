package com.qzd.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.qzd.qzdapp.R;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 飘动的心形
 * Created by QZD on 2015/8/4.
 */
public class flyHeart extends View {
    //同屏心的数量
    int heartMax=10;
    int view_height= 0;
    int view_width= 0;
    Bitmap bitmap_heart2 =null;
    Bitmap bitmap_heart3 =null;
    Bitmap bitmap_heart4 =null;
    Bitmap bitmap_heart5 =null;
    //画笔
    private final Paint mPaint = new Paint();
    //位置点
    private Point[] heartPosition;
    //图片类型
    private Bitmap[] picType;
    //横向速度
    private float[] xSpeed;
    //纵向速度
    private float[] ySpeed;
    //缩放比例
    private float[] heartScale;
    //透明度
    private int[] heartAlpha;
    //初始位置
    private float x0,y0;
    //动画高度
    private int animHeight=200;
    //动画步数
    private int animStep=20;

    //计时器
    //进度计时器
    private Timer animTimer;
    private TimerTask animTask;
    private Handler handler;

    private int[] stepCount;

    public flyHeart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public flyHeart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        heartPosition=new Point[heartMax];
        heartScale=new float[heartMax];
        heartAlpha=new int[heartMax];
        xSpeed=new float[heartMax];
        ySpeed=new float[heartMax];
        picType=new Bitmap[heartMax];
        stepCount=new int[heartMax];
    }

    public void initTimer(){
        Random random=new Random();
        for (int x = 0; x < heartMax; x += 1) {
            heartPosition[x]=new Point();
            resetHeart(x);
            stepCount[x]=-random.nextInt(10);
            int _type=random.nextInt(4);
            if(_type==0){
                picType[x]=bitmap_heart2;
            }else if(_type==1){
                picType[x]=bitmap_heart3;
            }else if(_type==2){
                picType[x]=bitmap_heart4;
            }else{
                picType[x]=bitmap_heart5;
            }
        }
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    //定义从Task发来的任务
                    case 0:
                        drawHeart();
                        break;
                }
            }
        };

        animTask = new TimerTask() {
            @Override
            public void run() {
                if(handler!=null) {
                    Message message = new Message();
                    message.what = 0;
                    handler.sendMessage(message);
                }
            }
        };
        animTimer=new Timer();
        animTimer.schedule(animTask, 50, 50);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(view_height>0) {
            for (int x = 0; x < heartMax; x += 1) {
                Matrix matrix = new Matrix();
                float scaleWidth = heartScale[x];
                float scaleHeight = heartScale[x];
                // 缩放图片动作
                matrix.postScale(scaleWidth, scaleHeight);
                //设置画笔透明度
                mPaint.setAlpha(heartAlpha[x]);
                Bitmap _bitmap = Bitmap.createBitmap(picType[x], 0, 0, picType[x].getWidth(), picType[x].getHeight(),matrix, true);
                canvas.drawBitmap(_bitmap, ((float) heartPosition[x].x), ((float) heartPosition[x].y), mPaint);
            }
        }
    }

    //加载图片到内存
    public void LoadFlowerImage()
    {
        bitmap_heart2= ((BitmapDrawable) this.getContext().getResources().getDrawable(R.drawable.xin2)).getBitmap();
        bitmap_heart3= ((BitmapDrawable) this.getContext().getResources().getDrawable(R.drawable.xin3)).getBitmap();
        bitmap_heart4= ((BitmapDrawable) this.getContext().getResources().getDrawable(R.drawable.xin4)).getBitmap();
        bitmap_heart5= ((BitmapDrawable) this.getContext().getResources().getDrawable(R.drawable.xin5)).getBitmap();
        initTimer();
    }

    //设置窗体大小（动画描绘区域的大小）
    public void SetViewSize(int width,int height,float _x0,float _y0)
    {
        view_height=height;
        view_width=width;
        x0=_x0;
        y0=_y0;
        Log.d("LOGCAT", "heart position:" + view_width + "-" + view_height + "-" + x0 + "-" + y0);
    }

    //绘制心型
    public void drawHeart(){
        for (int x = 0; x < heartMax; x += 1) {
            if (stepCount[x] < 0) {
                stepCount[x]++;
                break;
            }
            //计算位置
            float _y = stepCount[x] * ySpeed[x];
            if (_y > animHeight) {
                resetHeart(x);
                _y = stepCount[x] * ySpeed[x];
            }
            float _x = stepCount[x] * xSpeed[x];
            heartPosition[x].x = (int) (x0 - _x);
            heartPosition[x].y = (int) (y0 - _y);
            //计算透明度
            float _positionPec=(animHeight-_y)/(animHeight/2);
            if(_positionPec>1){
                _positionPec=1;
            }
            heartAlpha[x]=(int) (_positionPec*100);
            stepCount[x]++;
        }
        invalidate();
    }

    //重置心型单元属性
    private void resetHeart(int _index){
        Random random = new Random();
        xSpeed[_index] = random.nextInt(6) - 3;
        ySpeed[_index] = random.nextInt(5) + 3;
        heartScale[_index] =(float) random.nextInt(5)/5 + 1;
        stepCount[_index] = 0;
    }

    //停止动画
    public void stopTimer(){
        if(handler!=null) {
            handler.removeMessages(2);
            handler = null;
        }
        if(animTimer!=null) {
            animTimer.cancel();
            animTimer=null;
        }
        if(animTask!=null) {
            animTask.cancel();
            animTask=null;
        }
    }
}
