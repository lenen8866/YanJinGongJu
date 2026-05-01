/*
 * Copyright (c) 2013. wyouflf (wyouflf@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zxl.common.db.sqlite;

import android.database.Cursor;

import com.read.scriptures.util.LogUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 *
 * <一句话功能简述>
 * @author Administrator
 * @version V100R001C13, 2015-5-26
 * @since V100R001C13
 */
public class Column
{
    
    protected final Method setMethod;
    
    protected final Field columnField;
    
    protected final ColumnConverter columnConverter;
    
    protected final String columnName;
    
    protected final Method getMethod;
    
    protected Table table;
    
    protected int index = -1;
    
    protected final Object defaultValue;
    
    /**
     *  package
     */
    Column(final Class<?> entityType, final Field field)
    {
        columnField = field;
        columnConverter = ColumnConverterFactory.getColumnConverter(field.getType());
        columnName = ColumnUtils.getColumnNameByField(field);
        if (columnConverter != null)
        {
            defaultValue = columnConverter.getFieldValue(ColumnUtils.getColumnDefaultValue(field));
        }
        else
        {
            defaultValue = null;
        }
        getMethod = ColumnUtils.getColumnGetMethod(entityType, field);
        setMethod = ColumnUtils.getColumnSetMethod(entityType, field);
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param entity entity
     * @param cursor cursor
     * @param indx index
     * @see [类、类#方法、类#成员]
     */
    @SuppressWarnings("unchecked")
    public void setValue2Entity(final Object entity, final Cursor cursor, final int indx)
    {
        index = indx;
        final Object value = columnConverter.getFieldValue(cursor, indx);
        if ((null == value) && (null == defaultValue))
        {
            return;
        }
        
        if (null != setMethod)
        {
            try
            {
                setMethod.invoke(entity, value == null ? defaultValue : value);
            }
            catch (final IllegalArgumentException e)
            {
                LogUtil.error("IllegalArgumentException");
            }
            catch (final IllegalAccessException e)
            {
                LogUtil.error("IllegalAccessException");
            }
            catch (final InvocationTargetException e)
            {
                LogUtil.error("InvocationTargetException");
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
                
                columnField.set(entity, value == null ? defaultValue : value);
            }
            catch (final IllegalArgumentException e)
            {
                LogUtil.error("IllegalArgumentException");
            }
            catch (final IllegalAccessException e)
            {
                LogUtil.error("IllegalAccessException");
            }
        }
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param entity entity
     * @return getColumnValue
     * @see [类、类#方法、类#成员]
     */
    @SuppressWarnings("unchecked")
    public Object getColumnValue(final Object entity)
    {
        final Object fieldValue = getFieldValue(entity);
        return columnConverter.fieldValue2ColumnValue(fieldValue);
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param entity entity
     * @return entity
     * @see [类、类#方法、类#成员]
     */
    @SuppressWarnings("unchecked")
    public Object getFieldValue(final Object entity)
    {
        Object fieldValue = null;
        if (null != entity)
        {
            if (null != getMethod)
            {
                try
                {
                    fieldValue = getMethod.invoke(entity);
                }
                catch (final IllegalArgumentException e)
                {
                    LogUtil.error("getFieldValue");
                }
                catch (final IllegalAccessException e)
                {
                    LogUtil.error("getFieldValue");
                }
                catch (final InvocationTargetException e)
                {
                    LogUtil.error("getFieldValue");
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
                    fieldValue = columnField.get(entity);
                }
                catch (final IllegalArgumentException e)
                {
                    LogUtil.error("getFieldValue");
                }
                catch (final IllegalAccessException e)
                {
                    LogUtil.error("getFieldValue");
                }
            }
        }
        return fieldValue;
    }
    
    public Table getTable()
    {
        return table;
    }
    
    /**
     *  package
     */
    void setTable(final Table table)
    {
        this.table = table;
    }
    
    /**
     * The value set in setValue2Entity(...)
     *
     * @return -1 or the index of this column.
     */
    public int getIndex()
    {
        return index;
    }
    
    public String getColumnName()
    {
        return columnName;
    }
    
    public Object getDefaultValue()
    {
        return defaultValue;
    }
    
    public Field getColumnField()
    {
        return columnField;
    }
    
    public ColumnConverter getColumnConverter()
    {
        return columnConverter;
    }
    
    public ColumnDbType getColumnDbType()
    {
        return columnConverter.getColumnDbType();
    }
}
