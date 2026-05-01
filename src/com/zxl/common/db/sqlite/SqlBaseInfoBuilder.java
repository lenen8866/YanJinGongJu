package com.zxl.common.db.sqlite;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * SqlBaseInfoBuilder
 * @author g00218858
 *
 */
public abstract class SqlBaseInfoBuilder
{
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param db db
     * @param entity entity
     * @return buildInsertSqlInfo
     * @throws DbException DbException
     * @see [类、类#方法、类#成员]
     */
    public static SqlInfo buildInsertSqlInfo(final DbUtils db, final Object entity)
        throws DbException
    {
        
        final List<KeyValue> keyValueList = entity2KeyValueList(db, entity);
        if (keyValueList.isEmpty())
        {
            return null;
        }
        
        final SqlInfo result = new SqlInfo();
        final StringBuffer sqlBuffer = new StringBuffer();
        
        sqlBuffer.append("INSERT INTO ");
        sqlBuffer.append(TableUtils.getTableName(entity.getClass()));
        sqlBuffer.append(" (");
        for (final KeyValue kv : keyValueList)
        {
            sqlBuffer.append(kv.getKey()).append(',');
            result.addBindArgWithoutConverter(kv.getValue());
        }
        sqlBuffer.deleteCharAt(sqlBuffer.length() - 1);
        sqlBuffer.append(") VALUES (");
        
        final int length = keyValueList.size();
        for (int i = 0; i < length; i++)
        {
            sqlBuffer.append("?,");
        }
        sqlBuffer.deleteCharAt(sqlBuffer.length() - 1);
        sqlBuffer.append(')');
        
        result.setSql(sqlBuffer.toString());
        
        return result;
    }
    
    // *********************************************** replace sql ***********************************************
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param db db
     * @param entity entity
     * @return buildReplaceSqlInfo
     * @throws DbException DbException
     * @see [类、类#方法、类#成员]
     */
    public static SqlInfo buildReplaceSqlInfo(final DbUtils db, final Object entity)
        throws DbException
    {
        
        final List<KeyValue> keyValueList = entity2KeyValueList(db, entity);
        if (keyValueList.isEmpty())
        {
            return null;
        }
        
        final SqlInfo result = new SqlInfo();
        final StringBuffer sbuffer = new StringBuffer();
        
        sbuffer.append("REPLACE INTO ");
        sbuffer.append(TableUtils.getTableName(entity.getClass()));
        sbuffer.append(" (");
        for (final KeyValue kv : keyValueList)
        {
            sbuffer.append(kv.getKey()).append(',');
            result.addBindArgWithoutConverter(kv.getValue());
        }
        sbuffer.deleteCharAt(sbuffer.length() - 1);
        sbuffer.append(") VALUES (");
        
        final int length = keyValueList.size();
        for (int i = 0; i < length; i++)
        {
            sbuffer.append("?,");
        }
        sbuffer.deleteCharAt(sbuffer.length() - 1);
        sbuffer.append(')');
        
        result.setSql(sbuffer.toString());
        
        return result;
    }
    
    // *********************************************** delete sql ***********************************************
    
    private static String buildDeleteSqlByTableName(final String tableName)
    {
        return "DELETE FROM " + tableName;
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param db db
     * @param entity entity
     * @return buildDeleteSqlInfo
     * @throws DbException DbException
     * @see [类、类#方法、类#成员]
     */
    public static SqlInfo buildDeleteSqlInfo(final DbUtils db, final Object entity)
        throws DbException
    {
        final SqlInfo result = new SqlInfo();
        
        final Class<?> entityType = entity.getClass();
        final Table table = Table.get(db, entityType);
        final Id id = table.getId();
        final Object idValue = id.getColumnValue(entity);
        
        if (idValue == null)
        {
            throw new DbException("this entity[" + entity.getClass() + "]'s id value is null");
        }
        final StringBuilder sb = new StringBuilder(buildDeleteSqlByTableName(table.getTableName()));
        sb.append(" WHERE ").append(WhereBuilder.getInstance(id.getColumnName(), "=", idValue));
        
        result.setSql(sb.toString());
        
        return result;
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param db db
     * @param entityType entityType
     * @param idValue id Value
     * @return buildDeleteSqlInfo
     * @throws DbException DbException
     * @see [类、类#方法、类#成员]
     */
    public static SqlInfo buildDeleteSqlInfo(final DbUtils db, final Class<?> entityType, final Object idValue)
        throws DbException
    {
        final SqlInfo result = new SqlInfo();
        
        final Table table = Table.get(db, entityType);
        final Id id = table.getId();
        
        if (null == idValue)
        {
            throw new DbException("this entity[" + entityType + "]'s id value is null");
        }
        final StringBuilder sb = new StringBuilder(buildDeleteSqlByTableName(table.getTableName()));
        sb.append(" WHERE ").append(WhereBuilder.getInstance(id.getColumnName(), "=", idValue));
        
        result.setSql(sb.toString());
        
        return result;
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param db db
     * @param entityType entityType
     * @param whereBuilder whereBuilder
     * @return buildDeleteSqlInfo
     * @throws DbException DbException
     * @see [类、类#方法、类#成员]
     */
    public static SqlInfo buildDeleteSqlInfo(final DbUtils db, final Class<?> entityType,
        final WhereBuilder whereBuilder)
            throws DbException
    {
        final Table table = Table.get(db, entityType);
        final StringBuilder sb = new StringBuilder(buildDeleteSqlByTableName(table.getTableName()));
        
        if ((null != whereBuilder) && (whereBuilder.getWhereItemSize() > 0))
        {
            sb.append(" WHERE ").append(whereBuilder.toString());
        }
        
        return new SqlInfo(sb.toString());
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param db db
     * @param entity entity
     * @return entity2KeyValueList
     * @see [类、类#方法、类#成员]
     */
    public static List<KeyValue> entity2KeyValueList(final DbUtils db, final Object entity)
    {
        
        final List<KeyValue> keyValueList = new ArrayList<KeyValue>();
        
        final Class<?> entityType = entity.getClass();
        final Table table = Table.get(db, entityType);
        final Id id = table.getId();
        
        if (!id.isAutoIncrement())
        {
            final Object idValue = id.getColumnValue(entity);
            final KeyValue kv = new KeyValue(id.getColumnName(), idValue);
            keyValueList.add(kv);
        }
        
        final Collection<Column> columns = table.getColumnMap().values();
        for (final Column column : columns)
        {
            if (column instanceof Finder)
            {
                continue;
            }
            final KeyValue kv = column2KeyValue(entity, column);
            if (kv != null)
            {
                keyValueList.add(kv);
            }
        }
        
        return keyValueList;
    }
    
    private static KeyValue column2KeyValue(final Object entity, final Column column)
    {
        KeyValue kv = null;
        final String key = column.getColumnName();
        if (key != null)
        {
            Object value = column.getColumnValue(entity);
            value = value == null ? column.getDefaultValue() : value;
            kv = new KeyValue(key, value);
        }
        return kv;
    }
    
}
