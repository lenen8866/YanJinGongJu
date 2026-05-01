package com.read.scriptures.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.read.scriptures.widget.QProcessDialog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author lim
 * @ClassName: DatabaseHelper
 * @Package com.lgmshare.goodfoodm.db
 * @Description: SQLiteOpenHelper是一个辅助类，用来管理数据库的创建和版本他，它提供两个方面的功能
 *               第一，getReadableDatabase()、getWritableDatabase()
 *               可以获得SQLiteDatabase对象，通过该对象可以对数据库进行操作
 *               第二，提供了onCreate()、onUpgrade()两个回调函数，允许我们再创建和升级数据库时，进行自己的操作
 * @mail lgmshare@gmail.com
 * @date 2014年6月25日 上午9:48:37
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private Context mContext;
    /**
     * 数据库名称
     */
    public static final String DB_NAME = "hudong.db";
    /**
     * 数据库版本
     */
    private static final int DB_VERSION = 1;

    public static final String TABLE_CATEGORY = "category";
    public static final String TABLE_VOLUME = "volume";
    public static final String TABLE_CHAPTER = "chapter";
    public static final String TABLE_BOOKMARK = "bookmark";
    public static final String TABLE_SPIRITUALITY = "spirituality";
    public static final String TABLE_SYSCONFIG = "sysconfig";
    public static final String TABLE_BAIKE_CATEGORY = "baike_category";
    public static final String TABLE_SPIRITUALITY_CATEGORY = "spirituality_category";
    public static final String TABLE_BAIKE = "baike";

    private volatile static DatabaseHelper uniqueInstance;

    public static DatabaseHelper getInstance(Context context) {
        if (uniqueInstance == null) {
            synchronized (DatabaseHelper.class) {
                if (uniqueInstance == null) {
                    uniqueInstance = new DatabaseHelper(context,context.getFilesDir().getAbsolutePath()+"/foowwlite.db",1);
                }
            }
        }
        return uniqueInstance;
    }


    public DatabaseHelper(Context context) {
        this(context, DB_NAME, DB_VERSION);
        mContext = context;
    }

    public DatabaseHelper(Context context, String name) {
        this(context, name, DB_VERSION);
        mContext = context;
    }

    private DatabaseHelper(Context context, String name, int version) {
        this(context, name, null, version);
        mContext = context;
    }

    /**
     * 在SQLiteOpenHelper的子类当中，必须有该构造函数
     *
     * @param context 上下文对象
     * @param name 数据库名称
     * @param factory
     * @param version 当前数据库的版本，值必须是整数并且是递增的状态
     */
    private DatabaseHelper(Context context, String name, CursorFactory factory, int version) {
        // 必须通过super调用父类当中的构造函数
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 该函数是在第一次创建的时候执行，实际上是第一次得到SQLiteDatabase对象的时候才会调用这个方法
//        initDb(db, "hudong.sql");
        // execSQL用于执行SQL语句

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VOLUME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHAPTER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKMARK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SPIRITUALITY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SYSCONFIG);
        onCreate(db);
    }

    public void init(QProcessDialog dialog) {
//        initDbWithContent(getWritableDatabase(), "chapter.txt", dialog);
//        initDbWithContent(getWritableDatabase(), "spirituality.txt", dialog);
    }

    private void initDb(SQLiteDatabase sqliteDatabase, String sql) {
        BufferedReader bufferedReader = getBuffer(sql);
        sqliteDatabase.beginTransaction();
        try {
            boolean reader = true;
            while (reader) {
                String str = bufferedReader.readLine();
                if (str == null) {
                    reader = false;
                    bufferedReader.close();
                    sqliteDatabase.setTransactionSuccessful();
                    sqliteDatabase.endTransaction();
                } else {
                    // LoggerUtil.d("DatabaseHelper", str);
                    sqliteDatabase.execSQL(str);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initDbWithContent(SQLiteDatabase sqliteDatabase, String sql, QProcessDialog dialog) {
        BufferedReader bufferedReader = getBuffer(sql);
        sqliteDatabase.beginTransaction();
        try {
            boolean reader = true;
            int index = 0;
            while (reader) {
                String str = bufferedReader.readLine();
                if (str == null) {
                    reader = false;
                    bufferedReader.close();
                    sqliteDatabase.setTransactionSuccessful();
                    sqliteDatabase.endTransaction();
                } else {
                    str = str.replace(");", ",'" + "" + "');");
                    // LoggerUtil.d("DatabaseHelper", str);
                    sqliteDatabase.execSQL(str);
                }
                index++;
                if (index % 200 == 0) {
                    dialog.setProgress(dialog.getProgress() + 30);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BufferedReader getBuffer(String sql) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(mContext.getAssets().open(sql)));
            return reader;
        } catch (IOException localIOException) {
            localIOException.printStackTrace();
        }
        return null;
    }

}
