package com.read.scriptures.db;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

import com.read.scriptures.app.HuDongApplication;
import com.read.scriptures.util.LogUtil;
import com.read.scriptures.util.StringUtil;

import java.io.File;
import java.io.IOException;

public class DatabaseContext extends ContextWrapper {

    private Context mContext;
    private String dirName;
    private String sdCardPath;

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    public String getDirName() {
        return dirName;
    }

    public void setDirName(String dirName) {
        this.dirName = dirName;
    }

    public String getSdCardPath() {
        return sdCardPath;
    }

    public void setSdCardPath(String sdCardPath) {
        this.sdCardPath = sdCardPath;
    }

    /**
     * 构造函数
     * 
     * @param base 上下文环境
     */
    public DatabaseContext(Context base) {
        super(base);
        this.mContext = base;
    }

    /**
     * 构造函数
     * 
     * @param base
     * @param dirName
     * 修复：不再赋値 sdCardPath，该字段已废弃，路径统一由 getDBDir() 提供。
     */
    public DatabaseContext(Context base, String dirName) {
        super(base);
        this.mContext = base;
        this.dirName = dirName;
        // 修复：sdCardPath 不再赋値，getDatabasePath 已统一使用内部存储
    }

    /**
     * 修复：原实现首先尝试 sdCardPath（SD 卡），如果没有 SD 卡才回退到内部存储。
     * Android 10+ 已禁止访问外部存储根目录，将导致崩溃。
     * 现统一使用 HuDongApplication.getDBDir()ï¼࣌已包含迁移逻辑ï¼ৌ返回的内部存储目录。
     */
    @Override
    public File getDatabasePath(String name) {
        // 获取内部存储目录（已包含自动迁移逻辑）
        String dbDir = HuDongApplication.getInstance().getDBDir();
        File dirFile = new File(dbDir);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        File dbFile = new File(dbDir, name);
        if (!dbFile.exists()) {
            try {
                dbFile.createNewFile();
            } catch (IOException e) {
                LogUtil.error("DatabaseContext", e);
                return null;
            }
        }
        LogUtil.test(dbFile.getAbsolutePath());
        return dbFile;
    }

    /**
     * 重载这个方法，是用来打开SD卡上的数据库的，android 2.3及以下会调用这个方法。
     * 
     * @param name
     * @param mode
     * @param factory
     */
    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, CursorFactory factory) {
        SQLiteDatabase result = SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);
        return result;
    }

    /**
     * Android 4.0会调用此方法获取数据库。
     * 
     * @see ContextWrapper#openOrCreateDatabase(String,
     *      int, CursorFactory,
     *      DatabaseErrorHandler)
     * @param name
     * @param mode
     * @param factory
     * @param errorHandler
     */
    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, CursorFactory factory,
            DatabaseErrorHandler errorHandler) {
        SQLiteDatabase result = SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);
        return result;
    }
}
