/*
 * 文 件 名:  BaseDbUtil.java
 * 版    权:  Huawei Technologies Co., Ltd. Copyright YYYY-YYYY,  All rights reserved
 * 描    述:  <描述>
 * 修 改 人:  gWX204803
 * 修改时间:  2016-1-19
 * 跟踪单号:  <跟踪单号>
 * 修改单号:  <修改单号>
 * 修改内容:  <修改内容>
 */
package com.zxl.common.db.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <一句话功能简述>
 * <功能详细描述>
 *
 * @author gWX204803
 * @version [版本号, 2016-1-19]
 * @since [产品/模块版本]
 */
public class BaseDbUtil {

    /**
     * key: dbName
     */
    protected static final Map<String, DbUtils> DAOMAP = new HashMap<String, DbUtils>();

    protected static final int MYNUM = -1;

    protected final SQLiteDatabase database;

    protected final Lock writeLock = new ReentrantLock();

    protected final FindTempCache findTempCache = new FindTempCache();

    protected volatile boolean writeLocked = false;

    protected boolean allowTransaction = false;

    /**
     * <一句话功能简述>
     * <功能详细描述>
     *
     * @param config config
     * @see [类、类#方法、类#成员]
     */
    public BaseDbUtil(final DaoConfig config) {
        if (null == config) {
            throw new IllegalArgumentException("daoConfig may not be null");
        }
        database = createDatabase(config);

    }

    /**
     * 创建数据库表
     * [方法功能说明]
     *
     * @param config 配置信息
     * @return 表
     */
    private SQLiteDatabase createDatabase(final DaoConfig config) {
        SQLiteDatabase result = null;

        final String dbDir = config.getDbDir();
        final String dbName = config.getDbName();
        if (!TextUtils.isEmpty(dbDir)) {
            final File dir = new File(dbDir);
            if (dir.exists() || dir.mkdirs()) {
                final File dbFile = new File(dbDir, dbName);
                result = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
            }
        } else {
            result = config.getContext().openOrCreateDatabase(dbName, Context.MODE_PRIVATE, null);
        }
        return result;
    }

