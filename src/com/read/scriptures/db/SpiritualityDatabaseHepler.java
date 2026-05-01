package com.read.scriptures.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.read.scriptures.model.Spirituality;
import com.read.scriptures.util.CharUtils;
import com.read.scriptures.util.FileUtil;
import com.read.scriptures.util.TimeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lim
 * @Description: 数据库Bookmark表 hepler
 * @mail lgmshare@gmail.com
 * @date 2014年6月25日 上午10:17:57
 */
public class SpiritualityDatabaseHepler {

    private final String COLUM_ID = "id";
    private final String COLUM_DAYTIME = "daytime";
    private final String COLUM_BOOK = "book";
    private final String COLUM_NAME = "name";
    private final String COLUM_PARENT = "parent";
    private DatabaseHelper mDatabaseHelper;

    public SpiritualityDatabaseHepler(Context context) {
        DatabaseContext dbContext = new DatabaseContext(context, "test");
        mDatabaseHelper = new DatabaseHelper(dbContext);
    }

    /**
     * 获取SQLiteDatabase实例
     */
    private synchronized SQLiteDatabase getDb() {
        return mDatabaseHelper.getWritableDatabase();
    }

    public void addSpirituality(Spirituality bookmark) {
        SQLiteDatabase db = getDb();
        // put radio data into the table
        ContentValues values = new SpiritualityDataBaseBuilder().deconstruct(bookmark);
        db.insert(DatabaseHelper.TABLE_SPIRITUALITY, null, values);
        db.close();
    }

    public void addSpirituality(List<Spirituality> bookmarks) {
        SQLiteDatabase db = getDb();
        // put radio data into the table
        int length = bookmarks.size();
        for (int i = 0; i < length; i++) {
            ContentValues values = new SpiritualityDataBaseBuilder().deconstruct(bookmarks.get(i));
            db.insert(DatabaseHelper.TABLE_SPIRITUALITY, null, values);
        }
        db.close();
    }

    public ArrayList<Spirituality> getSpiritualityList() {
        ArrayList<Spirituality> arrayList = new ArrayList<Spirituality>();
        SQLiteDatabase db = getDb();
        String[] columns = {COLUM_ID, COLUM_DAYTIME, COLUM_BOOK, COLUM_NAME};
        Cursor query = db.query(DatabaseHelper.TABLE_SPIRITUALITY, columns, "", null, null, null, "id DESC", "");
        if (query != null) {
            query.moveToFirst();
            while (!query.isAfterLast()) {
                Spirituality bookmark = new SpiritualityDataBaseBuilder().build(query);
                arrayList.add(bookmark);
                query.moveToNext();
            }
            query.close();
        }
        db.close();
        return arrayList;
    }

    public ArrayList<Spirituality> getSpiritualityById(String volumeId) {
        ArrayList<Spirituality> arrayList = new ArrayList<Spirituality>();
        SQLiteDatabase db = getDb();
        String[] columns = {COLUM_ID, COLUM_DAYTIME, COLUM_BOOK, COLUM_NAME};
        String[] whereArgs = {volumeId};
        Cursor query = db.query(DatabaseHelper.TABLE_SPIRITUALITY, columns, "id=?", whereArgs, null, null, "id DESC",
                "");
        if (query != null) {
            query.moveToFirst();
            while (!query.isAfterLast()) {
                Spirituality bookmark = new SpiritualityDataBaseBuilder().build(query);
                arrayList.add(bookmark);
                query.moveToNext();
            }
            query.close();
        }
        db.close();
        return arrayList;
    }

