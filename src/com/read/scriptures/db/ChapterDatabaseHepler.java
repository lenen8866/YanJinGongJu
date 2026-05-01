package com.read.scriptures.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.read.scriptures.bean.IntroBean;
import com.read.scriptures.model.Chapter;
import com.read.scriptures.model.Volume;
import com.read.scriptures.util.SearchTextUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author lim
 * @Description: 数据库Chapter表 hepler
 * @mail lgmshare@gmail.com
 * @date 2014年6月25日 上午10:17:57
 */
public class ChapterDatabaseHepler {

    private DatabaseHelper mDatabaseHelper;

    public DatabaseHelper getmDatabaseHelper() {
        return mDatabaseHelper;
    }

    public void setmDatabaseHelper(DatabaseHelper mDatabaseHelper) {
        this.mDatabaseHelper = mDatabaseHelper;
    }


    public ChapterDatabaseHepler(Context context) {
        DatabaseContext dbContext = new DatabaseContext(context.getApplicationContext(), "test");
        mDatabaseHelper = new DatabaseHelper(dbContext);
    }

    private SQLiteDatabase getDb() {
        return mDatabaseHelper.getWritableDatabase();
    }

    /**
     * 获取SQLiteDatabase实例-取处不包含注释的数据
     */
    public ArrayList<Chapter> getChapterList(int volumeId) {
        ArrayList<Chapter> chapters = new ArrayList<Chapter>();
        String[] columns = { "id", "indexId", "name", "volumeId", "content","categoryId","parentId" };
        String[] whereArgs = { "" + volumeId };
        SQLiteDatabase db = getDb();
        Cursor query = db.query(DatabaseHelper.TABLE_CHAPTER, columns, "volumeId=?", whereArgs, null, null,
                "indexId ASC", "");
        if (query != null) {
            query.moveToFirst();
            while (!query.isAfterLast()) {
                Chapter chapter = new ChapterDataBaseBuilder().build(query);
                chapters.add(chapter);
                query.moveToNext();
            }
            query.close();
        }
        db.close();

        int chapterIndex = 0;
        ArrayList<Chapter> trueChapters = new ArrayList<Chapter>();
        if(chapters.size()>0){
            for(Chapter chapter : chapters){
                if(!chapter.getName().contains("注释") && !chapter.getShowName().contains("jieshao")){
                    //注释和介绍排除
                    chapter.setChapterIndex(chapterIndex);
                    trueChapters.add(chapter);
                    chapterIndex ++;
                }
            }
        }
        return trueChapters;
    }

    /**
     * 获取SQLiteDatabase实例-取处包含注释的数据content
     */
    public LinkedHashMap<String,String> getChapterAnnotationList(int volumeId, String showName) {

        String content = "暂无注释";

        ArrayList<Chapter> chapters = new ArrayList<Chapter>();
        String[] columns = { "id", "indexId", "name", "volumeId", "content","categoryId","parentId" };
        String[] whereArgs = { "" + volumeId };
        SQLiteDatabase db = getDb();
        Cursor query = db.query(DatabaseHelper.TABLE_CHAPTER, columns, "volumeId=?", whereArgs, null, null,
                "indexId ASC", "");
        if (query != null) {
            query.moveToFirst();
            while (!query.isAfterLast()) {
                Chapter chapter = new ChapterDataBaseBuilder().build(query);
                chapters.add(chapter);
                query.moveToNext();
            }
            query.close();
        }
        db.close();

        LinkedHashMap<String,String> annMap = new LinkedHashMap<>();
        if(chapters.size()>0 ){
            for(Chapter chapter : chapters){
                String name = chapter.getName();
                if(name.contains("注释")){
                    String newName = name.substring(0,name.indexOf("【")).trim();
                    if(showName.equals(newName)){
                        name = name.replace(newName,"");
                        name = name.replace("【","");
                        name = name.replace("】","");
                        annMap.put(name,chapter.getContent());
                    }
                }
            }
        }
        return annMap;
    }


