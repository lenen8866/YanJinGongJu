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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Build "insert", "replace",，"update", "delete" and "create" sql.
 */
public final class SqlInfoBuilder extends SqlBaseInfoBuilder
{
    
    private SqlInfoBuilder()
    {
    }
    
    // *********************************************** insert sql ***********************************************
    
    // *********************************************** update sql ***********************************************
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param db db
     * @param entity entity
     * @param  updateColumnNames updateColumnNames
     * @return buildUpdateSqlInfo
     * @throws DbException DbException
     * @see [类、类#方法、类#成员]
     */
    public static SqlInfo buildUpdateSqlInfo(final DbUtils db, final Object entity, final String... updateColumnNames)
        throws DbException
    {
        
        final List<KeyValue> keyValueList = entity2KeyValueList(db, entity);
        if (keyValueList.isEmpty())
        {
            return null;
        }
        
        HashSet<String> updateColumnNameSet = null;
        if (null != updateColumnNames && updateColumnNames.length > 0)
        {
            updateColumnNameSet = new HashSet<String>(updateColumnNames.length);
            Collections.addAll(updateColumnNameSet, updateColumnNames);
        }
        
        final Class<?> entityType = entity.getClass();
        final Table table = Table.get(db, entityType);
        final Id id = table.getId();
        final Object idValue = id.getColumnValue(entity);
        
        if (null == idValue)
        {
            throw new DbException("this entity[" + entity.getClass() + "]'s id value is null");
        }
        
        final SqlInfo result = new SqlInfo();
        final StringBuffer sqlBuffer = new StringBuffer("UPDATE ");
        sqlBuffer.append(table.getTableName());
        sqlBuffer.append(" SET ");
        buildResult(keyValueList, updateColumnNameSet, sqlBuffer, result);
        
        sqlBuffer.deleteCharAt(sqlBuffer.length() - 1);
        sqlBuffer.append(" WHERE ").append(WhereBuilder.getInstance(id.getColumnName(), "=", idValue));
        
        result.setSql(sqlBuffer.toString());
        return result;
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param db db
     * @param entity entity
     * @param whereBuilder  whereBuilder
     * @param updateColumnNames updateColumnNames
     * @return buildUpdateSqlInfo
     * @throws DbException DbException
     * @see [类、类#方法、类#成员]
     */
    public static SqlInfo buildUpdateSqlInfo(final DbUtils db, final Object entity, final WhereBuilder whereBuilder,
        final String... updateColumnNames)
            throws DbException
    {
        
        final List<KeyValue> keyValueList = entity2KeyValueList(db, entity);
        if (keyValueList.isEmpty())
        {
            return null;
        }
        
        HashSet<String> updateColumnNameSet = null;
        if (null != updateColumnNames && updateColumnNames.length > 0)
        {
            updateColumnNameSet = new HashSet<String>(updateColumnNames.length);
            Collections.addAll(updateColumnNameSet, updateColumnNames);
        }
        
        final Class<?> entityType = entity.getClass();
        final String tableName = TableUtils.getTableName(entityType);
        
        final SqlInfo result = new SqlInfo();
        final StringBuffer sqlBuffer = new StringBuffer("UPDATE ");
        sqlBuffer.append(tableName);
        sqlBuffer.append(" SET ");
        buildResult(keyValueList, updateColumnNameSet, sqlBuffer, result);
        
        sqlBuffer.deleteCharAt(sqlBuffer.length() - 1);
        if (whereBuilder != null && whereBuilder.getWhereItemSize() > 0)
        {
            sqlBuffer.append(" WHERE ").append(whereBuilder.toString());
        }
        
        result.setSql(sqlBuffer.toString());
        return result;
    }
    
    private static void buildResult(final List<KeyValue> keyValueList, final Set<String> updateColumnNameSet,
        final StringBuffer sqlBuffer, final SqlInfo result)
    {
        for (final KeyValue kv : keyValueList)
        {
            if (updateColumnNameSet == null || updateColumnNameSet.contains(kv.getKey()))
            {
                sqlBuffer.append(kv.getKey()).append("=?,");
                result.addBindArgWithoutConverter(kv.getValue());
            }
        }
    }
    
    // *********************************************** others ***********************************************
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param db db
     * @param entityType entityType
     * @return buildCreateTableSqlInfo
     * @throws DbException DbException
     * @see [类、类#方法、类#成员]
     */
    public static SqlInfo buildCreateTableSqlInfo(final DbUtils db, final Class<?> entityType)
        throws DbException
    {
        final Table table = Table.get(db, entityType);
        final Id id = table.getId();
        
        final StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("CREATE TABLE IF NOT EXISTS ");
        sqlBuffer.append(table.getTableName());
        sqlBuffer.append(" ( ");
        
        if (id.isAutoIncrement())
        {
            sqlBuffer.append('\"')
                .append(id.getColumnName())
                .append("\"  ")
                .append("INTEGER PRIMARY KEY AUTOINCREMENT,");
        }
        else
        {
            sqlBuffer.append('\"')
                .append(id.getColumnName())
                .append("\"  ")
                .append(id.getColumnDbType())
                .append(" PRIMARY KEY,");
        }
        
        final Collection<Column> columns = table.getColumnMap().values();
        for (final Column column : columns)
        {
            if (column instanceof Finder)
            {
                continue;
            }
            sqlBuffer.append('\"').append(column.getColumnName()).append("\"  ");
            sqlBuffer.append(column.getColumnDbType());
            if (ColumnUtils.isUnique(column.getColumnField()))
            {
                sqlBuffer.append(" UNIQUE");
            }
            if (ColumnUtils.isNotNull(column.getColumnField()))
            {
                sqlBuffer.append(" NOT NULL");
            }
            final String check = ColumnUtils.getCheck(column.getColumnField());
            if (check != null)
            {
                sqlBuffer.append(" CHECK(").append(check).append(')');
            }
            sqlBuffer.append(',');
        }
        
        sqlBuffer.deleteCharAt(sqlBuffer.length() - 1);
        sqlBuffer.append(" )");
        return new SqlInfo(sqlBuffer.toString());
    }
    
}
