/*
 * Copyright (c) 2013. wyouflf (wyouflf@gmail.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zxl.common.db.sqlite;

import android.text.TextUtils;

import com.zxl.common.db.annotation.Id;
import com.zxl.common.db.annotation.Table;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * <一句话功能简述>
 * <功能详细描述>
 * 
 * @author
 * @version [版本号, 2015-2-28]
 * @since [产品/模块版本]
 */
public final class TableUtils
{
    /**
     * key: entityType.name
     */
    private static ConcurrentHashMap<String, HashMap<String, Column>> entityColumnsMap =
        new ConcurrentHashMap<String, HashMap<String, Column>>();
        
    /**
     * key: entityType.name
     */
    private static ConcurrentHashMap<String, com.zxl.common.db.sqlite.Id> entityIdMap =
        new ConcurrentHashMap<String, com.zxl.common.db.sqlite.Id>();
        
    private TableUtils()
    {
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * 
     * @param entityType entityType
     * @return getTableName
     * @see [类、类#方法、类#成员]
     */
    public static String getTableName(final Class<?> entityType)
    {
        final Table table = entityType.getAnnotation(Table.class);
        if ((null == table) || TextUtils.isEmpty(table.name()))
        {
            return entityType.getName().replace('.', '_');
        }
        return table.name();
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * 
     * @param entityType entityType
     * @return getTableName
     * @see [类、类#方法、类#成员]
     */
    public static String getExecAfterTableCreated(final Class<?> entityType)
    {
        final Table table = entityType.getAnnotation(Table.class);
        if (null != table)
        {
            return table.execAfterTableCreated();
        }
        return null;
    }
    
    /* package */
    static synchronized Map<String, Column> getColumnMap(final Class<?> entityType)
    {
        
        if (entityColumnsMap.containsKey(entityType.getName()))
        {
            return entityColumnsMap.get(entityType.getName());
        }
        
        final HashMap<String, Column> columnMap = new HashMap<String, Column>();
        final String primaryKeyFieldName = getPrimaryKeyFieldName(entityType);
        addColumns2Map(entityType, primaryKeyFieldName, columnMap);
        entityColumnsMap.put(entityType.getName(), columnMap);
        
        return columnMap;
    }
    
    private static void addColumns2Map(final Class<?> entityType, final String primaryKeyFieldName,
        final Map<String, Column> columnMap)
    {
        if (Object.class == entityType)
        {
            return;
        }
        final Field[] fields = entityType.getDeclaredFields();
        for (final Field field : fields)
        {
            if (ColumnUtils.isTransient(field) || Modifier.isStatic(field.getModifiers()))
            {
                continue;
            }
            doExtract(entityType, primaryKeyFieldName, columnMap, field);
        }
        
        if (Object.class != entityType.getSuperclass())
        {
            addColumns2Map(entityType.getSuperclass(), primaryKeyFieldName, columnMap);
        }
    }
    
    private static void doExtract(final Class<?> entityType, final String primaryKeyFieldName,
        final Map<String, Column> columnMap, final Field field)
    {
        if (ColumnConverterFactory.isSupportColumnConverter(field.getType()))
        {
            if (!field.getName().equals(primaryKeyFieldName))
            {
                final Column column = new Column(entityType, field);
                if (!columnMap.containsKey(column.getColumnName()))
                {
                    columnMap.put(column.getColumnName(), column);
                }
            }
        }
        else if (ColumnUtils.isForeign(field))
        {
            final Foreign column = new Foreign(entityType, field);
            if (!columnMap.containsKey(column.getColumnName()))
            {
                columnMap.put(column.getColumnName(), column);
            }
        }
        else if (ColumnUtils.isFinder(field))
        {
            final Finder column = new Finder(entityType, field);
            if (!columnMap.containsKey(column.getColumnName()))
            {
                columnMap.put(column.getColumnName(), column);
            }
        }
    }
    
    /* package */
    static Column getColumnOrId(final Class<?> entityType, final String columnName)
    {
        final String colName = getPrimaryKeyColumnName(entityType);
        if ((colName != null) && colName.equals(columnName))
        {
            return getId(entityType);
        }
        return getColumnMap(entityType).get(columnName);
    }
    
    /* package */
    static synchronized com.zxl.common.db.sqlite.Id getId(final Class<?> entityType)
    {
        if (Object.class == entityType)
        {
            throw new IllegalArgumentException("field 'id' not found");
        }
        
        if (entityIdMap.containsKey(entityType.getName()))
        {
            return entityIdMap.get(entityType.getName());
        }
        
        Field primaryKeyField = null;
        final Field[] fields = entityType.getDeclaredFields();
        if (null != fields)
        {
            
            for (final Field field : fields)
            {
                if (null != field.getAnnotation(Id.class))
                {
                    primaryKeyField = field;
                    break;
                }
            }
            
            if (null == primaryKeyField)
            {
                for (final Field field : fields)
                {
                    if ("id".equals(field.getName()) || "_id".equals(field.getName()))
                    {
                        primaryKeyField = field;
                        break;
                    }
                }
            }
        }
        
        if (null == primaryKeyField)
        {
            return getId(entityType.getSuperclass());
        }
        
        final com.zxl.common.db.sqlite.Id id =
            new com.zxl.common.db.sqlite.Id(entityType, primaryKeyField);
        entityIdMap.put(entityType.getName(), id);
        return id;
    }
    
    private static String getPrimaryKeyFieldName(final Class<?> entityType)
    {
        final com.zxl.common.db.sqlite.Id id = getId(entityType);
        return id == null ? null : id.getColumnField().getName();
    }
    
    private static String getPrimaryKeyColumnName(final Class<?> entityType)
    {
        final com.zxl.common.db.sqlite.Id id = getId(entityType);
        return id == null ? null : id.getColumnName();
    }
}
