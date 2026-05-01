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

import com.read.scriptures.util.LogUtil;
import com.zxl.common.db.annotation.Check;
import com.zxl.common.db.annotation.Column;
import com.zxl.common.db.annotation.Finder;
import com.zxl.common.db.annotation.Foreign;
import com.zxl.common.db.annotation.Id;
import com.zxl.common.db.annotation.NotNull;
import com.zxl.common.db.annotation.Transient;
import com.zxl.common.db.annotation.Unique;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 *
 * <一句话功能简述>
 * <功能详细描述>
 *
 * @author
 * @version [版本号, 2015-2-28]
 * @since [产品/模块版本]
 */
public final class ColumnUtils
{
    private static final Set<String> DB_PRIMITIVE_TYPES = new HashSet<String>(14);
    
    private ColumnUtils()
    {
    }
    
    static
    {
        DB_PRIMITIVE_TYPES.add(int.class.getName());
        DB_PRIMITIVE_TYPES.add(long.class.getName());
        DB_PRIMITIVE_TYPES.add(short.class.getName());
        DB_PRIMITIVE_TYPES.add(byte.class.getName());
        DB_PRIMITIVE_TYPES.add(float.class.getName());
        DB_PRIMITIVE_TYPES.add(double.class.getName());
        
        DB_PRIMITIVE_TYPES.add(Integer.class.getName());
        DB_PRIMITIVE_TYPES.add(Long.class.getName());
        DB_PRIMITIVE_TYPES.add(Short.class.getName());
        DB_PRIMITIVE_TYPES.add(Byte.class.getName());
        DB_PRIMITIVE_TYPES.add(Float.class.getName());
        DB_PRIMITIVE_TYPES.add(Double.class.getName());
        DB_PRIMITIVE_TYPES.add(String.class.getName());
        DB_PRIMITIVE_TYPES.add(byte[].class.getName());
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     *
     * @param fieldType fieldType
     * @return isDbPrimitiveType
     * @see [类、类#方法、类#成员]
     */
    public static boolean isDbPrimitiveType(final Class<?> fieldType)
    {
        return DB_PRIMITIVE_TYPES.contains(fieldType.getName());
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     *
     * @param entityType entityType
     * @param field field
     * @return getColumnGetMethod
     * @see [类、类#方法、类#成员]
     */
    public static Method getColumnGetMethod(final Class<?> entityType, final Field field)
    {
        final String fieldName = field.getName();
        Method getMethod = null;
        if (field.getType() == boolean.class)
        {
            getMethod = getBooleanColumnGetMethod(entityType, fieldName);
        }
        if (null == getMethod)
        {
            final String methodName = "get" + fieldName.substring(0, 1).toUpperCase(Locale.US) + fieldName.substring(1);
            try
            {
                getMethod = entityType.getDeclaredMethod(methodName);
            }
            catch (final NoSuchMethodException e)
            {
                LogUtil.error(methodName, " not exist");
            }
        }
        
        if ((null == getMethod) && (Object.class != entityType.getSuperclass()))
        {
            return getColumnGetMethod(entityType.getSuperclass(), field);
        }
        return getMethod;
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     *
     * @param entityType entityType
     * @param field field
     * @return getColumnSetMethod
     * @see [类、类#方法、类#成员]
     */
    public static Method getColumnSetMethod(final Class<?> entityType, final Field field)
    {
        final String fieldName = field.getName();
        Method setMethod = null;
        if (boolean.class == field.getType())
        {
            setMethod = getBooleanColumnSetMethod(entityType, field);
        }
        if (setMethod == null)
        {
            final String methodName = "set" + fieldName.substring(0, 1).toUpperCase(Locale.US) + fieldName.substring(1);
            try
            {
                setMethod = entityType.getDeclaredMethod(methodName, field.getType());
            }
            catch (final NoSuchMethodException e)
            {
                LogUtil.error(methodName, " not exist");
            }
        }
        
        if ((null == setMethod) && (Object.class != entityType.getSuperclass()))
        {
            return getColumnSetMethod(entityType.getSuperclass(), field);
        }
        return setMethod;
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     *
     * @param field field
     * @return getColumnNameByField
     * @see [类、类#方法、类#成员]
     */
    public static String getColumnNameByField(final Field field)
    {
        final Column column = field.getAnnotation(Column.class);
        if ((null != column) && !TextUtils.isEmpty(column.column()))
        {
            return column.column();
        }
        
        final Id id = field.getAnnotation(Id.class);
        if ((null != id) && !TextUtils.isEmpty(id.column()))
        {
            return id.column();
        }
        
        final Foreign foreign = field.getAnnotation(Foreign.class);
        if ((null != foreign) && !TextUtils.isEmpty(foreign.column()))
        {
            return foreign.column();
        }
        
        final Finder finder = field.getAnnotation(Finder.class);
        if (null != finder)
        {
            return field.getName();
        }
        
        return field.getName();
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     *
     * @param field field
     * @return getForeignColumnNameByField
     * @see [类、类#方法、类#成员]
     */
    public static String getForeignColumnNameByField(final Field field)
    {
        
        final Foreign foreign = field.getAnnotation(Foreign.class);
        if (null != foreign)
        {
            return foreign.foreign();
        }
        
        return field.getName();
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     *
     * @param field field
     * @return field
     * @see [类、类#方法、类#成员]
     */
    public static String getColumnDefaultValue(final Field field)
    {
        final Column column = field.getAnnotation(Column.class);
        if ((null != column) && !TextUtils.isEmpty(column.defaultValue()))
        {
            return column.defaultValue();
        }
        return null;
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     *
     * @param field field
     * @return isTransient
     * @see [类、类#方法、类#成员]
     */
    public static boolean isTransient(final Field field)
    {
        return field.getAnnotation(Transient.class) != null;
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     *
     * @param field field
     * @return isForeign
     * @see [类、类#方法、类#成员]
     */
    public static boolean isForeign(final Field field)
    {
        return field.getAnnotation(Foreign.class) != null;
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     *
     * @param field field
     * @return isFinder
     * @see [类、类#方法、类#成员]
     */
    public static boolean isFinder(final Field field)
    {
        return field.getAnnotation(Finder.class) != null;
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     *
     * @param field field
     * @return isUnique
     * @see [类、类#方法、类#成员]
     */
    public static boolean isUnique(final Field field)
    {
        return field.getAnnotation(Unique.class) != null;
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     *
     * @param field field
     * @return isNotNull
     * @see [类、类#方法、类#成员]
     */
    public static boolean isNotNull(final Field field)
    {
        return field.getAnnotation(NotNull.class) != null;
    }
    
    /**
     * getCheck
     *
     * @param field field
     * @return check.value or null
     */
    public static String getCheck(final Field field)
    {
        final Check check = field.getAnnotation(Check.class);
        if (null != check)
        {
            return check.value();
        }
        else
        {
            return null;
        }
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     *
     * @param foreignColumn foreignColumn
     * @return getForeignEntityType
     * @see [类、类#方法、类#成员]
     */
    public static Class<?> getForeignEntityType(final com.zxl.common.db.sqlite.Foreign foreignColumn)
    {
        Class<?> result = foreignColumn.getColumnField().getType();
        if ((result == ForeignLazyLoader.class) || (result == List.class))
        {
            result = (Class<?>)((ParameterizedType)foreignColumn.getColumnField().getGenericType())
                .getActualTypeArguments()[0];
        }
        return result;
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     *
     * @param finderColumn finderColumn
     * @return getFinderTargetEntityType
     * @see [类、类#方法、类#成员]
     */
    public static Class<?> getFinderTargetEntityType(
        final com.zxl.common.db.sqlite.Finder finderColumn)
    {
        Class<?> result = finderColumn.getColumnField().getType();
        if ((result == FinderLazyLoader.class) || (result == List.class))
        {
            result = (Class<?>)((ParameterizedType)finderColumn.getColumnField().getGenericType())
                .getActualTypeArguments()[0];
        }
        return result;
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     *
     * @param value value
     * @return convert2DbColumnValueIfNeeded
     * @see [类、类#方法、类#成员]
     */
    @SuppressWarnings("unchecked")
    public static Object convert2DbColumnValueIfNeeded(final Object value)
    {
        Object result = value;
        if (null != value)
        {
            final Class<?> valueType = value.getClass();
            if (!isDbPrimitiveType(valueType))
            {
                final ColumnConverter converter = ColumnConverterFactory.getColumnConverter(valueType);
                if (converter != null)
                {
                    result = converter.fieldValue2ColumnValue(value);
                }
                else
                {
                    result = value;
                }
            }
        }
        return result;
    }
    
    private static boolean isStartWithIs(final String fieldName)
    {
        return (fieldName != null) && fieldName.startsWith("is");
    }
    
    private static Method getBooleanColumnGetMethod(final Class<?> entityType, final String fieldName)
    {
        String methodName = "is" + fieldName.substring(0, 1).toUpperCase(Locale.US) + fieldName.substring(1);
        if (isStartWithIs(fieldName))
        {
            methodName = fieldName;
        }
        try
        {
            return entityType.getDeclaredMethod(methodName);
        }
        catch (final NoSuchMethodException e)
        {
            LogUtil.error(methodName, " not exist");
        }
        return null;
    }
    
    private static Method getBooleanColumnSetMethod(final Class<?> entityType, final Field field)
    {
        final String fieldName = field.getName();
        String methodName = null;
        if (isStartWithIs(field.getName()))
        {
            methodName = "set" + fieldName.substring(2, 3).toUpperCase(Locale.US) + fieldName.substring(3);
        }
        else
        {
            methodName = "set" + fieldName.substring(0, 1).toUpperCase(Locale.US) + fieldName.substring(1);
        }
        try
        {
            return entityType.getDeclaredMethod(methodName, field.getType());
        }
        catch (final NoSuchMethodException e)
        {
            LogUtil.error(methodName, " not exist");
        }
        return null;
    }
    
}
