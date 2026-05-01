package com.zxl.common.db.sqlite;

import android.database.Cursor;
import android.text.TextUtils;

/**
 * Author: wyouflf
 * Date: 13-11-4
 * Time: 下午10:51
 */
public class CharColumnConverter implements ColumnConverter<Character>
{
    /**
     * {@inheritDoc}
     */
    @Override
    public Character getFieldValue(final Cursor cursor, final int index)
    {
        return cursor.isNull(index) ? null : (char)cursor.getInt(index);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Character getFieldValue(final String fieldStringValue)
    {
        if (TextUtils.isEmpty(fieldStringValue))
        {
            return null;
        }
        return fieldStringValue.charAt(0);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Object fieldValue2ColumnValue(final Character fieldValue)
    {
        if (fieldValue == null)
        {
            return null;
        }
        return (int)fieldValue;
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
