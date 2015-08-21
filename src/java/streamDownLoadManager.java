package com.qzd.net;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import com.qzd.asynctask.Async;
import com.qzd.asynctask.Utils;
import com.qzd.math.sqlHelper;
import com.qzd.values.messageCode;

import org.json.JSONObject;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 下载管理器，二进制流形式
 * Created by QZD on 2015/5/6.
 */
public class streamDownLoadManager {
    //获取SDCard根目录
    private static String sdcardRoot="";
    //视频下载请求地址
    private static String downPath="http://onclick.qzd.com:8180/download?id=";
    //要保存的目录
    public static String filepath="/qzddance/media/";
    private static Boolean hadSdcard=false;
    //最大下载任务数
    private static int maxFile=4;
    //当前下载任务总数
    private static int downLoadTotal=0;
    //列表序号
    private static int currentFile=0;
    //数据库对象
    private static sqlHelper downloadDbManager;
    private static SQLiteDatabase mDB;
    private static AtomicInteger mOpenCounter;

    public static void initSqlManager(sqlHelper downDbManager){
        downloadDbManager=downDbManager;
        mDB=downloadDbManager.getWritableDatabase();
        sdcardRoot= android.os.Environment.getExternalStorageDirectory().toString();
//        Log.d("LOGCAT","sdcardRoot:"+sdcardRoot);
    }

