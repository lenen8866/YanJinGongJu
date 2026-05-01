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

import android.text.TextUtils;

/**
 * Author: wyouflf
 * Date: 13-8-10
 * Time: 下午2:15
 */
public class DbModelSelector
{
    
    private String[] columnExpressions;
    
    private String groupByColumnName;
    
    private WhereBuilder whereBuilder;
    
    private final Selector selector;
    
    private DbModelSelector(final Class<?> entityType)
    {
        selector = Selector.from(entityType);
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param selector selector
     * @param groupByColumnName groupByColumnName
     * @see [类、类#方法、类#成员]
     */
    protected DbModelSelector(final Selector selector, final String groupByColumnName)
    {
        this.selector = selector;
        this.groupByColumnName = groupByColumnName;
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param selector selector
     * @param columnExpressions columnExpressions
     * @see [类、类#方法、类#成员]
     */
    protected DbModelSelector(final Selector selector, final String[] columnExpressions)
    {
        this.selector = selector;
        this.columnExpressions = columnExpressions;
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param entityType entityType
     * @return from
     * @see [类、类#方法、类#成员]
     */
    public static DbModelSelector from(final Class<?> entityType)
    {
        if (null == entityType)
        {
            return null;
            
        }
        return new DbModelSelector(entityType);
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param whereBuild whereBuilder
     * @return where
     * @see [类、类#方法、类#成员]
     */
    public DbModelSelector where(final WhereBuilder whereBuild)
    {
        if (null == whereBuild)
        {
            return null;
        }
        selector.where(whereBuild);
        return this;
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param columnName columnName
     * @param op op
     * @param value value
     * @return where
     * @see [类、类#方法、类#成员]
     */
    public DbModelSelector where(final String columnName, final String op, final Object value)
    {
        if ((null == columnName) || (null == op) || (null == value))
        {
            return null;
        }
        selector.where(columnName, op, value);
        return this;
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param columnName columnName
     * @param op op
     * @param value value
     * @return and
     * @see [类、类#方法、类#成员]
     */
    public DbModelSelector and(final String columnName, final String op, final Object value)
    {
        if ((null == columnName) || (null == op) || (null == value))
        {
            return null;
        }
        selector.and(columnName, op, value);
        return this;
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param where where
     * @return and
     * @see [类、类#方法、类#成员]
     */
    public DbModelSelector and(final WhereBuilder where)
    {
        if (null == where)
        {
            return null;
        }
        selector.and(where);
        return this;
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param columnName columnName
     * @param op op
     * @param value value
     * @return or
     * @see [类、类#方法、类#成员]
     */
    public DbModelSelector orSelector(final String columnName, final String op, final Object value)
    {
        if ((null == columnName) || (null == op) || (null == value))
        {
            return null;
        }
        selector.orSelector(columnName, op, value);
        return this;
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param where where
     * @return or
     * @see [类、类#方法、类#成员]
     */
    public DbModelSelector orSelector(final WhereBuilder where)
    {
        if (null == where)
        {
            return null;
        }
        selector.orSelector(where);
        return this;
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param expr expr
     * @return expr
     * @see [类、类#方法、类#成员]
     */
    public DbModelSelector expr(final String expr)
    {
        selector.expr(expr);
        return this;
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param columnName columnName
     * @param op op
     * @param value value 
     * @return expr
     * @see [类、类#方法、类#成员]
     */
    public DbModelSelector expr(final String columnName, final String op, final Object value)
    {
        if ((null == columnName) || (null == op) || (null == value))
        {
            return null;
        }
        selector.expr(columnName, op, value);
        return this;
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param columnName columnName
     * @return groupBy
     * @see [类、类#方法、类#成员]
     */
    public DbModelSelector groupBy(final String columnName)
    {
        this.groupByColumnName = columnName;
        return this;
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param whereBuild whereBuilder
     * @return having
     * @see [类、类#方法、类#成员]
     */
    public DbModelSelector having(final WhereBuilder whereBuild)
    {
        this.whereBuilder = whereBuild;
        return this;
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param columnExpress columnExpressions
     * @return select
     * @see [类、类#方法、类#成员]
     */
    public DbModelSelector select(final String... columnExpress)
    {
        this.columnExpressions = columnExpress;
        return this;
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param columnName columnName
     * @return orderBy
     * @see [类、类#方法、类#成员 ]
     */
    public DbModelSelector orderBy(final String columnName)
    {
        selector.orderBy(columnName);
        return this;
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param columnName columnName
     * @param desc desc
     * @return orderBy
     * @see [类、类#方法、类#成员]
     */
    public DbModelSelector orderBy(final String columnName, final boolean desc)
    {
        selector.orderBy(columnName, desc);
        return this;
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param limit limit
     * @return limit
     * @see [类、类#方法、类#成员]
     */
    public DbModelSelector limit(final int limit)
    {
        selector.limit(limit);
        return this;
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param offset offset
     * @return offset
     * @see [类、类#方法、类#成员]
     */
    public DbModelSelector offset(final int offset)
    {
        selector.offset(offset);
        return this;
    }
    
    public Class<?> getEntityType()
    {
        return selector.getEntityType();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        final StringBuffer result = new StringBuffer();
        result.append("SELECT ");
        doExtract(result);
        result.append(" FROM ").append(selector.tableName);
        if ((selector.whereBuilder != null) && (0 < selector.whereBuilder.getWhereItemSize()))
        {
            result.append(" WHERE ").append(selector.whereBuilder.toString());
        }
        if (!TextUtils.isEmpty(groupByColumnName))
        {
            result.append(" GROUP BY ").append(groupByColumnName);
            if ((whereBuilder != null) && (whereBuilder.getWhereItemSize() > 0))
            {
                result.append(" HAVING ").append(whereBuilder.toString());
            }
        }
        if (selector.orderByList != null)
        {
            for (int i = 0; i < selector.orderByList.size(); i++)
            {
                result.append(" ORDER BY ").append(selector.orderByList.get(i).toString());
            }
        }
        if (0 < selector.mLimit)
        {
            result.append(" LIMIT ").append(selector.mLimit);
            result.append(" OFFSET ").append(selector.mOffset);
        }
        return result.toString();
    }
    
    private void doExtract(final StringBuffer result)
    {
        if ((columnExpressions != null) && (columnExpressions.length > 0))
        {
            for (final String columnExpression : columnExpressions)
            {
                result.append(columnExpression);
                result.append(',');
            }
            result.deleteCharAt(result.length() - 1);
        }
        else
        {
            if (!TextUtils.isEmpty(groupByColumnName))
            {
                result.append(groupByColumnName);
            }
            else
            {
                result.append('*');
            }
        }
    }
}
