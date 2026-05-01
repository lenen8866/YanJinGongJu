package com.zxl.common.db.sqlite;

import android.database.Cursor;
import android.text.TextUtils;

/**
 * Author: wyouflf
 * Date: 13-11-4
 * Time: 下午10:51
 */
public class SqlDateColumnConverter implements ColumnConverter<java.sql.Date>
{
    /**
     * {@inheritDoc}
     */
    @Override
    public java.sql.Date getFieldValue(final Cursor cursor, final int index)
    {
        return cursor.isNull(index) ? null : new java.sql.Date(cursor.getLong(index));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public java.sql.Date getFieldValue(final String fieldStringValue)
    {
        if (TextUtils.isEmpty(fieldStringValue))
        {
            return null;
        }
        return new java.sql.Date(Long.valueOf(fieldStringValue));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Object fieldValue2ColumnValue(final java.sql.Date fieldValue)
    {
        if (fieldValue == null)
        {
            return null;
        }
        return fieldValue.getTime();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ColumnDbType getColumnDbType()
    {
        return ColumnDbType.INTEGER;
    }
}
