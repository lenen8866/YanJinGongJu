package com.zxl.common.db.sqlite;

import android.database.Cursor;

/**
 * Author: wyouflf
 * Date: 13-11-4
 * Time: 下午10:51
 */
public class StringColumnConverter implements ColumnConverter<String>
{
    /**
     * {@inheritDoc}
     */
    @Override
    public String getFieldValue(final Cursor cursor, final int index)
    {
        return cursor.isNull(index) ? null : cursor.getString(index);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getFieldValue(final String fieldStringValue)
    {
        return fieldStringValue;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Object fieldValue2ColumnValue(final String fieldValue)
    {
        return fieldValue;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ColumnDbType getColumnDbType()
    {
        return ColumnDbType.TEXT;
    }
}
