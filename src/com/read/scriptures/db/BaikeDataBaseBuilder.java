package com.read.scriptures.db;

import android.content.ContentValues;
import android.database.Cursor;

import com.read.scriptures.model.Baike;

/**
 * @author lim
 * @ClassName: UserDataBaseBuilder
 * @Package com.lgmshare.goodfoodm.db
 * @Description: User数据对象构建类
 * @mail lgmshare@gmail.com
 * @date 2014年6月25日 上午10:17:24
 */
public class BaikeDataBaseBuilder extends DatabaseBuilder<Baike> {

    private final String COLUMId = "id";
    private final String COLUM_INDEXID = "indexId";
    private final String COLUM_NAME = "name";
    private final String COLUM_CATEGORYID = "categoryId";
    private final String COLUM_CATENAME = "cateName";
    private final String COLUM_CONTENT = "content";

    @Override
    public Baike build(Cursor c) {
        int columnId = c.getColumnIndex(COLUMId);
        int colum_name = c.getColumnIndex(COLUM_NAME);
        int colum_indexId = c.getColumnIndex(COLUM_INDEXID);
        int colum_categoryId = c.getColumnIndex(COLUM_CATEGORYID);
        int colum_cateName = c.getColumnIndex(COLUM_CATENAME);
        int colum_content = c.getColumnIndex(COLUM_CONTENT);

        Baike user = new Baike();
        user.setId(c.getInt(columnId));
        user.setName(c.getString(colum_name));
        user.setIndexId(c.getInt(colum_indexId));
        user.setCategoryId(c.getInt(colum_categoryId));
        user.setCateName(c.getString(colum_cateName));
        user.setContent(c.getString(colum_content));
        return user;
    }


    @Override
    public ContentValues deconstruct(Baike user) {
        ContentValues values = new ContentValues();
        values.put(COLUMId, user.getId());
        values.put(COLUM_INDEXID, user.getIndexId());
        values.put(COLUM_NAME, user.getName());
        values.put(COLUM_CATEGORYID, user.getCategoryId());
        values.put(COLUM_CATENAME, user.getCateName());
        values.put(COLUM_CONTENT, user.getContent());
        return values;
    }

}
