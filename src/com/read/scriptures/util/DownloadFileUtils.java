package com.read.scriptures.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DownloadFileUtils {

    /**
     * 从网络Url中下载文件
     *
     * @param urlStr
     * @param fileName
     * @param savePath
     * @throws IOException
     */
    public static void downLoadFromUrl(String urlStr, String fileName, String savePath) throws IOException
    {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //设置超时间为3秒
            conn.setConnectTimeout(5 * 1000);
            //防止屏蔽程序抓取而返回403错误
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

            //得到输入流
            InputStream inputStream = conn.getInputStream();
            //获取自己数组
            byte[] getData = readInputStream(inputStream);

            //文件保存位
            File saveDir = new File(savePath);
            if (!saveDir.exists()) {
                saveDir.mkdir();
            }
            File file = new File(saveDir + File.separator + fileName);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(getData);
            fos.close();
            if (inputStream != null) {
                inputStream.close();
            }

            System.out.println("info:" + url + " download success");
        }catch (Exception e){
            File file = new File(savePath + File.separator + fileName);
            if (file.exists()){
                file.delete();
            }
            e.printStackTrace();
        }

    }

    /**
     * 从输入流中获取字节数组
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static byte[] readInputStream(InputStream inputStream) throws IOException
    {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while((len = inputStream.read(buffer)) != -1)
        {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

    // 解压
    public static boolean DecompressFile(String strSrcPathFileName, String strDstPathFileName)
    {
        File fileSrc = new File(strSrcPathFileName);
        if(!fileSrc.exists())
            return false;

        if(strDstPathFileName.endsWith(File.separator))
            return false;

        File fileDst = new File(strDstPathFileName);
        if(!fileDst.exists())
        {
            File folder = fileDst.getParentFile();
            if(!folder.exists())
            {
                if(!folder.mkdirs())
                    return false;
            }
        }

        File fileTemp = new File(strSrcPathFileName + ".tmp");
        fileTemp.delete();

        FileInputStream fis = null;
        ZipInputStream zis = null;
        ZipEntry ze = null;
        FileOutputStream fos = null;
        try
        {
            fis = new FileInputStream(fileSrc);
            zis = new ZipInputStream(fis);
            fos = new FileOutputStream(fileTemp);

            ze = zis.getNextEntry();
            if(ze.isDirectory())
                return false;

            boolean bSuccess = false;
            byte[] buf = new byte[1024 * 10];
            while(true)
            {
                int nLength = zis.read(buf, 0, buf.length);
                if(nLength <= 0)
                {
                    bSuccess = nLength == -1;
                    break;
                }

                fos.write(buf, 0, nLength);
            }

            if(bSuccess)
            {
                fileDst.delete();
                bSuccess = fileTemp.renameTo(fileDst);
            }

            return bSuccess;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return false;
        }
        finally
        {
            if(fis != null)
            {
                try
                {
                    fis.close();
                }
                catch(Exception e)
                {
                }
            }

            if(zis != null)
            {
                if(ze != null)
                {
                    try
                    {
                        zis.closeEntry();
                    }
                    catch(Exception e)
                    {
                    }
                }
                try
                {
                    zis.close();
                }
                catch(Exception e)
                {
                }
            }

            if(fos != null)
            {
                try
                {
                    fos.close();
                }
                catch(Exception e)
                {
                }
            }
        }
    }

}
