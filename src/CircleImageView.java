package com.qzd.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 圆形图片，图片外填充背景色，背景不透明，低版本系统可用
 * Created by QZD on 2015/1/20.
 */
public class CircleImageView extends ImageView {
    public CircleImageView(Context context) {
        super(context);
        initColor(null);
    }
    public CircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initColor(attrs);
    }
    public CircleImageView(Context context, AttributeSet attrs,int defStyle) {
        super(context, attrs, defStyle);
        initColor(attrs);
    }

    private Paint paint;
    private Path path;
    private boolean init = false;
    private int background = Color.WHITE;
    private float circleLineWidth = 6;
    private int circleColor = color.color_darkgraybg;

    private void initColor(AttributeSet attrs){
        if(attrs != null){
            String v = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "background");
            if(v != null){
                if(v.startsWith("#")){
                    background = Color.parseColor(v);
                }else{
                    background = getResources().getColor(Integer.parseInt(v.replaceAll("@", "")));
                }
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(!init){
            initPaint();
        }
    }

    private void initPaint(){
        circleLineWidth = getPaddingLeft();
        setPadding(0, 0, 0, 0);
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(background);
        paint.setAntiAlias(true);
        path = new Path();
//        try {
//            circleColor = Color.parseColor((String) getTag());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        init = true;
    }

    private RectF rect = new RectF();

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(background);
        paint.setStyle(Paint.Style.FILL);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        float radius = width/(float)2;
        path.reset();
        path.moveTo(0, radius);
        path.lineTo(0, 0);
        path.lineTo(width, 0);
        path.lineTo(width, height);
        path.lineTo(0, height);
        path.lineTo(0, radius);
        //圆弧左边中间起点是180,旋转360度
        path.arcTo(new RectF(0, 0, width, height), 180, -359, true);
        path.close();
        canvas.drawPath(path, paint);

        paint.setColor(circleColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(circleLineWidth);
        float dw = (float) (circleLineWidth/2+1);
        rect.top = dw;
        rect.bottom = height-dw;
        rect.left = dw;
        rect.right = width-dw;
        canvas.drawOval(rect, paint);
    }
}
