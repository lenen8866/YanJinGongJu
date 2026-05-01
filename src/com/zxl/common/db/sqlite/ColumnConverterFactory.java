package com.zxl.common.db.sqlite;

import com.read.scriptures.util.LogUtil;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Author: wyouflf
 * Date: 13-11-4
 * Time: 下午10:27
 */
public final class ColumnConverterFactory
{
    /**
     * columnType_columnConverter_map
     */
    private static final ConcurrentHashMap<String, ColumnConverter> COLUMNTYPE_COLUMNCONVERTER_MAP;
    
    private ColumnConverterFactory()
    {
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param columnType columnType
     * @return getColumnConverter
     * @see [类、类#方法、类#成员]
     */
    public static ColumnConverter getColumnConverter(final Class columnType)
    {
        if (COLUMNTYPE_COLUMNCONVERTER_MAP.containsKey(columnType.getName()))
        {
            return COLUMNTYPE_COLUMNCONVERTER_MAP.get(columnType.getName());
        }
        else if (ColumnConverter.class.isAssignableFrom(columnType))
        {
            try
            {
                final ColumnConverter columnConverter = (ColumnConverter)columnType.newInstance();
                if (columnConverter != null)
                {
                    COLUMNTYPE_COLUMNCONVERTER_MAP.put(columnType.getName(), columnConverter);
                }
                return columnConverter;
            }
            catch (final InstantiationException e)
            {
                LogUtil.error("InstantiationException");
            }
            catch (final IllegalAccessException e)
            {
                LogUtil.error("IllegalAccessException");
            }
        }
        return null;
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param columnType columnType
     * @return getDbColumnType
     * @see [类、类#方法、类#成员]
     */
    public static ColumnDbType getDbColumnType(final Class columnType)
    {
        final ColumnConverter converter = getColumnConverter(columnType);
        if (converter != null)
        {
            return converter.getColumnDbType();
        }
        return ColumnDbType.TEXT;
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param columnType columnType
     * @param columnConverter columnConverter
     * @see [类、类#方法、类#成员]
     */
    public static void registerColumnConverter(final Class columnType, final ColumnConverter columnConverter)
    {
        COLUMNTYPE_COLUMNCONVERTER_MAP.put(columnType.getName(), columnConverter);
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param columnType columnType
     * @return columnType
     * @see [类、类#方法、类#成员]
     */
    public static boolean isSupportColumnConverter(final Class columnType)
    {
        if (COLUMNTYPE_COLUMNCONVERTER_MAP.containsKey(columnType.getName()))
        {
            return true;
        }
        else if (ColumnConverter.class.isAssignableFrom(columnType))
        {
            try
            {
                final ColumnConverter columnConverter = (ColumnConverter)columnType.newInstance();
                boolean flag = true;
                
                if (columnConverter != null)
                {
                    COLUMNTYPE_COLUMNCONVERTER_MAP.put(columnType.getName(), columnConverter);
                    flag = false;
                }
                return flag;
                
            }
            catch (final InstantiationException e)
            {
                LogUtil.error("InstantiationException");
            }
            catch (final IllegalAccessException e)
            {
                LogUtil.error("IllegalAccessException");
            }
        }
        return false;
    }
    
    static
    {
        COLUMNTYPE_COLUMNCONVERTER_MAP = new ConcurrentHashMap<String, ColumnConverter>();
        
        final BooleanColumnConverter booleanColumnConverter = new BooleanColumnConverter();
        COLUMNTYPE_COLUMNCONVERTER_MAP.put(boolean.class.getName(), booleanColumnConverter);
        COLUMNTYPE_COLUMNCONVERTER_MAP.put(Boolean.class.getName(), booleanColumnConverter);
        
        final ByteArrayColumnConverter byteArrayColumnConverter = new ByteArrayColumnConverter();
        COLUMNTYPE_COLUMNCONVERTER_MAP.put(byte[].class.getName(), byteArrayColumnConverter);
        
        final ByteColumnConverter byteColumnConverter = new ByteColumnConverter();
        COLUMNTYPE_COLUMNCONVERTER_MAP.put(byte.class.getName(), byteColumnConverter);
        COLUMNTYPE_COLUMNCONVERTER_MAP.put(Byte.class.getName(), byteColumnConverter);
        
        final CharColumnConverter charColumnConverter = new CharColumnConverter();
        COLUMNTYPE_COLUMNCONVERTER_MAP.put(char.class.getName(), charColumnConverter);
        COLUMNTYPE_COLUMNCONVERTER_MAP.put(Character.class.getName(), charColumnConverter);
        
        final DateColumnConverter dateColumnConverter = new DateColumnConverter();
        COLUMNTYPE_COLUMNCONVERTER_MAP.put(Date.class.getName(), dateColumnConverter);
        
        final DoubleColumnConverter doubleColumnConverter = new DoubleColumnConverter();
        COLUMNTYPE_COLUMNCONVERTER_MAP.put(double.class.getName(), doubleColumnConverter);
        COLUMNTYPE_COLUMNCONVERTER_MAP.put(Double.class.getName(), doubleColumnConverter);
        
        final FloatColumnConverter floatColumnConverter = new FloatColumnConverter();
        COLUMNTYPE_COLUMNCONVERTER_MAP.put(float.class.getName(), floatColumnConverter);
        COLUMNTYPE_COLUMNCONVERTER_MAP.put(Float.class.getName(), floatColumnConverter);
        
        final IntegerColumnConverter integerColumnConverter = new IntegerColumnConverter();
        COLUMNTYPE_COLUMNCONVERTER_MAP.put(int.class.getName(), integerColumnConverter);
        COLUMNTYPE_COLUMNCONVERTER_MAP.put(Integer.class.getName(), integerColumnConverter);
        
        final LongColumnConverter longColumnConverter = new LongColumnConverter();
        COLUMNTYPE_COLUMNCONVERTER_MAP.put(long.class.getName(), longColumnConverter);
        COLUMNTYPE_COLUMNCONVERTER_MAP.put(Long.class.getName(), longColumnConverter);
        
        final ShortColumnConverter shortColumnConverter = new ShortColumnConverter();
        COLUMNTYPE_COLUMNCONVERTER_MAP.put(short.class.getName(), shortColumnConverter);
        COLUMNTYPE_COLUMNCONVERTER_MAP.put(Short.class.getName(), shortColumnConverter);
        
        final SqlDateColumnConverter sqlDateColumnConverter = new SqlDateColumnConverter();
        COLUMNTYPE_COLUMNCONVERTER_MAP.put(java.sql.Date.class.getName(), sqlDateColumnConverter);
        
        final StringColumnConverter stringColumnConverter = new StringColumnConverter();
        COLUMNTYPE_COLUMNCONVERTER_MAP.put(String.class.getName(), stringColumnConverter);
    }
}
