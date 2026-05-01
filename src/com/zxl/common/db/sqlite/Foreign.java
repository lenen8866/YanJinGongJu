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
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;

/**
 *
 * <一句话功能简述>
 * <功能详细描述>
 *
 * @author  c00207931
 * @version  [版本号, 2015-5-25]
 * @since  [产品/模块版本]
 */
public class Foreign extends Column
{
    
    private final String foreignColumnName;
    
    private final ColumnConverter foreignColumnConverter;
    
    /**
     * <默认构造函数>
     */
    Foreign(final Class<?> entityType, final Field field)
    {
        super(entityType, field);
        
        foreignColumnName = ColumnUtils.getForeignColumnNameByField(field);
        final Class<?> foreignColumnType =
            TableUtils.getColumnOrId(getForeignEntityType(), foreignColumnName).columnField.getType();
        foreignColumnConverter = ColumnConverterFactory.getColumnConverter(foreignColumnType);
    }
    
    public String getForeignColumnName()
    {
        return foreignColumnName;
    }
    
    public Class<?> getForeignEntityType()
    {
        return ColumnUtils.getForeignEntityType(this);
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void setValue2Entity(final Object entity, final Cursor cursor, final int index)
    {
        final Object fieldValue = foreignColumnConverter.getFieldValue(cursor, index);
        if (null == fieldValue)
        {
            return;
        }
        
        Object value = null;
        final Class<?> columnType = columnField.getType();
        value = doExtract(fieldValue, null, columnType);
        
        if (null != setMethod)
        {
            
            try
            {
                setMethod.invoke(entity, value);
            }
            catch (final IllegalArgumentException e)
            {
                LogUtil.error("Foreign setValue2Entity IllegalArgumentException");
            }
            catch (final IllegalAccessException e)
            {
                LogUtil.error("Foreign setValue2Entity IllegalAccessException");
            }
            catch (final InvocationTargetException e)
            {
                LogUtil.error("Foreign setValue2Entity InvocationTargetException");
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
            catch (final IllegalArgumentException e)
            {
                LogUtil.error("Foreign setValue2Entity IllegalArgumentException");
            }
            catch (final IllegalAccessException e)
            {
                LogUtil.error("Foreign setValue2Entity IllegalAccessException");
            }
            
        }
    }
    
    private Object doExtract(final Object fieldValue, Object value, final Class<?> columnType)
    {
        if (columnType == ForeignLazyLoader.class)
        {
            value = new ForeignLazyLoader(this, fieldValue);
        }
        else if (columnType == List.class)
        {
            try
            {
                value = new ForeignLazyLoader(this, fieldValue).getAllFromDb();
            }
            catch (final DbException e)
            {
                LogUtil.error("Foreign doExtract DbException");
            }
        }
        else
        {
            try
            {
                value = new ForeignLazyLoader(this, fieldValue).getFirstFromDb();
            }
            catch (final DbException e)
            {
                LogUtil.error("Foreign doExtract DbException");
            }
        }
        return value;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Object getColumnValue(final Object entity)
    {
        final Object fieldValue = getFieldValue(entity);
        Object columnValue = null;
        
        if (null != fieldValue)
        {
            final Class<?> columnType = columnField.getType();
            if (columnType == ForeignLazyLoader.class)
            {
                columnValue = ((ForeignLazyLoader)fieldValue).getColumnValue();
            }
            else if (columnType == List.class)
            {
                try
                {
                    final List<?> foreignEntities = (List<?>)fieldValue;
                    if (!foreignEntities.isEmpty())
                    {
                        
                        final Class<?> foreignEntityType = ColumnUtils.getForeignEntityType(this);
                        final Column column = TableUtils.getColumnOrId(foreignEntityType, foreignColumnName);
                        columnValue = column.getColumnValue(foreignEntities.get(0));
                        
                        setTable(column, foreignEntities);
                        
                        columnValue = column.getColumnValue(foreignEntities.get(0));
                    }
                }
                catch (final DbException e)
                {
                    LogUtil.error("Foreign getColumnValue DbException");
                }
            }
            else
            {
                try
                {
                    final Column column = TableUtils.getColumnOrId(columnType, foreignColumnName);
                    columnValue = column.getColumnValue(fieldValue);
                    
                    final Table table = getTable();
                    if ((null != table) && (null == columnValue) && (column instanceof Id))
                    {
                        table.getDb().saveOrUpdate(fieldValue);
                    }
                    
                    columnValue = column.getColumnValue(fieldValue);
                }
                catch (final DbException e)
                {
                    LogUtil.error("Foreign getColumnValue DbException");
                }
            }
        }
        
        return columnValue;
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param column column
     * @param foreignEntities foreignEntities
     * @throws DbException
     * @see [类、类#方法、类#成员]
     */
    private void setTable(final Column column, final List<?> foreignEntities)
        throws DbException
    {
        final Table table = getTable();
        if ((null != table) && (column instanceof Id))
        {
            for (final Object foreignObj : foreignEntities)
            {
                final Object idValue = column.getColumnValue(foreignObj);
                if (null == idValue)
                {
                    table.getDb().saveOrUpdate(foreignObj);
                }
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ColumnDbType getColumnDbType()
    {
        return foreignColumnConverter.getColumnDbType();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Object getDefaultValue()
    {
        return null;
    }
}
