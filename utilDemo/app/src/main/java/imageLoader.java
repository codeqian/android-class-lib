import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 图片加载类
 * Created by QZD on 2015/2/10.
 */
public class imageLoader {
    private static MemoryCache imgCache = new MemoryCache();
    private static List<String> imagekeyList=new ArrayList<String>();
    private static final int cacheMaxSize=100;
    public static Boolean iscui=true;//是否加载缓存
    public static Bitmap returnBitMap(String url, int _w, int _h) {
//        Log.d("LOGCAT","imgUrl:"+_w+"-"+_h+"-"+url);
        //检测是否已在内存
        Bitmap bitmapCache = imgCache.get(url);
        if (bitmapCache != null && iscui) {
            int width = bitmapCache.getWidth();
            int height = bitmapCache.getHeight();
            Bitmap cache2NewBitmap;
//            Log.d("LOGCAT","scale:"+width+"-"+height+"-"+_w+"-"+_h);
            if(width>0 && height>0 && _w>0 && _h>0) {
                float scaleWidth = ((float) bitmapCache.getHeight() * _w / _h) / bitmapCache.getWidth();
                Matrix matrix = new Matrix();
                matrix.postScale(scaleWidth, 1);
                cache2NewBitmap = Bitmap.createBitmap(bitmapCache, 0, 0, width, height, matrix, true);
                return cache2NewBitmap;
            }
        }
        URL myFileUrl = null;
        Bitmap bitmap = null;
        try {
            myFileUrl = new URL(url);
            //获得缩放比例
            HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
           // conn.setConnectTimeout(3000);
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            BitmapFactory.Options _options = new BitmapFactory.Options();
            //设成true就只返回宽高
            _options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(is,null,_options);
//            Log.d("LOGCAT","picSize:"+options.outWidth+"-"+options.outHeight+"-"+_w+"-"+_h);
            int scaleSize=1;
            if(_w>0 && _h>0){
                //缩略图的比例
                scaleSize=calculateInSampleSize(_options,_w,_h);
                if(scaleSize<1){
                    scaleSize=1;
                }
            }
            conn.disconnect();
            is.close();
            //获得实际的图片数据
            BitmapFactory.Options options = new BitmapFactory.Options();
            //不涉及透明度可使用RGB_565
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            //设置成内存不足时可回收
            options.inPurgeable = true;
            //设施是否深拷贝inPurgeable=false时无效
            options.inInputShareable = true;
            options.inJustDecodeBounds = false;
            options.inSampleSize=scaleSize;
            conn = (HttpURLConnection) myFileUrl.openConnection();
            //conn.setConnectTimeout(3000);
            conn.setDoInput(true);
            conn.connect();
            is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is,null,options);
            //对位图的宽度进行缩放
            try{
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                Bitmap newBitmap;
//                Log.d("LOGCAT","scale:"+width+"-"+height+"-"+_w+"-"+_h);
                if(width>0 && height>0 && _w>0 && _h>0) {
                    float scaleWidth = ((float) bitmap.getHeight() * _w / _h) / bitmap.getWidth();
                    Matrix matrix = new Matrix();
                    matrix.postScale(scaleWidth, 1);
                    newBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
                    is.close();
                    if(imagekeyList.size()>cacheMaxSize) {
                        imgCache.remove(imagekeyList.get(0));
                        imagekeyList.remove(0);
                    }
                    imagekeyList.add(url);
                    imgCache.put(url, bitmap);
                    return newBitmap;
                }
            }catch (Exception e){
            }
            is.close();
            if(imagekeyList.size()>cacheMaxSize) {
                imgCache.remove(imagekeyList.get(0));
                imagekeyList.remove(0);
            }
            imagekeyList.add(url);
            imgCache.put(url, bitmap);
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //加载本地图片
    public static Bitmap returnBitMapLocal(String url, int _w, int _h) {
//        Log.d("LOGCAT","imgUrl:"+url);
        Bitmap bitmap = null;
        try {
            InputStream is = new FileInputStream(url);
            BitmapFactory.Options _options = new BitmapFactory.Options();
            _options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(is,null,_options);
////            Log.d("LOGCAT","picSize:"+options.outWidth+"-"+options.outHeight+"-"+_w+"-"+_h);
            int scaleSize=1;
            if(_w>0 && _h>0){
                scaleSize=calculateInSampleSize(_options,_w,_h);
                if(scaleSize<1){
                    scaleSize=1;
                }
            }
            is.close();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inJustDecodeBounds = false;
            options.inSampleSize=scaleSize;
            is = new FileInputStream(url);
            bitmap = BitmapFactory.decodeStream(is,null,options);
            try{
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                Bitmap newBitmap;
                if(width>0 && height>0 && _w>0 && _h>0) {
                    float scaleWidth = ((float) bitmap.getHeight() * _w / _h) / bitmap.getWidth();
                    Matrix matrix = new Matrix();
                    matrix.postScale(scaleWidth, 1);
                    newBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
                    is.close();
                    return newBitmap;
                }
            }catch (Exception e){
            }
            is.close();
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //加载app库内图片
    public static Bitmap returnBitMapLib(Context context, int resId, int _w, int _h) {
        Bitmap bitmap = null;
        try {
            InputStream is = context.getResources().openRawResource(resId);
            BitmapFactory.Options _options = new BitmapFactory.Options();
            _options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(is,null,_options);
////            Log.d("LOGCAT","picSize:"+options.outWidth+"-"+options.outHeight+"-"+_w+"-"+_h);
            int scaleSize=1;
            if(_w>0 && _h>0){
                scaleSize=calculateInSampleSize(_options,_w,_h);
                if(scaleSize<1){
                    scaleSize=1;
                }
            }
            is.close();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inJustDecodeBounds = false;
            options.inSampleSize=scaleSize;
            is = context.getResources().openRawResource(resId);
            bitmap = BitmapFactory.decodeStream(is,null,options);
            try{
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                Bitmap newBitmap;
                if(width>0 && height>0 && _w>0 && _h>0) {
                    float scaleWidth = ((float) bitmap.getHeight() * _w / _h) / bitmap.getWidth();
                    Matrix matrix = new Matrix();
                    matrix.postScale(scaleWidth, 1);
                    newBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
                    is.close();
                    return newBitmap;
                }
            }catch (Exception e){
            }
            is.close();
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //计算缩放量
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight){
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    /**
     * 清除图片cache
     */
    public static void clearCache(){
        imgCache.clear();
    }
}
