package com.read.scriptures.db;

import com.read.scriptures.app.HuDongApplication;

public class DatabaseManager {
    public static boolean isInit = false;

	private static HistoryDatabaseHelper historyDBOpenHelper;


    public static void initDB() {
        try {
            if (isInit)
                return;
			historyDBOpenHelper = new HistoryDatabaseHelper(HuDongApplication.getInstance());
            isInit = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static HistoryDatabaseHelper getHistoryHelper() {
        if (historyDBOpenHelper == null)
            historyDBOpenHelper = new HistoryDatabaseHelper(HuDongApplication.getInstance());
        return historyDBOpenHelper;
    }
}
