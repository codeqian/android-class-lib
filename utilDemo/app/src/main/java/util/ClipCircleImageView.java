package util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 遮罩圆形图片，实现背景透明，不兼容低版本系统，暂时未使用
 * Created by QZD on 2015/1/20.
 */
public class ClipCircleImageView extends ImageView {
    public ClipCircleImageView(Context context) {
        super(context);
    }
    public ClipCircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initColor(attrs);
    }
    public ClipCircleImageView(Context context, AttributeSet attrs,int defStyle) {
        super(context, attrs, defStyle);
        initColor(attrs);
    }

    private int background = Color.WHITE;
    private Paint paint;
    private boolean set = false;
    private int padding = 0;
    private RectF viewRect;

    private void initColor(final AttributeSet attrs){
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
        setBackgroundResource(android.R.color.transparent);
        paint = new Paint();
        paint.setColor(background);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        padding = getPaddingLeft();
        setPadding(0, 0, 0, 0);
    }

    @Override
    public void setImageBitmap(final Bitmap bm) {
        post(new Runnable() {
            @Override
            public void run() {
                set = true;
                ClipCircleImageView.super.setImageBitmap(getCroppedBitmap(bm));
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(!set){
            set = true;
            if(getDrawable() != null && ((BitmapDrawable) getDrawable()).getBitmap() != null){
                setImageBitmap(getCroppedBitmap(((BitmapDrawable) getDrawable()).getBitmap()));
            }
        }
        super.onDraw(canvas);
        paint.setStrokeWidth(padding);
        if(viewRect == null){
            float p2 = (float)padding/2;
            viewRect = new RectF(p2, p2, getMeasuredWidth()-p2, getMeasuredHeight()-p2);
        }
        canvas.drawOval(viewRect, paint);
    }

    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        Bitmap output = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        Rect srcRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        float p2 = padding/2;
        RectF rect = new RectF(p2, p2, width-p2, height-p2);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rect, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, srcRect, rect, paint);
        return output;
    }
}
