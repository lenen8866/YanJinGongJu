package com.read.scriptures.db;

import android.content.ContentValues;
import android.database.Cursor;

import com.read.scriptures.model.Category;

/**
 * @author lim
 * @ClassName: UserDataBaseBuilder
 * @Package com.lgmshare.goodfoodm.db
 * @Description: User数据对象构建类
 * @mail lgmshare@gmail.com
 * @date 2014年6月25日  上午10:17:24
 */
public class CategoryDataBaseBuilder extends DatabaseBuilder<Category> {

	private final String COLUMId = "id";
	private final String COLUM_CATEName = "cateName";
	private final String COLUM_VOLCount = "volCount";
	private final String COLUM_PARENTId = "parentId";

	@Override
	public Category build(Cursor c) {
		int columnId = c.getColumnIndex(COLUMId);
		int column_volName = c.getColumnIndex(COLUM_CATEName);
		int column_volCount = c.getColumnIndex(COLUM_VOLCount);
		int column_categoryId = c.getColumnIndex(COLUM_PARENTId);

		Category user = new Category();
		user.setId(c.getInt(columnId));
		user.setCateName(c.getString(column_volName));
		user.setVolCount(c.getInt(column_volCount));
		user.setParentId(c.getInt(column_categoryId));
		return user;
	}

	@Override
	public ContentValues deconstruct(Category user) {
		ContentValues values = new ContentValues();
		values.put(COLUMId, user.getId());
		values.put(COLUM_CATEName, user.getCateName());
		values.put(COLUM_VOLCount, user.getVolCount());
		values.put(COLUM_PARENTId, user.getParentId());
		return values;
	}

}
