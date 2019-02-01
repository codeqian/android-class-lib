package util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.ThumbnailUtils;
import android.util.Log;

import java.io.FileOutputStream;

/**
 * 生成图片
 * Created by QZD on 2017/5/8.
 */

public class bitmapFactory {
    private static int[] lineHeight=new int[3];
    private static int imageH=360;//图片高
    private static int imageW=640;//图片宽
    private static float[] brushSize={45,30,30};//画笔粗细，三行文字，三种大小。
    private static int brushColor= Color.BLACK;//画笔颜色
    private static int bgColor= Color.TRANSPARENT;//背景颜色
    private static int imageQuality=90;//图片压缩质量

    /**
     * 设置图片大小
     */
    public static void sizeConfig(int _lh,int _w){
        imageH=_lh;
        imageW=_w;
    }

    /**
     * 设置样式
     * @param _lh
     * @param _w
     * @param _brushS
     * @param _brushC
     */
    public static void imageConfig(float[] _brushS,int _brushC,int _bgC){
        brushSize=_brushS;
        brushColor=_brushC;
        bgColor=_bgC;
    }

    /**
     * 绘制图片（单文字）
     * @param path 生成图片的地址
     * @param _msg 文字
     * @return
     */
    public static boolean writeImage4Text(String path, String[] _msg,int _w,int _h){
        try {
            Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);//Paint.ANTI_ALIAS_FLAG参数开启抗锯齿
            p.setColor(brushColor);

            //图片尺寸
            imageW=_w;
            imageH=_h;

            //按输出视频大小创建画布
            Bitmap bitmap = Bitmap.createBitmap(imageW, imageH, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);

            //文字尺寸
            float[] textWidth={0,0,0};
            float[] textHeight={0,0,0};
            float _height=0;
            //创建一个矩形来获取文字区域宽高，作为图片大小
            for(int i=0;i<_msg.length;i++){
                p.setTextSize(brushSize[i]);
                Rect rect = new Rect();
                p.getTextBounds(_msg[i],0,_msg[i].length(),rect);
                textWidth[i] = rect.width();
                lineHeight[i]=rect.height();
                textHeight[i] += rect.height()*2;//这里的图片高度*2是为了留出空间，不然drawText时设置baseline的y位置为文字高度的话像g这样的字母下半身就太监了。
                _height+=textHeight[i];
//                Log.d("LOGCAT","text size:"+textWidth[i]+"-"+textHeight[i]+"-"+_height);
            }

            float y0=(imageH-_height)/2-brushSize[0]/2;
            //接下来画文字
            canvas.drawColor(bgColor);
            int baseHeight=0;
            for(int t=0;t<_msg.length;t++) {
                p.setTextSize(brushSize[t]);
                baseHeight += textHeight[t];
                float _x=(imageW-textWidth[t])/2;
                float _y=y0+baseHeight;
                canvas.drawText(_msg[t], _x, _y, p);//注意这里的y参数是baseline的位置而不是文字开始或中心的位置
//                Log.d("LOGCAT",_msg[t]+"---text size:"+baseHeight);
            }

//            Log.d("LOGCAT", "path:"+path);
            //将Bitmap保存为png图片
            FileOutputStream out = new FileOutputStream(path);
            bitmap.compress(Bitmap.CompressFormat.PNG, imageQuality, out);
//            Log.d("LOGCAT", "png done");
            return true;
        } catch (Exception e) {
            // TODO: handle exception
            Log.d("LOGCAT", "e:"+e.toString());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 绘制图片（文字加背景图片）
     * @param path 生成图片的地址
     * @param _imageBM 背景原图
     * @param _msg 文字
     * @return
     */
    public static boolean writeTitleImage(String path, Bitmap _imageBM, String[] _msg,int _w,int _h){
        try {
            Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);//Paint.ANTI_ALIAS_FLAG参数开启抗锯齿
            p.setColor(brushColor);

            //图片尺寸
            imageW=_w;
            imageH=_h;

            //按输出视频大小创建画布
            Bitmap bitmap = Bitmap.createBitmap(imageW, imageH, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            //先画上背景图
            canvas.drawBitmap(_imageBM, 0, 0, null);

            //文字尺寸
            float[] textWidth={0,0,0};
            float[] textHeight={0,0,0};
            float _height=0;
            //创建一个矩形来获取文字区域宽高，作为图片大小
            for(int i=0;i<_msg.length;i++){
                p.setTextSize(brushSize[i]);
                Rect rect = new Rect();
                p.getTextBounds(_msg[i],0,_msg[i].length(),rect);
                textWidth[i] = rect.width();
                lineHeight[i]=rect.height();
                textHeight[i] += rect.height()*2;//这里的图片高度*2是为了留出空间，不然drawText时设置baseline的y位置为文字高度的话像g这样的字母下半身就太监了。
                _height+=textHeight[i];
//                Log.d("LOGCAT","text size:"+textWidth[i]+"-"+textHeight[i]+"-"+_height);
            }

            float y0=(imageH-_height)/2-brushSize[0]/2;
            //接下来画文字
            canvas.drawColor(bgColor);
            int baseHeight=0;
            for(int t=0;t<_msg.length;t++) {
                p.setTextSize(brushSize[t]);
                baseHeight += textHeight[t];
                float _x=(imageW-textWidth[t])/2;
                float _y=y0+baseHeight;
                canvas.drawText(_msg[t], _x, _y, p);//注意这里的y参数是baseline的位置而不是文字开始或中心的位置
//                Log.d("LOGCAT",_msg[t]+"---text size:"+baseHeight);
            }

//            Log.d("LOGCAT", "path:"+path);
            //将Bitmap保存为png图片
            FileOutputStream out = new FileOutputStream(path);
            bitmap.compress(Bitmap.CompressFormat.JPEG, imageQuality, out);
//            Log.d("LOGCAT", "png done");
            return true;
        } catch (Exception e) {
            // TODO: handle exception
            Log.d("LOGCAT", "e:"+e.toString());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取视频的缩略图
     * 先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。
     * 如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的值，这样会节省内存。
     */
    public static Bitmap getVideoThumbnail(String videoPath, int width, int height, int kind) {
        Bitmap bitmap = null;
        // 获取视频的缩略图
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    /**
     * bitmap裁剪
     */
    public static Bitmap cutBitmap(Bitmap _source, float _wScale, float _hScale){
        if (_source == null)
        {
            return null;
        }
        try {
            Bitmap _cut;
            float _w=_source.getWidth();
            float _h=_source.getHeight();
            int _x0=0;
            int _y0=0;
            if(_w/_h>_wScale/_hScale){
                _x0=(int)(_w-_h*_wScale/_hScale)/2;
                _w=_h*_wScale/_hScale;
            }else{
                _y0=(int)(_h-_w*_hScale/_wScale)/2;
                _h=_w*_hScale/_wScale;
            }
            _cut= Bitmap.createBitmap(_source, _x0, _y0, (int)_w, (int)_h, null,false);
            {
//                _source.recycle();
            }
            return _cut;
        }catch (Exception e){
            Log.d("LOGCAT","cutBitmap error:"+e.toString());
            return null;
        }
    }
}
