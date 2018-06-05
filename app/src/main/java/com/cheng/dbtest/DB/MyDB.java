package com.cheng.dbtest.DB;

import android.content.Context;
import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteException;

import java.io.File;
import java.io.IOException;

public class MyDB {

    private SQLiteDatabase mDB;
    private MyDBHelp mDbHelper;
    private static MyDB mInstance;
    private Context context;

    private MyDB(Context context) {
        try {
            // db helper为空则创建一个，避免重复创建
            this.context = context;
            if (mDbHelper == null) mDbHelper = new MyDBHelp(context);
            // 数据库存在并处于打开状态那么直接使用
            if (mDB != null && mDB.isOpen()) return;
                // 否则创建
            else getDB();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    public synchronized static MyDB getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new MyDB(context);
        }
        return mInstance;
    }

    /**
     * 获取DB对象。
     * 这里会使用当前设置的密码打开数据库，如果出现密码异常，说明已更换密码，则会重置数据库当前密码
     * @return SQLiteDatabase
     */
    public SQLiteDatabase getDB() {
        try{
            if(mDB==null) mDB = mDbHelper.getWritableDatabase(MyDBHelp.DB_PWD);
        }catch (SQLiteException e){
            Log.i("111", "数据库打开异常");
            String message = e.getMessage();
            if(message.contains("encrypt") || message.contains("sqlite_master")){
                try {
                    encrypt(MyDBHelp.DB_NMAE, MyDBHelp.DB_PWD);
                } catch (IOException e1) {
                    Log.i("111", "数据库加密异常");
                }
            }
        }
        return mDB;
    }

    //重置密码操作，实际上相当于新建了一张表拷贝原表数据设置密码后删除原表并重命名新表
    public void encrypt(String dbName, String passphrase) throws IOException {
        File originalFile = context.getDatabasePath(dbName);
        if (originalFile.exists()) {
            File newFile = File.createTempFile("sqlcipherutils", "tmp", context.getCacheDir());
            SQLiteDatabase db = SQLiteDatabase.openDatabase(originalFile.getAbsolutePath(),"", null, SQLiteDatabase.OPEN_READWRITE);

            db.rawExecSQL(String.format("ATTACH DATABASE '%s' AS encrypted KEY '%s';", newFile.getAbsolutePath(), passphrase));
            db.rawExecSQL("SELECT sqlcipher_export('encrypted')");
            db.rawExecSQL("DETACH DATABASE encrypted;");

            int version = db.getVersion();
            db.close();

            db = SQLiteDatabase.openDatabase(newFile.getAbsolutePath(), passphrase, null, SQLiteDatabase.OPEN_READWRITE);
            db.setVersion(version);
            db.close();

            originalFile.delete();
            newFile.renameTo(originalFile);
        }
    }

    /**
     * 关闭数据库的操作
     */
    public void close() {
        mDB.close();
        mDbHelper.close();
    }

}
