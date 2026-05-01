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

import java.util.ArrayList;
import java.util.List;

/**
 * Author: wyouflf
 * Date: 13-8-9
 * Time: 下午10:19
 */
public class Selector
{

    protected Class<?> entityType;

    protected String tableName;

    protected WhereBuilder whereBuilder;

    protected List<OrderBy> orderByList;

    protected int mLimit = 0;

    protected int mOffset = 0;

    private Selector(final Class<?> entityType)
    {
        this.entityType = entityType;
        this.tableName = TableUtils.getTableName(entityType);
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param entityType entityType
     * @return from
     * @see [类、类#方法、类#成员]
     */
    public static Selector from(final Class<?> entityType)
    {
        return new Selector(entityType);
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param whereBuild whereBuilder
     * @return where
     * @see [类、类#方法、类#成员]
     */
    public Selector where(final WhereBuilder whereBuild)
    {
        this.whereBuilder = whereBuild;
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
    public Selector where(final String columnName, final String op, final Object value)
    {
        this.whereBuilder = WhereBuilder.getInstance(columnName, op, value);
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
    public Selector and(final String columnName, final String op, final Object value)
    {
        this.whereBuilder.and(columnName, op, value);
        return this;
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param where where
     * @return and
     * @see [类、类#方法、类#成员]
     */
    public Selector and(final WhereBuilder where)
    {
        this.whereBuilder.expr("AND (" + where.toString() + ")");
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
    public Selector orSelector(final String columnName, final String op, final Object value)
    {
        this.whereBuilder.orCondition(columnName, op, value);
        return this;
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param where where
     * @return or
     * @see [类、类#方法、类#成员]
     */
    public Selector orSelector(final WhereBuilder where)
    {
        this.whereBuilder.expr("OR (" + where.toString() + ")");
        return this;
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param expr expr
     * @return expr
     * @see [类、类#方法、类#成员]
     */
    public Selector expr(final String expr)
    {
        if (this.whereBuilder == null)
        {
            this.whereBuilder = WhereBuilder.getInstance();
        }
        this.whereBuilder.expr(expr);
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
    public Selector expr(final String columnName, final String op, final Object value)
    {
        if (null == this.whereBuilder)
        {
            this.whereBuilder = WhereBuilder.getInstance();
        }
        this.whereBuilder.expr(columnName, op, value);
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
        return new DbModelSelector(this, columnName);
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param columnExpressions columnExpressions
     * @return select
     * @see [类、类#方法、类#成员]
     */
    public DbModelSelector select(final String... columnExpressions)
    {
        return new DbModelSelector(this, columnExpressions);
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param columnName columnName
     * @return orderBy
     * @see [类、类#方法、类#成员]
     */
    public Selector orderBy(final String columnName)
    {
        if (null == orderByList)
        {
            orderByList = new ArrayList<OrderBy>(2);
        }
        orderByList.add(new OrderBy(columnName));
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
    public Selector orderBy(final String columnName, final boolean desc)
    {
        if (null == orderByList)
        {
            orderByList = new ArrayList<OrderBy>(2);
        }
        orderByList.add(new OrderBy(columnName, desc));
        return this;
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param lim limit
     * @return limit
     * @see [类、类#方法、类#成员]
     */
    public Selector limit(final int lim)
    {
        this.mLimit = lim;
        return this;
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param offst offset
     * @return offset
     * @see [类、类#方法、类#成员]
     */
    public Selector offset(final int offst)
    {
        this.mOffset = offst;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        final StringBuilder result = new StringBuilder();
        result.append("SELECT ");
        result.append('*');
        result.append(" FROM ").append(tableName);
        if ((null != whereBuilder) && (whereBuilder.getWhereItemSize() > 0))
        {
            result.append(" WHERE ").append(whereBuilder.toString());
        }
        if (null != orderByList && orderByList.size()>0)
        {
            result.append(" ORDER BY ");
            final int k = orderByList.size();
            for (int i = 0; i < k; i++) {
                result.append(" ").append(orderByList.get(i).toString()).append(",");
            }
            result.deleteCharAt(result.length() - 1);
        }
        if (0 < mLimit)
        {
            result.append(" LIMIT ").append(mLimit);
            result.append(" OFFSET ").append(mOffset);
        }
        return result.toString();
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @return entityType
     * @see [类、类#方法、类#成员]
     */
    public Class<?> getEntityType()
    {
        return entityType;
    }

    /**
     *
     * <一句话功能简述>
     * <功能详细描述>
     *
     * @author
     * @version  [版本号, 2015-2-28]
     * @since  [产品/模块版本]
     */
    protected class OrderBy
    {
        private final String columnName;

        private boolean desc;

        /**
         * <一句话功能简述>
         * <功能详细描述>
         * @param columnName columnName
         * @see [类、类#方法、类#成员]
         */
        public OrderBy(final String columnName)
        {
            this.columnName = columnName;
        }

        /**
         * <一句话功能简述>
         * <功能详细描述>
         * @param columnName columnName
         * @param desc desc
         * @see [类、类#方法、类#成员]
         */
        public OrderBy(final String columnName, final boolean desc)
        {
            this.columnName = columnName;
            this.desc = desc;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString()
        {
            return columnName + (desc ? " DESC" : " ASC");
        }
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param columnName columnName
     * @return entityType
     * @see [类、类#方法、类#成员]
     */
    public Class<?> getEntityTypeByName(final String columnName)
    {
        if ((null != columnName) && "EntityType".equals(columnName))
        {
            return entityType;
        }
        return Object.class;
    }
}