    public Spirituality getSpiritualityById(String book, String daytime) {
        SQLiteDatabase db = getDb();
        Spirituality bookmark = null;
        String[] columns = {COLUM_ID, COLUM_DAYTIME, COLUM_BOOK, COLUM_NAME};
        String[] whereArgs = {book, daytime};
        Cursor query = db.query(DatabaseHelper.TABLE_SPIRITUALITY, columns, "book=? and daytime=?", whereArgs, null,
                null, "id DESC", "");
        if (query != null) {
            query.moveToFirst();
            while (!query.isAfterLast()) {
                bookmark = new SpiritualityDataBaseBuilder().build(query);
                query.moveToNext();
            }
            query.close();
        }
        db.close();
        return bookmark;
    }

//    public ArrayList<Spirituality> getSpiritualityListByDaytime(String daytime,String type) {
//        ArrayList<Spirituality> arrayList = new ArrayList<Spirituality>();
//        SQLiteDatabase db = getDb();
//        String[] columns = {  COLUM_ID, COLUM_DAYTIME, COLUM_BOOK, COLUM_NAME,COLUM_PARENT };
//        String[] whereArgs = { daytime, '%'+type+'%' };
//        Cursor query = db.query(DatabaseHelper.TABLE_SPIRITUALITY, columns, "daytime like ? and parent like ?", whereArgs, null, null,
//                "id DESC", "");
//        if (query != null) {
//            query.moveToFirst();
//            while (!query.isAfterLast()) {
//                Spirituality bookmark = new SpiritualityDataBaseBuilder().build(query);
//                arrayList.add(bookmark);
//                query.moveToNext();
//            }
//            query.close();
//        }
//        db.close();
//        return arrayList;
//    }

    public ArrayList<Spirituality> getSpiritualityListByDaytime(String daytime, String type) {
        ArrayList<Spirituality> arrayList = new ArrayList<Spirituality>();
        SQLiteDatabase db = getDb();
//        db.beginTransaction();
        //先清空表数据
        String sql = "select * from spirituality where daytime like '" + daytime + "' and parent like '%" + type + "%'";
        Cursor cur = db.rawQuery(sql, null);
        //先插入第一层目录数据
        while (cur.moveToNext()) {
            int id = cur.getInt(cur.getColumnIndex("id"));
            String time = cur.getString(cur.getColumnIndex("daytime"));
            String book = cur.getString(cur.getColumnIndex("book"));
            String name = cur.getString(cur.getColumnIndex("name"));
            String content = cur.getString(cur.getColumnIndex("content"));
            String patrent = cur.getString(cur.getColumnIndex("parent"));
            arrayList.add(new Spirituality(id, time, book, name, content, patrent));
        }
        cur.close();
//        db.setTransactionSuccessful();
//        db.endTransaction();
        return arrayList;
    }

    public ArrayList<Spirituality> getSpiritualityListByWeektime(String daytime, String type) {
        ArrayList<Spirituality> arrayList = new ArrayList<Spirituality>();
        ArrayList<Spirituality> result = new ArrayList<Spirituality>();
        SQLiteDatabase db = getDb();
//        db.beginTransaction();
        //先清空表数据
        String sql = "select * from spirituality where parent like '%" + type + "%'";
        Cursor cur = db.rawQuery(sql, null);
        //先插入第一层目录数据
        while (cur.moveToNext()) {
            int id = cur.getInt(cur.getColumnIndex("id"));
            String time = cur.getString(cur.getColumnIndex("daytime"));
            String book = cur.getString(cur.getColumnIndex("book"));
            String name = cur.getString(cur.getColumnIndex("name"));
            String content = cur.getString(cur.getColumnIndex("content"));
            String patrent = cur.getString(cur.getColumnIndex("parent"));
            arrayList.add(new Spirituality(id, time, book, name, content, patrent));
        }
        cur.close();
//        db.setTransactionSuccessful();
//        db.endTransaction();
        for (Spirituality spirituality : arrayList) {
            if (TimeUtils.getDateWeekSeq(TimeUtils.getYearStr() + "年" + spirituality.getDaytime()) == TimeUtils.getDateWeekSeq(daytime)) {
                result.add(spirituality);
            }
        }
        return result;
    }

