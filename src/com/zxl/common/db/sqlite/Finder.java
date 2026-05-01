package com.zxl.common.db.sqlite;

import android.database.Cursor;

import com.read.scriptures.util.LogUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;

/**
 * Author: wyouflf
 * Date: 13-9-10
 * Time: 下午7:43
 */
public class Finder extends Column
{
    
    private final String valueColumnName;
    
    private final String targetColumnName;
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     *
     * @param entityType entityType
     * @param field field
     * @see [类、类#方法、类#成员]
     */
    Finder(final Class<?> entityType, final Field field)
    {
        super(entityType, field);
        
        final com.zxl.common.db.annotation.Finder finder =
            field.getAnnotation(com.zxl.common.db.annotation.Finder.class);
        valueColumnName = finder.valueColumn();
        targetColumnName = finder.targetColumn();
    }
    
    public Class<?> getTargetEntityType()
    {
        return ColumnUtils.getFinderTargetEntityType(this);
    }
    
    public String getTargetColumnName()
    {
        return targetColumnName;
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void setValue2Entity(final Object entity, final Cursor cursor, final int index)
    {
        Object value = null;
        final Class<?> columnType = columnField.getType();
        final Object finderValue = TableUtils.getColumnOrId(entity.getClass(), valueColumnName).getColumnValue(entity);
        if (columnType == FinderLazyLoader.class)
        {
            value = new FinderLazyLoader(this, finderValue);
        }
        else if (columnType == List.class)
        {
            try
            {
                value = new FinderLazyLoader(this, finderValue).getAllFromDb();
            }
            catch (final DbException e)
            {
                LogUtil.error("Finder setValue2Entity DbException");
            }
        }
        else
        {
            try
            {
                value = new FinderLazyLoader(this, finderValue).getFirstFromDb();
            }
            catch (final DbException e)
            {
                LogUtil.error("Finder setValue2Entity DbException");
            }
        }
        
        if (setMethod != null)
        {
            try
            {
                setMethod.invoke(entity, value);
            }
            catch (final IllegalAccessException e)
            {
                LogUtil.error("Finder setValue2Entity IllegalAccessException");
            }
            catch (final IllegalArgumentException e)
            {
                LogUtil.error("Finder setValue2Entity IllegalArgumentException");
            }
            catch (final InvocationTargetException e)
            {
                LogUtil.error("Finder setValue2Entity InvocationTargetException");
            }
        }
        else
        {
            try
            {
                AccessController.doPrivileged(new PrivilegedAction()
                {
                    @Override
                    public Object run()
                    {
                        columnField.setAccessible(true);
                        return null;
                    }
                });
                columnField.set(entity, value);
            }
            catch (final IllegalAccessException e)
            {
                LogUtil.error("Finder setValue2Entity IllegalAccessException");
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Object getColumnValue(final Object entity)
    {
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Object getDefaultValue()
    {
        return null;
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
