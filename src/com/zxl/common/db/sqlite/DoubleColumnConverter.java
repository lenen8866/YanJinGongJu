package com.zxl.common.db.sqlite;

import android.database.Cursor;
import android.text.TextUtils;

/**
 * Author: wyouflf
 * Date: 13-11-4
 * Time: 下午10:51
 */
public class DoubleColumnConverter implements ColumnConverter<Double>
{
    /**
     * {@inheritDoc}
     */
    @Override
    public Double getFieldValue(final Cursor cursor, final int index)
    {
        return cursor.isNull(index) ? null : cursor.getDouble(index);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Double getFieldValue(final String fieldStringValue)
    {
        if (TextUtils.isEmpty(fieldStringValue))
        {
            return null;
        }
        return Double.valueOf(fieldStringValue);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Object fieldValue2ColumnValue(final Double fieldValue)
    {
        return fieldValue;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ColumnDbType getColumnDbType()
    {
        return ColumnDbType.REAL;
    }
}
