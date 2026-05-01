package com.read.scriptures.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.read.scriptures.bean.CategoryBean;
import com.read.scriptures.model.Category;
import com.read.scriptures.util.CharUtils;
import com.read.scriptures.util.FileUtil;
import com.read.scriptures.util.TimeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lim
 * @Description: 数据库category表 hepler
 * @mail lgmshare@gmail.com
 * @date 2014年6月25日 上午10:17:57
 */
public class CategoryDatabaseHelper {

    private DatabaseHelper mDatabaseHelper;

    public DatabaseHelper getmDatabaseHelper() {
        return mDatabaseHelper;
    }

    public void setmDatabaseHelper(DatabaseHelper mDatabaseHelper) {
        this.mDatabaseHelper = mDatabaseHelper;
    }

    public CategoryDatabaseHelper(Context context) {
        DatabaseContext dbContext = new DatabaseContext(context.getApplicationContext(), "test");
        mDatabaseHelper = new DatabaseHelper(dbContext);
    }

    /**
     * 获取SQLiteDatabase实例
     */
    private SQLiteDatabase getDb() {
        return mDatabaseHelper.getWritableDatabase();
    }

    public void addUserToRecent(Category radio) {
        SQLiteDatabase db = getDb();
        // put radio data into the table
        ContentValues values = new CategoryDataBaseBuilder().deconstruct(radio);
        String[] whereArgs = {radio.getId() + ""};
        int rowCount = db.update(DatabaseHelper.TABLE_CATEGORY, values, "id=?", whereArgs);
        if (rowCount == 0) {
            db.insert(DatabaseHelper.TABLE_CATEGORY, null, values);
        }
        db.close();
    }

    public Category getCategoryById(int categroyId) {
        Category radio = null;
        SQLiteDatabase db = getDb();
        String[] whereArgs = {categroyId + ""};
        String[] columns = {"id", "cateName", "volCount", "parentId"};
        Cursor query = db.query(DatabaseHelper.TABLE_CATEGORY, columns, "id=?", whereArgs, null, null, null,
                "");

        if (query != null) {
            query.moveToFirst();
            while (!query.isAfterLast()) {
                radio = new CategoryDataBaseBuilder().build(query);
                break;
            }
            query.close();
        }
        db.close();
        return radio;
    }

    public ArrayList<Category> getCategroyList(int parentId) {
        ArrayList<Category> radios = new ArrayList<Category>();
        SQLiteDatabase db = getDb();
        String[] whereArgs = {parentId + ""};
        String[] columns = {"id", "cateName", "volCount", "parentId"};
        Cursor query = db.query(DatabaseHelper.TABLE_CATEGORY, columns, "parentId=?", whereArgs, null, null, "id ASC",
                "");

        if (query != null) {
            query.moveToFirst();
            while (!query.isAfterLast()) {
                Category radio = new CategoryDataBaseBuilder().build(query);
                radios.add(radio);
                query.moveToNext();
            }
            query.close();
        }
        db.close();
        return radios;
    }

    public void replaceNewTable(String filePath, List<CategoryBean> list) {
        long start = TimeUtils.getNow();
        SQLiteDatabase db = getDb();
        db.beginTransaction();
        //先清空表数据
        String sql = "delete from category";
        db.execSQL(sql);
        //先插入第一层目录数据
        int where = 0;
        List<String> topList = FileUtil.getFileFolderName(filePath);
        where = topList.size();
        for (int i = 1; i <= topList.size(); i++) {
            ContentValues values = new ContentValues();
            //开始添加第一条数据

            values.put("id", i);
            values.put("cateName", CharUtils.removeHead(topList.get(i - 1)));
            values.put("volCount", 0);
            values.put("parentId", 0);
            db.insert(DatabaseHelper.TABLE_CATEGORY, null, values);//插入第一条数据
            List<String> secondList = FileUtil.getFileFolderName(filePath + "/" + topList.get(i - 1));
            for (int j = 1; j <= secondList.size(); j++) {//传出id与地址
                where++;
                list.add(new CategoryBean(String.valueOf(where), filePath + "/" + topList.get(i - 1) + "/" + secondList.get(j - 1), String.valueOf(i)));
                ContentValues contentValues = new ContentValues();
                //开始添加第一条数据
                contentValues.put("id", where);
                contentValues.put("cateName", CharUtils.removeHead(secondList.get(j - 1)));
                contentValues.put("volCount", 0);
                contentValues.put("parentId", i);
                db.insert(DatabaseHelper.TABLE_CATEGORY, null, contentValues);//插入第一条数据
            }
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        long end = TimeUtils.getNow();
        long diff = TimeUtils.diffTime(start, end);
    }
}
