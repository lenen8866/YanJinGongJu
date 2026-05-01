package com.zxl.common.db.sqlite;

import android.database.Cursor;
import android.text.TextUtils;

import java.util.Date;

/**
 * Author: wyouflf
 * Date: 13-11-4
 * Time: 下午10:51
 */
public class DateColumnConverter implements ColumnConverter<Date>
{
    /**
     * {@inheritDoc}
     */
    @Override
    public Date getFieldValue(final Cursor cursor, final int index)
    {
        return cursor.isNull(index) ? null : new Date(cursor.getLong(index));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Date getFieldValue(final String fieldStringValue)
    {
        if (TextUtils.isEmpty(fieldStringValue))
        {
            return null;
        }
        return new Date(Long.valueOf(fieldStringValue));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Object fieldValue2ColumnValue(final Date fieldValue)
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
