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

import com.read.scriptures.util.LogUtil;
import com.zxl.common.db.annotation.NoAutoIncrement;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * <一句话功能简述>
 * <功能详细描述>
 *
 * @author
 * @version  [版本号, 2015-2-28]
 * @since  [产品/模块版本]
 */
public class Id extends Column
{
    
    private static final Set<String> INTEGER_TYPES = new HashSet<String>(2);
    
    private static final Set<String> AUTO_INCREMENT_TYPES = new HashSet<String>(4);
    
    private final String columnFieldClassName;
    
    private boolean isAutoIncrementChecked = false;
    
    private boolean autoIncreMent = false;
    
    /* package */ Id(final Class<?> entityType, final Field field)
    {
        super(entityType, field);
        columnFieldClassName = columnField.getType().getName();
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @return isAutoIncrement
     * @see [类、类#方法、类#成员]
     */
    public boolean isAutoIncrement()
    {
        if (!isAutoIncrementChecked)
        {
            isAutoIncrementChecked = true;
            autoIncreMent = (columnField.getAnnotation(NoAutoIncrement.class) == null)
                && AUTO_INCREMENT_TYPES.contains(columnFieldClassName);
        }
        return autoIncreMent;
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param entity entity
     * @param value value
     * @see [类、类#方法、类#成员]
     */
    @SuppressWarnings("unchecked")
    public void setAutoIncrementId(final Object entity, final long value)
    {
        Object idValue = value;
        if (INTEGER_TYPES.contains(columnFieldClassName))
        {
            idValue = (int)value;
        }
        
        if (null != setMethod)
        {
            try
            {
                setMethod.invoke(entity, idValue);
            }
            catch (final IllegalAccessException e)
            {
                LogUtil.error("Id setAutoIncrementId IllegalAccessException");
            }
            catch (final IllegalArgumentException e)
            {
                LogUtil.error("Id setAutoIncrementId IllegalArgumentException");
            }
            catch (final InvocationTargetException e)
            {
                LogUtil.error("Id setAutoIncrementId InvocationTargetException");
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
                columnField.set(entity, idValue);
            }
            catch (final IllegalAccessException e)
            {
                LogUtil.error("Id setAutoIncrementId IllegalAccessException");
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Object getColumnValue(final Object entity)
    {
        final Object idValue = super.getColumnValue(entity);
        if (null != idValue)
        {
            if (isAutoIncrement() && String.valueOf(idValue).equals("0"))
            {
                return null;
            }
            else
            {
                return idValue;
            }
        }
        return null;
    }
    
    static
    {
        INTEGER_TYPES.add(int.class.getName());
        INTEGER_TYPES.add(Integer.class.getName());
        
        AUTO_INCREMENT_TYPES.addAll(INTEGER_TYPES);
        AUTO_INCREMENT_TYPES.add(long.class.getName());
        AUTO_INCREMENT_TYPES.add(Long.class.getName());
    }
}
