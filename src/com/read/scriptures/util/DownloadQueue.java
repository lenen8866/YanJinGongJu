package com.read.scriptures.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.music.player.lib.util.XToast;
import com.read.scriptures.app.HuDongApplication;
import com.read.scriptures.config.SystemConfig;
import com.read.scriptures.config.ZConfig;
import com.read.scriptures.model.Baike;
import com.read.scriptures.model.BaikeCategory;
import com.read.scriptures.model.Category;
import com.read.scriptures.model.Chapter;
import com.read.scriptures.model.DownLoadItem;
import com.read.scriptures.model.Volume;
import com.read.scriptures.util.rsa.DES;
import com.read.scriptures.util.rsa.RSA;
import com.read.scriptures.widget.CommPopWindow;
import com.zxl.common.db.sqlite.DbException;
import com.zxl.common.db.sqlite.DbUtils;
import com.zxl.common.db.sqlite.Selector;
import com.zxl.common.db.sqlite.WhereBuilder;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.AllClientPNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.read.scriptures.constants.SystemConstants.SHUKU;

@SuppressLint("SdCardPath")
public class DownloadQueue {
    // 0是书库 2是百科
    private int type;

    private DownLoadItem downLoadItem;

    public DownLoadItem getDownLoadItem() {
        return downLoadItem;
    }

    public void setDownLoadItem(DownLoadItem downLoadItem) {
        this.downLoadItem = downLoadItem;
    }

    private int progress;

    private long downloadSize;

    private long totalSize;
    String bookName;

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public long getDownloadSize() {
        return downloadSize;
    }

    public void setDownloadSize(long downloadSize) {
        this.downloadSize = downloadSize;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    private final int BUFFER_SIZE = 4096;

    public DownloadQueue(DownLoadItem downLoadItem) {
        this.downLoadItem = downLoadItem;
        this.bookName = downLoadItem.getBookName();
    }

    public DownloadQueue(DownLoadItem downLoadItem, int type) {
        this.downLoadItem = downLoadItem;
        this.type = type;
        this.bookName = downLoadItem.getBookName();
    }

    public boolean downloadSuccess(Context context) {
        if (!checkDESPAssword(context)) {
            return false;
        }
        if (downLoadItem.getProgressBar() != null) {
            downLoadItem.getProgressBar().post(new Runnable() {
                public void run() {
                    downLoadItem.getProgressBar().setSecondaryProgress(10);
                }
            });
        }

        if (type == 0) {
            if (!checkBook(context)) {
                return false;
            }
        } else if (type == 2) {
            if (!checkBaike(context)) {
                return false;
            }
        }
        return true;
    }

    public void downloadFiles(Context context, final Activity activity, final LinearLayout linearLayout, final CommPopWindow commPopWindow, final TextView textView) throws NotOnlineException {
        int connectionTimeout = 60000;
        int soTimeout = 60000;
        String url = downLoadItem.getUrl();
        String bookName = downLoadItem.getBookName();
        String code = downLoadItem.getBookcode();
        if (StringUtil.isEmpty(bookName)) {
            bookName = "temp";
        }
//        try {
//            DownloadFileUtils.downLoadFromUrl(url,bookName+".zip",DOWNLOAD_FILE_PATH);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
        if (downloadFile(context,activity, url, bookName + ".zip", connectionTimeout, soTimeout)){

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    commPopWindow.showPop(linearLayout);
                    textView.setText("正在合并");
                }
            });
            long start = TimeUtils.getNow();
            String filePath = HuDongApplication.getInstance().getFilesDir()
                    .getAbsolutePath();
//                File zipFile = new File(filePath + "/hudong.db.zip");
//            File zipFile = new File(SHUKU + "hudong.zip");
//                FileUtil.copySD2ZIP(ATHIS, SHUKU+"hudong.zip", zipFile.getAbsolutePath());
//            try {
//                ZipUtil.unzip(zipFile, new File(filePath));
//            } catch (IOException e1) {
//                LogUtil.error("initData", e1);
//            }
            //TODO 替换解压缩带密码
            Zip4jUtils.uncompressZip4j(SHUKU + "hudong.zip", filePath, SystemConfig.ZIP_PASSWORD);
            long end = TimeUtils.getNow();
            long diff = TimeUtils.diffTime(start, end);
            Log.e("ASDASDASDAD", "解压 all: " + diff);

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (commPopWindow != null)
                        commPopWindow.dismissPop();
                }
            });

            if (bookName.contains("百科")){
                SharedPreferencesUtils.saveBooklibCode(context,code);
                SharedPreferencesUtils.saveBooklibName(context,bookName);
            }
