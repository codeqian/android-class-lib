package com.qzd.net;

import android.app.DownloadManager;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import com.qzd.math.sqlHelper;
import com.qzd.values.messageCode;

import org.json.JSONObject;

import java.io.File;

/**
 * 下载文件(使用系统downloadManager)
 * Created by QZD on 2015/3/20.
 */
public class downloadFile {
    //获取SDCard根目录
    private static String sdcardRoot;
    //获取手机根目录
//    private static String phoneRoot;
    //视频下载请求地址
    private static String downPath="";
    //要保存的目录
    public static String filepath="/qzddance/media/";
    private static Boolean hadSdcard=false;
    //最大下载任务数
    private static int maxFile=3;
    //当前下载任务总数
    private static int downLoadTotal=0;
    //下载管理类
    private static DownloadManager mDownMag;
    //视频下载进行列表
    private static JSONObject[] videoDownList;
    //列表序号
    private static int currentFile=0;
    //数据库对象
    private static sqlHelper downloadDbManager;
    private static SQLiteDatabase mDB;

    //初始化,获得系统下载管理
    public static void init(DownloadManager sysmag){
        mDownMag=sysmag;
        videoDownList=new JSONObject[maxFile];
    }

    public static void initSqlManager(sqlHelper downDbManager){
        downloadDbManager=downDbManager;
        mDB=downloadDbManager.getWritableDatabase();
    }

    //判断sd卡是否存在
    public static boolean ExistSDCard() {
        String _path="";
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            hadSdcard=true;
            sdcardRoot=Environment.getExternalStorageDirectory().toString();
            _path=sdcardRoot+filepath;
            creatDir(_path);
        } else{
            hadSdcard=false;
            //data目录通常不允许访问
//            phoneRoot=Environment.getDataDirectory().toString();
//            _path=phoneRoot+filepath;
        }
        return hadSdcard;
    }

    //创建文件目录
    private static boolean creatDir(String _path){
        try{
            File file = new File(_path);
            if (!file.exists())
            {
                if (file.mkdirs())
                {
                    return true;
                }
            }else{
                return true;
            }
        }catch (Exception e){
        }
        return false;
    }

    //sd卡剩余大小
    public static long getSDFreeSize(){
        //取得SD卡文件路径
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        //获取单个数据块的大小(Byte)
        long blockSize = sf.getBlockSize();
        //空闲的数据块的数量
        long freeBlocks = sf.getAvailableBlocks();
        //返回SD卡空闲大小
        return (freeBlocks * blockSize)/1024 /1024; //单位MB
    }

    //sd卡总容量
    public static long getSDAllSize(){
        //取得SD卡文件路径
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        //获取单个数据块的大小(Byte)
        long blockSize = sf.getBlockSize();
        //获取所有数据块数
        long allBlocks = sf.getBlockCount();
        //返回SD卡大小
        return (allBlocks * blockSize)/1024/1024; //单位MB
    }

    //获得机身内存总大小
//    public static long getRomTotalSize() {
//        File path = Environment.getDataDirectory();
//        StatFs stat = new StatFs(path.getPath());
//        long blockSize = stat.getBlockSize();
//        long totalBlocks = stat.getBlockCount();
//        return (totalBlocks * blockSize)/1024/1024; //单位MB
//    }

    //获得机身可用内存
