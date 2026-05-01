package com.zxl.common.db.sqlite;

import android.database.Cursor;
import android.text.TextUtils;

/**
 * Author: wyouflf
 * Date: 13-11-4
 * Time: 下午10:51
 */
public class BooleanColumnConverter implements ColumnConverter<Boolean>
{
    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean getFieldValue(final Cursor cursor, final int index)
    {
        return cursor.isNull(index) ? null : 1 == cursor.getInt(index);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean getFieldValue(final String fieldStringValue)
    {
        
        if (TextUtils.isEmpty(fieldStringValue))
        {
            return false;
        }
        return 1 == fieldStringValue.length() ? Boolean.valueOf("1".equals(fieldStringValue))
            : Boolean.valueOf(fieldStringValue);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Object fieldValue2ColumnValue(final Boolean fieldValue)
    {
        if (null == fieldValue)
        {
            return null;
        }
        return fieldValue ? 1 : 0;
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