//            else  if (bookName.contains("灵修")){
//                SharedPreferencesUtils.saveLingXiuCode(context,code);
//                SharedPreferencesUtils.saveLingXiuName(context,bookName);
//            }else  if (bookName.contains("百科")){
//                SharedPreferencesUtils.saveBaiKeCode(context,code);
//                SharedPreferencesUtils.saveBaiKeName(context,bookName);
//            }
            File file = new File(getFileName(context, bookName + ".zip"));
            downLoadItem.setFile(file);
            // downloadFiles.add(file);
            //解压缩刚下载文件
//            ZipUtils.readByApacheZipFile(SHUKU +"hudong.zip", SHUKU);
            //将解压缩的文件导入数据库
//            if (bookName.contains("书库")){
//                long start = TimeUtils.getNow();
//                CategoryDatabaseHelper categoryHepler = new CategoryDatabaseHelper(context);
//                List<CategoryBean> list = new ArrayList<>();//给volume做参数用
//                //category表修改
//                categoryHepler.replaceNewTable(DOWNLOAD_FILE_PATH+SharedPreferencesUtils.getBooklibName(HuDongApplication.getInstance()),list);
//                //volume表修改
//                List<VolumeBean> volumeBeanList = new ArrayList<>();//给volume做参数用
//                VolumeDatabaseHepler volumeDatabaseHepler = new VolumeDatabaseHepler(context);
//                volumeDatabaseHepler.replaceNewTable(list,volumeBeanList);
//                //chapter表修改
//                ChapterDatabaseHepler chapterDatabaseHepler = new ChapterDatabaseHepler(context);
//                chapterDatabaseHepler.replaceNewTable(volumeBeanList);
//                long end = TimeUtils.getNow();
//                long diff = TimeUtils.diffTime(start, end);
//                Log.e("ASDASDASDAD", "书库 all: " + diff);
//            }else  if (bookName.contains("灵修")){
//                long start = TimeUtils.getNow();
//                SpiritualityDatabaseHepler spiritualityDatabaseHepler = new SpiritualityDatabaseHepler(context);
//                spiritualityDatabaseHepler.replaceNewTable(DOWNLOAD_FILE_PATH+SharedPreferencesUtils.getLingXiuName(HuDongApplication.getInstance()));
//                long end = TimeUtils.getNow();
//                long diff = TimeUtils.diffTime(start, end);
//                Log.e("ASDASDASDAD", "灵修 all: " + diff);
//            }else  if (bookName.contains("百科")){
//                long start = TimeUtils.getNow();
//                List<CategoryBean> list = new ArrayList<>();//给volume做参数用
//                //先插入baikecategory
//                BaikeCategoryDatabaseHelper baikeCategoryDatabaseHelper = new BaikeCategoryDatabaseHelper(context);
//                baikeCategoryDatabaseHelper.replaceNewTable(DOWNLOAD_FILE_PATH+SharedPreferencesUtils.getBaiKeName(HuDongApplication.getInstance()),list);
//                //再插入baike
//                BaikeDatabaseHepler baikeDatabaseHepler = new BaikeDatabaseHepler(context);
//                baikeDatabaseHepler.replaceNewTable(list);
//                long end = TimeUtils.getNow();
//                long diff = TimeUtils.diffTime(start, end);
//                Log.e("ASDASDASDAD", "百科 all: " + diff);
//            }
            if (progress >= 100) {
                if (downloadSuccess(context)) {
                    downLoadItem.setProgress(100);
                    downLoadItem.setSecondaryProgress(100);
                }
            }
            if (downLoadItem.getProgressBar() != null) {
                downLoadItem.getProgressBar().post(new Runnable() {
                    public void run() {
                        downLoadItem.getProgressBar().setProgress(0);
                        downLoadItem.getProgressBar().setSecondaryProgress(0);
                    }
                });

            }
        }else {
            if (activity != null) {
               activity.runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       XToast.showToast(activity, "下载失败");
                   }
               });
            }
        }
    }


    public void downloadFiles(final Context context) throws NotOnlineException {
        int connectionTimeout = 60000;
        int soTimeout = 60000;
        String url = downLoadItem.getUrl();
        String bookName = downLoadItem.getBookName();
        String code = downLoadItem.getBookcode();
        if (StringUtil.isEmpty(bookName)) {
            bookName = "temp";
        }
//        try {
//            DownloadFileUtils.downLoadFromUrl(url,bookName+".zip",DOWNLOAD_FILE_PATH);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
        if (downloadFile(context,null, url, bookName + ".zip", connectionTimeout, soTimeout)){
            long start = TimeUtils.getNow();
            String filePath = HuDongApplication.getInstance().getFilesDir()
                    .getAbsolutePath();
//                File zipFile = new File(filePath + "/hudong.db.zip");
            File zipFile = new File(SHUKU + "hudong.zip");
//                FileUtil.copySD2ZIP(ATHIS, SHUKU+"hudong.zip", zipFile.getAbsolutePath());
//            try {
//                ZipUtil.unzip(zipFile, new File(filePath));
//            } catch (IOException e1) {
//                LogUtil.error("initData", e1);
//            }
            //TODO 替换解压缩带密码
            Zip4jUtils.uncompressZip4j(SHUKU + "hudong.zip", filePath, SystemConfig.ZIP_PASSWORD);
            long end = TimeUtils.getNow();
            long diff = TimeUtils.diffTime(start, end);
            Log.e("ASDASDASDAD", "解压 all: " + diff);

            if (bookName.contains("百科")){
                SharedPreferencesUtils.saveBooklibCode(context,code);
                SharedPreferencesUtils.saveBooklibName(context,bookName);
            }
//            else  if (bookName.contains("灵修")){
//                SharedPreferencesUtils.saveLingXiuCode(context,code);
//                SharedPreferencesUtils.saveLingXiuName(context,bookName);
//            }else  if (bookName.contains("百科")){
//                SharedPreferencesUtils.saveBaiKeCode(context,code);
//                SharedPreferencesUtils.saveBaiKeName(context,bookName);
//            }
            File file = new File(getFileName(context, bookName + ".zip"));
            // Log.i("test", file.getAbsolutePath());
            downLoadItem.setFile(file);
            // downloadFiles.add(file);
            //解压缩刚下载文件
//            ZipUtils.readByApacheZipFile(SHUKU +"hudong.zip", SHUKU);
            //将解压缩的文件导入数据库
//            if (bookName.contains("书库")){
//                long start = TimeUtils.getNow();
//                CategoryDatabaseHelper categoryHepler = new CategoryDatabaseHelper(context);
//                List<CategoryBean> list = new ArrayList<>();//给volume做参数用
//                //category表修改
//                categoryHepler.replaceNewTable(DOWNLOAD_FILE_PATH+SharedPreferencesUtils.getBooklibName(HuDongApplication.getInstance()),list);
//                //volume表修改
//                List<VolumeBean> volumeBeanList = new ArrayList<>();//给volume做参数用
//                VolumeDatabaseHepler volumeDatabaseHepler = new VolumeDatabaseHepler(context);
//                volumeDatabaseHepler.replaceNewTable(list,volumeBeanList);
//                //chapter表修改
//                ChapterDatabaseHepler chapterDatabaseHepler = new ChapterDatabaseHepler(context);
//                chapterDatabaseHepler.replaceNewTable(volumeBeanList);
//                long end = TimeUtils.getNow();
//                long diff = TimeUtils.diffTime(start, end);
//                Log.e("ASDASDASDAD", "书库 all: " + diff);
//            }else  if (bookName.contains("灵修")){
//                long start = TimeUtils.getNow();
//                SpiritualityDatabaseHepler spiritualityDatabaseHepler = new SpiritualityDatabaseHepler(context);
//                spiritualityDatabaseHepler.replaceNewTable(DOWNLOAD_FILE_PATH+SharedPreferencesUtils.getLingXiuName(HuDongApplication.getInstance()));
//                long end = TimeUtils.getNow();
//                long diff = TimeUtils.diffTime(start, end);
//                Log.e("ASDASDASDAD", "灵修 all: " + diff);
//            }else  if (bookName.contains("百科")){
//                long start = TimeUtils.getNow();
//                List<CategoryBean> list = new ArrayList<>();//给volume做参数用
//                //先插入baikecategory
//                BaikeCategoryDatabaseHelper baikeCategoryDatabaseHelper = new BaikeCategoryDatabaseHelper(context);
//                baikeCategoryDatabaseHelper.replaceNewTable(DOWNLOAD_FILE_PATH+SharedPreferencesUtils.getBaiKeName(HuDongApplication.getInstance()),list);
//                //再插入baike
//                BaikeDatabaseHepler baikeDatabaseHepler = new BaikeDatabaseHepler(context);
//                baikeDatabaseHepler.replaceNewTable(list);
//                long end = TimeUtils.getNow();
//                long diff = TimeUtils.diffTime(start, end);
//                Log.e("ASDASDASDAD", "百科 all: " + diff);
//            }
            if (progress >= 100) {
                if (downloadSuccess(context)) {
                    downLoadItem.setProgress(100);
                    downLoadItem.setSecondaryProgress(100);
                }
            }
            if (downLoadItem.getProgressBar() != null) {
                downLoadItem.getProgressBar().post(new Runnable() {
                    public void run() {
                        downLoadItem.getProgressBar().setProgress(0);
                        downLoadItem.getProgressBar().setSecondaryProgress(0);
                    }
                });
            }
        }else {
            if (context != null && context instanceof Activity) {
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        XToast.showToast(context, "下载失败");
                    }
                });
            }
        }
    }

    public boolean downloadFiles(Context context, final Activity activity, final TextView textView) throws NotOnlineException {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText("正在下载中...");
            }
        });
        int connectionTimeout = 60000;
        int soTimeout = 60000;
        String url = downLoadItem.getUrl();
        String bookName = downLoadItem.getBookName();
        String code = downLoadItem.getBookcode();
        if (StringUtil.isEmpty(bookName)) {
            bookName = "temp";
        }