//    public static long getRomAvailableSize() {
//        File path = Environment.getDataDirectory();
//        StatFs stat = new StatFs(path.getPath());
//        long blockSize = stat.getBlockSize();
//        long availableBlocks = stat.getAvailableBlocks();
//        return (availableBlocks * blockSize)/1024/1024; //单位MB
//    }

    //下载视频
    public static String downloadVideo(JSONObject videoInfo){
        if(downLoadTotal<maxFile){
            try{
                String imgUrl=videoInfo.getString("imageUrl");
                String vid=videoInfo.getString("vid");
                String vname=videoInfo.getString("vname");
                String uname=videoInfo.getString("uname");
                Log.d("LOGCAT", "had:" + checkdownLoaded(vid));
                if(checkdownLoaded(vid)){
                    return "视频已下载";
                }
                videoDownList[currentFile]=new JSONObject();
                videoDownList[currentFile].put("vid",vid);
                //下载预览图
                saveToSDCard(imgUrl, vid + messageCode.IMAGEFILETYPE,false);
                //下载视频
                saveToSDCard(downPath + vid, vid + messageCode.VIDEOFILETYPE,true);
                //提交下载统计
                httpData.getDownloadCollect(vid);
                //保存下载记录到数据库
                SQLiteDatabase mDB=downloadDbManager.getWritableDatabase();
                ContentValues cv=new ContentValues();
                cv.put("vid", vid);
                cv.put("vname", vname);
                cv.put("uname", uname);
                cv.put("imgUrl", imgUrl);
                cv.put("done", 0);
                //插入下载记录，插入失败会返回-1
                long _id=mDB.insert(messageCode.DOWNLOADTABLENAME, "null",cv);
                if(_id>=0) {
                    return "开始下载，您可以在下载列表中查看。";
                }else{
                    return "下载记录插入失败。";
                }
            }catch (Exception e){
                return "下载失败";
            }
        }else{
            return "下载进程已满";
        }

    }

    //检查视频是否已下载
    private static boolean checkdownLoaded(String _vid){
        int _id=Integer.parseInt(_vid);
        String[] columns = new String[] { "vid" };
        String selection = "vid=?";
        String[] selectionArgs = new String[] { _vid };
        String groupBy = null;
        String having = null;
        String orderBy = null;
        String limit = "1";
        Cursor c = mDB.query(messageCode.DOWNLOADTABLENAME, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
        c.moveToFirst();
        Boolean _downBefor=false;
        if(c.getCount()>0){
            _downBefor=true;
        }
        c.close();
        return _downBefor;
    }

    //存到sd卡(DownloadManager方式，某些网络环境下无法下载，原因不明)
    private static void saveToSDCard(String _url,String _filename,Boolean _isVideo){
//        Log.d("LOGCAT","startLoad:"+_filename+"\n"+_url);
        final String filename=_filename;
        final String vurl=_url;
        final Boolean isVideo=_isVideo;
        Runnable downLoadRun=new Runnable() {
            @Override
            public void run() {
                try {
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(vurl));
                    request.setDestinationInExternalPublicDir(filepath,filename);
                    request.setTitle(""+filename);
// request.setDescription("描述文字");
// request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);//完成后在通知栏提示
// request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
// request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);//通知栏不提示
// request.setMimeType("application/cn.trinea.download.file");
                    long downloadId = mDownMag.enqueue(request);
                    Log.d("LOGCAT", "reqId:" + downloadId + ":" + currentFile);
                    if(isVideo) {
                        videoDownList[currentFile].put("Downid", downloadId);
                        if (currentFile < maxFile - 1) {
                            currentFile++;
                        } else {
                            currentFile = 0;
                        }
                        downLoadTotal++;
                    }
                }catch (Exception e){
                    Log.d("LOGCAT",e.toString());
                }
            }
        };
        ThreadPoolUtils.execute(downLoadRun);
    }

    //删除SD卡上的文件
    public static boolean delSDFile(String fileName) {
        File file = new File(sdcardRoot + filepath + fileName);
        if (file == null || !file.exists() || file.isDirectory())
            return false;
        file.delete();
        return true;
    }

    //DownloadManager查询进度
    public static int queryDownloadPec(long _id) {
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(_id);
        Cursor cursor = mDownMag.query(query);
        while (cursor.moveToNext()) {
            int mDownload_so_far = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
            int mDownload_all = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
            int mProgress = (mDownload_so_far * 100) / mDownload_all;
            //有时会出现-值或者很大的值，不知道为什么
//            Log.d("LOGCAT","pec:"+_id+"-"+mProgress);
            return mProgress;
        }
        return 0;
    }

    //下载完成
    public  static void downloadComplete(long _id){
        for (int i=0;i<videoDownList.length;i++){
            try {
                if (videoDownList[i].getLong("Downid")==_id) {
                    downLoadDone(videoDownList[i].getString("vid"));
                    break;
                }
            }catch (Exception e){
            }
        }
    }

    //更新文件下载完成
    private static void downLoadDone(String _vid){
        String table = messageCode.DOWNLOADTABLENAME;
        String selection = "vid=?";
        String[] selectionArgs = new String[] {_vid};
        ContentValues values = new ContentValues();
        values.put("done", 1);
        mDB.update(table, values, selection, selectionArgs);
        downLoadTotal--;
    }

    //取消下载
    public static void removeDownload(String _vid){
        for (int i=0;i<videoDownList.length;i++){
            try {
                if (videoDownList[i].getString("vid").equals(_vid)) {
                    if(mDownMag.remove(videoDownList[i].getLong("Downid"))>0){
                        downLoadTotal--;
                    }
                    break;
                }
            }catch (Exception e){
            }
        }
    }

    //获得sd卡地址
    public static String getSdPath(){
        return sdcardRoot;
    }

    //获得文件下载地址
    public static String getDownloadPath(){
        return sdcardRoot+filepath;
    }

    //获得当前是否有文件在下载
    public static Boolean isDownLoading(){
        Boolean ifLoading=false;
        if(downLoadTotal>0){
            ifLoading=true;
        }
        return ifLoading;
    }

    //取消当前全部下载
    public static void clearAllDown(){
        for(int i=0;i<videoDownList.length;i++){
            try{
                mDownMag.remove(videoDownList[i].getLong("Downid"));
                delVideoInDB(videoDownList[i].getString("vid"));
            }catch (Exception e){}
        }
    }

    //删除数据库中的条目
    public static void delVideoInDB(String _vid){
        try{
            String table = messageCode.DOWNLOADTABLENAME;
            String selection = "vid=?";
            String[] selectionArgs = new String[] { _vid };
            mDB.delete(table, selection, selectionArgs);
        }catch (Exception e){}
    }

    //查询下载id
    public static long getDownloadId(String _vid){
        for (int i=0;i<videoDownList.length;i++){
            try {
                if (videoDownList[i].getString("vid").equals(_vid)) {
                    return videoDownList[i].getLong("Downid");
                }
            }catch (Exception e){
            }
        }
        return -1;
    }

    //关闭数据库对象
    public static void closeDB(){
        mDB.close();
    }
}