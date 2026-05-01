package com.zxl.common.db.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.read.scriptures.model.Baike;
import com.read.scriptures.util.LogUtil;
import com.read.scriptures.util.SearchTextUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据库操作工具类
 *
 * @author yWX272422
 * @version V100R001C13, 2015-2-28
 * @since V100R001C13
 */
public final class DbUtils extends BaseDbUtil {

    private DaoConfig daoConfig;

    private DbUtils(final DaoConfig config) {
        super(config);
        daoConfig = config;
    }

    /**
     * 初始化实例 <功能详细描述>
     *
     * @param daoConfig config
     * @return 数据库操作实例
     * @see [类、类#方法、类#成员]
     */
    public static synchronized DbUtils getInstance(final DaoConfig daoConfig) {
        if (null == daoConfig) {
            return null;
        }
        DbUtils dao = DAOMAP.get(daoConfig.getDbName());
        if (null == dao) {
            dao = new DbUtils(daoConfig);
            DAOMAP.put(daoConfig.getDbName(), dao);
        } else {
            dao.daoConfig = daoConfig;
        }

        // update the database if needed
        final SQLiteDatabase database = dao.database;
        final int oldVersion = database.getVersion();
        final int newVersion = daoConfig.getDbVersion();
        if (oldVersion != newVersion) {

            database.setVersion(newVersion);
        }

        return dao;
    }