//        try {
//            DownloadFileUtils.downLoadFromUrl(url,bookName+".zip",DOWNLOAD_FILE_PATH);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
        if (downloadFile(context,activity, url, bookName + ".zip", connectionTimeout, soTimeout)){
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textView.setText("正在加载到程序中，请勿退出app...(加载后自动启动程序)");
                }
            });
            long start = TimeUtils.getNow();
            String filePath = HuDongApplication.getInstance().getFilesDir()
                    .getAbsolutePath();
//                File zipFile = new File(filePath + "/hudong.db.zip");
            File zipFile = new File(SHUKU + "hudong.zip");
//                FileUtil.copySD2ZIP(ATHIS, SHUKU+"hudong.zip", zipFile.getAbsolutePath());
//            try {
//                ZipUtil.unzip(zipFile, new File(filePath));
//            } catch (IOException e1) {
//                LogUtil.error("initData", e1);
//            }

            //TODO 替换解压缩带密码
            Zip4jUtils.uncompressZip4j(SHUKU + "hudong.zip", filePath, SystemConfig.ZIP_PASSWORD);
            long end = TimeUtils.getNow();
            long diff = TimeUtils.diffTime(start, end);
            Log.e("ASDASDASDAD", "解压 all: " + diff);
            SharedPreferencesUtils.saveBooklibCode(context,code);
            SharedPreferencesUtils.saveBooklibName(context,bookName);