    public ArrayList<Chapter> getChapterList(List<Volume> list) {
        ArrayList<Chapter> chapters = new ArrayList<Chapter>();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i).getId());
            if (i < list.size() - 1) {
                sb.append(",");
            }
        }
        SQLiteDatabase db = getDb();
        Cursor query = db.rawQuery(
                "select c.id, c.indexId, c.name, c.volumeId,c.content, v.volName as volumeName, v.chpCount from "
                        + DatabaseHelper.TABLE_CHAPTER + " c, " + DatabaseHelper.TABLE_VOLUME
                        + " v where c.volumeId in (" + sb.toString() + ") " + "and c.volumeId=v.id",
                null);
        if (query != null) {
            query.moveToFirst();
            while (!query.isAfterLast()) {
                Chapter chapter = new ChapterDataBaseBuilder().buildVolumeName(query);
                chapters.add(chapter);
                query.moveToNext();
            }
            query.close();
        }
        db.close();
        return chapters;
    }

    public ArrayList<Chapter> getChaptersLikeName(String keyword) {
        ArrayList<Chapter> chapters = new ArrayList<Chapter>();
        SQLiteDatabase db = getDb();
        String[] columns = { "id", "indexId", "name", "volumeId,content" };
        Cursor query = db.query(DatabaseHelper.TABLE_CHAPTER, columns, "name like '%" + keyword + "%'", null, null,
                null, "", "");
        if (query != null) {
            query.moveToFirst();
            while (!query.isAfterLast()) {
                Chapter chapter = new ChapterDataBaseBuilder().build(query);
                chapters.add(chapter);
                query.moveToNext();
            }
            query.close();
        }
        db.close();
        return chapters;
    }

    public List<IntroBean> getIntroList() {
        try {
            SQLiteDatabase db = getDb();
            List<IntroBean> list = new ArrayList<>();
            String sql = "select * from chapter where name like '%jieshao%'";
            Cursor cur = db.rawQuery(sql, null);
            while (cur.moveToNext()) {
                int id = cur.getInt(cur.getColumnIndex("volumeId"));
                String intro = cur.getString(cur.getColumnIndex("content"));
                list.add(new IntroBean(id, intro));
            }
            cur.close();
            db.close();
            return list;
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public List<IntroBean> getIntroList(String volumeIds) {
        SQLiteDatabase db = getDb();
        List<IntroBean> list = new ArrayList<>();
//        String sql = "select * from chapter where name like '%jieshao%'" + " and volumeId in "+volumeIds+") " ;
        String sql = String.format("SELECT * FROM chapter where  name like '%%jieshao%%' and volumeId in (%s)",volumeIds);
        Cursor cur = db.rawQuery(sql, null);
        while(cur.moveToNext())
        {
            int id = cur.getInt(cur.getColumnIndex("volumeId"));
            String intro = cur.getString(cur.getColumnIndex("content"));
            list.add(new IntroBean(id,intro));
        }
        cur.close();
        db.close();
        return list;
    }

    public IntroBean getIntroInfoByVolumeId(int volumeId) {
        SQLiteDatabase db = getDb();
        IntroBean introBean = null;
        String sql = "select * from chapter where volumeId = "+volumeId+" and name like '%jieshao%'";
        Cursor query = db.rawQuery(sql, null);
        if (query != null) {
            while(query.moveToNext())
            {
                int id = query.getInt(query.getColumnIndex("volumeId"));
                String intro = query.getString(query.getColumnIndex("content"));
                introBean = new IntroBean(id,intro);
            }
            query.close();
        }
        db.close();
        return introBean;
    }

    /**
     * 搜索所有书籍章节
     *
     * @param keyword
     * @return
     */
    public ArrayList<Chapter> getChaptersLikeNameJoinVolume(String keyword,int parentId) {
        ArrayList<Chapter> chapters = new ArrayList<Chapter>();
        SQLiteDatabase db = getDb();
        String sql = "select * from "+DatabaseHelper.TABLE_CHAPTER+" where name like '%"+keyword+"%' and parentId = "+parentId;
        Cursor cur = db.rawQuery(sql, null);
        SearchTextUtil.searchByKeywordLoadFinish = true;
        while (cur.moveToNext()) {
            SearchTextUtil.searchByKeywordLoadFinish = false;
            Chapter chapter = new Chapter();
            chapter.setId(cur.getString(cur.getColumnIndex("id")));
            chapter.setIndexId(cur.getInt(cur.getColumnIndex("indexId")));
            chapter.setName(cur.getString(cur.getColumnIndex("name")));
            chapter.setVolumeId(cur.getInt(cur.getColumnIndex("volumeId")));
            chapter.setContent(cur.getString(cur.getColumnIndex("content")));
            chapter.setCategoryId(cur.getInt(cur.getColumnIndex("categoryId")));
            chapter.setParentId(cur.getInt(cur.getColumnIndex("parentId")));
            chapters.add(chapter);
        }
        cur.close();
        db.close();
        return chapters;
    }

    /**
     * 为chapter设置volumeName
     *
     * @return
     */
    public void setVolumeName(List<Chapter> chapters) {
        SQLiteDatabase db = getDb();
        db.beginTransaction();
        for (Chapter chapter:chapters){
            String sql = "select * from "+DatabaseHelper.TABLE_VOLUME+" where id = "+chapter.getVolumeId();
            Cursor cur = db.rawQuery(sql, null);
            while(cur.moveToNext())
            {
                chapter.setVolumeName(cur.getString(cur.getColumnIndex("volName")));
            }
            cur.close();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }
    /**
     * 搜索书籍下的关键字匹配
     *
     * @param voulmeId
     * @param keyword
     * @return
     */
    public ArrayList<Chapter> getChaptersByVolumeIdLikeName(String voulmeId, String keyword) {
        ArrayList<Chapter> chapters = new ArrayList<Chapter>();
        SQLiteDatabase db = getDb();
        Cursor query = db.rawQuery(
                "select c.id, c.indexId, c.name, c.volumeId,c.content,c.categoryId,c.parentId,v.volName as volumeName, v.chpCount from "
                        + DatabaseHelper.TABLE_CHAPTER + " c, " + DatabaseHelper.TABLE_VOLUME
                        + " v where c.name like '%" + keyword + "%' " + "and c.volumeId=v.id and c.volumeId="
                        + voulmeId,
                null);
        if (query != null) {
            query.moveToFirst();
            while (!query.isAfterLast()) {
                Chapter chapter = new ChapterDataBaseBuilder().buildVolumeName(query);
                chapters.add(chapter);
                query.moveToNext();
            }
            query.close();
        }
        db.close();
        return chapters;
    }
}
