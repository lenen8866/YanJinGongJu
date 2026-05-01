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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Author: wyouflf
 * Date: 13-7-29
 * Time: 上午9:35
 */
public final class WhereBuilder
{
    
    private final List<String> whereItems;
    
    private WhereBuilder()
    {
        this.whereItems = new ArrayList<String>();
    }
    
    /**
     * create new instance
     *
     * @return WhereBuilder
     */
    public static WhereBuilder getInstance()
    {
        return new WhereBuilder();
    }
    
    /**
     * create new instance
     * 
     * @param columnName columnName
     * @param op         operator: "=","<","LIKE","IN","BETWEEN"...
     * @param value value
     * @return VWhereBuilder
     */
    public static WhereBuilder getInstance(final String columnName, final String op, final Object value)
    {
        final WhereBuilder result = new WhereBuilder();
        result.appendCondition(null, columnName, op, value);
        return result;
    }
    
    /**
     * add AND condition
     *
     * @param columnName columnName
     * @param op         operator: "=","<","LIKE","IN","BETWEEN"...
     * @param value value
     * @return and
     */
    public WhereBuilder and(final String columnName, final String op, final Object value)
    {
        appendCondition(whereItems.isEmpty() ? null : "AND", columnName, op, value);
        return this;
    }
    
    /**
     * add OR condition
     *
     * @param columnName columnName
     * @param op         operator: "=","<","LIKE","IN","BETWEEN"...
     * @param value value
     * @return  OR condition
     */
    public WhereBuilder orCondition(final String columnName, final String op, final Object value)
    {
        appendCondition(whereItems.isEmpty() ? null : "OR", columnName, op, value);
        return this;
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param expr expr
     * @return expr
     * @see [类、类#方法、类#成员]
     */
    public WhereBuilder expr(final String expr)
    {
        whereItems.add(" " + expr);
        return this;
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param columnName columnName
     * @param op op
     * @param value value
     * @return expr
     * @see [类、类#方法、类#成 员]
     */
    public WhereBuilder expr(final String columnName, final String op, final Object value)
    {
        appendCondition(null, columnName, op, value);
        return this;
    }
    
    public int getWhereItemSize()
    {
        return whereItems.size();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        if (whereItems.isEmpty())
        {
            return "";
        }
        final StringBuilder sb = new StringBuilder();
        for (final String item : whereItems)
        {
            sb.append(item);
        }
        return sb.toString();
    }
    
    private void appendCondition(final String conj, final String columnName, String op, Object value)
    {
        final StringBuilder sqlSb = new StringBuilder();
        
        extract(conj, sqlSb);
        
        // append columnName
        sqlSb.append(columnName);
        
        // convert op
        if ("!=".equals(op))
        {
            op = "<>";
        }
        else if ("==".equals(op))
        {
            op = "=";
        }
        
        // append op & value
        if (value == null)
        {
            extract1(op, sqlSb);
        }
        else
        {
            sqlSb.append(' ' + op + ' ');
            
            if ("IN".equalsIgnoreCase(op))
            {
                Iterable<?> items = extract3(value);
                if (items != null)
                {
                    extract5(sqlSb, items);
                }
                else
                {
                    throw new IllegalArgumentException("value must be an Array or an Iterable.");
                }
            }
            else if ("BETWEEN".equalsIgnoreCase(op))
            {
                extract6(value, sqlSb);
            }
            else
            {
                value = ColumnUtils.convert2DbColumnValueIfNeeded(value);
                extract7(value, sqlSb);
            }
        }
        whereItems.add(sqlSb.toString());
    }
    
    private void extract7(Object value, final StringBuilder sqlSb)
    {
        if (ColumnDbType.TEXT.equals(ColumnConverterFactory.getDbColumnType(value.getClass())))
        {
            String valueStr = value.toString();
            if (valueStr.indexOf('\'') != -1)
            { // convert single quotations
                valueStr = valueStr.replace("'", "''");
            }
            sqlSb.append("'" + valueStr + "'");
        }
        else
        {
            sqlSb.append(value);
        }
    }
    
    private void extract6(Object value, final StringBuilder sqlSb)
    {
        Iterable<?> items = null;
        if (value instanceof Iterable)
        {
            items = (Iterable<?>)value;
        }
        else if (value.getClass().isArray())
        {
            final ArrayList<Object> arrayList = new ArrayList<Object>();
            final int len = Array.getLength(value);
            for (int i = 0; i < len; i++)
            {
                arrayList.add(Array.get(value, i));
            }
            items = arrayList;
        }
        if (items != null)
        {
            final Iterator<?> iterator = items.iterator();
            if (!iterator.hasNext())
            {
                throw new IllegalArgumentException("value must have tow items.");
            }
            final Object start = iterator.next();
            if (!iterator.hasNext())
            {
                throw new IllegalArgumentException("value must have tow items.");
            }
            final Object end = iterator.next();
            
            final Object startColValue = ColumnUtils.convert2DbColumnValueIfNeeded(start);
            final Object endColValue = ColumnUtils.convert2DbColumnValueIfNeeded(end);
            
            extract8(sqlSb, startColValue, endColValue);
        }
        else
        {
            throw new IllegalArgumentException("value must be an Array or an Iterable.");
        }
    }
    
    private void extract8(final StringBuilder sqlSb, final Object startColValue, final Object endColValue)
    {
        if (ColumnDbType.TEXT.equals(ColumnConverterFactory.getDbColumnType(startColValue.getClass())))
        {
            String startStr = startColValue.toString();
            if (startStr.indexOf('\'') != -1)
            { // convert single quotations
                startStr = startStr.replace("'", "''");
            }
            String endStr = endColValue.toString();
            if (endStr.indexOf('\'') != -1)
            { // convert single quotations
                endStr = endStr.replace("'", "''");
            }
            sqlSb.append("'" + startStr + "'");
            sqlSb.append(" AND ");
            sqlSb.append("'" + endStr + "'");
        }
        else
        {
            sqlSb.append(startColValue);
            sqlSb.append(" AND ");
            sqlSb.append(endColValue);
        }
    }
    
    private void extract5(final StringBuilder sqlSb, Iterable<?> items)
    {
        final StringBuffer stringBuffer = new StringBuffer("(");
        for (final Object item : items)
        {
            final Object itemColValue = ColumnUtils.convert2DbColumnValueIfNeeded(item);
            if (ColumnDbType.TEXT.equals(ColumnConverterFactory.getDbColumnType(itemColValue.getClass())))
            {
                String valueStr = itemColValue.toString();
                if (valueStr.indexOf('\'') != -1)
                { // convert single quotations
                    valueStr = valueStr.replace("'", "''");
                }
                stringBuffer.append("'" + valueStr + "'");
            }
            else
            {
                stringBuffer.append(itemColValue);
            }
            stringBuffer.append(',');
        }
        stringBuffer.deleteCharAt(stringBuffer.length() - 1);
        stringBuffer.append(')');
        sqlSb.append(stringBuffer.toString());
    }
    
    private Iterable<?> extract3(Object value)
    {
        Iterable<?> items = null;
        if (value instanceof Iterable)
        {
            items = (Iterable<?>)value;
        }
        else if (value.getClass().isArray())
        {
            final ArrayList<Object> arrayList = new ArrayList<Object>();
            final int len = Array.getLength(value);
            for (int i = 0; i < len; i++)
            {
                arrayList.add(Array.get(value, i));
            }
            items = arrayList;
        }
        return items;
    }
    
    private void extract(final String conj, final StringBuilder sqlSb)
    {
        if (!whereItems.isEmpty())
        {
            sqlSb.append(' ');
        }
        
        // append conj
        if (!TextUtils.isEmpty(conj))
        {
            sqlSb.append(conj + ' ');
        }
    }
    
    private void extract1(String op, final StringBuilder sqlSb)
    {
        if ("=".equals(op))
        {
            sqlSb.append(" IS NULL");
        }
        else if ("<>".equals(op))
        {
            sqlSb.append(" IS NOT NULL");
        }
        else
        {
            sqlSb.append(' ' + op + " NULL");
        }
    }
}
