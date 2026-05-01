package com.read.scriptures.util;

import android.content.Context;
import android.util.Log;

import com.zxl.common.db.sqlite.OutOfTimeException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.AllClientPNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NetConnectUtil {
    private final static String ENCODE = "GBK";

    /**
     * 发送指定地址请求
     * 
     * @param context
     * @param url
     * @param connectionTimeout
     * @param soTimeout
     * @return
     * @throws NotOnlineException
     * @throws OutOfTimeException
     */
    public static HttpResponse request(Context context, String url, int connectionTimeout, int soTimeout)
            throws OutOfTimeException {
        if (!SystemUtils.isOnline(context)) {
            return null;
        }
        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpParams params = httpclient.getParams();
        HttpConnectionParams.setConnectionTimeout(params, connectionTimeout);
        HttpConnectionParams.setSoTimeout(params, soTimeout);
        params.setBooleanParameter(AllClientPNames.USE_EXPECT_CONTINUE, false);
        HttpGet request = new HttpGet(url);
        // setRequestCookies(request);
        try {
            return httpclient.execute(request);
        } catch (Exception e) {
            // e.printStackTrace();
            throw new OutOfTimeException(e);
        }
    }

    /**
     * 获取JSON内容
     * 
     * @param activity
     * @return
     * @throws NotOnlineException
     * @throws OutOfTimeException
     */
    public static String getContent(Context activity, String url) throws NotOnlineException, OutOfTimeException {
        int connectionTimeout = 5000;
        int soTimeout = 5000;
        try {
            url = URLEncoder.encode(url, "utf-8");
            url = URLDecoder.decode(url, "utf-8");
            LogUtil.test("url:" + url);
        } catch (UnsupportedEncodingException e) {
            // e.printStackTrace();
        }
        LogUtil.test("url:" + url);
        HttpResponse response = request(activity, url, connectionTimeout, soTimeout);
        return getContentFromResponse(response);
    }


    /**
     * 获取JSON内容
     *
     * @param activity
     * @return
     * @throws NotOnlineException
     * @throws OutOfTimeException
     */
    public static String getContent(Context activity, String url,int connectionTimeout,int soTimeout) throws NotOnlineException, OutOfTimeException {
        try {
            url = URLEncoder.encode(url, "utf-8");
            url = URLDecoder.decode(url, "utf-8");
            LogUtil.test("url:" + url);
        } catch (UnsupportedEncodingException e) {
            // e.printStackTrace();
        }
        LogUtil.test("url:" + url);
        HttpResponse response = request(activity, url, connectionTimeout, soTimeout);
        return getContentFromResponse(response);
    }

    /**
     * 获取JSON内容
     * 
     * @param activity
     * @return
     * @throws NotOnlineException
     * @throws OutOfTimeException
     */
    public static byte[] getByteArray(Context activity, String url) throws NotOnlineException, OutOfTimeException {
        int connectionTimeout = 10000;
        int soTimeout = 10000;
        try {
            url = URLEncoder.encode(url, "utf-8");
            url = URLDecoder.decode(url, "utf-8");
        } catch (UnsupportedEncodingException e) {
            // e.printStackTrace();
        }
        LogUtil.test("url:" + url);
        HttpResponse response = request(activity, url, connectionTimeout, soTimeout);
        return getByteArrayFromResponse(response);
    }

    public static String getContent(Context activity, String url, int retry) {
        String result = null;
        try {
            result = getContent(activity, url);
        } catch (NotOnlineException e) {
            // e.printStackTrace();
        } catch (OutOfTimeException e) {
            // e.printStackTrace();
            if (retry > 0) {
                return getContent(activity, url, retry - 1);
            }
        }
        return result;
    }

    public static String getWelContent(Context activity, String url ) {
        String result = null;
        try {
            result = getContent(activity, url,2000,1500);
        } catch (NotOnlineException e) {
            // e.printStackTrace();
        } catch (OutOfTimeException e) {
        }
        return result;
    }

    public static byte[] getByteArray(Context activity, String url, int retry) {
        byte[] result = null;
        try {
            result = getByteArray(activity, url);
        } catch (NotOnlineException e) {
            // e.printStackTrace();
        } catch (OutOfTimeException e) {
            // e.printStackTrace();
            if (retry > 0) {
                return getByteArray(activity, url, retry - 1);
            }
        }
        return result;
    }

    /**
     * URL 转码
     *
     * @return String
     * @author lifq
     * @date 2015-3-17 下午04:10:28
     */
    public static String getURLEncoderString(String str) {
        String result = "";
        if (null == str) {
            return "";
        }
        try {
            result = URLEncoder.encode(str, ENCODE);
        } catch (UnsupportedEncodingException e) {
            // e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取网络返回结果
     * 
     * @param response
     * @return
     * @throws NotOnlineException
     */
    public static String getContentFromResponse(HttpResponse response) throws NotOnlineException {
        if (response == null || response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            throw new NotOnlineException();
        }
        BufferedReader br = null;
        String result;
        try {
            InputStream in = response.getEntity().getContent();
            br = new BufferedReader(new InputStreamReader(in));
            StringBuffer temp = new StringBuffer();
            String brtemp;
            while ((brtemp = br.readLine()) != null) {
                temp.append(brtemp);
            }
            result = temp.toString();
        } catch (Exception e) {
            throw new NotOnlineException(e);
        } finally {
            try {
                br.close();
            } catch (Exception e) {
                // N/A
            }
        }
        return result;
    }

    /**
     * 获取网络返回结果
     * 
     * @param response
     * @return
     * @throws NotOnlineException
     */
    public static byte[] getByteArrayFromResponse(HttpResponse response) throws NotOnlineException {
        if (response == null || response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            throw new NotOnlineException();
        }
        byte[] result = new byte[1024 * 1024];
        try {
            InputStream in = response.getEntity().getContent();
            byte[] b = new byte[1024];
            int length = 0;
            int size = 0;
            while ((length = in.read(b)) != -1) {
                byte[] temp = new byte[size + length];
                System.arraycopy(result, 0, temp, 0, size);
                result = temp;
                System.arraycopy(b, 0, result, size, length);
                size += length;
            }
        } catch (Exception e) {
            throw new NotOnlineException(e);
        } finally {
            try {
            } catch (Exception e) {
                // N/A
            }
        }
        return result;
    }

    public static String post(String url, Map<String, String> parm) {

        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpParams params = httpclient.getParams();
        HttpConnectionParams.setConnectionTimeout(params, 30000);
        HttpConnectionParams.setSoTimeout(params, 30000);
        params.setBooleanParameter(AllClientPNames.USE_EXPECT_CONTINUE, false);
        // HttpGet request = new HttpGet(url);
        List<NameValuePair> pair = new ArrayList<NameValuePair>();
        if (parm != null) {
            Set<String> set = parm.keySet();
            for (String key : set) {
                pair.add(new BasicNameValuePair(key, parm.get(key)));
            }
        }
        HttpPost post = new HttpPost(url);
        try {
            post.setEntity(new UrlEncodedFormEntity(pair, HTTP.UTF_8));
            HttpResponse response = httpclient.execute(post);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                // LogUtil.test("" + response.getStatusLine().getStatusCode());
                return "failed";
            }
            return getContentFromResponse(response);
        } catch (Exception e) {
            Log.e(DownloadQueue.class.getSimpleName(), "Error", e);
            return "failed";
        }
    }

    public static String getContentByPost(String url, Map<String, String> parm, int retry) {
        String result = "failed";
        while ("failed".equals(result) && retry > 1) {
            result = post(url, parm);
            retry--;
        }
        if ("failed".equals(result)) {
            return null;
        }
        return result;
    }
}
