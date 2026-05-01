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

import java.util.LinkedList;
import java.util.List;

/**
 * 
 * SQL语句
 * @author yWX272422
 * @version V100R001C13, 2015-2-28
 * @since V100R001C13
 */
public class SqlInfo
{
    
    private String sql;
    
    private List<Object> bindArgs;
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @see [类、类#方法、类#成员]
     */
    public SqlInfo()
    {
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param sql SQL
     * @see [类、类#方法、类#成员]
     */
    public SqlInfo(final String sql)
    {
        this.sql = sql;
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param sql sql
     * @param bindArgs bindArgs
     * @see [类、类#方法、类#成员]
     */
    public SqlInfo(final String sql, final Object... bindArgs)
    {
        this.sql = sql;
        addBindArgs(bindArgs);
    }
    
    public String getSql()
    {
        return sql;
    }
    
    public void setSql(final String sql)
    {
        this.sql = sql;
    }
    
    public List<Object> getBindArgs()
    {
        return bindArgs;
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @return getBindArgsAsArray
     * @see [类、类#方法、类#成员]
     */
    public Object[] getBindArgsAsArray()
    {
        if (null != bindArgs)
        {
            return bindArgs.toArray();
        }
        return new Object[0];
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @return getBindArgsAsStrArray
     * @see [类、类#方法、类#成员]
     */
    public String[] getBindArgsAsStrArray()
    {
        if (null != bindArgs)
        {
            final String[] strings = new String[bindArgs.size()];
            final int k = bindArgs.size();
            for (int i = 0; i < k; i++)
            {
                final Object value = bindArgs.get(i);
                strings[i] = value == null ? null : value.toString();
            }
            return strings;
        }
        return new String[0];
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param arg arg
     * @see [类、类#方法、类#成员]
     */
    public void addBindArg(final Object arg)
    {
        if (null == bindArgs)
        {
            bindArgs = new LinkedList<Object>();
        }
        
        bindArgs.add(ColumnUtils.convert2DbColumnValueIfNeeded(arg));
    }
    
    /* package */void addBindArgWithoutConverter(final Object arg)
    {
        if (bindArgs == null)
        {
            bindArgs = new LinkedList<Object>();
        }
        
        bindArgs.add(arg);
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param bindArs bindArgs
     * @see [类、类#方法、类#成员]
     */
    public void addBindArgs(final Object... bindArs)
    {
        if (null != bindArs)
        {
            for (final Object arg : bindArs)
            {
                addBindArg(arg);
            }
        }
    }
    
}
