package com.zxl.common.db.sqlite;

import java.util.concurrent.ConcurrentHashMap;

/**
 * FindTempCache
 * @author g00218858
 *
 */
public class FindTempCache
{
    /**
     * key: sql;
     * value: find result
     */
    private final ConcurrentHashMap<String, Object> cache = new ConcurrentHashMap<String, Object>();
    
    private long seq = 0;
    
    /**
     * 设置SQL语句
     * @param sql 语句
     * @param result 实体类
     */
    public void put(final String sql, final Object result)
    {
        if (null != sql && null != result)
        {
            cache.put(sql, result);
        }
    }
    
    /**
     * 缓存
     * @param sql 语句
     * @return 实体类
     */
    public Object get(final String sql)
    {
        if (null != sql)
        {
            return cache.get(sql);
        }
        return null;
    }
    
    /**
     * 设置seq
     * @param seq  s
     */
    public void setSeq(final long seq)
    {
        if (this.seq != seq)
        {
            cache.clear();
            this.seq = seq;
        }
    }
}