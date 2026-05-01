package com.read.scriptures.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.read.scriptures.bean.CategoryBean;
import com.read.scriptures.bean.VolumeBean;
import com.read.scriptures.model.Category;
import com.read.scriptures.model.Volume;
import com.read.scriptures.util.CharUtils;
import com.read.scriptures.util.FileUtil;
import com.read.scriptures.util.TimeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lim
 * @Description: 数据库Volume表 hepler
 * @mail lgmshare@gmail.com
 * @date 2014年6月25日 上午10:17:57
 */
public class VolumeDatabaseHepler {

    private DatabaseHelper mDatabaseHelper;

    public VolumeDatabaseHepler(Context context) {
        DatabaseContext dbContext = new DatabaseContext(context, "test");
        mDatabaseHelper = new DatabaseHelper(dbContext);
    }

    /**
     * 获取SQLiteDatabase实例
     */
    private SQLiteDatabase getDb() {
        return mDatabaseHelper.getWritableDatabase();
    }

    public void addVolume(Volume radio) {
        SQLiteDatabase db = getDb();
        // put radio data into the table
        ContentValues values = new VolumeDataBaseBuilder().deconstruct(radio);
        String[] whereArgs = { "" + radio.getId() };
        int rowCount = db.update(DatabaseHelper.TABLE_VOLUME, values, "id=?", whereArgs);
        if (rowCount == 0) {
            db.insert(DatabaseHelper.TABLE_VOLUME, null, values);
        }
        db.close();
    }

    public ArrayList<Volume> getVolumes() {
        ArrayList<Volume> volumes = new ArrayList<Volume>();
        SQLiteDatabase db = getDb();
        String[] columns = { "id", "volName", "chpCount", "categoryId", "updateTime" };
        Cursor query = db.query(DatabaseHelper.TABLE_VOLUME, columns, "", null, null, null, "", "");

        if (query != null) {
            query.moveToFirst();
            while (!query.isAfterLast()) {
                Volume volume = new VolumeDataBaseBuilder().build(query);
                volumes.add(volume);
                query.moveToNext();
            }
            query.close();
        }
        db.close();
        return volumes;
    }

    public ArrayList<Volume> getVolumeById(int id) {
        ArrayList<Volume> volumes = new ArrayList<Volume>();
        SQLiteDatabase db = getDb();
        String[] columns = { "id", "volName", "chpCount", "categoryId", "updateTime" };
        String[] whereArgs = { id + "" };

        Cursor query = db.query(DatabaseHelper.TABLE_VOLUME, columns, " id=?", whereArgs, null, null, "", "");
        if (query != null) {
            query.moveToFirst();
            while (!query.isAfterLast()) {
                Volume volume = new VolumeDataBaseBuilder().build(query);
                volumes.add(volume);
                query.moveToNext();
            }
            query.close();
        }
        db.close();
        return volumes;
    }

    public ArrayList<Volume> getVolumes(int categoryId) {
        ArrayList<Volume> volumes = new ArrayList<Volume>();
        SQLiteDatabase db = getDb();
//        db.beginTransaction();
        String[] columns = { "id", "volName", "chpCount", "categoryId", "updateTime" };
        String[] whereArgs = { categoryId + "" };
        Cursor query = db.query(DatabaseHelper.TABLE_VOLUME, columns, "categoryId=?", whereArgs, null, null, "id ASC", "");
        if (query != null) {
            query.moveToFirst();
            while (!query.isAfterLast()) {
                Volume volume = new VolumeDataBaseBuilder().build(query);
                volumes.add(volume);
                query.moveToNext();
            }
            query.close();
        }
//        db.setTransactionSuccessful();
//        db.endTransaction();
        db.close();

        return volumes;
    }



    public ArrayList<Volume> getVolumesByName(String keyword) {
        ArrayList<Volume> volumes = new ArrayList<Volume>();
        SQLiteDatabase db = getDb();
        String[] columns = { "id", "volName", "chpCount", "categoryId", "updateTime" };
        Cursor query = db.query(DatabaseHelper.TABLE_VOLUME, columns, "volName like '%" + keyword + "%'", null, null,
                null, "", "");
        if (query != null) {
            query.moveToFirst();
            while (!query.isAfterLast()) {
                Volume volume = new VolumeDataBaseBuilder().build(query);
                volumes.add(volume);
                query.moveToNext();
            }
            query.close();
        }
        db.close();
        return volumes;
    }

