package com.cheng.dbtest.DB;

import android.content.Context;
import android.util.Log;
import com.cheng.dbtest.model.Student;
import com.cheng.dbtest.model.Teacher;
import net.sqlcipher.DatabaseErrorHandler;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteDatabaseHook;
import net.sqlcipher.database.SQLiteOpenHelper;

public class MyDBHelp extends SQLiteOpenHelper {

    private static final String TAG = "MyDBHelp";
    //数据库名称，这个应该一直不用动，不管他
    public static final String DB_NMAE = "Enterprise.db";
    //数据库版本，每对数据库需要做修改的时候，版本号加1
    public static final int DB_VERSION = 3;
    //数据库密码，加密使用，修改密码也只需要改这个字段的值，不需要做额外处理
    public static final String DB_PWD = "";//数据库密码，为空时等于没有加密

    public MyDBHelp(Context context){
        super(context, DB_NMAE, null, DB_VERSION);
        //不可忽略的 进行so库加载
        SQLiteDatabase.loadLibs(context);
        Log.i(TAG, "MyDBHelp(this)");
    }

    public MyDBHelp(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public MyDBHelp(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, SQLiteDatabaseHook hook) {
        super(context, name, factory, version, hook);
    }

    public MyDBHelp(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, SQLiteDatabaseHook hook, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, hook, errorHandler);
    }

    //数据库创建或者获取时调用
    //此处一般作为当前版本数据库需要初始化的操作，比如创建表
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "MyDBHelp --- onCreate()");
        db.execSQL(DBUtils.getCreateTableSql(Teacher.class));
        db.execSQL(DBUtils.getCreateTableSql(Student.class));
    }

    //版本升级时要做的操作，没有操作不需处理
    //此处一般作为修改上个版本数据库信息时需要改动的数据库字段等等
    //比如删除上个版本某个表，修改旧版本表中某个字段名等等
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //线上版本直接更新的话会走这里根据数据库版本号执行代码
        Log.i(TAG, "MyDBHelp --- onUpgrade()");
        switch (newVersion) {
            case 1:
                db.execSQL(DBUtils.getCreateTableSql(Teacher.class));
                break;
            case 2:
                db.execSQL(DBUtils.getCreateTableSql(Student.class));
                break;
            case 3:
                db.execSQL(DBUtils.insertTableColumn(Student.class, "project", "string", ""));
                break;
        }
    }

}
