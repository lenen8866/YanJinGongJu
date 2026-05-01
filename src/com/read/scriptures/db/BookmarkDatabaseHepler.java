//package com.read.scriptures.db;
//
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//
//import com.read.scriptures.app.HuDongApplication;
//import com.read.scriptures.model.Bookmark;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static com.read.scriptures.db.DatabaseHelper.TABLE_BOOKMARK;
//
///**
// * @author lim
// * @Description: 数据库Bookmark表 hepler
// * @mail lgmshare@gmail.com
// * @date 2014年6月25日 上午10:17:57
// */
//public class BookmarkDatabaseHepler {
//
//    private final String COLUMId = "id";
//    private final String COLUM_VOLUMEId = "volumeId";
//    private final String COLUM_VOLUMEName = "volumeName";
//    private final String COLUM_CHAPTERId = "chapterId";
//    private final String COLUM_CHAPTERName = "chapterName";
//    private final String COLUM_CHAPTERPosition = "chapterPosition";
//    private final String COLUM_CHPCount = "chpCount";
//    private final String COLUM_CONTENT = "content";
//    private final String COLUM_DESCRIPTION = "description";
//    private final String COLUM_TYPE = "type";
//    private final String COLUM_TIME = "createTime";
//    private final String COLUM_CATEGORY_ID = "categoryId";
//    private final String COLUM_CATEGORY_NAME = "categoryName";
//
//    private DatabaseHelper mDatabaseHelper;
//
//    public BookmarkDatabaseHepler(Context context) {
//        DatabaseContext dbContext = new DatabaseContext(context, "test");
//        mDatabaseHelper = new DatabaseHelper(dbContext);
//        //修改表结构，添加没有的字段
//        alterTable(COLUM_TIME, "varchar(20)", "'2020-09-01 09:00:00'");
//        alterTable(COLUM_CATEGORY_ID, "varchar(20)", null);
//        alterTable(COLUM_CATEGORY_NAME, "varchar(100)", null);
//        //添加索引
//        addIndex();
//
//    }
//
//    /**
//     * 获取SQLiteDatabase实例
//     */
//    private SQLiteDatabase getDb() {
//        return mDatabaseHelper.getWritableDatabase();
//    }
//
//    /**
//     * 修改表结构
//     */
//    private void alterTable(String columName, String type, String defaultValue) {
//        SQLiteDatabase db = getDb();
//        boolean isExistColum = HuDongApplication.getInstance().getDbUtils().checkColumnExists2(db, TABLE_BOOKMARK, columName);
//        if (isExistColum) {
//            return;
//        } else {
//            try {
//                //修改表结构
//                String sql = "ALTER TABLE " + TABLE_BOOKMARK + " ADD COLUMN " + columName + " " + type + " default " + defaultValue;
//                db.execSQL(sql);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    /**
//     * 添加索引
//     */
//    private void addIndex(){
//        //判断
//        SQLiteDatabase db = getDb();
//        boolean isExistIndex = HuDongApplication.getInstance().getDbUtils().checkIndexExists(db, "mark_index");
//        if (isExistIndex){
//            return;
//        }
//        try {
//            //添加索引
//            String sql = "create index mark_index on "+TABLE_BOOKMARK+" ("+COLUM_CONTENT+", "+COLUM_DESCRIPTION+")";
//            db.execSQL(sql);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//
//
//    public void addBookmark(Bookmark bookmark) {
//        SQLiteDatabase db = getDb();
//        // put radio data into the table
//        ContentValues values = new BookmarkDataBaseBuilder().deconstruct(bookmark);
//        db.insert(TABLE_BOOKMARK, null, values);
//        db.close();
//    }
//
//    public void addBookmark(List<Bookmark> bookmarks) {
//        SQLiteDatabase db = getDb();
//        // put radio data into the table
//        int length = bookmarks.size();
//        for (int i = 0; i < length; i++) {
//            ContentValues values = new BookmarkDataBaseBuilder().deconstruct(bookmarks.get(i));
//            db.insert(TABLE_BOOKMARK, null, values);
//        }
//        db.close();
//    }
//
//    public ArrayList<Bookmark> getBookmarkList(String sortType) {
//        ArrayList<Bookmark> arrayList = new ArrayList<Bookmark>();
//        SQLiteDatabase db = getDb();
//        String[] columns = {COLUMId, COLUM_VOLUMEId, COLUM_VOLUMEName, COLUM_CHAPTERId, COLUM_CHAPTERName,
//                COLUM_CHAPTERPosition, COLUM_CHPCount, COLUM_CONTENT, COLUM_DESCRIPTION, COLUM_TYPE, COLUM_TIME, COLUM_CATEGORY_ID, COLUM_CATEGORY_NAME};
//        Cursor query = db.query(TABLE_BOOKMARK, columns, "", null, null, null, "id " + sortType, "");
//        if (query != null) {
//            query.moveToFirst();
//            while (!query.isAfterLast()) {
//                Bookmark bookmark = new BookmarkDataBaseBuilder().build(query);
//                arrayList.add(bookmark);
//                query.moveToNext();
//            }
//            query.close();
//        }
//        db.close();
//        return arrayList;
//    }
//
//    public ArrayList<Bookmark> getBookmarkList(String sortType, List<String> keyWordList) {
//        ArrayList<Bookmark> arrayList = new ArrayList<Bookmark>();
//        SQLiteDatabase db = getDb();
//        String[] columns = {COLUMId, COLUM_VOLUMEId, COLUM_VOLUMEName, COLUM_CHAPTERId, COLUM_CHAPTERName,
//                COLUM_CHAPTERPosition, COLUM_CHPCount, COLUM_CONTENT, COLUM_DESCRIPTION, COLUM_TYPE, COLUM_TIME, COLUM_CATEGORY_ID, COLUM_CATEGORY_NAME};
//        String[] whereArgs = new String[keyWordList.size() * 2];
//        StringBuffer buffer = new StringBuffer();
//
//        for (int i = 0; i < whereArgs.length; i+=2) {
////            { "%" + keyWord + "%", "%" + keyWord + "%"};
//            String keyWord = keyWordList.get(i / 2);
//            keyWord = sqliteEscape(keyWord);
//
//
//            whereArgs[i] = "%" + keyWord + "%";
//            whereArgs[i+1] = "%" + keyWord + "%";
//
//            //"content like ? or description like ?"
//            buffer.append("(content like ? escape '/' or description like ? escape '/' )").append(" and ");
//        }
//        String whereSql = buffer.toString();
//        if (whereSql.endsWith(" and ")){
//            whereSql = whereSql.substring(0,whereSql.lastIndexOf(" and ")) ;
//        }
//
//        Cursor query = db.query(TABLE_BOOKMARK, columns, whereSql, whereArgs, null, null, "id " + sortType, "");
//        if (query != null) {
//            query.moveToFirst();
//            while (!query.isAfterLast()) {
//                Bookmark bookmark = new BookmarkDataBaseBuilder().build(query);
//                arrayList.add(bookmark);
//                query.moveToNext();
//            }
//            query.close();
//        }
//        db.close();
//        return arrayList;
//    }
//
//
//    public ArrayList<Bookmark> getBookmarkListByVolumeId(String volumeId) {
//        ArrayList<Bookmark> arrayList = new ArrayList<Bookmark>();
//        SQLiteDatabase db = getDb();
//        String[] columns = {COLUMId, COLUM_VOLUMEId, COLUM_VOLUMEName, COLUM_CHAPTERId, COLUM_CHAPTERName,
//                COLUM_CHAPTERPosition, COLUM_CHPCount, COLUM_CONTENT, COLUM_DESCRIPTION, COLUM_TYPE, COLUM_TIME, COLUM_CATEGORY_ID, COLUM_CATEGORY_NAME};
//        String[] whereArgs = {volumeId};
//        Cursor query = db.query(TABLE_BOOKMARK, columns, "volumeId=?", whereArgs, null, null, "id DESC",
//                "");
//        if (query != null) {
//            query.moveToFirst();
//            while (!query.isAfterLast()) {
//                Bookmark bookmark = new BookmarkDataBaseBuilder().build(query);
//                arrayList.add(bookmark);
//                query.moveToNext();
//            }
//            query.close();
//        }
//        db.close();
//        return arrayList;
//    }
//
//    public ArrayList<Bookmark> getBookmarkListByChapterId(String chapterId) {
//        ArrayList<Bookmark> arrayList = new ArrayList<Bookmark>();
//        SQLiteDatabase db = getDb();
//        String[] columns = {COLUMId, COLUM_VOLUMEId, COLUM_VOLUMEName, COLUM_CHAPTERId, COLUM_CHAPTERName,
//                COLUM_CHAPTERPosition, COLUM_CHPCount, COLUM_CONTENT, COLUM_DESCRIPTION, COLUM_TYPE, COLUM_TIME, COLUM_CATEGORY_ID, COLUM_CATEGORY_NAME};
//        String[] whereArgs = {chapterId};
//        Cursor query = db.query(TABLE_BOOKMARK, columns, "chapterId=?", whereArgs, null, null,
//                "id DESC", "");
//        if (query != null) {
//            query.moveToFirst();
//            while (!query.isAfterLast()) {
//                Bookmark bookmark = new BookmarkDataBaseBuilder().build(query);
//                arrayList.add(bookmark);
//                query.moveToNext();
//            }
//            query.close();
//        }
//        db.close();
//        return arrayList;
//    }
//
//    public void deleteById(String id) {
//        SQLiteDatabase db = getDb();
//        String[] whereArgs = {id};
//        db.delete(TABLE_BOOKMARK, "id=?", whereArgs);
//    }
//
//    public void deleteByIds(String ids) {
//        SQLiteDatabase db = getDb();
//        String sql = "delete from " + TABLE_BOOKMARK + " where id in (" + ids + ")";
//        db.execSQL(sql);
//    }
//
//    public void deleteAll() {
//        SQLiteDatabase db = getDb();
//        db.delete(TABLE_BOOKMARK, null, null);
//    }
//
//    public static String sqliteEscape(String keyWord){
//        keyWord = keyWord.replaceAll("/", "//");
//        keyWord = keyWord.replaceAll("'", "''");
//        keyWord = keyWord.replaceAll("\\[", "/[");
//        keyWord = keyWord.replaceAll("]", "/]");
//        keyWord = keyWord.replaceAll("%", "/%");
//        keyWord = keyWord.replaceAll("&","/&");
//        keyWord = keyWord.replaceAll("_", "/_");
//        keyWord = keyWord.replaceAll("\\(", "/(");
//        keyWord = keyWord.replaceAll("\\)", "/)");
//        return keyWord;
//    }
//}
