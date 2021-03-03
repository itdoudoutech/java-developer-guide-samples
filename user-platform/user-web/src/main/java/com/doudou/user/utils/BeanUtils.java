package com.doudou.user.utils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Timestamp;
import java.util.*;

public class BeanUtils {

    public static <T> T resultSetToBean(Class<T> clazz, ResultSet resultSet) {
        T obj = null;

        try {
            Map<String, Field> fieldMap = getFields(clazz);
            obj = clazz.newInstance();
            final ResultSetMetaData metaData = resultSet.getMetaData();
            int columnLength = metaData.getColumnCount();
            String columnName = null;

            for (int i = 1; i <= columnLength; i++) {
                columnName = metaData.getColumnLabel(i).toLowerCase();
                Class fieldClazz = fieldMap.get(columnName).getType();
                Field field = fieldMap.get(columnName);
                field.setAccessible(true);
                if (fieldClazz == int.class || fieldClazz == Integer.class) { // int
                    field.set(obj, resultSet.getInt(columnName));
                } else if (fieldClazz == boolean.class || fieldClazz == Boolean.class) { // boolean
                    field.set(obj, resultSet.getBoolean(columnName));
                } else if (fieldClazz == String.class) { // string
                    field.set(obj, resultSet.getString(columnName));
                } else if (fieldClazz == float.class) { // float
                    field.set(obj, resultSet.getFloat(columnName));
                } else if (fieldClazz == double.class || fieldClazz == Double.class) { // double
                    field.set(obj, resultSet.getDouble(columnName));
                } else if (fieldClazz == BigDecimal.class) { // bigdecimal
                    field.set(obj, resultSet.getBigDecimal(columnName));
                } else if (fieldClazz == short.class || fieldClazz == Short.class) { // short
                    field.set(obj, resultSet.getShort(columnName));
                } else if (fieldClazz == Date.class) { // date
                    field.set(obj, resultSet.getDate(columnName));
                } else if (fieldClazz == Timestamp.class) { // timestamp
                    field.set(obj, resultSet.getTimestamp(columnName));
                } else if (fieldClazz == Long.class || fieldClazz == long.class) { // long
                    field.set(obj, resultSet.getLong(columnName));
                }

                field.setAccessible(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }


    private static Map<String, Field> getFields(Class<?> clazz) {
        List<Field> fieldList = new ArrayList<>();
        while (clazz != null) {
            fieldList.addAll(new ArrayList<>(Arrays.asList(clazz.getDeclaredFields())));
            clazz = clazz.getSuperclass();
        }
        Field[] fields = new Field[fieldList.size()];
        fieldList.toArray(fields);

        Map<String, Field> map = new HashMap<>(fields.length);
        for (Field field : fields) {
            // 阿里开发公约，建议表名、字段名必须使用小写字母或数字；禁止出现数字开头，禁止两个下划线中间只出现数字。
            map.put(field.getName().toLowerCase(), field);
        }
        return map;
    }
}