    public ArrayList<Spirituality> getSpiritualityListByYear(String daytime, String type) {
        ArrayList<Spirituality> arrayList = new ArrayList<Spirituality>();
        SQLiteDatabase db = getDb();
//        db.beginTransaction();
        //先清空表数据
        String sql = "select * from spirituality where parent like '%" + type + "%'";
        Cursor cur = db.rawQuery(sql, null);
        //先插入第一层目录数据
        while (cur.moveToNext()) {
            int id = cur.getInt(cur.getColumnIndex("id"));
            String time = cur.getString(cur.getColumnIndex("daytime"));
            String book = cur.getString(cur.getColumnIndex("book"));
            String name = cur.getString(cur.getColumnIndex("name"));
            String content = cur.getString(cur.getColumnIndex("content"));
            String patrent = cur.getString(cur.getColumnIndex("parent"));
            arrayList.add(new Spirituality(id, time, book, name, content, patrent));
        }
        cur.close();
//        db.setTransactionSuccessful();
//        db.endTransaction();
        return arrayList;
    }

    public ArrayList<Spirituality> getSpiritualityList(String nookname) {
        ArrayList<Spirituality> arrayList = new ArrayList<Spirituality>();
        SQLiteDatabase db = getDb();
//        db.beginTransaction();
        //先清空表数据
        String sql = "select * from spirituality where book like '%" + nookname + "%'";
        Cursor cur = db.rawQuery(sql, null);
        //先插入第一层目录数据
        while (cur.moveToNext()) {
            int id = cur.getInt(cur.getColumnIndex("id"));
            String time = cur.getString(cur.getColumnIndex("daytime"));
            String book = cur.getString(cur.getColumnIndex("book"));
            String name = cur.getString(cur.getColumnIndex("name"));
            String content = cur.getString(cur.getColumnIndex("content"));
            String patrent = cur.getString(cur.getColumnIndex("parent"));
            arrayList.add(new Spirituality(id, time, book, name, content, patrent));
        }
        cur.close();
//        db.setTransactionSuccessful();
//        db.endTransaction();
        return TimeUtils.sortData(arrayList);
    }

    public void deleteById(String id) {
        SQLiteDatabase db = getDb();
        String[] whereArgs = {id};
        db.delete(DatabaseHelper.TABLE_SPIRITUALITY, "id=?", whereArgs);
    }

    public void deleteAll() {
        SQLiteDatabase db = getDb();
        db.delete(DatabaseHelper.TABLE_SPIRITUALITY, null, null);
    }

    public void replaceNewTable(String filePath) {
        SQLiteDatabase db = getDb();
        db.beginTransaction();
        //先清空表数据
        String sql = "delete from spirituality";
        db.execSQL(sql);
        //先插入第一层目录数据
        int where = 0;
        List<String> topList = FileUtil.getFileFolderName(filePath);
        for (int i = 1; i <= topList.size(); i++) {
            List<String> secondList = FileUtil.getFileFolderName(filePath + "/" + topList.get(i - 1));
            for (int j = 1; j <= secondList.size(); j++) {
                List<String> bookList = FileUtil.getFileFolderName(filePath + "/" + topList.get(i - 1) + "/" + secondList.get(j - 1));
                for (int k = 1; k <= bookList.size(); k++) {
                    where++;
                    ContentValues contentValues = new ContentValues();
                    //开始添加第一条数据
                    String daytime = CharUtils.match("(^\\d+?月\\d+?日)", bookList.get(k - 1));
                    if (daytime == null)
                        daytime = "";
                    contentValues.put("id", where);
                    contentValues.put("daytime", daytime.replaceAll(String.valueOf((char) 12288), "").trim());
                    contentValues.put("book", CharUtils.removeHead(secondList.get(j - 1)));
                    contentValues.put("name", bookList.get(k - 1).replaceAll(daytime, "").replaceAll(".txt", "").replaceAll(String.valueOf((char) 12288), "").trim());
                    contentValues.put("parent", topList.get(i - 1));
                    contentValues.put("content", FileUtil.readTxt(filePath + "/" + topList.get(i - 1) + "/" + secondList.get(j - 1) + "/" + bookList.get(k - 1), "utf-8"));
                    db.insert(DatabaseHelper.TABLE_SPIRITUALITY, null, contentValues);//插入第一条数据
                }
            }
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }
}
