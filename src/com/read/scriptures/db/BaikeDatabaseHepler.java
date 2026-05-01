package com.read.scriptures.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ProgressBar;

import com.read.scriptures.bean.CategoryBean;
import com.read.scriptures.model.Bookmark;
import com.read.scriptures.util.CharUtils;
import com.read.scriptures.util.FileUtil;
import com.read.scriptures.util.SearchTextUtil;
import com.read.scriptures.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author lim
 * @Description: 数据库Bookmark表 hepler
 * @mail lgmshare@gmail.com
 * @date 2014年6月25日 上午10:17:57
 */
public class BaikeDatabaseHepler {


    private DatabaseHelper mDatabaseHelper;

    public BaikeDatabaseHepler(Context context) {
         DatabaseContext dbContext = new DatabaseContext(context, "test");
        mDatabaseHelper = new DatabaseHelper(dbContext);
    }

    /**
     * 获取SQLiteDatabase实例
     */
    private SQLiteDatabase getDb() {
        return mDatabaseHelper.getWritableDatabase();
    }

    public void replaceNewTable(List<CategoryBean> list){
        SQLiteDatabase db = getDb();
        db.beginTransaction();
        //先清空表数据
        String sql = "delete from baike";
        db.execSQL(sql);
        //先插入第一层目录数据
        int where = 0;
        for (int i = 1;i <= list.size();i++){
            List<String> bookList = FileUtil.getFileFolderNameOrderByChinese(list.get(i-1).getPath());
            for (int j = 1;j <= bookList.size();j++){
                where++;
                ContentValues values = new ContentValues();
                //开始添加第一条数据
                values.put("id",where);
                values.put("name", bookList.get(j-1).replaceAll(".txt",""));
                values.put("indexId",where);
                values.put("content",FileUtil.readTxt(list.get(i-1).getPath()+"/"+bookList.get(j-1),"utf-8"));
                values.put("categoryId",list.get(i-1).getId());
                values.put("cateName",list.get(i-1).getParentId());
                db.insert(DatabaseHelper.TABLE_BAIKE,null,values);//插入第一条数据
            }
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public long getAllBaikeCount(){
        long count = 0;
        SQLiteDatabase db = getDb();
        db.beginTransaction();
        //先清空表数据
        String sql = "select count(*) from baike";
        Cursor cur = db.rawQuery(sql, null);
        //先插入第一层目录数据
        cur.moveToFirst();
        count = cur.getLong(0);
        cur.close();
        db.setTransactionSuccessful();
        db.endTransaction();
        return count;
    }

    public List<Bookmark> selectKeywordByTitle(String keyword, ProgressBar progressBar, String where){
        List<Bookmark> bookmarkList = new ArrayList<Bookmark>();
        SQLiteDatabase db = getDb();
        db.beginTransaction();
        //先清空表数据
        String sql = "select * from baike where name like '%"+keyword+"%' and cateName like '%"+where+"%'";
        Cursor cur = db.rawQuery(sql, null);
        int length = cur.getCount();
        progressBar.setMax(length);
        progressBar.setProgress(0);
        //先插入第一层目录数据
        int position = 0;
        while(cur.moveToNext())
        {
            //			String book_id = cur.getString(cur.getColumnIndex("BOOK_ID"));
            int id = cur.getInt(cur.getColumnIndex("id"));
            String name = cur.getString(cur.getColumnIndex("name"));
            int indexId = cur.getInt(cur.getColumnIndex("indexId"));
            String content = cur.getString(cur.getColumnIndex("content"));
            int categoryId = cur.getInt(cur.getColumnIndex("categoryId"));
            String cateName = cur.getString(cur.getColumnIndex("cateName"));

            position++;
            String macthLine = SearchTextUtil.textMacth(name, keyword);
            Bookmark point = new Bookmark();
            point.setChapterName(macthLine);
            point.setVolumeId(categoryId);
            point.setVolumeName(cateName);
            point.setIndex(position);
            point.setId(id);
            point.setContent(content);
            point.setType(1);
            bookmarkList.add(point);

            progressBar.setProgress(position);
        }
        cur.close();
        db.setTransactionSuccessful();
        db.endTransaction();
        return bookmarkList;
    }

    public List<Bookmark> selectKeywordByContent(String keyword,ProgressBar progressBar,String where){
        List<Bookmark> bookmarkList = new ArrayList<Bookmark>();
        SQLiteDatabase db = getDb();
        db.beginTransaction();
        //先清空表数据
        String sql = "select * from baike where content like '%"+keyword+"%' and cateName like '%"+where+"%'";
        Cursor cur = db.rawQuery(sql, null);
        int length = cur.getCount();
        progressBar.setMax(length);
        progressBar.setProgress(0);
        //先插入第一层目录数据
        int len = 0;
        while(cur.moveToNext())
        {
            len++;
            //			String book_id = cur.getString(cur.getColumnIndex("BOOK_ID"));
            int id = cur.getInt(cur.getColumnIndex("id"));
            String name = cur.getString(cur.getColumnIndex("name"));
            int indexId = cur.getInt(cur.getColumnIndex("indexId"));
            String content = cur.getString(cur.getColumnIndex("content"));
            int categoryId = cur.getInt(cur.getColumnIndex("categoryId"));
            String cateName = cur.getString(cur.getColumnIndex("cateName"));
            int position = 0;
            List<String> contentList = new ArrayList<String>(Arrays.asList(content.split("\n")));
            for (String line : contentList) {
                if (!line.trim().equals("\n") && !line.trim().equals("\n\r") &&
                        StringUtil.isNotEmpty(line.trim())) {
                    position++;
                    String macthLine = SearchTextUtil.textMacth(line, keyword);
                    if (macthLine != null) {
                        Bookmark point = new Bookmark();
                        point.setChapterName(CharUtils.getShowName(name));
                        point.setVolumeId(categoryId);
                        point.setVolumeName(cateName);
                        point.setIndex(position);
                        point.setId(id);
                        point.setContent(macthLine);
                        point.setType(1);
                        bookmarkList.add(point);
                    }
                }
            }
            progressBar.setProgress(len);
        }
        cur.close();
        db.setTransactionSuccessful();
        db.endTransaction();
        return bookmarkList;
    }
}
