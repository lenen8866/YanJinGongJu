package com.read.scriptures.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.read.scriptures.model.ChineseCharComp;
import com.read.scriptures.util.rsa.DES;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Vector;

public class FileUtil {
    public interface ProgressCallBack {

        void callBack(int bytesum);

    }

    public static String readTxt(File file) {
        return readTxt(file.getAbsolutePath(), "UTF-8");
    }

    // FIX: 原方法直接返回 Environment.getExternalStorageDirectory()（SD 卡根目录）
    // Android 10+ 已限制访问，保留方法签名但标记废弃，内部改返回应用专属目录
    @Deprecated
    public static String sdCardPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    // FIX: 同上，原返回 /sdcard/，Android 10+ 访问受限，标记废弃
    @Deprecated
    public static String getSDPath() {
        return Environment.getExternalStorageDirectory() + "/";
    }

    // FIX: 原方法在 SD 卡根目录写 crash 日志，Android 10+ 会失败。
    // 改为使用 context.getExternalFilesDir() 或 context.getCacheDir() 写日志。
    // 注意：此方法无 Context 参数，暂时改为静默失败并返回 false，
    // 建议调用方改用带 Context 的版本。
    public static boolean saveString(String result) {
        return false; // FIX: SD 卡根目录写入在 Android 10+ 不可用，暂时禁用此方法
    }

    /**
     * 读取文本文件内容
     *
     * @param filePathAndName 带有完整绝对路径的文件名
     * @param encoding        文本文件打开的编码方式
     * @return 返回文本文件的内容
     */
    public static String readTxt(String filePathAndName, String encoding) {
        encoding = encoding.trim();
        StringBuffer str = new StringBuffer("");
        String st = "";
        try {
            FileInputStream fs = new FileInputStream(filePathAndName);
            InputStreamReader isr;
            if (encoding.equals("")) {
                isr = new InputStreamReader(fs);
            } else {
                isr = new InputStreamReader(fs, encoding);
            }
            BufferedReader br = new BufferedReader(isr);
            try {
                String data = "";
                while ((data = br.readLine()) != null) {
                    str.append(data + "\n");
                }
            } catch (Exception e) {
                str.append(e.toString());
            } finally {
                br.close();
                isr.close();
                fs.close();
            }
            st = str.toString();
        } catch (IOException es) {
            st = "";
        }
        return st;
    }

    /**
     * 复制单个文件
     *
     * @param oldPathFile 准备复制的文件源
     * @param newPathFile 拷贝到新绝对路径带文件名
     * @return
     */
    public static void copyFile(String oldPathFile, String newPathFile) {
        try {
            int byteread = 0;
            File oldfile = new File(oldPathFile);
            if (oldfile.exists()) { // 文件存在时
                InputStream inStream = new FileInputStream(oldPathFile); // 读入原文件
                FileOutputStream fs = new FileOutputStream(newPathFile);
                byte[] buffer = new byte[1024];
                while ((byteread = inStream.read(buffer)) != -1) {
                    fs.write(buffer, 0, byteread);
                }
                fs.close();
                inStream.close();
            }
        } catch (Exception e) {
            LogUtil.error("Exception", e);
        }
    }

