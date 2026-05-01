package com.zxl.common.db.sqlite;

import android.database.Cursor;

/**
 * Author: wyouflf
 * Date: 13-11-4
 * Time: 下午10:51
 */
public class ByteArrayColumnConverter implements ColumnConverter<byte[]>
{
    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getFieldValue(final Cursor cursor, final int index)
    {
        return cursor.isNull(index) ? null : cursor.getBlob(index);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getFieldValue(final String fieldStringValue)
    {
        return new byte[0];
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Object fieldValue2ColumnValue(final byte[] fieldValue)
    {
        return fieldValue;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ColumnDbType getColumnDbType()
    {
        return ColumnDbType.BLOB;
    }
}
