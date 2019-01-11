package util;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import bean.Videoinfo;

/**
 *
 */
public class DBOService {
	public static DBOpenHelper openHelper;
	public static  void initdata(DBOpenHelper openHelper1) {
        openHelper=openHelper1;
	}

    /**
     * 根据状态查询 视频列表
     *
     * @return
     */
    public  static List<Videoinfo> getData(String state) {
        SQLiteDatabase db = openHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select vname, path,size,upurl,token,imagepath,title,miaoshu,biaoqian from upvideo where isok=?",new String[] { state });
        List<Videoinfo> vs=new ArrayList<Videoinfo>();
        while (cursor.moveToNext()) {
             Videoinfo v=new Videoinfo();
            v.setVideoName(cursor.getString(0));
            v.setAddresspath(cursor.getString(1));
            v.setSize(cursor.getString(2));
            v.setUpurl(cursor.getString(3));
            v.setToken(cursor.getString(4));
            v.setVideoImgurl(cursor.getString(5));
            v.setTitle(cursor.getString(6));
            v.setMiaoshu(cursor.getString(7));
            v.setBiaoqian(cursor.getString(8));
            File f=new File(v.getAddresspath());
            if(f.exists()){
                vs.add(v);
            }
        }
        cursor.close();
        db.close();
        return vs;
    }
    /**
     * 根据名称 视频列表
     *
     * @return
     */
    public static Videoinfo getVideoByname(String name) {
        SQLiteDatabase db = openHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select isok, path  from upvideo where vname=?  and isok=? ",new String[] { name ,"2"});
        Videoinfo v=new Videoinfo();
        if (cursor.moveToNext()) {
            v.setIsok(cursor.getString(0));
            v.setAddresspath(cursor.getString(1));
        }
        cursor.close();
        db.close();
        if(StringCommon.isNotblank(v.getAddresspath())){
            return  v;
        }
        return null;
    }

    /**
     *保存已经上传的视频
     *
     */
    public static void save(Videoinfo v) {// int threadid,
        SQLiteDatabase db = openHelper.getWritableDatabase();
        db.beginTransaction();
        try {
                db.execSQL("insert into upvideo(path, vname, isok,size,upurl,token,imagepath,title,miaoshu,biaoqian) values(?,?,?,?,?,?,?,?,?,?)",new Object[] { v.getAddresspath(), v.getVideoName(), "2",v.getSize(),v.getUpurl(),v.getToken(),v.getVideoImgurl(),v.getTitle(),v.getMiaoshu(),v.getBiaoqian()});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        db.close();
    }

    /**
     * 更改上传的状态 是否完成
     *
     *
     */
    public static void updatestate(String vname, String state) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        db.execSQL("update upvideo set isok=? where  vname=?",new Object[] { state, vname });
        db.close();
    }
    /**
     * 更改上传的状态 是否完成
     *
     *
     */
    public static void updatename(String vnameold, String vnamenew) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        db.execSQL("update upvideo set isok=? where  vname=?",new Object[] { vnamenew, vnameold });
        db.close();
    }
    /**
     * 删除数据
     *
     *
     */
    public static void deleteupvideo(String vname) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        db.execSQL("delete from upvideo where   vname=?",new Object[] {  vname });
        db.close();
    }

    /**
     * 删除数据
     *
     *
     */
    public static void deleteupvideobystate(String isok) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        db.execSQL("delete from upvideo where   isok=?",new Object[] {  isok });
        db.close();
    }
}