    public ArrayList<Volume> getVolumesByNameAndRoot(String keyword, Category mSearchRoot) {
        ArrayList<Volume> volumes = new ArrayList<Volume>();
        SQLiteDatabase db = getDb();
        String sql = "select * from volume where volName like '%" + keyword + "%' and categoryId like '"+mSearchRoot.getId()+"'";
        Cursor query = db.rawQuery(sql, null);
//        String[] columns = { "id", "volName", "chpCount", "categoryId", "updateTime" };
//        Cursor query = db.query(DatabaseHelper.TABLE_VOLUME, columns, "volName like '%" + keyword + "%'", null, null,
//                null, "", "");
        if (query != null) {
            query.moveToFirst();
            while (!query.isAfterLast()) {
                Volume volume = new VolumeDataBaseBuilder().build(query);
                volumes.add(volume);
                query.moveToNext();
            }
            query.close();
        }
        db.close();
        return volumes;
    }
    /**
     * 根据类别获取书籍
     *
     * @param list
     * @return
     */
    public ArrayList<Volume> getVolumes(List<Category> list) {
        ArrayList<Volume> volumes = new ArrayList<Volume>();
        SQLiteDatabase db = getDb();
        String[] columns = { "id", "volName", "chpCount", "categoryId", "updateTime" };
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i).getId());
            if (i < list.size() - 1) {
                sb.append(",");
            }
        }
        Cursor query = db.query(DatabaseHelper.TABLE_VOLUME, columns, "categoryId in (" + sb.toString() + ")", null,
                null, null, "", "");
        if (query != null) {
            query.moveToFirst();
            while (!query.isAfterLast()) {
                Volume volume = new VolumeDataBaseBuilder().build(query);
                volumes.add(volume);
                query.moveToNext();
            }
            query.close();
        }
        db.close();
        return volumes;
    }

    public void replaceNewTable(List<CategoryBean> list, List<VolumeBean> volumeBeanList ) {//
        long start = TimeUtils.getNow();
        SQLiteDatabase db = getDb();
        db.beginTransaction();
        //先清空表数据
        String sql = "delete from volume";
        db.execSQL(sql);
        int where = 0;
        //先获取第一层目录数据
        for (int i = 1; i <= list.size(); i++) {
            List<String> book = FileUtil.getFileFolderName(list.get(i-1).getPath());
            for (int j = 1;j <= book.size();j++){
                where++;
                volumeBeanList.add(new VolumeBean(String.valueOf(where),list.get(i-1).getPath()+"/"+book.get(j-1),list.get(i-1).getId(),list.get(i-1).getParentId()));
                ContentValues contentValues = new ContentValues();
                //开始添加第一条数据
                contentValues.put("id",where);
                contentValues.put("volName", CharUtils.removeHead(book.get(j-1)));
                contentValues.put("chpCount",FileUtil.getFileSize(list.get(i-1).getPath()+"/"+book.get(j-1)));
                contentValues.put("categoryId",list.get(i-1).getId());
                contentValues.put("updateTime",TimeUtils.getDate());
                db.insert(DatabaseHelper.TABLE_VOLUME,null,contentValues);//插入第一条数据
            }
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        long end = TimeUtils.getNow();
        long diff = TimeUtils.diffTime(start, end);
    }

    //根据书籍id获取对应分类的一级分类id
    public int getCategoryLeve1IdByVolumeID(int id) {
        int categoryId = -1;
        ArrayList<Volume> volumes = getVolumeById(id);

        if (volumes !=null && !volumes.isEmpty()) {
            SQLiteDatabase db = getDb();
            String[] columns = {"id", "cateName", "volCount", "parentId"};
            String[] whereArgs = {volumes.get(0).getCategoryId() + ""};

            Cursor query = db.query(DatabaseHelper.TABLE_CATEGORY, columns, " id=?", whereArgs, null, null, "", "");
            if (query != null) {
                query.moveToFirst();
                while (!query.isAfterLast()) {
                    Category category = new CategoryDataBaseBuilder().build(query);
                    categoryId = category.getParentId();
                    break;
                }
                query.close();
            }
            db.close();
        }
        return categoryId;
    }

}
