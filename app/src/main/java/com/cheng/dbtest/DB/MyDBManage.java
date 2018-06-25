package com.cheng.dbtest.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 务必注意表名即类名，使用请注意有无重复的类名
 * 务必注意使用的model属性必须设置set方法
 * 另外model不要有内部类或者类属性相关，我没测试过会不会有影响，估计有
 * 不要在代码中操作删除表，升级数据库版本来操作
 * 操作数据库都使用这个类，不要直接操作MyDB或者MyDBHelp
 */
public class MyDBManage {

    private static MyDBManage myDBManage;
    private SQLiteDatabase mDb;
    private Context context;

    /**
     * 创建StudentDao对象
     * @param context
     */
    private MyDBManage(Context context) {
        this.mDb = MyDB.getInstance(context).getDB();
        this.context = context;
    }

    public synchronized static MyDBManage getInstance(Context context){
        if(myDBManage==null){
            myDBManage = new MyDBManage(context);
        }
        return myDBManage;
    }

    /**
     * 查询数据库中所有表名
     * 测试用
     */
    public List<String> getAllTable(){
        List<String> lists = new ArrayList<>();
        Cursor cursor = mDb.rawQuery("select name from sqlite_master where type='table' order by name", null);
        while(cursor.moveToNext()){
            //遍历出表名
            String name = cursor.getString(0);
            lists.add(name);
        }
        if(cursor != null) cursor.close();
        return lists;
    }

    /**
     * 判断数据库中是否存在表
     */
    public <T> boolean hasTable(Class<T> clazz){
//        select count(*) from sqlite_master where type='table' and name ='yourtablename'
        String sql = "select count(*) from sqlite_master where type='table' and name='" + clazz.getSimpleName() + "' ";
        Cursor cursor = mDb.rawQuery(sql, null);
        if(cursor.moveToNext()){
            int count = cursor.getInt(0);
            if(count>0){
                return true;
            }
        }
        return false;
    }

    /**
     * 查询表中所有列名
     * 测试用
     */
    public <T> List<String> getAllCulName(Class<T> clazz){
        return getAllCulName(clazz.getSimpleName());
    }

