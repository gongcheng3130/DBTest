package com.cheng.dbtest.DB;

import android.content.ContentValues;
import android.text.TextUtils;
import android.util.Log;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

public class DBUtils {

    private static final String TAG = "DBUtils";

    /**
     * 得到对象的键对值
     * @param obj 对象
     * @return ContentValues
     */
    public static ContentValues getInsertValue(Object obj) {
        Class<?> modeClass = obj.getClass();
        Field[] fields = modeClass.getDeclaredFields();
        ContentValues values = new ContentValues();
        for (Field fd : fields) {
            fd.setAccessible(true);
            String fieldName = fd.getName();
            //剔除主键id值得保存，由于框架默认设置id为主键自动增长
            if (fieldName.equalsIgnoreCase("id") || fieldName.equalsIgnoreCase("_id")) {
                continue;
            }else if(fieldName.equalsIgnoreCase("$change") || fieldName.equalsIgnoreCase("serialVersionUID")){
                continue;
            }
            putValues(values, fd, obj);
        }
        return values;
    }

    /**
     * 设置ContentValues中的参数
     * @param values ContentValues object
     * @param fd     the Field
     * @param obj    the value
     */
    private static void putValues(ContentValues values, Field fd, Object obj) {
        Class<?> clazz = values.getClass();
        try {
            Object[] parameters = new Object[]{fd.getName(), fd.get(obj)};
            Class<?>[] parameterTypes = getParameterTypes(fd, fd.get(obj), parameters);
            Method method = clazz.getDeclaredMethod("put", parameterTypes);
            method.setAccessible(true);
            method.invoke(values, parameters);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 得到建表语句
     * @param clazz 指定类
     * @return sql语句
     */
    public static String getCreateTableSql(Class<?> clazz) {
        StringBuilder sb = new StringBuilder();
        //将类名作为表名
        String tabName = getTableName(clazz);
        sb.append("create table if not exists ").append(tabName).append(" (id  INTEGER PRIMARY KEY AUTOINCREMENT, ");
        //得到类中所有属性对象数组
        Field[] fields = clazz.getDeclaredFields();
        for (Field fd : fields) {
            String fieldName = fd.getName();
            String fieldType = fd.getType().getName();
            Log.i(TAG, "fieldName = " + fieldName + " --- fieldType =  " + fieldType);
            if(fieldName.equalsIgnoreCase("_id") || fieldName.equalsIgnoreCase("id")){//id自增长主键
                continue;
            }else if(fieldName.equalsIgnoreCase("$change") || fieldName.equalsIgnoreCase("serialVersionUID")){
                continue;
            }else{
                sb.append(fieldName).append(getColumnType(fieldType)).append(", ");
            }
        }
        int len = sb.length();
        sb.replace(len - 2, len, ")");
        Log.i(TAG, "the result is " + sb.toString());
        return sb.toString();
    }

    /**
     * 得到删除表语句
     * @param clazz 指定类
     * @return sql语句
     */
    public static String getDeleteTableSql(Class<?> clazz) {
        String tabName = getTableName(clazz);
        return "drop table " + tabName;
    }

    //表增加字段
    public static String insertTableColumn(Class<?> clazz, String column, String type, String _default){
        Log.i(TAG, "alter table " + clazz.getSimpleName() + " add " + column + " " + type + " default " + _default);
        return "alter table " + clazz.getSimpleName() + " add " + column + " " + type + " default " + _default;
    }
    
    //得到表名
    public static String getTableName(Class<?> clazz){
        Log.i(TAG, "clazz.name = " + clazz.getSimpleName());
        return clazz.getSimpleName();
    }

    //得到字段的数据类型
    public static String getColumnType(String type) {
        String value = null;
        if (type.contains("String")) {
            value = " text ";
        } else if (type.contains("int")) {
            value = " integer ";
        } else if (type.contains("boolean")) {
            value = " boolean ";
        } else if (type.contains("float")) {
            value = " float ";
        } else if (type.contains("double")) {
            value = " double ";
        } else if (type.contains("char")) {
            value = " varchar ";
        } else if (type.contains("long")) {
            value = " long ";
        }
        Log.i(TAG, "type + " + type + " --- value.type =  " + value);
        return value;
    }

    /**
     * 得到反射方法中的参数类型
     * @param field
     * @param fieldValue
     * @param parameters
     * @return
     */
    public static Class<?>[] getParameterTypes(Field field, Object fieldValue, Object[] parameters) {
        Class<?>[] parameterTypes;
        if (isCharType(field)) {
            parameters[1] = String.valueOf(fieldValue);
            parameterTypes = new Class[]{String.class, String.class};
        } else {
            if (field.getType().isPrimitive()) {
                parameterTypes = new Class[]{String.class, getObjectType(field.getType())};
            } else if ("java.util.Date".equals(field.getType().getName())) {
                parameterTypes = new Class[]{String.class, Long.class};
            } else {
                parameterTypes = new Class[]{String.class, field.getType()};
            }
        }
        return parameterTypes;
    }

    /**
     * 是否是字符类型
     * @param field
     * @return
     */
    public static boolean isCharType(Field field) {
        String type = field.getType().getName();
        return type.equals("char") || type.endsWith("Character");
    }

    /**
     * 得到对象的类型
     * @param primitiveType
     * @return
     */
    public static Class<?> getObjectType(Class<?> primitiveType) {
        if (primitiveType != null) {
            if (primitiveType.isPrimitive()) {
                String basicTypeName = primitiveType.getName();
                if ("int".equals(basicTypeName)) {
                    return Integer.class;
                } else if ("short".equals(basicTypeName)) {
                    return Short.class;
                } else if ("long".equals(basicTypeName)) {
                    return Long.class;
                } else if ("float".equals(basicTypeName)) {
                    return Float.class;
                } else if ("double".equals(basicTypeName)) {
                    return Double.class;
                } else if ("boolean".equals(basicTypeName)) {
                    return Boolean.class;
                } else if ("char".equals(basicTypeName)) {
                    return Character.class;
                }
            }
        }
        return null;
    }

    public static String getColumnMethodName(Class<?> fieldType) {
        String typeName;
        if (fieldType.isPrimitive()) {
            typeName = DBUtils.capitalize(fieldType.getName());
        } else {
            typeName = fieldType.getSimpleName();
        }
        String methodName = "get" + typeName;
        if ("getBoolean".equals(methodName)) {
            methodName = "getInt";
        } else if ("getChar".equals(methodName) || "getCharacter".equals(methodName)) {
            methodName = "getString";
        } else if ("getDate".equals(methodName)) {
            methodName = "getLong";
        } else if ("getInteger".equals(methodName)) {
            methodName = "getInt";
        }
        return methodName;
    }

    public static String makeSetterMethodName(Field field) {
        String setterMethodName;
        String setterMethodPrefix = "set";
        if (isPrimitiveBooleanType(field) && field.getName().matches("^is[A-Z]{1}.*$")) {
            setterMethodName = setterMethodPrefix + field.getName().substring(2);
        } else if (field.getName().matches("^[a-z]{1}[A-Z]{1}.*")) {
            setterMethodName = setterMethodPrefix + field.getName();
        } else {
            setterMethodName = setterMethodPrefix + DBUtils.capitalize(field.getName());
        }
        return setterMethodName;
    }

    public static  boolean isPrimitiveBooleanType(Field field) {
        Class<?> fieldType = field.getType();
        if ("boolean".equals(fieldType.getName())) {
            return true;
        }
        return false;
    }

    public static String capitalize(String string) {
        if (!TextUtils.isEmpty(string)) {
            return string.substring(0, 1).toUpperCase(Locale.US) + string.substring(1);
        }
        return string == null ? null : "";
    }

}
