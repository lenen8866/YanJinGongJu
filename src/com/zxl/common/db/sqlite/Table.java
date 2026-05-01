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
public final class Table
{
    /**
     * key: dbName#className
     */
    private static final Map<String, Table> TABLEMAP = new HashMap<String, Table>();
    
    /**
     * 链接符号
     */
    private static final String COLLECTSIGN = "#";
    
    /**
     *  db
     */
    private final DbUtils db;
    
    /**
     * tableName
     */
    private final String tableName;
    
    /**
     * id
     */
    private final Id id;
    
    /**
     * key: columnName
     */
    private final Map<String, Column> columnMap;
    
    /**
     * key: columnName
     */
    private final Map<String, Finder> finderMap;
    
    private boolean checkedDatabase;
    
    private Table(final DbUtils db, final Class<?> entityType)
    {
        this.db = db;
        this.tableName = TableUtils.getTableName(entityType);
        this.id = TableUtils.getId(entityType);
        this.columnMap = TableUtils.getColumnMap(entityType);
        
        finderMap = new HashMap<String, Finder>();
        for (final Column column : columnMap.values())
        {
            column.setTable(this);
            if (column instanceof Finder)
            {
                finderMap.put(column.getColumnName(), (Finder)column);
            }
        }
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param db db
     * @param entityType entityType
     * @return Table
     * @see [类、类#方法、类#成员]
     */
    public static synchronized Table get(final DbUtils db, final Class<?> entityType)
    {
        final String tableKey = db.getDaoConfig().getDbName() + COLLECTSIGN + entityType.getName();
        Table table = TABLEMAP.get(tableKey);
        if (table == null)
        {
            table = new Table(db, entityType);
            TABLEMAP.put(tableKey, table);
        }
        
        return table;
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param db db
     * @param entityType entityType
     * @see [类、类#方法、类#成员]
     */
    public static synchronized void remove(final DbUtils db, final Class<?> entityType)
    {
        final String tableKey = db.getDaoConfig().getDbName() + COLLECTSIGN + entityType.getName();
        TABLEMAP.remove(tableKey);
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param db db
     * @param tableName tableName
     * @see [类、类#方法、类#成员]
     */
    public static synchronized void remove(final DbUtils db, final String tableName)
    {
        if (!TABLEMAP.isEmpty())
        {
            String key = null;
            for (final Map.Entry<String, Table> entry : TABLEMAP.entrySet())
            {
                final Table table = entry.getValue();
                if ((table != null) && table.tableName.equals(tableName))
                {
                    key = entry.getKey();
                    if (key.startsWith(db.getDaoConfig().getDbName() + COLLECTSIGN))
                    {
                        break;
                    }
                }
            }
            if (TextUtils.isEmpty(key))
            {
                TABLEMAP.remove(key);
            }
        }
    }
    
    public boolean isCheckedDatabase()
    {
        return checkedDatabase;
    }
    
    public void setCheckedDatabase(final boolean checkedDatabase)
    {
        this.checkedDatabase = checkedDatabase;
    }
    
    public DbUtils getDb()
    {
        return db;
    }
    
    public String getTableName()
    {
        return tableName;
    }
    
    public Id getId()
    {
        return id;
    }
    
    public Map<String, Column> getColumnMap()
    {
        return columnMap;
    }
    
    public Map<String, Finder> getFinderMap()
    {
        return finderMap;
    }
    
    public String getColumnName()
    {
        return getId().getColumnName();
    }
}