    /**
     * 查询表中所有列名
     * 测试用
     */
    public List<String> getAllCulName(String table_name){
        List<String> lists = new ArrayList<>();
        Cursor cursor = null;
        try{
            cursor = mDb.rawQuery("SELECT * FROM " + table_name, null);
            if (cursor != null) {
                String[] columnNames = cursor.getColumnNames();
                for (int i = 0; i < columnNames.length; i++) {
                    lists.add(columnNames[i]);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if(cursor != null){
                cursor.close();
            }
        }
        return lists;
    }

    /**
     * 查询数据库中所有的数据
     *
     * @param clazz
     * @param <T>  以 List的形式返回数据库中所有数据
     * @return 返回list集合
     */
    public <T> List<T> findAll(Class<T> clazz) {
        Cursor cursor = getCursor(clazz, null, null, null, null, false);
        if(cursor!=null){
            return getEntity(cursor, clazz);
        }else{
            return new ArrayList<>();
        }
    }

    /**
     * 通过id查找制定数据
     *
     * @param clazz 指定类
     * @param id    条件id
     * @return 返回满足条件的对象
     */
    public <T> T findById(Class<T> clazz, int id) {
        Cursor cursor = getCursor(clazz, null, "id=" + id, null, null, false);
        if(cursor==null){
            return null;
        }else{
            List<T> list = getEntity(cursor, clazz);
            if(list!=null && list.size()>0){
                return list.get(0);
            }else{
                return null;
            }
        }
    }

//    sql = "select * from (tablename) where (select + selectArgs)
    /**
     * 根据指定条件返回满足条件的记录
     *
     * @param clazz      类
     * @param select     条件语句 ：（"id>? and age=?"）
     * @param selectArgs 条件(new String[]{"15", "13"}) 查询id大于15并且年龄等于13的记录
     * @return 返回满足条件的list集合
     */
    public <T> List<T> findByArgs(Class<T> clazz, String select, String[] selectArgs) {
        Cursor cursor = getCursor(clazz, null, select, selectArgs, null, false);
        if(cursor!=null){
            return getEntity(cursor, clazz);
        }else{
            return new ArrayList<>();
        }
    }

    /**
     * 根据指定条件返回满足条件的记录
     *
     * @param clazz      类
     * @param select     条件语句 ：（"id>? and age=?"）
     * @param selectArgs 条件(new String[]{"15", "13"}) 查询id大于15并且年龄等于13的记录
     * @param order     排序  某个字段
     * @return 返回满足条件的list集合
     */
    public <T> List<T> findByArgs(Class<T> clazz, String select, String[] selectArgs, String order) {
        Cursor cursor = getCursor(clazz, null, select, selectArgs, order, false);
        if(cursor!=null){
            return getEntity(cursor, clazz);
        }else{
            return new ArrayList<>();
        }
    }

    /**
     * 根据指定条件返回满足条件的记录
     *
     * @param clazz      类
     * @param order     排序  某个字段
     * @param desc      排序  true 倒序
     * @param limit     查询数量 指定查询条目数
     * @param offset    跳过数量  忽略前面条目数
     * @return 返回满足条件的list集合
     */
    public <T> List<T> findByArgs(Class<T> clazz, String order, boolean desc, int limit, int offset) {
        Cursor cursor = getCursor(clazz, null, null, null, order, desc, limit, offset);
        if(cursor!=null){
            return getEntity(cursor, clazz);
        }else{
            return new ArrayList<>();
        }
    }

//    sql = "select * from (tablename) where (select + selectArgs) order by (order) limit (count),(offset)
    /**
     * 根据指定条件返回满足条件的记录
     *
     * @param clazz      类
     * @param select     条件语句 ：（"id>? and age=?"）
     * @param selectArgs 条件(new String[]{"15", "13"}) 查询id大于15并且年龄等于13的记录
     * @param order     排序  某个字段
     * @param desc      排序  true 倒序
     * @param limit     查询数量 指定查询条目数
     * @param offset    跳过数量  忽略前面条目数
     * @return 返回满足条件的list集合
     */
    public <T> List<T> findByArgs(Class<T> clazz, String select, String[] selectArgs, String order, boolean desc, int limit, int offset) {
        Cursor cursor = getCursor(clazz, null, select, selectArgs, order, desc, limit, offset);
        if(cursor!=null){
            return getEntity(cursor, clazz);
        }else{
            return new ArrayList<>();
        }
    }

    //    sql = "select (colunm) from (tablename) where (select + selectArgs) order by (order) limit (count),(offset)
    /**
     * 根据指定条件返回满足条件的记录
     *
     * @param clazz      类
     * @param colunm   列名(new String[]{"name", "sex"}) 查询指定列
     * @param select     条件语句 ：（"id>? and age=?"）
     * @param selectArgs 条件(new String[]{"15", "13"}) 查询id大于15并且年龄等于13的记录
     * @param order     排序  某个字段
     * @param desc      排序  true 倒序
     * @param limit     查询数量 指定查询条目数
     * @param offset    跳过数量  忽略前面条目数
     * @return 返回满足条件的list集合
     */
    public <T> List<T> findByArgs(Class<T> clazz, String[] colunm, String select, String[] selectArgs, String order, boolean desc, int limit, int offset) {
        Cursor cursor = getCursor(clazz, colunm, select, selectArgs, order, desc, limit, offset);
        if(cursor!=null){
            return getEntity(cursor, clazz);
        }else{
            return new ArrayList<>();
        }
    }

    public <T> Cursor getCursor(Class<T> clazz, String[] colunm, String select, String[] selectArgs, String order, boolean desc){
        try{
             Cursor cursor = mDb.query(clazz.getSimpleName()
                , colunm
                , select
                , selectArgs
                , null
                , null
                , order + (desc ? " desc " : " asc "));
             return cursor;
        }catch(net.sqlcipher.database.SQLiteException e){
            e.printStackTrace();
            if(hasTable(clazz)) mDb.execSQL(DBUtils.getDeleteTableSql(clazz));
            mDb.execSQL(DBUtils.getCreateTableSql(clazz));
        }
        return null;
    }

    public <T> Cursor getCursor(Class<T> clazz, String[] colunm, String select, String[] selectArgs, String order, boolean desc, int limit, int offset){
        try{
             Cursor cursor = mDb.query(clazz.getSimpleName()
                , colunm
                , select
                , selectArgs
                , null
                , null
                , order + (desc ? " desc " : " asc ")
                , offset+ "," + limit);
             return cursor;
        }catch(net.sqlcipher.database.SQLiteException e){
            e.printStackTrace();
            if(hasTable(clazz)) mDb.execSQL(DBUtils.getDeleteTableSql(clazz));
            mDb.execSQL(DBUtils.getCreateTableSql(clazz));
        }
        return null;
    }

    /**
     * 从数据库得到实体类
     *
     * @param cursor
     * @param clazz
     * @return 返回list集合
     */
    private <T> List<T> getEntity(Cursor cursor, Class<T> clazz) {
        List<T> list = new ArrayList<>();
        try {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Field[] fields = clazz.getDeclaredFields();
                    T modeClass = clazz.newInstance();
                    for (Field field : fields) {
                        Class<?> cursorClass = cursor.getClass();
                        String columnMethodName = DBUtils.getColumnMethodName(field.getType());
                        if("getIncrementalChange".equals(columnMethodName)) continue;

                        Method cursorMethod = cursorClass.getMethod(columnMethodName, int.class);

                        int columnIndex = cursor.getColumnIndex(field.getName());
                        Object value = null;
                        if(columnIndex>=0) value = cursorMethod.invoke(cursor, cursor.getColumnIndex(field.getName()));

                        if (field.getType() == boolean.class || field.getType() == Boolean.class) {
                            if ("0".equals(String.valueOf(value))) {
                                value = false;
                            } else if ("1".equals(String.valueOf(value))) {
                                value = true;
                            }
                        } else if (field.getType() == char.class || field.getType() == Character.class) {
                            value = ((String) value).charAt(0);
                        } else if (field.getType() == Date.class) {
                            long date = (Long) value;
                            if (date <= 0) {
                                value = null;
                            } else {
                                value = new Date(date);
                            }
                        }

                        if(value==null) continue;

                        String methodName = DBUtils.makeSetterMethodName(field);
                        Method method = clazz.getDeclaredMethod(methodName, field.getType());
                        method.invoke(modeClass, value);
                    }
                    list.add(modeClass);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    /**
     * 添加一条数据
     *
     * @param object
     * @return 返回添加结果
     */
    public long addObject(Object object){
        return mDb.insert(DBUtils.getTableName(object.getClass()), null, DBUtils.getInsertValue(object));
    }

    /**
     * 删除一条记录
     *
     * @param clazz 需要删除的类名
     * @param id    需要删除的 id索引
     * @return 返回删除结果
     */
    public int deleteById(Class<?> clazz, long id) {
        return mDb.delete(DBUtils.getTableName(clazz), "id=" + id, null);
    }

    /**
     * 清除表数据并重置自增长id
     *
     * @param clazz 需要删除的类名
     * @param start 自增长id开始位置，一般为0
     * @return 返回删除结果
     */
    public int deleteByClass(Class<?> clazz, int start) {
        int delete = mDb.delete(DBUtils.getTableName(clazz), null, null);
        String sql = "update sqlite_sequence set seq=" + start + " where name='" + DBUtils.getTableName(clazz) + "'";
        mDb.execSQL(sql);
        return delete;
    }

    /**
     * 更新一条记录
     *
     * @param clazz  类
     * @param obj     对象
     * @param id     更新id索引
     * @return 返回修改结果
     */
    public int updateById(Class<?> clazz, Object obj, long id) {
        ContentValues insertValue = DBUtils.getInsertValue(obj);
        return mDb.update(clazz.getSimpleName(), insertValue, "id=" + id, null);
    }

    /**
     * 更新一条记录
     *
     * @param clazz  类
     * @param obj     对象
     * @param select   条件判断
     * @param selectArgs  条件值
     * @return 返回修改结果
     */
    public int updateByArgs(Class<?> clazz, Object obj, String select, String[] selectArgs) {
        ContentValues insertValue = DBUtils.getInsertValue(obj);
        return mDb.update(clazz.getSimpleName(), insertValue, select, selectArgs);
    }

    /**
     * 更新一条记录
     *
     * @param clazz  类
     * @param values 更新对象
     * @param id     更新id索引
     * @return 返回修改结果
     */
    public int updateById(Class<?> clazz, ContentValues values, long id) {
        return mDb.update(clazz.getSimpleName(), values, "id=" + id, null);
    }
