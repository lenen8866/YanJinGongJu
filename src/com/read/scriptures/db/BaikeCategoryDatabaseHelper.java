//package com.read.scriptures.db;
//
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.sqlite.SQLiteDatabase;
//import android.util.Log;
//
//import com.read.scriptures.bean.CategoryBean;
//import com.read.scriptures.util.CharUtils;
//import com.read.scriptures.util.FileUtil;
//import com.read.scriptures.util.TimeUtils;
//
//import java.util.List;
//
///**
// * @author lim
// * @Description: 数据库category表 hepler
// * @mail lgmshare@gmail.com
// * @date 2014年6月25日 上午10:17:57
// */
//public class BaikeCategoryDatabaseHelper {
//
//    private SQLiteDataProxy mDatabaseHelper;
//
//    public DatabaseHelper getmDatabaseHelper() {
//        return mDatabaseHelper;
//    }
//
//    public void setmDatabaseHelper(DatabaseHelper mDatabaseHelper) {
//        this.mDatabaseHelper = mDatabaseHelper;
//    }
//
//    public BaikeCategoryDatabaseHelper(Context context) {
//        DatabaseContext dbContext = new DatabaseContext(context, "test");
//        mDatabaseHelper = new DatabaseHelper(dbContext);
//    }
//
//    /**
//     * 获取SQLiteDatabase实例
//     */
//    private SQLiteDatabase getDb() {
//        return mDatabaseHelper.getWritableDatabase();
//    }
//
//    public void replaceNewTable(String filePath,List<CategoryBean> list){
//        SQLiteDatabase db = getDb();
//        db.beginTransaction();
//        //先清空表数据
//        String sql = "delete from baike_category";
//        db.execSQL(sql);
//        //先插入第一层目录数据
//        List<String> topList = FileUtil.getFileFolderName(filePath);
//        for (int i = 1;i <= topList.size();i++){
//            list.add(new CategoryBean(String.valueOf(i),filePath+"/"+topList.get(i-1),topList.get(i-1)));
//            ContentValues values = new ContentValues();
//            //开始添加第一条数据
//            values.put("id",i);
//            values.put("cateName", CharUtils.removeHead(topList.get(i-1)));
//            Log.e("asdasdadadsdasd", "replaceNewTable: baike");
//            values.put("volCount",FileUtil.getFileSize(filePath+"/"+topList.get(i-1)));
//            values.put("parentId",0);
//            values.put("updateTime", TimeUtils.getDate());
//            db.insert(DatabaseHelper.TABLE_BAIKE_CATEGORY,null,values);//插入第一条数据
//        }
//        db.setTransactionSuccessful();
//        db.endTransaction();
//    }
//}
