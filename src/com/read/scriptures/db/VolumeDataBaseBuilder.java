package com.read.scriptures.db;

import android.content.ContentValues;
import android.database.Cursor;

import com.read.scriptures.model.Volume;

/**
 * @ClassName: UserDataBaseBuilder
 * @Package com.lgmshare.goodfoodm.db
 * @Description: User数据对象构建类
 * @author lim
 * @mail lgmshare@gmail.com
 * @date 2014年6月25日 上午10:17:24
 */
public class VolumeDataBaseBuilder extends DatabaseBuilder<Volume> {

    private final String COLUMId = "id";
    private final String COLUM_VOLName = "volName";
    private final String COLUM_CHPCount = "chpCount";
    private final String COLUM_CATEGORYId = "categoryId";

    private final String COLUM_UPDATETIME = "updateTime";

    @Override
    public Volume build(Cursor c) {
        int columnId = c.getColumnIndex(COLUMId);
        int column_volName = c.getColumnIndex(COLUM_VOLName);
        int column_chpCount = c.getColumnIndex(COLUM_CHPCount);
        int column_categoryId = c.getColumnIndex(COLUM_CATEGORYId);
        int column_updateTime = c.getColumnIndex(COLUM_UPDATETIME);

        Volume user = new Volume();
        user.setId(c.getInt(columnId));
        user.setVolName(c.getString(column_volName));
        user.setChpCount(c.getInt(column_chpCount));
        user.setCategoryId(c.getInt(column_categoryId));
        user.setUpdateTime(c.getString(column_updateTime));
        return user;
    }

    @Override
    public ContentValues deconstruct(Volume user) {
        ContentValues values = new ContentValues();
        values.put(COLUMId, user.getId());
        values.put(COLUM_VOLName, user.getVolName());
        values.put(COLUM_CHPCount, user.getChpCount());
        values.put(COLUM_CATEGORYId, user.getCategoryId());
        values.put(COLUM_UPDATETIME, user.getUpdateTime());
        return values;
    }

}
