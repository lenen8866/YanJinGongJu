package com.read.scriptures.db;

import android.content.ContentValues;
import android.database.Cursor;

import com.read.scriptures.model.Spirituality;

/**
 * @author lim
 * @ClassName: UserDataBaseBuilder
 * @Package com.lgmshare.goodfoodm.db
 * @Description: User数据对象构建类
 * @mail lgmshare@gmail.com
 * @date 2014年6月25日  上午10:17:24
 */
public class SpiritualityDataBaseBuilder extends DatabaseBuilder<Spirituality> {

    private final String COLUM_ID = "id";
    private final String COLUM_DAYTIME = "daytime";
    private final String COLUM_BOOK = "book";
    private final String COLUM_NAME = "name";
    private final String COLUM_PARENT = "parent";
    @Override
    public Spirituality build(Cursor c) {
        int column_id = c.getColumnIndex(COLUM_ID);
        int colum_daytime = c.getColumnIndex(COLUM_DAYTIME);
        int colum_book = c.getColumnIndex(COLUM_BOOK);
        int colum_name = c.getColumnIndex(COLUM_NAME);
        int colum_parent = c.getColumnIndex(COLUM_PARENT);
        Spirituality spiri = new Spirituality();
        spiri.setId(c.getInt(column_id));
        String date = c.getString(colum_daytime);
        spiri.setDaytime(date.substring(0, 2) + "月" + date.substring(2, 4) + "日");
        spiri.setBook(c.getString(colum_book));
        spiri.setName(c.getString(colum_name));
        spiri.setPatrent(c.getString(colum_parent));
        return spiri;
    }

    @Override
    public ContentValues deconstruct(Spirituality spiri) {
        ContentValues values = new ContentValues();
        values.put(COLUM_ID, spiri.getId());
        values.put(COLUM_DAYTIME, spiri.getDaytime());
        values.put(COLUM_BOOK, spiri.getShowBook());
        values.put(COLUM_NAME, spiri.getName());
        values.put(COLUM_PARENT,spiri.getPatrent());
        return values;
    }

}