    /**
     * 创建数据库操作实例
     *
     * @param context 实例
     * @return 返回数据库操作实例
     */
    public static DbUtils create(final Context context) {
        if (null != context) {
            final DaoConfig config = new DaoConfig(context);
            return getInstance(config);
        }
        return null;
    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @see [类、类#方法、类#成员]
     */
    public void close() {
        final String dbName = daoConfig.getDbName();
        if (DAOMAP.containsKey(dbName)) {
            DAOMAP.remove(dbName);
            database.close();
        }
    }

    /**
     * 方法
     *
     * @param context d
     * @param dbName  dd
     * @return 属性
     */
    public static DbUtils create(final Context context, final String dbName) {
        final DaoConfig config = new DaoConfig(context);
        config.setDbName(dbName);
        final DbUtils dbUtils = getInstance(config);
        return dbUtils;
    }

    /**
     * 方法
     *
     * @param context dd
     * @param dbDir   asd
     * @param dbName  ads
     * @return 属性
     */
    public static DbUtils create(final Context context, final String dbDir, final String dbName) {
        if ((null != context) && (null != dbDir) && (null != dbName)) {
            final DaoConfig config = new DaoConfig(context);
            config.setDbDir(dbDir);
            config.setDbName(dbName);
            return getInstance(config);
        }
        return null;
    }

    /**
     * 方法
     *
     * @param context   dasasd
     * @param dbName    asdadasdas
     * @param dbVersion asda asd
     * @return 属性
     */
    public static DbUtils create(final Context context, final String dbName, final int dbVersion) {
        if ((null != context) && (null != dbName)) {
            final DaoConfig config = new DaoConfig(context);
            config.setDbName(dbName);
            config.setDbVersion(dbVersion);
            return getInstance(config);
        }
        return null;
    }

    /**
     * 方法
     *
     * @param context   asda
     * @param dbDir     ads
     * @param dbVersion adsa
     * @param dbName    das
     * @return 属性
     */
    public static DbUtils create(final Context context, final String dbDir, final String dbName, final int dbVersion) {
        if ((null != context) && (null != dbDir) && (null != dbName)) {
            final DaoConfig config = new DaoConfig(context);
            config.setDbDir(dbDir);
            config.setDbName(dbName);
            config.setDbVersion(dbVersion);
            return getInstance(config);
        }
        return null;
    }

    /**
     * 创建数据库操作实例
     *
     * @param daoConfig 数据库初始化配置
     * @return 数据库操作实例
     */
    public static DbUtils create(final DaoConfig daoConfig) {
        return getInstance(daoConfig);
    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @param allowTransac allowTransac
     * @return configAllowTransaction
     * @see [类、类#方法、类#成员]
     */
    public DbUtils configAllowTransaction(final boolean allowTransac) {
        allowTransaction = allowTransac;
        return this;
    }

    public SQLiteDatabase getDatabase() {
        return database;
    }

    public DaoConfig getDaoConfig() {
        return daoConfig;
    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @param entity entity
     * @throws DbException 异常
     * @see [类、类#方法、类#成员]
     */
    public void saveOrUpdate(final Object entity) throws DbException {
        if (null == entity) {
            return;
        }
        try {
            beginTransaction();
            createTableIfNotExist(entity.getClass());
            saveOrUpdateWithoutTransaction(entity);

            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @param entities entities
     * @throws DbException 异常
     * @see [类、类#方法、类#成员]
     */
    public void saveOrUpdateAll(final List<?> entities) throws DbException {
        if ((null == entities) || entities.isEmpty()) {
            return;
        }
        try {
            beginTransaction();

            createTableIfNotExist(entities.get(0).getClass());
            for (final Object entity : entities) {
                saveOrUpdateWithoutTransaction(entity);
            }

            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @param entity entity
     * @throws DbException 异常
     * @see [类、类#方法、类#成员]
     */
    public void replace(final Object entity) throws DbException {
        try {
            beginTransaction();
            if (null != entity) {
                createTableIfNotExist(entity.getClass());
                if (null != SqlInfoBuilder.buildReplaceSqlInfo(this, entity)) {
                    execNonQuery(SqlInfoBuilder.buildReplaceSqlInfo(this, entity));
                }
            }
            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @param entity entity
     * @throws DbException 异常
     * @see [类、类#方法、类#成员]
     */
    public void save(final Object entity) throws DbException {
        if (null == entity) {
            return;
        }
        try {
            beginTransaction();

            createTableIfNotExist(entity.getClass());
            execNonQuery(SqlInfoBuilder.buildInsertSqlInfo(this, entity));

            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @param entities entities
     * @throws DbException 异常
     * @see [类、类#方法、类#成员]
     */
    public void saveAll(final List<?> entities) throws DbException {
        if ((null == entities) || entities.isEmpty()) {
            return;
        }
        try {
            beginTransaction();

            createTableIfNotExist(entities.get(0).getClass());
            for (final Object entity : entities) {
                execNonQuery(SqlInfoBuilder.buildInsertSqlInfo(this, entity));
            }

            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @param entity entity
     * @return saveBindingId
     * @throws DbException 异常
     * @see [类、类#方法、类#成员]
     */
    public boolean saveBindingId(final Object entity) throws DbException {
        if (null == entity) {
            return false;
        }
        boolean result = false;
        try {
            beginTransaction();

            createTableIfNotExist(entity.getClass());
            result = saveBindingIdWithoutTransaction(entity);

            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
        return result;
    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @param entityType entityType
     * @param idValue    idValue
     * @throws DbException 异常
     * @see [类、类#方法、类#成员]
     */
    public void deleteById(final Class<?> entityType, final Object idValue) throws DbException {
        if (!tableIsExist(entityType)) {
            return;
        }
        try {
            beginTransaction();

            execNonQuery(SqlInfoBuilder.buildDeleteSqlInfo(this, entityType, idValue));

            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @param entity entity
     * @throws DbException DbException
     * @see [类、类#方法、类#成员]
     */
    public void delete(final Object entity) throws DbException {
        if (!tableIsExist(entity.getClass())) {
            return;
        }
        try {
            beginTransaction();

            execNonQuery(SqlInfoBuilder.buildDeleteSqlInfo(this, entity));

            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @param entityType   entityType
     * @param whereBuilder whereBuilder
     * @throws DbException DbException
     * @see [类、类#方法、类#成员]
     */
    public void delete(final Class<?> entityType, final WhereBuilder whereBuilder) throws DbException {
        if (!tableIsExist(entityType)) {
            return;
        }
        try {
            beginTransaction();

            execNonQuery(SqlInfoBuilder.buildDeleteSqlInfo(this, entityType, whereBuilder));

            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @param entities entities
     * @throws DbException DbException
     * @see [类、类#方法、类#成员]
     */
    public void deleteAll(final List<?> entities) throws DbException {
        if ((null == entities) || entities.isEmpty() || !tableIsExist(entities.get(0).getClass())) {
            return;
        }
        try {
            beginTransaction();

            for (final Object entity : entities) {
                execNonQuery(SqlInfoBuilder.buildDeleteSqlInfo(this, entity));
            }

            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @param entityType entityType
     * @throws DbException DbException
     * @see [类、类#方法、类#成员]
     */
    public void deleteAll(final Class<?> entityType) throws DbException {
        delete(entityType, null);
    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @param entity            entity
     * @param updateColumnNames updateColumnNames
     * @throws DbException DbException
     * @see [类、类#方法、类#成员]
     */
    public void update(final Object entity, final String... updateColumnNames) throws DbException {
        if (!tableIsExist(entity.getClass())) {
            return;
        }
        try {
            beginTransaction();
            if (null != SqlInfoBuilder.buildUpdateSqlInfo(this, entity, updateColumnNames)) {
                execNonQuery(SqlInfoBuilder.buildUpdateSqlInfo(this, entity, updateColumnNames));
            }
            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @param entity            entity
     * @param whereBuilder      whereBuilder
     * @param updateColumnNames updateColumnNames
     * @throws DbException DbException
     * @see [类、类#方法、类#成员]
     */
    public void update(final Object entity, final WhereBuilder whereBuilder, final String... updateColumnNames)
            throws DbException {
        if (!tableIsExist(entity.getClass())) {
            return;
        }
        try {
            beginTransaction();
            if (null != SqlInfoBuilder.buildUpdateSqlInfo(this, entity, whereBuilder, updateColumnNames)) {
                execNonQuery(SqlInfoBuilder.buildUpdateSqlInfo(this, entity, whereBuilder, updateColumnNames));
            }
            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @param entities          entities
     * @param updateColumnNames updateColumnNames
     * @throws DbException DbException
     * @see [类、类#方法、类#成员]
     */
    public void updateAll(final List<?> entities, final String... updateColumnNames) throws DbException {
        if ((null == entities) || entities.isEmpty() || !tableIsExist(entities.get(0).getClass())) {
            return;
        }
        try {
            beginTransaction();

            for (final Object entity : entities) {
                if (null != SqlInfoBuilder.buildUpdateSqlInfo(this, entity, updateColumnNames)) {
                    execNonQuery(SqlInfoBuilder.buildUpdateSqlInfo(this, entity, updateColumnNames));
                }
            }

            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @param entities          entities
     * @param whereBuilder      whereBuilder
     * @param updateColumnNames updateColumnNames
     * @throws DbException DbException
     * @see [类、类#方法、类#成员]
     */
    public void updateAll(final List<?> entities, final WhereBuilder whereBuilder, final String... updateColumnNames)
            throws DbException {
        if ((null == entities) || entities.isEmpty() || !tableIsExist(entities.get(0).getClass())) {
            return;
        }
        try {
            beginTransaction();

            for (final Object entity : entities) {
                if (null != SqlInfoBuilder.buildUpdateSqlInfo(this, entity, whereBuilder, updateColumnNames)) {
                    execNonQuery(SqlInfoBuilder.buildUpdateSqlInfo(this, entity, whereBuilder, updateColumnNames));
                }
            }

            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @param entityType entityType
     * @param idValue    idValue
     * @param <T>        T
     * @return findById
     * @throws DbException DbException
     * @see [类、类#方法、类#成员]
     */
    @SuppressWarnings("unchecked")
    public <T> T findById(final Class<T> entityType, final Object idValue) throws DbException {
        if (!tableIsExist(entityType)) {
            return null;
        }

        final Table table = Table.get(this, entityType);
        final Selector selector = Selector.from(entityType).where(table.getColumnName(), "=", idValue);

        final String sql = selector.limit(1).toString();
        final long seq = CursorUtils.FindCacheSequence.getSeq();
        findTempCache.setSeq(seq);
        final Object obj = findTempCache.get(sql);
        if (null != obj) {
            return (T) obj;
        }

        final Cursor cursor = execQuery(sql);
        if (null != cursor) {
            try {
                if (cursor.moveToNext()) {
                    final T entity = CursorUtils.getEntity(this, cursor, entityType, seq);
                    findTempCache.put(sql, entity);
                    return entity;
                }
            } finally {
                IOUtils.closeQuietly(cursor);
            }
        }
        return null;
    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @param selector selector
     * @param <T>      T
     * @return findFirst
     * @throws DbException DbException
     * @see [类、类#方法、类#成员]
     */
    @SuppressWarnings("unchecked")
    public <T> T findFirst(final Selector selector) throws DbException {
        if ((null == selector) || !tableIsExist(selector.getEntityTypeByName("EntityType"))) {
            return null;
        }

        final String sql = selector.limit(1).toString();
        final long seq = CursorUtils.FindCacheSequence.getSeq();
        findTempCache.setSeq(seq);
        final Object obj = findTempCache.get(sql);
        if (null != obj) {
            return (T) obj;
        }

        final Cursor cursor = execQuery(sql);
        if (null != cursor) {
            try {
                if (cursor.moveToNext()) {
                    final T entity = (T) CursorUtils.getEntity(this, cursor, selector.getEntityTypeByName("EntityType"),
                            seq);
                    findTempCache.put(sql, entity);
                    return entity;
                }
            } finally {
                IOUtils.closeQuietly(cursor);
            }
        }
        return null;
    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @param entityType entityType
     * @param <T>        T
     * @return findFirst
     * @throws DbException DbException
     * @see [类、类#方法、类#成员]
     */
    public <T> T findFirst(final Class<T> entityType) throws DbException {
        return findFirst(Selector.from(entityType));
    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @param selector selector
     * @param <T>      T
     * @return findAll
     * @throws DbException DbException
     * @see [类、类#方法、类#成员]
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> findAll(final Selector selector) throws DbException {
        if ((null == selector) || !tableIsExist(selector.getEntityTypeByName("EntityType"))) {
            return null;
        }
        final String sql = selector.toString();
        final long seq = CursorUtils.FindCacheSequence.getSeq();
        findTempCache.setSeq(seq);
        final Object obj = findTempCache.get(sql);
        if (null != obj) {
            return (List<T>) obj;
        }
        final List<T> result = new ArrayList<T>();
        beginTransaction();
        try {
            final Cursor cursor = execQuery(sql);
            if (null != cursor) {
                try {
                    while (cursor.moveToNext()) {
                        final T entity = (T) CursorUtils.getEntity(this, cursor, selector.getEntityTypeByName("EntityType"),
                                seq);
                        result.add(entity);
                    }
                    findTempCache.put(sql, result);
                } finally {
                    IOUtils.closeQuietly(cursor);
                }
            }
            setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            endTransaction();
        }
        return result;
    }

    public List<Baike> findAll(String sql) {
        final List<Baike> result = new ArrayList<>();
        getDatabase().beginTransaction();
        try {
            Cursor cur = getDatabase().rawQuery(sql, null);
            while (cur.moveToNext()) {
                int id = cur.getInt(cur.getColumnIndex("id"));
                String name = cur.getString(cur.getColumnIndex("name"));
                int indexId = cur.getInt(cur.getColumnIndex("indexId"));
                String content = cur.getString(cur.getColumnIndex("content"));
                int categoryId = cur.getInt(cur.getColumnIndex("categoryId"));
                String cateName = cur.getString(cur.getColumnIndex("cateName"));
                Baike baike = new Baike(id, name, indexId, content, categoryId, cateName);
                result.add(baike);
            }
            cur.close();
            getDatabase().setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            getDatabase().endTransaction();
        }
        return result;
    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @param <T> T
     * @return findAll
     * @throws DbException DbException
     * @see [类、类#方法、类#成员]
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> findAll(final String sql, Class<T> clazz) throws DbException {
        if (sql == null) {
            return null;
        }

//        findAllCount(sql);

        final long seq = CursorUtils.FindCacheSequence.getSeq();
        findTempCache.setSeq(seq);
        final Object obj = findTempCache.get(sql);
        if (null != obj) {
            return (List<T>) obj;
        }

        final List<T> result = new ArrayList<T>();

        final Cursor cursor = execQuery(sql);
        if (null != cursor) {
            try {
                int largeLimit = 5000;
                if (sql.contains("OFFSET") && cursor.getCount() > SearchTextUtil.searchLimit) {
                    largeLimit = SearchTextUtil.searchLimit;
                    cursor.moveToPosition(Integer.parseInt(sql.substring(sql.length() - 1)));
                }
                while (cursor.moveToNext() && result.size() <= largeLimit) {
                    final T entity = (T) CursorUtils.getEntity(this, cursor, clazz, seq);
                    result.add(entity);
                }
                findTempCache.put(sql, result);
            } finally {
                IOUtils.closeQuietly(cursor);
            }
        }
        return result;
    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @param <T> T
     * @return findAll
     * @throws DbException DbException
     * @see [类、类#方法、类#成员]
     */
    public <T> List<T> find1(final String sql, Class<T> clazz) throws DbException {
        if (sql == null) {
            return null;
        }
        final long seq = CursorUtils.FindCacheSequence.getSeq();

        final List<T> result = new ArrayList<T>();
        final Cursor cursor = execQuery(sql);
        if (null != cursor) {
            while (cursor.moveToNext()) {
                final T entity = (T) CursorUtils.getEntity(this, cursor, clazz, seq);
                result.add(entity);
            }
            IOUtils.closeQuietly(cursor);
        }
        return result;
    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @param <T> T
     * @return findAll
     * @throws DbException DbException
     * @see [类、类#方法、类#成员]
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> find(Class<T> clazz, String[] keys, int id, int type) {
        //1 volume != null
        //2 nodeCategory != null
        //3 else
        StringBuilder arg1 = new StringBuilder();
        ArrayList<String> arg2 = new ArrayList();
        arg2.add(id + "");
        switch (type) {
            case 1:
                arg1.append("  categoryId =  ? ");
                break;
            case 2:
                arg1.append("  categoryId =  ? ");
                break;
            case 3:
                arg1.append("  parentId =  ? ");
                break;
        }
        for (String key : keys) {
            if (!TextUtils.isEmpty(key)) {
                arg1.append(" and content like ? ");
                arg2.add("%" + key + "%");
            }
        }
        arg1.append(" and content NOT like ? " +
                "and content NOT like ? " +
                "and  name  NOT like  ? " +
                "and  name  NOT like ? " +
                "and  name  NOT like ? ");

        arg2.add("%【%");
        arg2.add("%】%");
        arg2.add("%【%");
        arg2.add("%】%");
        arg2.add("%jieshao%");

        final long seq = CursorUtils.FindCacheSequence.getSeq();
        final List<T> result = new ArrayList<T>();
        String[] strings = arg2.toArray(new String[arg2.size()]);
        Cursor chapter = database.query("chapter", null, arg1.toString(), strings, null, null, null, "50");
        while (chapter.moveToNext()) {
            final T entity = (T) CursorUtils.getEntity(this, chapter, clazz, seq);
            result.add(entity);

        }
        return result;
    }

    public Long findAllCount(String sql) throws DbException {
        if (sql.contains("FROM")) {
            sql = "SELECT COUNT(*) " + sql.substring(sql.indexOf("FROM"), sql.length());
        } else if (sql.contains("from")) {
            sql = "SELECT COUNT(*) " + sql.substring(sql.indexOf("from"), sql.length());
        }
        if (sql.indexOf("LIMIT") > 0) {
            sql = sql.substring(0, sql.indexOf("LIMIT"));
        }

        final long seq = CursorUtils.FindCacheSequence.getSeq();
        final Object obj = findTempCache.get(sql);
        if (null != obj) {
            return (long) obj;
        }
        findTempCache.setSeq(seq);
        long count = 0;
        final Cursor cursor = execQuery(sql);
        if (null != cursor) {
            try {
                if (cursor.moveToFirst()) {
                    count = cursor.getLong(0);
                    findTempCache.put(sql, count);
                } else {
                    count = 0;
                }
            } finally {
                IOUtils.closeQuietly(cursor);
            }
        }
        return count;
    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @param entityType entityType
     * @param <T>        T
     * @return findAll
     * @throws DbException DbException
     * @see [类、类#方法、类#成员]
     */
    public <T> List<T> findAll(final Class<T> entityType) throws DbException {
        if (null == entityType) {
            return null;
        }
        return findAll(Selector.from(entityType));
    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @param selector selector
     * @return count
     * @throws DbException DbException
     * @see [类、类#方法、类#成员]
     */
    public long count(final Selector selector) throws DbException {
        if (null == selector) {
            return 0;
        }
        final Class<?> entityType = selector.getEntityTypeByName("EntityType");
        if (!tableIsExist(entityType)) {
            return 0;
        }
        Long mCount = 0L;
        final Table table = Table.get(this, entityType);
        final DbModelSelector dmSelector = selector.select("count(" + table.getColumnName() + ") as count");
        if (null != findDbModelFirst(dmSelector)) {
            mCount = findDbModelFirst(dmSelector).getLong("count");
        }
        if (null == mCount) {
            return 0;
        }
        return mCount;
    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @param entityType entityType
     * @return count
     * @throws DbException DbException
     * @see [类、类#方法、类#成员]
     */
    public long count(final Class<?> entityType) throws DbException {
        return count(Selector.from(entityType));

    }

    private void saveOrUpdateWithoutTransaction(final Object entity) throws DbException {
        final Table table = Table.get(this, entity.getClass());
        final Id id = table.getId();
        if (id.isAutoIncrement()) {
            if (null != id.getColumnValue(entity)) {
                if (null != SqlInfoBuilder.buildUpdateSqlInfo(this, entity)) {
                    execNonQuery(SqlInfoBuilder.buildUpdateSqlInfo(this, entity));
                }
            } else {
                saveBindingIdWithoutTransaction(entity);
            }
        } else {
            if (null != SqlInfoBuilder.buildUpdateSqlInfo(this, entity)) {
                execNonQuery(SqlInfoBuilder.buildUpdateSqlInfo(this, entity));
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean saveBindingIdWithoutTransaction(final Object entity) throws DbException {
        final Class<?> entityType = entity.getClass();
        final Table table = Table.get(this, entityType);
        final Id idColumn = table.getId();
        if (idColumn.isAutoIncrement()) {
            execNonQuery(SqlInfoBuilder.buildInsertSqlInfo(this, entity));
            final long id = getLastAutoIncrementId(table.getTableName());
            if (id == MYNUM) {
                return false;
            }
            idColumn.setAutoIncrementId(entity, id);
            return true;
        } else {
            execNonQuery(SqlInfoBuilder.buildInsertSqlInfo(this, entity));
            return true;
        }
    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @param entityType entityType
     * @throws DbException DbException
     * @see [类、类#方法、类#成员]
     */
    @Override
    public void createTableIfNotExist(final Class<?> entityType) throws DbException {
        if (!tableIsExist(entityType)) {
            final SqlInfo sqlInfo = SqlInfoBuilder.buildCreateTableSqlInfo(this, entityType);
            execNonQuery(sqlInfo);
            final String execAfterTableCreated = TableUtils.getExecAfterTableCreated(entityType);
            if (!TextUtils.isEmpty(execAfterTableCreated)) {
                execNonQuery(execAfterTableCreated);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean tableIsExist(final Class<?> entityType) throws DbException {
        if (null == entityType) {
            return false;
        }
        final Table table = Table.get(this, entityType);
        if (table.isCheckedDatabase()) {
            return true;
        }

        final Cursor cursor = execQuery(
                "SELECT COUNT(*) AS c FROM sqlite_master WHERE type='table' AND name='" + table.getTableName() + "'");
        if (null != cursor) {
            try {
                if (cursor.moveToNext()) {
                    final int count = cursor.getInt(0);
                    if (count > 0) {
                        table.setCheckedDatabase(true);
                        return true;
                    }
                }
            } finally {
                IOUtils.closeQuietly(cursor);
            }

        }

        return false;
    }

    /**
     * 检查表中某列是否存在
     *
     * @param db
     * @param tableName  表名
     * @param columnName 列名
     * @return
     */
    public boolean checkColumnExists2(SQLiteDatabase db, String tableName
            , String columnName) {
        boolean result = false;
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("select * from sqlite_master where name = ? and sql like ?"
                    , new String[]{tableName, "%" + columnName + "%"});
            result = null != cursor && cursor.moveToFirst();
        } catch (Exception e) {
            LogUtil.error("checkColumnExists2..." + e.getMessage());
        } finally {
            if (null != cursor && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return result;
    }

    /**
     * 检查表中索引是否存在
     *
     * @param db
     * @return
     */
    public boolean checkIndexExists(SQLiteDatabase db, String indexName) {
        boolean result = false;
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("select * from sqlite_master where type = ? and name = ?"
                    , new String[]{"index", indexName});
            result = null != cursor && cursor.moveToFirst();
        } catch (Exception e) {
            LogUtil.error("checkIndexExists..." + e.getMessage());
        } finally {
            if (null != cursor && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return result;
    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @throws DbException DbException
     * @see [类、类#方法、类#成员]
     */
    public void dropDb() throws DbException {
        final Cursor cursor = execQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name<>'sqlite_sequence'");
        if (null != cursor) {
            try {
                while (cursor.moveToNext()) {
                    try {
                        final String tableName = cursor.getString(0);
                        execNonQuery("DROP TABLE " + tableName);
                        Table.remove(this, tableName);
                    } catch (final DbException e) {
                        LogUtil.error("DbUtils dropDb DbException");
                    }
                }

            } catch (final Exception e) {
                throw new DbException(e);
            } finally {
                IOUtils.closeQuietly(cursor);
            }
        }
    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @param entityType entityType
     * @throws DbException DbException
     * @see [类、类#方法、类#成员]
     */
    public void dropTable(final Class<?> entityType) throws DbException {
        if (!tableIsExist(entityType)) {
            return;
        }
        final String tableName = TableUtils.getTableName(entityType);
        execNonQuery("DROP TABLE " + tableName);
        Table.remove(this, entityType);
    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @param entities entities
     * @throws DbException 异常
     * @see [类、类#方法、类#成员]
     */
    public void replaceAll(final List<?> entities) throws DbException {
        if ((null == entities) || entities.isEmpty()) {
            return;
        }
        try {
            beginTransaction();

            createTableIfNotExist(entities.get(0).getClass());
            for (final Object entity : entities) {
                if (null != SqlInfoBuilder.buildReplaceSqlInfo(this, entity)) {
                    execNonQuery(SqlInfoBuilder.buildReplaceSqlInfo(this, entity));
                }
            }
            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
    }

}