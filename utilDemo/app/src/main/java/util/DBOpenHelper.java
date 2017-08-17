package util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBOpenHelper extends SQLiteOpenHelper {
	private static final String DBNAME = "eric.db";

    public DBOpenHelper(Context context)
    {
        super(context,DBNAME, null,8);//this(context, name, factory, version, null);
    }
    @Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS upvideo (id integer primary key autoincrement , title varchar(100) , miaoshu varchar(150) , biaoqian varchar(100), path varchar(100), vname varchar(100), isok varchar(2),size varchar(50),upurl varchar(100),imagepath varchar(100),token varchar(150))");
	}

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO 更改数据库版本的操作
        Log.d("LOGCAT", "Vers" + "ion:" + oldVersion + " to " + newVersion);
        if(oldVersion < newVersion) {
            db.execSQL("DROP TABLE IF EXISTS upvideo" );
        }
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        // TODO 每次成功打开数据库后首先被执�?
    }
}
