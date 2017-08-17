package math;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import values.messageCode;

/**
 * 管理sql数据
 * Created by QZD on 2015/3/25.
 */
public class sqlHelper extends SQLiteOpenHelper {
    public sqlHelper(Context context)
    {
        super(context, messageCode.APPDBNAME, null, 1);//最后个参数用以更新数据库
        //this(context, name, factory, version, null);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO 创建数据库后，对数据库的操作
        Log.d("LOGCAT","creatDb");
        //创建数据库时创建下载信息表
        db.execSQL("CREATE TABLE " + messageCode.DOWNLOADTABLENAME +" (_id INTEGER PRIMARY KEY AUTOINCREMENT, vid VARCHAR, type VARCHAR, vname VARCHAR, uname VARCHAR, imgUrl VARCHAR, vUrl VARCHAR, position INT, done TINYINT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO 更改数据库版本的操作
        Log.d("LOGCAT","Version:"+oldVersion+" to "+newVersion);
        if(oldVersion < newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + messageCode.DOWNLOADTABLENAME);
        }
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        // TODO 每次成功打开数据库后首先被执行
    }
}
