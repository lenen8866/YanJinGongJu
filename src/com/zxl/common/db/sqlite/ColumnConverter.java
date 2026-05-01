package com.zxl.common.db.sqlite;

import android.database.Cursor;

/**
 * Author: wyouflf
 * Date: 13-11-4
 * Time: 下午8:57
 * 
 * @param <T> t
 */
public interface ColumnConverter<T>
{
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * 
     * @param cursor cursor
     * @param index index
     * @return getFieldValue
     * @see [类、类#方法、类#成员]
     */
    T getFieldValue(final Cursor cursor, int index);
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * 
     * @param fieldStringValue fieldStringValue
     * @return getFieldValue
     * @see [类、类 #方法、类#成员]
     */
    T getFieldValue(String fieldStringValue);
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * 
     * @param fieldValue fieldValue
     * @return fieldValue2ColumnValue
     * @see [类、类#方法、类#成员]
     */
    Object fieldValue2ColumnValue(T fieldValue);
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * 
     * @return ColumnDbType
     * @see [类、类#方法、类#成员]
     */
    com.zxl.common.db.sqlite.ColumnDbType getColumnDbType();
}
