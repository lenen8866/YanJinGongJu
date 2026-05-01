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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * <一句话功能简述>
 * <功能详细描述>
 * 
 * @author   
 * @version  [版本号, 2015-2-28]
 * @since  [产品/模块版本]
 */
public class DbModel
{
    
    /**
     * key: columnName
     * value: valueStr
     */
    private final Map<String, String> dataMap = new HashMap<String, String>();
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param columnName columnName
     * @return getString
     * @see [类、类#方法、类#成员]
     */
    public String getString(final String columnName)
    {
        return dataMap.get(columnName);
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param columnName columnName
     * @return getString
     * @see [类、类#方法、类#成员]
     */
    public int getInt(final String columnName)
    {
        return Integer.valueOf(dataMap.get(columnName));
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param columnName columnName
     * @return getString
     * @see [类、类#方法、类#成员]
     */
    public boolean getBoolean(final String columnName)
    {
        final String value = dataMap.get(columnName);
        if (value != null)
        {
            return value.length() == 1 ? "1".equals(value) : Boolean.valueOf(value);
        }
        return false;
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param columnName columnName
     * @return getString
     * @see [类、类#方法、类#成员]
     */
    public double getDouble(final String columnName)
    {
        return Double.valueOf(dataMap.get(columnName));
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param columnName columnName
     * @return getString
     * @see [类、类#方法、类#成员]
     */
    public float getFloat(final String columnName)
    {
        return Float.valueOf(dataMap.get(columnName));
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param columnName columnName
     * @return getString
     * @see [类、类#方法、类#成员]
     */
    public long getLong(final String columnName)
    {
        return Long.valueOf(dataMap.get(columnName));
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param columnName columnName
     * @return getString
     * @see [类、类#方法、类#成员]
     */
    public Date getDate(final String columnName)
    {
        final long date = Long.valueOf(dataMap.get(columnName));
        return new Date(date);
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param columnName columnName
     * @return getString
     * @see [类、类#方法、类#成员]
     */
    public java.sql.Date getSqlDate(final String columnName)
    {
        final long date = Long.valueOf(dataMap.get(columnName));
        return new java.sql.Date(date);
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param columnName columnName
     * @param valueStr valueStr
     * @see [类、类#方法、类#成员]
     */
    public void add(final String columnName, final String valueStr)
    {
        dataMap.put(columnName, valueStr);
    }
    
    /**
     * isEmpty
     * @return key: columnName
     */
    public Map<String, String> getDataMap()
    {
        return dataMap;
    }
    
    /**
     * isEmpty
     * @param columnName columnName
     * @return isEmpty
     */
    public boolean isEmpty(final String columnName)
    {
        return TextUtils.isEmpty(dataMap.get(columnName));
    }
}
