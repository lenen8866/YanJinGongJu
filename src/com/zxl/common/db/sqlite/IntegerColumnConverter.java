package com.zxl.common.db.sqlite;

import android.database.Cursor;
import android.text.TextUtils;

/**
 * Author: wyouflf
 * Date: 13-11-4
 * Time: 下午10:51
 */
public class IntegerColumnConverter implements ColumnConverter<Integer>
{
    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getFieldValue(final Cursor cursor, final int index)
    {
        return cursor.isNull(index) ? null : cursor.getInt(index);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getFieldValue(final String fieldStringValue)
    {
        if (TextUtils.isEmpty(fieldStringValue))
        {
            return null;
        }
        return Integer.valueOf(fieldStringValue);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Object fieldValue2ColumnValue(final Integer fieldValue)
    {
        return fieldValue;
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
