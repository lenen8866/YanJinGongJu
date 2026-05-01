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

import java.util.List;

/**
 * 
 * <一句话功能简述>
 * <功能详细描述>
 * 
 * @author   
 * @version  [版本号, 2015-2-28]
 * @since  [产品/模块版本]
 * @param <T>  T
 * 
 */
public class ForeignLazyLoader<T>
{
    private final Foreign foreignColumn;
    
    private Object columnValue;
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param foreignColumn foreignColumn
     * @param value value
     * @see [类、类#方法、类#成员]
     */
    public ForeignLazyLoader(final Foreign foreignColumn, final Object value)
    {
        this.foreignColumn = foreignColumn;
        this.columnValue = ColumnUtils.convert2DbColumnValueIfNeeded(value);
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @return getAllFromDb
     * @throws DbException DbException
     * @see [类、类#方法、类#成员]
     */
    public List<T> getAllFromDb()
        throws DbException
    {
        List<T> entities = null;
        final Table table = foreignColumn.getTable();
        if (null != table)
        {
            entities = table.getDb().findAll(Selector.from(foreignColumn.getForeignEntityType())
                .where(foreignColumn.getForeignColumnName(), "=", columnValue));
        }
        return entities;
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @return getFirstFromDb
     * @throws DbException DbException
     * @see [类、类#方法、类#成员]
     */
    public T getFirstFromDb()
        throws DbException
    {
        T entity = null;
        final Table table = foreignColumn.getTable();
        if (null != table)
        {
            entity = table.getDb().findFirst(Selector.from(foreignColumn.getForeignEntityType())
                .where(foreignColumn.getForeignColumnName(), "=", columnValue));
        }
        return entity;
    }
    
    public void setColumnValue(final Object value)
    {
        this.columnValue = ColumnUtils.convert2DbColumnValueIfNeeded(value);
    }
    
    public Object getColumnValue()
    {
        return columnValue;
    }
}