//            else  if (bookName.contains("灵修")){
//                SharedPreferencesUtils.saveLingXiuCode(context,code);
//                SharedPreferencesUtils.saveLingXiuName(context,bookName);
//            }else  if (bookName.contains("百科")){
//                SharedPreferencesUtils.saveBaiKeCode(context,code);
//                SharedPreferencesUtils.saveBaiKeName(context,bookName);
//            }
            File file = new File(getFileName(context, bookName + ".zip"));
            // Log.i("test", file.getAbsolutePath());
            downLoadItem.setFile(file);
            // downloadFiles.add(file);
            //解压缩刚下载文件
//            ZipUtils.readByApacheZipFile(SHUKU +"hudong.zip", SHUKU);
            //将解压缩的文件导入数据库
//            if (bookName.contains("书库")){
//                long start = TimeUtils.getNow();
//                CategoryDatabaseHelper categoryHepler = new CategoryDatabaseHelper(context);
//                List<CategoryBean> list = new ArrayList<>();//给volume做参数用
//                //category表修改
//                categoryHepler.replaceNewTable(DOWNLOAD_FILE_PATH+SharedPreferencesUtils.getBooklibName(HuDongApplication.getInstance()),list);
//                //volume表修改
//                List<VolumeBean> volumeBeanList = new ArrayList<>();//给volume做参数用
//                VolumeDatabaseHepler volumeDatabaseHepler = new VolumeDatabaseHepler(context);
//                volumeDatabaseHepler.replaceNewTable(list,volumeBeanList);
//                //chapter表修改
//                ChapterDatabaseHepler chapterDatabaseHepler = new ChapterDatabaseHepler(context);
//                chapterDatabaseHepler.replaceNewTable(volumeBeanList);
//                long end = TimeUtils.getNow();
//                long diff = TimeUtils.diffTime(start, end);
//                Log.e("ASDASDASDAD", "书库 all: " + diff);
//            }else  if (bookName.contains("灵修")){
//                long start = TimeUtils.getNow();
//                SpiritualityDatabaseHepler spiritualityDatabaseHepler = new SpiritualityDatabaseHepler(context);
//                spiritualityDatabaseHepler.replaceNewTable(DOWNLOAD_FILE_PATH+SharedPreferencesUtils.getLingXiuName(HuDongApplication.getInstance()));
//                long end = TimeUtils.getNow();
//                long diff = TimeUtils.diffTime(start, end);
//                Log.e("ASDASDASDAD", "灵修 all: " + diff);
//            }else  if (bookName.contains("百科")){
//                long start = TimeUtils.getNow();
//                List<CategoryBean> list = new ArrayList<>();//给volume做参数用
//                //先插入baikecategory
//                BaikeCategoryDatabaseHelper baikeCategoryDatabaseHelper = new BaikeCategoryDatabaseHelper(context);
//                baikeCategoryDatabaseHelper.replaceNewTable(DOWNLOAD_FILE_PATH+SharedPreferencesUtils.getBaiKeName(HuDongApplication.getInstance()),list);
//                //再插入baike
//                BaikeDatabaseHepler baikeDatabaseHepler = new BaikeDatabaseHepler(context);
//                baikeDatabaseHepler.replaceNewTable(list);
//                long end = TimeUtils.getNow();
//                long diff = TimeUtils.diffTime(start, end);
//                Log.e("ASDASDASDAD", "百科 all: " + diff);
//            }
            if (progress >= 100) {
                if (downloadSuccess(context)) {
                    downLoadItem.setProgress(100);
                    downLoadItem.setSecondaryProgress(100);
                }
            }
            if (downLoadItem.getProgressBar() != null) {
                downLoadItem.getProgressBar().post(new Runnable() {
                    public void run() {
                        downLoadItem.getProgressBar().setProgress(0);
                        downLoadItem.getProgressBar().setSecondaryProgress(0);
                    }
                });
            }
        }else {
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        XToast.showToast(context, "下载失败");
                    }
                });
            }
            return false;
        }
        return true;
    }

    public void removeTempFile(Context context, String tempFileName) {
        context.deleteFile(tempFileName);
        // LogUtil.test("removeTempFile:" + tempFileName);
    }

    private boolean checkDESPAssword(final Context context) {
        String desPassword = HuDongApplication.getInstance().getFileSystem().getAsString
                ("desPassWord");
        if (StringUtil.isEmpty(desPassword)) {
            byte[] netByteArray = NetConnectUtil.getByteArray(context, ZConfig.SERVICE_URL +
                            "/Sys/android/file/des.do",
                    10);
            // LogUtil.test(Arrays.toString(netByteArray));
            if (netByteArray != null) {
                try {
                    String passWord = new String(RSA.decryptByPublicKey(netByteArray, RSA
                            .getPublicKey()));
                    String des = "";
                    if (passWord != null && !"".equals(passWord)) {
                        for (int i = 0; i < passWord.length(); i++) {
                            if (passWord.charAt(i) >= 48 && passWord.charAt(i) <= 57) {
                                des += passWord.charAt(i);
                            }
                        }
                    }
                    // LogUtil.test("netpassWord：" + des);
                    if (!StringUtil.isEmpty(des)) {
                        DES.setPassword(des);
                    }
                    HuDongApplication.getInstance().getFileSystem().put("desPassWord", des);
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            } else {
                if (context != null && context instanceof Activity)  {
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            XToast.showToast(context, "服务器密钥获取失败，解压失败");
                        }
                    });
                }

                downLoadItem.setProgress(0);
                if (downLoadItem.getProgressBar() != null) {
                    downLoadItem.getProgressBar().setProgress(0);
                }
                // LogUtil.test("密钥获取失败");
                return false;
            }
        } else {
            DES.setPassword(desPassword);
            // LogUtil.test("desPassword：" + desPassword);
        }
        return true;
    }


    private boolean downloadFile(final Context context, Activity activity,String url, String tempFileName, int
            connectionTimeout,
                                 int soTimeout) throws NotOnlineException {
        // LogUtil.test(String.format("start download: url [%s], tempFileName
        // [%s]", url, tempFileName));

        if (!SystemUtils.isOnline(context.getApplicationContext())) {
            LogUtil.test("not online");
            throw new NotOnlineException();
        }

//        File fileLoc = new File(SHUKU+"hudong.zip");
//        if (fileLoc.exists()){
//            progress = 100;
//            return true;
//        }
        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpParams params = httpclient.getParams();
        HttpConnectionParams.setConnectionTimeout(params, connectionTimeout);
        HttpConnectionParams.setSoTimeout(params, soTimeout);
        params.setBooleanParameter(AllClientPNames.USE_EXPECT_CONTINUE, false);
        // HttpGet request = new HttpGet(url);
        String temp = url.split("\\?")[0]+"/";
        String en = url.substring(0, 19);
        //  String en = url.split("\\=")[1];
        List<NameValuePair> pair = new ArrayList<NameValuePair>();
        pair.add(new BasicNameValuePair("path", en));
        HttpGet post = new HttpGet(url);
        try {
//            post.setEntity(new UrlEncodedFormEntity(pair, HTTP.UTF_8));
            HttpResponse response = httpclient.execute(post);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                // LogUtil.test("" + response.getStatusLine().getStatusCode());
                return false;
            }
            FileOutputStream output = null;
            InputStream in = null;
            try {
                File file = new File(SHUKU+"hudong.zip");
                if (file.exists())
                    file.delete();
                else {
                    File parent = file.getParentFile();
                    if (!parent.exists())
                        parent.mkdirs();
                    file.createNewFile();
                }
                output =new FileOutputStream(file,true);
//                context.getApplicationContext().deleteFile(tempFileName);
//                // context.getCacheDir().getAbsoluteFile()
//                output = context.getApplicationContext().openFileOutput(tempFileName,
//                        // Context.MODE_WORLD_READABLE
//                        Context.MODE_PRIVATE);
                in = response.getEntity().getContent();
                totalSize = response.getEntity().getContentLength();
                byte[] b = new byte[BUFFER_SIZE];
                int length = 0;
                while ((length = in.read(b)) != -1) {
                    output.write(b, 0, length);
                    downloadSize += length;
                    progress = (int) (downloadSize * 100 / totalSize);
                    downLoadItem.setProgress(progress);
                    if (downLoadItem.getProgressBar() != null) {
                        downLoadItem.getProgressBar().post(new Runnable() {
                            public void run() {
                                downLoadItem.getProgressBar().setProgress(progress);
                            }
                        });
                    }
                    if (downLoadItem.getTvProgress() != null){
                        downLoadItem.getProgressBar().post(new Runnable() {
                            public void run() {
                                String progressText = ((int)(downloadSize / Double.valueOf(totalSize) *100))+"%";
                                downLoadItem.getTvProgress().setText(progressText);
                            }
                        });
//                        activity.runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                String progressText = (downloadSize / totalSize)+"%";
//                                downLoadItem.getTvProgress().setText(progressText);
//                            }
//                        });
                    }
                }
            } finally {
                try {
                    if (output != null) {
                        output.flush();
                        output.close();
                    }
                } catch (Exception e) {
                    // N/A
                }
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (Exception e) {
                }
            }
        } catch (Exception e) {
            Log.e(DownloadQueue.class.getSimpleName(), "Error", e);
            throw new NotOnlineException(e);
        }
        return true;
    }


    private boolean checkBook(Context context) {
        DbUtils dbUtils = HuDongApplication.getInstance().getDbUtils();
        Volume volume = null;
        Category category = null;
        try {
            synchronized (dbUtils) {
                category = dbUtils.findFirst(Selector.from(Category.class)
                        .where(WhereBuilder.getInstance("cateName", "=", downLoadItem
                                .getType().replaceAll("^\\d{1,}-", "")).orCondition
                                ("cateName", "like",
                                        "%-" + downLoadItem
                                                .getType().replaceAll("^\\d{1,}-", ""))));
                if (category == null) {
                    category = dbUtils.findFirst(Selector.from(Category.class)
                            .where(WhereBuilder.getInstance("cateName", "=", downLoadItem
                                    .getCategory().replaceAll("^\\d{1,}-", "")).orCondition
                                    ("cateName", "like",
                                            "%-" + downLoadItem
                                                    .getCategory().replaceAll("^\\d{1,}-", ""))));
                    if (category != null) {
                        Category newCategory = new Category();
                        newCategory.setCateName(downLoadItem.getType());
                        newCategory.setParentId(category.getId());

                        dbUtils.save(newCategory);
                        category = dbUtils.findFirst(Selector.from(Category.class)
                                .where(WhereBuilder.getInstance("cateName", "=", downLoadItem
                                        .getType())));
                    }
                }
            }

            volume = dbUtils
                    .findFirst(Selector.from(Volume.class)
                            .where(WhereBuilder
                                    .getInstance()
                                    .expr(" (volName = '" + bookName.replaceAll
                                            ("^\\d{1,}-", "") +
                                            "' or " + "volName like '%-" + bookName.replaceAll
                                            ("^\\d{1,}-", "") + "')")
                                    .and("categoryId", "=", 1)));// category.getId()
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }
        if (volume == null) {
            volume = new Volume();
            volume.setCategoryId(1);
            volume.setUpdateTime(downLoadItem.getTime());
            volume.setVolName(bookName);
            volume.setChpCount(0);
            try {
                dbUtils.save(volume);
            } catch (DbException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            try {
                dbUtils.delete(Chapter.class, WhereBuilder.getInstance("volumeId", "=", volume
                        .getId()));
            } catch (DbException e) {
                e.printStackTrace();
                return false;
            }
        }
        try {
            volume = dbUtils
                    .findFirst(Selector.from(Volume.class)
                            .where(WhereBuilder
                                    .getInstance()
                                    .expr(" (volName = '" + bookName.replaceAll
                                            ("^\\d{1,}-", "") +
                                            "' or " + "volName like '%-" + bookName.replaceAll
                                            ("^\\d{1,}-", "") + "')")
                                    .and("categoryId", "=", 1)));
            volume.setChpCount(0);
            volume.setVolName(bookName);
            volume.setUpdateTime(downLoadItem.getTime());
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }
        try {
            File outDir = dealWithTempFile(context);
            if (outDir.exists()) {
                File[] txts = outDir.listFiles();
                int size = txts.length;
                int index = 0;
                for (File txt : txts) {
                    index++;
                    final int second = index * 70 / size + 30;
                    if (downLoadItem.getProgressBar() != null) {
                        downLoadItem.getProgressBar().post(new Runnable() {
                            public void run() {
                                downLoadItem.getProgressBar().setSecondaryProgress(second);
                            }
                        });
                    }
                    downLoadItem.setSecondaryProgress(second);
                    if (txt.getName().equals("jianjie.txt")) {
                        continue;
                    }
                    String content = FileUtil.readTxt(txt);
                    Chapter chapter = new Chapter();
                    chapter.setVolumeId(volume.getId());
                    chapter.setContent(content);
                    chapter.setName(txt.getName().replace(".txt", ""));
                    chapter.setIndexId(volume.getChpCount() + 1);
                    volume.setChpCount(volume.getChpCount() + 1);
                    chapter.setCategoryId(1);
                    chapter.setParentId(1);
                    dbUtils.save(chapter);
                }
            }
            FileUtil.delFolder(outDir.getAbsolutePath());
            // outDir.delete();
        } catch (IOException e1) {
            e1.printStackTrace();
            return false;
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        try {
            dbUtils.update(volume);
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean checkBaike(Context context) {
        DbUtils dbUtils = HuDongApplication.getInstance().getDbUtils();
        BaikeCategory baikeCategory = null;
        try {
            synchronized (dbUtils) {
                baikeCategory = dbUtils.findFirst(Selector.from(BaikeCategory.class)
                        .where(WhereBuilder.getInstance("cateName", "=", downLoadItem.getBookName
                                ().replaceAll
                                ("^\\d{1,}-", "")).orCondition
                                ("cateName", "like",
                                        "%-" + downLoadItem
                                                .getBookName().replaceAll("^\\d{1,}-", ""))));
                if (baikeCategory == null) {
                    BaikeCategory newBaikeCategory = new BaikeCategory();
                    newBaikeCategory.setCateName(downLoadItem.getBookName());
                    newBaikeCategory.setParentId(0);
                    dbUtils.save(newBaikeCategory);
                    baikeCategory = dbUtils.findFirst(Selector.from(BaikeCategory.class)
                            .where(WhereBuilder.getInstance("cateName", "=", downLoadItem
                                    .getBookName())));
                } else {
                    dbUtils.delete(Baike.class, WhereBuilder.getInstance("categoryId", "=",
                            baikeCategory
                                    .getId()));
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }
        baikeCategory.setVolCount(0);
        baikeCategory.setUpdateTime(downLoadItem.getTime());
        try {
            File outDir = dealWithTempFile(context);
            if (outDir.exists()) {
                File[] txts = outDir.listFiles();
                List<String> names = new ArrayList<>();
                // System.out.println("result:" + result);
                for (File txt : txts) {
                    names.add(txt.getAbsolutePath());
                }
                CollectionUtil.getSortOfChinese(names);
                int size = txts.length;
                int index = 0;
                for (String fileName : names) {
                    File txt = new File(fileName);
                    index++;
                    final int second = index * 70 / size + 30;
                    if (downLoadItem.getProgressBar() != null) {
                        downLoadItem.getProgressBar().post(new Runnable() {
                            public void run() {
                                downLoadItem.getProgressBar().setSecondaryProgress(second);
                            }
                        });
                        downLoadItem.setSecondaryProgress(second);
                    }
                    if (txt.getName().equals("jianjie.txt")) {
                        continue;
                    }
                    String content = FileUtil.readTxt(txt);
                    Baike baike = new Baike();
                    baike.setCateName(baikeCategory.getCateName());
                    baike.setCategoryId(baikeCategory.getId());
                    baike.setName(txt.getName().replace(".txt", ""));
                    baike.setContent(content);
                    baike.setIndexId(baikeCategory.getVolCount() + 1);
                    baikeCategory.setVolCount(baikeCategory.getVolCount() + 1);
                    dbUtils.save(baike);
                }
            }
            FileUtil.delFolder(outDir.getAbsolutePath());
        } catch (IOException e1) {
            e1.printStackTrace();
            return false;
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        try {
            dbUtils.update(baikeCategory);
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    private File dealWithTempFile(Context context) throws IOException {
        File file = downLoadItem.getFile();
        String dbDir = context.getFilesDir().getAbsolutePath();
        String fileName = dbDir + "/hudong/" + bookName;
        new File(dbDir + "/hudong/").mkdir();
        File tempFile = new File(fileName + ".temp");
        File zipFile = new File(fileName + ".zip");
        FileUtil.copyFile(file.getAbsolutePath(), tempFile.getAbsolutePath());
//        FileUtil.decryptFile(tempFile.getAbsolutePath(), zipFile.getAbsolutePath(), null);
        if (downLoadItem.getProgressBar() != null) {
            downLoadItem.getProgressBar().post(new Runnable() {
                public void run() {
                    downLoadItem.getProgressBar().setSecondaryProgress(30);
                }
            });
        }
        File outDir = new File(fileName);
//        ZipUtil.unzip(zipFile, outDir);
        //TODO 替换解压缩带密码
        Zip4jUtils.uncompressZip4j(fileName + ".zip", fileName, SystemConfig.ZIP_PASSWORD);
        zipFile.delete();
        tempFile.delete();
        file.delete();
        return outDir;
    }

    private String getFileName(Context context, String tempFileName) {
        return "/data/data/" + context.getPackageName() + "/files/" + tempFileName;
    }
}
