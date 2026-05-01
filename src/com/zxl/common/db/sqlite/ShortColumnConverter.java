package com.zxl.common.db.sqlite;

import android.database.Cursor;
import android.text.TextUtils;

/**
 * Author: wyouflf
 * Date: 13-11-4
 * Time: 下午10:51
 */
public class ShortColumnConverter implements ColumnConverter<Short>
{
    /**
     * {@inheritDoc}
     */
    @Override
    public Short getFieldValue(final Cursor cursor, final int index)
    {
        return cursor.isNull(index) ? null : cursor.getShort(index);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Short getFieldValue(final String fieldStringValue)
    {
        if (TextUtils.isEmpty(fieldStringValue))
        {
            return null;
        }
        return Short.valueOf(fieldStringValue);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Object fieldValue2ColumnValue(final Short fieldValue)
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