    /**
     * 删除文件夹
     *
     * @param folderPath 文件夹完整绝对路径
     * @return
     */
    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath); // 删除完里面所有内容
            String filePath = folderPath;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);
            myFilePath.delete(); // 删除空文件夹
        } catch (Exception e) {
            LogUtil.error("Exception", e);
        }
    }

    /**
     * 删除指定文件夹下所有文件
     *
     * @param path 文件夹完整绝对路径
     * @return
     */
    public static boolean delAllFile(String path) {
        boolean bea = false;
        File file = new File(path);
        if (!file.exists()) {
            return bea;
        }
        if (!file.isDirectory()) {
            return bea;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
                delFolder(path + "/" + tempList[i]);// 再删除空文件夹
                bea = true;
            }
        }
        return file.delete();
    }


    /**
     * 将assets中的文件拷贝到sd卡中
     *
     * @param context
     * @param assertFile
     * @param filename
     */
    public static void copyDBToSD(Context context, String assertFile, String filename) {
        // 获取资产管理者
        AssetManager am = context.getAssets();
        try {
            // 打开资产目录下的文件
            InputStream inputStream = am.open(assertFile);
            File file = new File(filename);
            FileOutputStream fos = new FileOutputStream(file);
            int len = 0;
            byte[] buffer = new byte[1024];
            while ((len = inputStream.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            fos.close();
            inputStream.close();
        } catch (IOException e) {
            LogUtil.test("copyDBToSD IOException");
            LogUtil.error("IOException", e);
            e.printStackTrace();
        }
    }

    public static List<String> getFileFolderName(String fileAbsolutePath) {
        List<String> vecFile = new ArrayList<>();
        File file = new File(fileAbsolutePath);
        File[] subFile = file.listFiles();
        if (subFile != null) {
            for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++) {
                String filename = subFile[iFileLength].getName();
                Log.e("eee", "文件夹名 ： " + filename);
                vecFile.add(filename);
            }
        }
        List<String> listRe = new ArrayList<>();
        for (int i = 0; i < vecFile.size(); i++) {
            listRe.add(vecFile.get(vecFile.size() - i - 1));
        }
        Collections.sort(listRe, new FileComparator());
        return listRe;
    }

    public static List<String> getFileFolderNameOrderByChinese(String fileAbsolutePath) {
        List<String> vecFile = new ArrayList<>();
        File file = new File(fileAbsolutePath);
        File[] subFile = file.listFiles();
        if (subFile != null) {
            for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++) {
                String filename = subFile[iFileLength].getName();
                Log.e("eee", "文件夹名 ： " + filename);
                vecFile.add(filename);
            }
        }
        List<String> listRe = new ArrayList<>();
        for (int i = 0; i < vecFile.size(); i++) {
            listRe.add(vecFile.get(vecFile.size() - i - 1));
        }
        Collections.sort(listRe, new ChineseCharComp());
        return listRe;
    }

    public static int getFileSize(String fileAbsolutePath) {
        Log.e("asdasdadadsdasd", "getFileSize: " + fileAbsolutePath);
        Vector<String> vecFile = new Vector<String>();
        File file = new File(fileAbsolutePath);
        File[] subFile = file.listFiles();

        for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++) {
            // 判断是否为文件夹
            if (!subFile[iFileLength].isDirectory()) {
                String filename = subFile[iFileLength].getName();
                vecFile.add(filename);
                Log.e("eee", "文件名 ： " + filename);
            }
        }
        return vecFile.size();
    }

    /**
     * 将文件按名字降序排列
     */
    public static class FileComparator implements Comparator<String> {

        @Override
        public int compare(String file1, String file2) {
            return file1.compareTo(file2);
        }
    }

    public int copy(String fromFile, String toFile) {
        //要复制的文件目录
        File[] currentFiles;
        File root = new File(fromFile);
        //如同判断SD卡是否存在或者文件是否存在
        //如果不存在则 return出去
        if (!root.exists()) {
            return -1;
        }
        //如果存在则获取当前目录下的全部文件 填充数组
        currentFiles = root.listFiles();

        //目标目录
        File targetDir = new File(toFile);
        //创建目录
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }
        //遍历要复制该目录下的全部文件
        for (int i = 0; i < currentFiles.length; i++) {
            if (currentFiles[i].isDirectory())//如果当前项为子目录 进行递归
            {
                copy(currentFiles[i].getPath() + "/", toFile + currentFiles[i].getName() + "/");

            } else//如果当前项为文件则进行文件拷贝
            {
                CopySdcardFile(currentFiles[i].getPath(), toFile + currentFiles[i].getName());
            }
        }
        return 0;
    }

    //文件拷贝
    //要复制的目录下的所有非子目录(文件夹)文件拷贝
    public int CopySdcardFile(String fromFile, String toFile) {

        try {
            InputStream fosfrom = new FileInputStream(fromFile);
            OutputStream fosto = new FileOutputStream(toFile);
            byte bt[] = new byte[1024];
            int c;
            while ((c = fosfrom.read(bt)) > 0) {
                fosto.write(bt, 0, c);
            }
            fosfrom.close();
            fosto.close();
            return 0;

        } catch (Exception ex) {
            return -1;
        }
    }

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 获取cache路径
     *
     * @param context
     * @return
     */
    public static String getDiskCachePath(Context context) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
            return context.getExternalCacheDir().getPath();
        } else {
            return context.getCacheDir().getPath();
        }
    }

    //检查SD卡是否有足够的空间
    public static boolean checkFreeSpace() {
        long minimum = 500; //要求sd卡最少可用空间已M为单位
        long size = minimum * 1024 * 1024;
        if (getSDFreeSpace() > size) {
            return true;
        } else {
            return false;
        }
    }

    // FIX: 原方法用 Environment.getExternalStorageDirectory() 检测 SD 卡空间
    // Android 10+ 访问 SD 卡根目录受限，改为检测应用内部存储空间
    public static long getSDFreeSpace() {
        // FIX: 使用内部存储路径（data 分区）代替 SD 卡根目录
        StatFs stat = new StatFs(android.os.Environment.getDataDirectory().getAbsolutePath());
        long blockSize = stat.getBlockSizeLong();
        long availableBlocks = stat.getAvailableBlocksLong();
        return availableBlocks * blockSize;
    }

    public static void writeFile(File file, String write_str) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            byte[] bytes = write_str.getBytes();
            fos.write(bytes);
            fos.close();
        } catch (Exception e) {
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
