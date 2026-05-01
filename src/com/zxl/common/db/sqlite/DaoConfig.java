package com.zxl.common.db.sqlite;

import android.content.Context;
import android.text.TextUtils;

/**
 * DaoConfig
 * 
 * @author g00218858
 *
 */
public class DaoConfig {
    private final Context context;

    private String dbName = "esight.db"; // 默认数据库名称

    private int dbVersion = 1;

    private String dbDir;

    /**
     *
     * 构造方法
     * 
     * @param context comn
     */
    public DaoConfig(final Context context) {
        // this.context = context.getApplicationContext();
        this.context = context;
    }

    /**
     *
     * 获取实例
     * 
     * @return con
     */
    public Context getContext() {
        return context;
    }

    /**
     * 数据库名称
     * 
     * @return 数据库名称
     */
    public String getDbName() {
        return dbName;
    }

    /**
     * 设置数据库名称
     * 
     * @param dbName 数据库名称
     */
    public void setDbName(final String dbName) {
        if (!TextUtils.isEmpty(dbName)) {
            this.dbName = dbName;
        }
    }

    /**
     * 版本号
     * 
     * @return 版本号
     */
    public int getDbVersion() {
        return dbVersion;
    }

    /**
     * 设置版本号
     * 
     * @param dbVersion 版本号
     */
    public void setDbVersion(final int dbVersion) {
        this.dbVersion = dbVersion;
    }

    /**
     *
     * 数据库保存路径
     * 
     * @return 路径
     */
    public String getDbDir() {
        return dbDir;
    }

    /**
     * 设置数据库路径
     *
     * @param dbDir 设置数据库路径 默认安装文件系统目录路径
     */
    public void setDbDir(final String dbDir) {
        this.dbDir = dbDir;
    }
}