    //判断sd卡是否存在
    public static boolean ExistSDCard() {
        String _path="";
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            hadSdcard=true;
            if(sdcardRoot.equals("")){
                sdcardRoot= android.os.Environment.getExternalStorageDirectory().toString();
            }
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
                Log.d("LOGCAT","creat dir:"+_path);
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

    //下载视频
    public static String downloadVideo(JSONObject videoInfo){
        if(downLoadTotal<=maxFile){
            try{
                String imgUrl=videoInfo.getString("imageUrl");
                String vid=videoInfo.getString("vid");
                String vname=videoInfo.getString("vname");
                String uname=videoInfo.getString("uname");
//                Log.d("LOGCAT", "had:" + checkdownLoaded(vid));
                if(checkdownLoaded(vid)){
                    return "视频已下载";
                }
                Utils.vid[currentFile]=vid;
                Utils.title[currentFile]=vname;
                Utils.uname[currentFile]=uname;
                //下载预览图
//                Log.d("LOGCAT", "imgUrl:" + imgUrl+"\n"+vid + messageCode.IMAGEFILETYPE);
                Utils.imgUrl[currentFile]=imgUrl;
                saveToSDCard(false);
                //下载视频
                Log.d("LOGCAT", "downPath:" + (downPath + vid)+"\n"+vid + messageCode.VIDEOFILETYPE);
                Utils.url[currentFile]=downPath + vid;
                saveToSDCard(true);
                //提交下载统计
                httpData.getDownloadCollect(vid);
                //保存下载记录到数据库
                ContentValues cv=new ContentValues();
                cv.put("vid", vid);
                cv.put("vname", vname);
                cv.put("uname", uname);
                cv.put("imgUrl", imgUrl);
                cv.put("vUrl", downPath + vid);
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

    //存到sd卡（文件流方式）
    private static void saveToSDCard(Boolean _isVideo){
        //每次都检查目录是否存在，不存在则新建
        String _path=sdcardRoot+filepath;
        creatDir(_path);

        Async asyncTask = new Async();  // 创建新异步
        String _type="img";
        if(_isVideo){
            _type="mov";
            Utils.videoTask[currentFile]=asyncTask;
            // 当调用AsyncTask的方法execute时，就回去自动调用doInBackground方法
            asyncTask.execute(String.valueOf(currentFile), _type);
            if(currentFile<maxFile) {
                currentFile++;
            }else{
                currentFile=0;
            }
            downLoadTotal++;
        }else{
            _type="img";
            Utils.imgTask[currentFile]=asyncTask;
            asyncTask.execute(String.valueOf(currentFile), _type);
        }
    }

    //暂停下载(停止读取流，并未中断Async)
    public static void downloadPause(String _vid){
        int _index=findIndex(_vid);
        if(_index>=0) {
            Utils.videoTask[_index].pause();
        }
    }

    //继续下载
    public static void downloadGoon(String _vid){
        int _index=findIndex(_vid);
        if(_index>=0) {
            Utils.videoTask[_index].continued();
        }
    }

    //停止下载(完全停止Async)
    public static void downloadStop(String _vid){
        int _index=findIndex(_vid);
        if(_index>=0 && Utils.videoTask[_index]!=null) {
            Utils.videoTask[_index].cancel(true);
            Utils.videoTask[_index]=null;
            downLoadTotal--;
        }
    }

    //重新开始下载
    public static void downloadRestart(String _vid){
        //每次都检查目录是否存在，不存在则新建
        String _path=sdcardRoot+filepath;
        creatDir(_path);
        int _index=findIndex(_vid);
        if(_index>=0) {
            Async asyncTask = new Async();  // 重新创建新异步（Async是一次性用品）
            Utils.videoTask[_index]=asyncTask;
            asyncTask.execute(String.valueOf(_index), "mov");
            downLoadTotal++;
        }
    }

    //获得sd卡地址
    public static String getSdPath(){
        return sdcardRoot;
    }

    //获得文件本地存放地址
    public static String getDownloadPath(){
        return sdcardRoot+filepath;
    }

    //删除SD卡上的文件
    public static boolean delSDFile(String fileName) {
        File file = new File(sdcardRoot + filepath + fileName);
        if (file == null || !file.exists() || file.isDirectory())
            return false;
        file.delete();
        return true;
    }

    //查询进度
    public static int queryDownloadPec(String _vid) {
        int _vIndex=findIndex(_vid);
        if(_vIndex>=0) {
            return Utils.progress[findIndex(_vid)];
        }
        return 0;
    }

    //查询文件大小
    public static int queryFileSize(String _vid) {
        int _vIndex=findIndex(_vid);
        if(_vIndex>=0) {
            return Utils.fileLength[findIndex(_vid)];
        }
        return 0;
    }

    //向数据库更新进度
    public static void updataPosition(String _vid,int _position){
        String table = messageCode.DOWNLOADTABLENAME;
        String selection = "vid=?";
        String[] selectionArgs = new String[] {_vid};
        ContentValues values = new ContentValues();
        values.put("position", _position);
        mDB.update(table, values, selection, selectionArgs);
    }

    //根据vid查询编号
    public static int findIndex(String _vid){
        for(int i=0;i<Utils.vid.length;i++){
            if (Utils.vid[i].equals(_vid)){
                return i;
            }
        }
        return -1;
    }

    //查询是否下载中
    public static Boolean isLoading(String _vid){
        for(int i=0;i<Utils.vid.length;i++){
            if (Utils.vid[i].equals(_vid)){
                if(Utils.videoTask[i]==null){
                    return false;
                }else{
                    return !Utils.videoTask[i].isPaused();
                }
            }
        }
        return false;
    }

    //更新文件下载完成
    public static void downLoadDone(String _vid){
        String table = messageCode.DOWNLOADTABLENAME;
        String selection = "vid=?";
        String[] selectionArgs = new String[] {_vid};
        ContentValues values = new ContentValues();
        values.put("done", 1);
        mDB.update(table, values, selection, selectionArgs);
        delVideoInUtils(findIndex(_vid));
        downLoadTotal--;
    }

    //删除Utils里的状态
    public static void delVideoInUtils(int _index){
        Utils.vid[_index]="";
        Utils.progress[_index]=0;
        Utils.imgTask[_index]=null;
        Utils.videoTask[_index]=null;
        Utils.downloadPosition[_index]=0;
        Utils.fileLength[_index]=0;
        Utils.progress[_index]=0;
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

    //获得当前是否有文件在下载
    public static Boolean isDownLoading(){
        Boolean ifLoading=false;
        if(downLoadTotal>0){
            ifLoading=true;
        }
        return ifLoading;
    }

    //停止当前全部下载
    public static void stopAllDown(){
        for(int i=0;i<Utils.videoTask.length;i++){
            if(Utils.videoTask[i]!=null){
                Utils.videoTask[i].cancel(true);
                Utils.videoTask[i]=null;
            }
        }
    }

    //取消当前全部下载
    public static void clearAllDown(){
        for(int i=0;i<Utils.videoTask.length;i++){
            if(Utils.videoTask[i]!=null){
                cancelTask(i);
            }
        }
    }

    //取消下载
    public static void removeDownload(String vid) {
        int _index=findIndex(vid);
        if(_index>=0) {
            if (Utils.videoTask[_index] != null) {
                cancelTask(_index);
            }
        }
    }

    //删除Task
    private static void cancelTask(int _i){
        Utils.videoTask[_i].cancel(true);
        Utils.videoTask[_i]=null;
        downLoadTotal--;
        delSDFile(Utils.vid[_i] + messageCode.VIDEOFILETYPE);
        delSDFile(Utils.vid[_i]+messageCode.IMAGEFILETYPE);
        delVideoInDB(Utils.vid[_i]);
        delVideoInUtils(_i);
    }

    //关闭数据库对象
    public static void closeDB(){
        mDB.close();
    }
}
