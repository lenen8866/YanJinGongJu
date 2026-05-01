package com.read.scriptures.db;

import android.content.ContentValues;
import android.database.Cursor;

import com.read.scriptures.model.Bookmark;

/**
 * @author lim
 * @Description: Bookmark数据对象构建类
 * @mail lgmshare@gmail.com
 * @date 2014年6月25日  上午10:17:24
 */
public class BookmarkDataBaseBuilder extends DatabaseBuilder<Bookmark> {

    private final String COLUM_ID = "id";
    private final String COLUM_VOLUME_ID = "volumeId";
    private final String COLUM_VOLUME_NAME = "volumeName";
    private final String COLUM_CHAPTER_ID = "chapterId";
    private final String COLUM_CHAPTER_NAME = "chapterName";
    private final String COLUM_CHAPTER_POSITION = "chapterPosition";
    private final String COLUM_CHP_COUNT = "chpCount";
    private final String COLUM_CONTENT = "content";
    private final String COLUM_DESCRIPTION = "description";
    private final String COLUM_TYPE = "type";
    private final String COLUM_TIME = "createTime";
    private final String COLUM_CATEGORY_ID = "categoryId";
    private final String COLUM_CATEGORY_NAME = "categoryName";

    @Override
    public Bookmark build(Cursor c) {
        int column_id = c.getColumnIndex(COLUM_ID);
        int colum_volume_id = c.getColumnIndex(COLUM_VOLUME_ID);
        int colum_volume_name = c.getColumnIndex(COLUM_VOLUME_NAME);
        int colum_chapter_id = c.getColumnIndex(COLUM_CHAPTER_ID);
        int colum_chapter_name = c.getColumnIndex(COLUM_CHAPTER_NAME);
        int colum_chapter_position = c.getColumnIndex(COLUM_CHAPTER_POSITION);
        int colum_chp_count = c.getColumnIndex(COLUM_CHP_COUNT);
        int colum_content = c.getColumnIndex(COLUM_CONTENT);
        int colum_description = c.getColumnIndex(COLUM_DESCRIPTION);
        int colum_type = c.getColumnIndex(COLUM_TYPE);
        int colum_time = c.getColumnIndex(COLUM_TIME);
        int colum_category_id = c.getColumnIndex(COLUM_CATEGORY_ID);
        int colum_category_name = c.getColumnIndex(COLUM_CATEGORY_NAME);

        Bookmark mark = new Bookmark();
        mark.setId(c.getInt(column_id));
        mark.setVolumeId(c.getInt(colum_volume_id));
        mark.setVolumeName(c.getString(colum_volume_name));
        mark.setChapterIndexId(c.getInt(colum_chapter_id));
        mark.setChapterName(c.getString(colum_chapter_name));
        mark.setIndex(c.getInt(colum_chapter_position));
        mark.setChapterCount(c.getInt(colum_chp_count));
        mark.setContent(c.getString(colum_content));
        mark.setDescription(c.getString(colum_description));
        mark.setType(c.getInt(colum_type));
        mark.setCreateTime(c.getString(colum_time));
        mark.setCategroyId(c.getString(colum_category_id));
        mark.setCategroyName(c.getString(colum_category_name));
        return mark;
    }

    @Override
    public ContentValues deconstruct(Bookmark bookmark) {
        ContentValues values = new ContentValues();
        values.put(COLUM_VOLUME_ID, bookmark.getVolumeId());
        values.put(COLUM_VOLUME_NAME, bookmark.getVolumeName());
        values.put(COLUM_CHAPTER_ID, bookmark.getChapterIndexId());
        values.put(COLUM_CHAPTER_NAME, bookmark.getChapterName());
        values.put(COLUM_CHAPTER_POSITION, bookmark.getIndex());
        values.put(COLUM_CHP_COUNT, bookmark.getChapterCount());
        values.put(COLUM_CONTENT, bookmark.getContent());
        values.put(COLUM_DESCRIPTION, bookmark.getDescription());
        values.put(COLUM_TYPE, bookmark.getType());
        values.put(COLUM_TIME, bookmark.getCreateTime());
        values.put(COLUM_CATEGORY_ID, bookmark.getCategroyId());
        values.put(COLUM_CATEGORY_NAME, bookmark.getCategroyName());
        return values;
    }

}
