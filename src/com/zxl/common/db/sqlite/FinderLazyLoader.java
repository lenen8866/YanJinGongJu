package com.zxl.common.db.sqlite;

import java.util.List;

/**
 * Author: wyouflf
 * Date: 13-9-10
 * Time: 下午10:50
 * @param <T> T
 * 
 */
public class FinderLazyLoader<T>
{
    private final Finder finderColumn;
    
    private final Object finderValue;
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param finderColumn finderColumn
     * @param value value
     * @see [类、类#方法、类#成员]
     */
    public FinderLazyLoader(final Finder finderColumn, final Object value)
    {
        this.finderColumn = finderColumn;
        this.finderValue = ColumnUtils.convert2DbColumnValueIfNeeded(value);
    }
    
    /***
     * <一句话功能简述>
     * <功能详细描述>
     * @return getAllFromDb
     * @throws DbException  DbException
     * @see [类、类#方法、类#成员]
     */
    public List<T> getAllFromDb()
        throws DbException
    {
        List<T> entities = null;
        final Table table = finderColumn.getTable();
        if (null != table)
        {
            entities = table.getDb().findAll(Selector.from(finderColumn.getTargetEntityType())
                .where(finderColumn.getTargetColumnName(), "=", finderValue));
        }
        return entities;
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @return getFirstFromDb
     * @throws DbException DbException
     * @see [类、类#方法、类#成员]
     */
    public T getFirstFromDb()
        throws DbException
    {
        T entity = null;
        final Table table = finderColumn.getTable();
        if (table != null)
        {
            entity = table.getDb().findFirst(Selector.from(finderColumn.getTargetEntityType())
                .where(finderColumn.getTargetColumnName(), "=", finderValue));
        }
        return entity;
    }
}
