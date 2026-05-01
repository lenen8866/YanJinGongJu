package com.zxl.common.db.sqlite;

import android.database.Cursor;
import android.text.TextUtils;

/**
 * Author: wyouflf
 * Date: 13-11-4
 * Time: 下午10:51
 */
public class ByteColumnConverter implements ColumnConverter<Byte>
{
    /**
     * {@inheritDoc}
     */
    @Override
    public Byte getFieldValue(final Cursor cursor, final int index)
    {
        return cursor.isNull(index) ? null : (byte)cursor.getInt(index);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Byte getFieldValue(final String fieldStringValue)
    {
        if (TextUtils.isEmpty(fieldStringValue))
        {
            return null;
        }
        return Byte.valueOf(fieldStringValue);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Object fieldValue2ColumnValue(final Byte fieldValue)
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
