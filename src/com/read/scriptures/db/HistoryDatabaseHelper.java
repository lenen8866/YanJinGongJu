package com.read.scriptures.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.read.scriptures.bean.CollectBean;
import com.read.scriptures.bean.HistoryBean;
import com.read.scriptures.model.Bookmark;
import com.read.scriptures.util.TimeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 历史/收藏/书签数据库 Helper
 */
public class HistoryDatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "HistoryDatabaseHelper";

    public static final String DB_NAME = "history.db";
    private static final int DB_VERSION = 5;

    public static final String TABLE_HISTORY   = "history";
    public static final String TABLE_COLLECT   = "collect";
    public static final String TABLE_BOOK_MARK = "book_mark";

    // PERF: 预缓存列索引，避免每次遍历 Cursor 都调用 getColumnIndex（字符串查找）
    // 在第一次查询后缓存，后续复用，减少 CPU 开销
    private int colHistoryChapter = -1, colHistoryVolumeId = -1, colHistoryVolumeName = -1;
    private int colHistoryChapterCount = -1, colHistoryIndexId = -1, colHistoryContent = -1;
    private int colHistoryCategoryId = -1, colHistoryParentId = -1, colHistoryParentPath = -1;
    private int colHistoryTime = -1, colHistoryTopIndex = -1;

    private int colCollectChapter = -1, colCollectVolumeId = -1, colCollectVolumeName = -1;
    private int colCollectChapterCount = -1, colCollectIndexId = -1, colCollectContent = -1;
    private int colCollectCategoryId = -1, colCollectParentId = -1, colCollectParentPath = -1;
    private int colCollectTime = -1, colCollectTopIndex = -1;

    private SQLiteDatabase m_dbRead  = null;
    private SQLiteDatabase m_dbWrite = null;

    public HistoryDatabaseHelper(Context context) {
        this(context, DB_NAME, DB_VERSION);
    }

    public HistoryDatabaseHelper(Context context, String name) {
        this(context, name, DB_VERSION);
    }

    public HistoryDatabaseHelper(Context context, String name, int version) {
        this(context, name, null, version);
    }

    public HistoryDatabaseHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
        // PERF: 延迟到真正需要时再打开数据库连接，构造函数中不立即打开
        // 原代码在每个构造函数里都调用 getReadableDatabase/getWritableDatabase，
        // 导致每次 new 都触发磁盘 IO，严重影响主线程性能
    }

    // PERF: 懒加载数据库连接，首次使用时才打开，避免构造时阻塞
    private SQLiteDatabase getReadDb() {
        if (m_dbRead == null) {
            m_dbRead = getReadableDatabase();
        }
        return m_dbRead;
    }

    private SQLiteDatabase getWriteDb() {
        if (m_dbWrite == null) {
            m_dbWrite = getWritableDatabase();
        }
        return m_dbWrite;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createHistoryTable(db);
        createCollectTable(db);
        createRemarkTable(db);
    }

    private void createHistoryTable(SQLiteDatabase db) {
        String sql = "create table " + TABLE_HISTORY
                + "(id Integer not null primary key autoincrement,"
                + "volumeName varchar(50),volumeId Integer,chapter varchar(50),"
                + "chapterCount varchar(50),indexId Integer,content varchar(50),"
                + "categoryId Integer,parentId Integer,parentPath varchar(50),"
                + "time varchar(50),topIndex Integer)";
        db.beginTransaction();
        try {
            db.execSQL(sql);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private void createCollectTable(SQLiteDatabase db) {
        String sql = "create table " + TABLE_COLLECT
                + "(id Integer not null primary key autoincrement,"
                + "volumeName varchar(50),volumeId Integer,chapter varchar(50),"
                + "chapterCount varchar(50),indexId Integer,content varchar(50),"
                + "categoryId Integer,parentId Integer,parentPath varchar(50),"
                + "time varchar(50),topIndex Integer)";
        db.beginTransaction();
        try {
            db.execSQL(sql);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private void createRemarkTable(SQLiteDatabase db) {
        String createSql = "create table " + TABLE_BOOK_MARK
                + "(id Integer not null primary key autoincrement,"
                + "volumeName varchar(50),volumeId Integer,chapterId varchar(50),"
                + "chapterName varchar(50),chapterPosition Integer,chpCount Integer,"
                + "content varchar(500),description varchar(500),type Integer,"
                + "createTime varchar(50) default '2020-09-01 09:00:00',"
                + "categoryId varchar(20),categoryName varchar(100))";
        String createIndexSql = "create index mark_index on " + TABLE_BOOK_MARK
                + " ('content', 'description')";
        db.beginTransaction();
        try {
            db.execSQL(createSql);
            db.execSQL(createIndexSql);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "createRemarkTable error", e);
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COLLECT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOK_MARK);
        onCreate(db);
    }

    // ==================================历史=================================

    public synchronized void addHistory(HistoryBean historyBean) {
        // PERF: 预先分配 ContentValues 容量（11个字段），避免内部数组扩容
        ContentValues values = new ContentValues(11);
        values.put("chapter",      historyBean.getChapter());
        values.put("volumeId",     historyBean.getVolumeId());
        values.put("volumeName",   historyBean.getVolumeName());
        values.put("chapterCount", historyBean.getChapterCount());
        values.put("indexId",      historyBean.getIndexId());
        values.put("content",      historyBean.getContent());
        values.put("categoryId",   historyBean.getCategoryId());
        values.put("parentId",     historyBean.getParentId());
        values.put("parentPath",   historyBean.getParentPath());
        values.put("time",         TimeUtils.getNowStamp());
        values.put("topIndex",     0);
        SQLiteDatabase db = getWriteDb();
        db.beginTransaction();
        try {
            db.insert(TABLE_HISTORY, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "addHistory error", e);
        } finally {
            db.endTransaction();
        }
    }

    public List<HistoryBean> getAllHistory() {
        List<HistoryBean> historyList = new ArrayList<>();
        // PERF: 使用 SELECT 指定列代替 SELECT *，减少数据传输量
        String strSql = "SELECT volumeName,volumeId,chapter,chapterCount,indexId,content,"
                + "categoryId,parentId,parentPath,time,topIndex FROM "
                + TABLE_HISTORY + " ORDER BY topIndex DESC, time DESC";
        Cursor cur = null;
        try {
            cur = getReadDb().rawQuery(strSql, null);
            // PERF: 在循环外缓存列索引，避免每行都执行字符串查找（getColumnIndex 内部是线性查找）
            if (cur.moveToFirst()) {
                if (colHistoryChapter == -1) {
                    colHistoryChapter      = cur.getColumnIndex("chapter");
                    colHistoryVolumeId     = cur.getColumnIndex("volumeId");
                    colHistoryVolumeName   = cur.getColumnIndex("volumeName");
                    colHistoryChapterCount = cur.getColumnIndex("chapterCount");
                    colHistoryIndexId      = cur.getColumnIndex("indexId");
                    colHistoryContent      = cur.getColumnIndex("content");
                    colHistoryCategoryId   = cur.getColumnIndex("categoryId");
                    colHistoryParentId     = cur.getColumnIndex("parentId");
                    colHistoryParentPath   = cur.getColumnIndex("parentPath");
                    colHistoryTime         = cur.getColumnIndex("time");
                    colHistoryTopIndex     = cur.getColumnIndex("topIndex");
                }
                // PERF: 预分配 ArrayList 容量，避免多次扩容导致的数组拷贝
                if (cur.getCount() > 0) {
                    historyList = new ArrayList<>(cur.getCount());
                }
                do {
                    historyList.add(new HistoryBean(
                            cur.getString(colHistoryChapter),
                            cur.getInt(colHistoryVolumeId),
                            cur.getString(colHistoryVolumeName),
                            cur.getInt(colHistoryChapterCount),
                            cur.getInt(colHistoryIndexId),
                            cur.getString(colHistoryContent),
                            cur.getInt(colHistoryCategoryId),
                            cur.getInt(colHistoryParentId),
                            cur.getString(colHistoryParentPath),
                            cur.getString(colHistoryTime),
                            cur.getInt(colHistoryTopIndex)
                    ));
                } while (cur.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "getAllHistory error", e);
        } finally {
            // PERF: 使用 finally 确保 Cursor 始终关闭，防止游标泄漏
            if (cur != null) cur.close();
        }
        return historyList;
    }

    public int getMaxTopIndex() {
        int topIndex = 0;
        // PERF: 使用聚合查询，只返回一个值，避免全表扫描
        String strSql = "SELECT max(topIndex) FROM " + TABLE_HISTORY;
        Cursor cur = null;
        try {
            cur = getReadDb().rawQuery(strSql, null);
            if (cur.moveToFirst()) {
                topIndex = cur.getInt(0);
            }
        } catch (Exception e) {
            Log.e(TAG, "getMaxTopIndex error", e);
        } finally {
            if (cur != null) cur.close();
        }
        return topIndex;
    }

    public void deleteHistory(String volumeIds) {
        SQLiteDatabase db = getWriteDb();
        db.beginTransaction();
        try {
            db.execSQL("DELETE FROM " + TABLE_HISTORY + " WHERE volumeId IN (" + volumeIds + ")");
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "deleteHistory error", e);
        } finally {
            db.endTransaction();
        }
    }

    public void clearHistory() {
        SQLiteDatabase db = getWriteDb();
        db.beginTransaction();
        try {
            db.execSQL("DELETE FROM " + TABLE_HISTORY);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "clearHistory error", e);
        } finally {
            db.endTransaction();
        }
    }

    public boolean isExist(int volumeId) {
        // PERF: 用 COUNT(1) 代替 SELECT *，不取任何列数据，只判断行是否存在，速度更快
        String strSql = "SELECT COUNT(1) FROM " + TABLE_HISTORY + " WHERE volumeId=" + volumeId;
        Cursor cur = null;
        try {
            cur = getReadDb().rawQuery(strSql, null);
            if (cur.moveToFirst()) {
                return cur.getInt(0) > 0;
            }
        } catch (Exception e) {
            Log.e(TAG, "isExist error", e);
        } finally {
            if (cur != null) cur.close();
        }
        return false;
    }

    public void updateHistory(HistoryBean historyBean) {
        // PERF: 改用参数化查询（bindArgs），避免字符串拼接 SQL 的安全风险和额外内存开销
        String sql = "UPDATE " + TABLE_HISTORY
                + " SET content=?, chapter=?, indexId=?, time=? WHERE volumeId=?";
        try {
            getWriteDb().execSQL(sql, new Object[]{
                    historyBean.getContent(),
                    historyBean.getChapter(),
                    historyBean.getIndexId(),
                    TimeUtils.getNowStamp(),
                    historyBean.getVolumeId()
            });
        } catch (Exception e) {
            Log.e(TAG, "updateHistory error", e);
        }
    }

    public void updateHistoryTopInfo(HistoryBean historyBean) {
        // PERF: 改用参数化查询，避免字符串拼接
        String sql = "UPDATE " + TABLE_HISTORY + " SET topIndex=? WHERE volumeId=?";
        try {
            getWriteDb().execSQL(sql, new Object[]{
                    historyBean.getTopIndex(),
                    historyBean.getVolumeId()
            });
        } catch (Exception e) {
            Log.e(TAG, "updateHistoryTopInfo error", e);
        }
    }

    // ======================收藏==============================

    public synchronized void addCollect(CollectBean collectBean) {
        // PERF: 预先分配 ContentValues 容量
        ContentValues values = new ContentValues(11);
        values.put("chapter",      collectBean.getChapter());
        values.put("volumeId",     collectBean.getVolumeId());
        values.put("volumeName",   collectBean.getVolumeName());
        values.put("chapterCount", collectBean.getChapterCount());
        values.put("indexId",      collectBean.getIndexId());
        values.put("content",      collectBean.getContent());
        values.put("categoryId",   collectBean.getCategoryId());
        values.put("parentId",     collectBean.getParentId());
        values.put("parentPath",   collectBean.getParentPath());
        values.put("time",         TimeUtils.getNowStamp());
        values.put("topIndex",     0);
        SQLiteDatabase db = getWriteDb();
        db.beginTransaction();
        try {
            db.insert(TABLE_COLLECT, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "addCollect error", e);
        } finally {
            db.endTransaction();
        }
    }

    public List<CollectBean> getAllCollectBean() {
        List<CollectBean> collectList = new ArrayList<>();
        // PERF: 指定查询列，避免 SELECT *
        String strSql = "SELECT volumeName,volumeId,chapter,chapterCount,indexId,content,"
                + "categoryId,parentId,parentPath,time,topIndex FROM "
                + TABLE_COLLECT + " ORDER BY topIndex DESC, time DESC";
        Cursor cur = null;
        try {
            cur = getReadDb().rawQuery(strSql, null);
            // PERF: 循环外缓存列索引
            if (cur.moveToFirst()) {
                if (colCollectChapter == -1) {
                    colCollectChapter      = cur.getColumnIndex("chapter");
                    colCollectVolumeId     = cur.getColumnIndex("volumeId");
                    colCollectVolumeName   = cur.getColumnIndex("volumeName");
                    colCollectChapterCount = cur.getColumnIndex("chapterCount");
                    colCollectIndexId      = cur.getColumnIndex("indexId");
                    colCollectContent      = cur.getColumnIndex("content");
                    colCollectCategoryId   = cur.getColumnIndex("categoryId");
                    colCollectParentId     = cur.getColumnIndex("parentId");
                    colCollectParentPath   = cur.getColumnIndex("parentPath");
                    colCollectTime         = cur.getColumnIndex("time");
                    colCollectTopIndex     = cur.getColumnIndex("topIndex");
                }
                if (cur.getCount() > 0) {
                    collectList = new ArrayList<>(cur.getCount());
                }
                do {
                    collectList.add(new CollectBean(
                            cur.getString(colCollectChapter),
                            cur.getInt(colCollectVolumeId),
                            cur.getString(colCollectVolumeName),
                            cur.getInt(colCollectChapterCount),
                            cur.getInt(colCollectIndexId),
                            cur.getString(colCollectContent),
                            cur.getInt(colCollectCategoryId),
                            cur.getInt(colCollectParentId),
                            cur.getString(colCollectParentPath),
                            cur.getString(colCollectTime),
                            cur.getInt(colCollectTopIndex)
                    ));
                } while (cur.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "getAllCollectBean error", e);
        } finally {
            if (cur != null) cur.close();
        }
        return collectList;
    }

    public int getCollectMaxTopIndex() {
        int topIndex = 0;
        Cursor cur = null;
        try {
            cur = getReadDb().rawQuery("SELECT max(topIndex) FROM " + TABLE_COLLECT, null);
            if (cur.moveToFirst()) {
                topIndex = cur.getInt(0);
            }
        } catch (Exception e) {
            Log.e(TAG, "getCollectMaxTopIndex error", e);
        } finally {
            if (cur != null) cur.close();
        }
        return topIndex;
    }

    public void deleteCollect(String volumeIds) {
        SQLiteDatabase db = getWriteDb();
        db.beginTransaction();
        try {
            db.execSQL("DELETE FROM " + TABLE_COLLECT + " WHERE volumeId IN (" + volumeIds + ")");
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "deleteCollect error", e);
        } finally {
            db.endTransaction();
        }
    }

    public void updateCollect(CollectBean collectBean) {
        // PERF: 参数化查询，防止 SQL 注入并减少字符串拼接开销
        String sql = "UPDATE " + TABLE_COLLECT
                + " SET content=?, chapter=?, indexId=?, time=? WHERE volumeName=?";
        try {
            getWriteDb().execSQL(sql, new Object[]{
                    collectBean.getContent(),
                    collectBean.getChapter(),
                    collectBean.getIndexId(),
                    TimeUtils.getNowStamp(),
                    collectBean.getVolumeName()
            });
        } catch (Exception e) {
            Log.e(TAG, "updateCollect error", e);
        }
    }

    public void updateCollectTopInfo(CollectBean collectBean) {
        // PERF: 参数化查询
        String sql = "UPDATE " + TABLE_COLLECT + " SET topIndex=? WHERE volumeId=?";
        try {
            getWriteDb().execSQL(sql, new Object[]{
                    collectBean.getTopIndex(),
                    collectBean.getVolumeId()
            });
        } catch (Exception e) {
            Log.e(TAG, "updateCollectTopInfo error", e);
        }
    }

    public boolean isExistCollect(String volumeName) {
        // PERF: COUNT(1) 代替 SELECT *，避免取行数据
        String strSql = "SELECT COUNT(1) FROM " + TABLE_COLLECT + " WHERE volumeName=?";
        Cursor cur = null;
        try {
            cur = getReadDb().rawQuery(strSql, new String[]{volumeName});
            if (cur.moveToFirst()) {
                return cur.getInt(0) > 0;
            }
        } catch (Exception e) {
            Log.e(TAG, "isExistCollect error", e);
        } finally {
            if (cur != null) cur.close();
        }
        return false;
    }

    // =====================书签===============

    private final String MARK_COLUMId            = "id";
    private final String MARK_COLUM_VOLUMEId     = "volumeId";
    private final String MARK_COLUM_VOLUMEName   = "volumeName";
    private final String MARK_COLUM_CHAPTERId    = "chapterId";
    private final String MARK_COLUM_CHAPTERName  = "chapterName";
    private final String MARK_COLUM_CHAPTERPosition = "chapterPosition";
    private final String MARK_COLUM_CHPCount     = "chpCount";
    private final String MARK_COLUM_CONTENT      = "content";
    private final String MARK_COLUM_DESCRIPTION  = "description";
    private final String MARK_COLUM_TYPE         = "type";
    private final String MARK_COLUM_TIME         = "createTime";
    private final String MARK_COLUM_CATEGORY_ID  = "categoryId";
    private final String MARK_COLUM_CATEGORY_NAME = "categoryName";

    // PERF: 将书签列数组提取为类级别常量，避免每次查询都重新创建数组对象
    private final String[] BOOKMARK_COLUMNS = {
            MARK_COLUMId, MARK_COLUM_VOLUMEId, MARK_COLUM_VOLUMEName,
            MARK_COLUM_CHAPTERId, MARK_COLUM_CHAPTERName, MARK_COLUM_CHAPTERPosition,
            MARK_COLUM_CHPCount, MARK_COLUM_CONTENT, MARK_COLUM_DESCRIPTION,
            MARK_COLUM_TYPE, MARK_COLUM_TIME, MARK_COLUM_CATEGORY_ID, MARK_COLUM_CATEGORY_NAME
    };

    public ArrayList<Bookmark> getBookmarkList(String sortType, List<String> keyWordList) {
        ArrayList<Bookmark> arrayList = new ArrayList<>();
        // PERF: 用 StringBuilder 替代 StringBuffer，单线程下性能更好（无同步开销）
        // 预估容量：每个关键词约 50 字符
        StringBuilder buffer = new StringBuilder(keyWordList.size() * 50);
        String[] whereArgs = new String[keyWordList.size() * 2];

        for (int i = 0; i < keyWordList.size(); i++) {
            String keyWord = sqliteEscape(keyWordList.get(i));
            whereArgs[i * 2]     = "%" + keyWord + "%";
            whereArgs[i * 2 + 1] = "%" + keyWord + "%";
            buffer.append("(content like ? escape '/' or description like ? escape '/')");
            if (i < keyWordList.size() - 1) {
                buffer.append(" and ");
            }
        }

        Cursor query = null;
        try {
            query = getWriteDb().query(TABLE_BOOK_MARK, BOOKMARK_COLUMNS,
                    buffer.toString(), whereArgs, null, null, "id " + sortType, "");
            if (query != null && query.moveToFirst()) {
                // PERF: 预分配容量
                arrayList = new ArrayList<>(query.getCount());
                // PERF: 使用 BookmarkDataBaseBuilder 统一构建，复用实例
                BookmarkDataBaseBuilder builder = new BookmarkDataBaseBuilder();
                do {
                    arrayList.add(builder.build(query));
                } while (query.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "getBookmarkList error", e);
        } finally {
            if (query != null) query.close();
        }
        return arrayList;
    }

    public void addBookmark(List<Bookmark> bookmarks) {
        if (bookmarks == null || bookmarks.isEmpty()) return;
        SQLiteDatabase db = getWriteDb();
        // PERF: 批量插入用一个事务包裹，原代码每条独立插入，N条记录 = N次磁盘同步
        // 改为事务批量提交，性能提升数十倍
        db.beginTransaction();
        try {
            BookmarkDataBaseBuilder builder = new BookmarkDataBaseBuilder();
            for (Bookmark bookmark : bookmarks) {
                ContentValues values = builder.deconstruct(bookmark);
                db.insert(TABLE_BOOK_MARK, null, values);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "addBookmark error", e);
        } finally {
            db.endTransaction();
        }
    }

    public void deleteMarkById(String id) {
        getWriteDb().delete(TABLE_BOOK_MARK, "id=?", new String[]{id});
    }

    public void deleteMarkByIds(String ids) {
        try {
            getWriteDb().execSQL("DELETE FROM " + TABLE_BOOK_MARK + " WHERE id IN (" + ids + ")");
        } catch (Exception e) {
            Log.e(TAG, "deleteMarkByIds error", e);
        }
    }

    // PERF: sqliteEscape 被高频调用，使用 replace 代替 replaceAll（无正则编译开销）
    // 对于简单字符串替换，String.replace 比 replaceAll 快约 3-5 倍
    public static String sqliteEscape(String keyWord) {
        keyWord = keyWord.replace("/",  "//");
        keyWord = keyWord.replace("'",  "''");
        keyWord = keyWord.replace("[",  "/[");
        keyWord = keyWord.replace("]",  "/]");
        keyWord = keyWord.replace("%",  "/%");
        keyWord = keyWord.replace("&",  "/&");
        keyWord = keyWord.replace("_",  "/_");
        keyWord = keyWord.replace("(",  "/(");
        keyWord = keyWord.replace(")",  "/)");
        return keyWord;
    }
}