    /***
     * <一句话功能简述>
     * <功能详细描述>
     * @param sqlInfo sqlInfo
     * @return findDbModelAll
     * @throws DbException DbException
     * @see [类、类#方法、类#成员]
     */
    public List<DbModel> findDbModelAll(final SqlInfo sqlInfo)
            throws DbException {
        if (null == sqlInfo) {
            return null;
        }
        final List<DbModel> dbModelList = new ArrayList<DbModel>();

        final Cursor cursor = execQuery(sqlInfo);
        if (null != cursor) {
            try {
                while (cursor.moveToNext()) {
                    dbModelList.add(CursorUtils.getDbModel(cursor));
                }
            } finally {
                IOUtils.closeQuietly(cursor);
            }

        }
        return dbModelList;
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     *
     * @param sqlInfo sqlInfo
     * @return execQuery
     * @throws DbException DbException
     * @see [类、类#方法、类#成员]
     */
    public Cursor execQuery(final SqlInfo sqlInfo)
            throws DbException {
        if (null != sqlInfo) {
            try {
                return database.rawQuery(sqlInfo.getSql(), sqlInfo.getBindArgsAsStrArray());
            } catch (final Exception e) {
                throw new DbException(e);
            }
        }
        return null;
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     *
     * @param sqlInfo sqlInfo
     * @throws DbException DbException
     * @see [类、类#方法、类#成员]
     */
    public void execNonQuery(final SqlInfo sqlInfo)
            throws DbException {
        try {
            if (null != sqlInfo.getBindArgs()) {
                database.execSQL(sqlInfo.getSql(), sqlInfo.getBindArgsAsArray());
            } else {
                database.execSQL(sqlInfo.getSql());
            }
        } catch (final Exception e) {
            throw new DbException(e);
        }
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     *
     * @param sql sql
     * @throws DbException DbException
     * @see [类、类#方法、类#成员]
     */
    public void execNonQuery(final String sql)
            throws DbException {
        if ((null != sql) && !sql.isEmpty()) {
            try {
                database.execSQL(sql);
            } catch (final Exception e) {
                throw new DbException(e);
            }
        }
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     *
     * @param sql sql
     * @return execQuery
     * @throws DbException DbException
     * @see [类、类#方法、类#成员]
     */
    public Cursor execQuery(final String sql) throws DbException {
        if ((null != sql) && !sql.isEmpty()) {
            try {
                return database.rawQuery(sql, null);
            } catch (final Exception e) {
                throw new DbException(e);
            }
        }
        return null;

    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     *
     * @param tableName 表名
     * @return 返回id
     * @throws DbException 数据库异常
     * @see [类、类#方法、类#成员]
     */
    protected long getLastAutoIncrementId(final String tableName)
            throws DbException {
        long id = -1;
        final Cursor cursor = execQuery("SELECT seq FROM sqlite_sequence WHERE name='" + tableName + "'");
        if (null != cursor) {
            try {
                if (cursor.moveToNext()) {
                    id = cursor.getLong(0);
                }
            } finally {
                IOUtils.closeQuietly(cursor);
            }

        }
        return id;
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     *
     * @param sqlInfo sqlInfo
     * @return findDbModelFirst
     * @throws DbException DbException
     * @see [类、类#方法、类#成员]
     */
    public DbModel findDbModelFirst(final SqlInfo sqlInfo)
            throws DbException {
        if (null == sqlInfo) {
            return null;
        }
        final Cursor cursor = execQuery(sqlInfo);
        if (null != cursor) {
            try {
                if (cursor.moveToNext()) {
                    return CursorUtils.getDbModel(cursor);
                }
            } finally {
                IOUtils.closeQuietly(cursor);
            }
        }
        return null;
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     *
     * @param selector selector
     * @return findDbModelFirst
     * @throws DbException DbException
     * @see [类、类#方法、类#成员]
     */
    public DbModel findDbModelFirst(final DbModelSelector selector)
            throws DbException {
        if (null == selector) {
            return null;
        }
        if (!tableIsExist(selector.getEntityType())) {
            return null;
        }

        final Cursor cursor = execQuery(selector.limit(1).toString());
        if (null != cursor) {
            try {
                if (cursor.moveToNext()) {
                    return CursorUtils.getDbModel(cursor);
                }
            } finally {
                IOUtils.closeQuietly(cursor);
            }
        }
        return null;
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     *
     * @param entityType entityType
     * @return tableIsExist
     * @throws DbException DbException
     * @see [类、类#方法、类#成员]
     */
    public boolean tableIsExist(final Class<?> entityType)
            throws DbException {
        return false;
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     *
     * @param selector selector
     * @return findDbModelAll
     * @throws DbException DbException
     * @see [类、类#方法、类#成员]
     */
    public List<DbModel> findDbModelAll(final DbModelSelector selector)
            throws DbException {
        if (null == selector) {
            return null;
        }
        if (!tableIsExist(selector.getEntityType())) {
            return null;
        }

        final List<DbModel> dbModelList = new ArrayList<DbModel>();

        final Cursor cursor = execQuery(selector.toString());
        if (null != cursor) {
            try {
                while (cursor.moveToNext()) {
                    dbModelList.add(CursorUtils.getDbModel(cursor));
                }
            } finally {
                IOUtils.closeQuietly(cursor);
            }

        }
        return dbModelList;
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     *
     * @param entities entities
     * @throws DbException 异常
     * @see [类、类#方法、类#成员]
     */
    public void saveBindingIdAll(final List<?> entities)
            throws DbException {
        if ((null == entities) || entities.isEmpty()) {
            return;
        }
        try {
            beginTransaction();

            createTableIfNotExist(entities.get(0).getClass());
            for (final Object entity : entities) {
                if (!saveBindingIdWithoutTransaction(entity)) {
                    throw new DbException("saveBindingId error, transaction will not commit!");
                }
            }

            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     *
     * @param entity entity
     * @return boolean
     * @throws DbException 数据库异常
     * @see [类、类#方法、类#成员]
     */
    protected boolean saveBindingIdWithoutTransaction(final Object entity)
            throws DbException {
        return false;
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     *
     * @param class1 class1
     * @throws DbException DbException
     * @see [类、类#方法、类#成员]
     */
    public void createTableIfNotExist(final Class<? extends Object> class1)
            throws DbException {

    }

    /**
     * 开启事务
     */
    protected void beginTransaction() {
        if (allowTransaction) {
            database.beginTransaction();
        } else {
            writeLock.lock();
            writeLocked = true;
        }
    }

    /**
     * 事务处理
     */
    protected void setTransactionSuccessful() {
        if (allowTransaction) {
            database.setTransactionSuccessful();
        }
    }

    /**
     * 结束事务
     * [方法功能说明] [参数说明]
     */
    protected void endTransaction() {
        if (allowTransaction) {
            database.endTransaction();
        }
        if (writeLocked) {
            writeLock.unlock();
            writeLocked = false;
        }
    }
}
