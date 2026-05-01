package com.read.scriptures.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.read.scriptures.model.SpiritualityCategory;
import com.read.scriptures.util.CharUtils;
import com.read.scriptures.util.FileUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lim
 * @Description: 数据库Bookmark表 hepler
 * @mail lgmshare@gmail.com
 * @date 2014年6月25日 上午10:17:57
 */
public class SpiritualityCategoryDatabaseHepler {

    private final String COLUMId = "id";
    private final String COLUM_CATE_NAME = "cateName";
    private final String COLUM_VOL_COUNT = "volCount";
    private final String COLUM_PARENT_ID = "parentId";
    private final String COLUM_UPDATE_TIME = "updateTime";
    private DatabaseHelper mDatabaseHelper;

    public SpiritualityCategoryDatabaseHepler(Context context) {
        DatabaseContext dbContext = new DatabaseContext(context, "test");
        mDatabaseHelper = new DatabaseHelper(dbContext);
    }

    /**
     * 获取SQLiteDatabase实例
     */
    private SQLiteDatabase getDb() {
        return mDatabaseHelper.getWritableDatabase();
    }

    public void replaceNewTable(String filePath){
        SQLiteDatabase db = getDb();
        db.beginTransaction();
        //先清空表数据
        String sql = "delete from spirituality_category";
        db.execSQL(sql);
        //先插入第一层目录数据
        int where = 0;
        List<String> topList = FileUtil.getFileFolderName(filePath);
        for (int i = 1;i <= topList.size();i++){
            ContentValues contentValues = new ContentValues();
            contentValues.put("id",i);
            contentValues.put("cateName", CharUtils.removeHead(topList.get(i-1)));
            contentValues.put("volCount",0);
            contentValues.put("parentId",0);
            contentValues.put("updateTime",0);
            db.insert(DatabaseHelper.TABLE_SPIRITUALITY_CATEGORY,null,contentValues);//插入第一条数据
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public ArrayList<SpiritualityCategory> getSpiritualityCategoryList() {
        ArrayList<SpiritualityCategory> arrayList = new ArrayList<>();
        SQLiteDatabase db = getDb();
        String sql = "select * from spirituality_category order by id ASC";
        Cursor query = db.rawQuery(sql, null);
        if (query != null) {
            while(query.moveToNext())
            {
                int id = query.getInt(query.getColumnIndex("id"));
                String cateName = query.getString(query.getColumnIndex("cateName"));
                int volCount = query.getInt(query.getColumnIndex("volCount"));
                int parentId = query.getInt(query.getColumnIndex("parentId"));
                String updateTime = query.getString(query.getColumnIndex("updateTime"));
                arrayList.add(new SpiritualityCategory(id,cateName,volCount,parentId,updateTime));
            }
            query.close();
        }
        db.close();
        return arrayList;
//        String[] columns = { COLUMId, COLUM_CATE_NAME,COLUM_VOL_COUNT , COLUM_PARENT_ID, COLUM_UPDATE_TIME};
//        Cursor query = db.query(DatabaseHelper.TABLE_SPIRITUALITY_CATEGORY, columns, "", null, null, null, "id ASC", "");
//        if (query != null) {
//            query.moveToFirst();
//            while (!query.isAfterLast()) {
//                SpiritualityCategory bookmark = new SpiritualityCategoryDataBaseBuilder().build(query);
//                arrayList.add(bookmark);
//                query.moveToNext();
//            }
//            query.close();
//        }
//        db.close();
//        return arrayList;
    }
}
