package com.read.scriptures.db;

import android.content.ContentValues;
import android.database.Cursor;

import com.read.scriptures.model.Chapter;

/**
 * @author lim
 * @ClassName: UserDataBaseBuilder
 * @Package com.lgmshare.goodfoodm.db
 * @Description: User数据对象构建类
 * @mail lgmshare@gmail.com
 * @date 2014年6月25日 上午10:17:24
 */
public class ChapterDataBaseBuilder extends DatabaseBuilder<Chapter> {

    private final String COLUMId = "id";
    private final String COLUM_ORDERID = "indexId";
    private final String COLUM_CHPNAME = "name";
    private final String COLUM_VOLID = "volumeId";
    private final String COLUM_VOLNAME = "volumeName";
    private final String COLUM_CHPCount = "chpCount";
    private final String COLUM_CONTENT = "content";
    private final String COLUM_CATEGORY_ID = "categoryId";
    private final String COLUM_PARENT_ID = "parentId";

    @Override
    public Chapter build(Cursor c) {
        int columnId = c.getColumnIndex(COLUMId);
        int colum_chpname = c.getColumnIndex(COLUM_CHPNAME);
        int colum_orderid = c.getColumnIndex(COLUM_ORDERID);
        int colum_volid = c.getColumnIndex(COLUM_VOLID);
        int colum_content = c.getColumnIndex(COLUM_CONTENT);
        int colum_category_id = c.getColumnIndex(COLUM_CATEGORY_ID);
        int colum_parent_id = c.getColumnIndex(COLUM_PARENT_ID);

        Chapter user = new Chapter();
        user.setId(c.getString(columnId));
        user.setName(c.getString(colum_chpname));
        user.setIndexId(c.getInt(colum_orderid));
        user.setVolumeId(c.getInt(colum_volid));
        user.setContent(c.getString(colum_content));
        user.setCategoryId(c.getInt(colum_category_id));
        user.setParentId(c.getInt(colum_parent_id));

        return user;
    }

    public Chapter buildVolumeName(Cursor c) {
        int columnId = c.getColumnIndex(COLUMId);
        int colum_chpname = c.getColumnIndex(COLUM_CHPNAME);
        int colum_orderid = c.getColumnIndex(COLUM_ORDERID);
        int colum_orderName = c.getColumnIndex(COLUM_VOLNAME);
        int colum_volid = c.getColumnIndex(COLUM_VOLID);
        int colum_chpCount = c.getColumnIndex(COLUM_CHPCount);
        int colum_content = c.getColumnIndex(COLUM_CONTENT);
        int colum_category_id = c.getColumnIndex(COLUM_CATEGORY_ID);
        int colum_parent_id = c.getColumnIndex(COLUM_PARENT_ID);

        Chapter user = new Chapter();
        user.setId(c.getString(columnId));
        user.setName(c.getString(colum_chpname));
        user.setIndexId(c.getInt(colum_orderid));
        user.setVolumeId(c.getInt(colum_volid));
        user.setVolumeName(c.getString(colum_orderName));
        user.setChapterCount(c.getInt(colum_chpCount));
        user.setContent(c.getString(colum_content));
        user.setCategoryId(c.getInt(colum_category_id));
        user.setParentId(c.getInt(colum_parent_id));
        return user;
    }

    @Override
    public ContentValues deconstruct(Chapter user) {
        ContentValues values = new ContentValues();
        values.put(COLUMId, user.getId());
        values.put(COLUM_ORDERID, user.getIndexId());
        values.put(COLUM_CHPNAME, user.getName());
        values.put(COLUM_VOLID, user.getVolumeId());
        values.put(COLUM_CONTENT, user.getContent());
        values.put(COLUM_CATEGORY_ID, user.getCategoryId());
        values.put(COLUM_PARENT_ID, user.getParentId());
        return values;
    }

}
