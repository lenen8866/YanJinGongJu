package com.read.scriptures.http.okhttp;

import com.read.scriptures.app.HuDongApplication;
import com.read.scriptures.util.LogUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Time: 2020/8/31
 * Author: a123
 * Description:
 */
public class OkHttpUtils {
    private static OkHttpUtils instance;

    public static OkHttpUtils getInstance() {
        if (instance == null){
            instance = new OkHttpUtils();
        }
        return instance;
    }

    private OkHttpUtils() {
    }

    public void get(String url, HashMap<String, String> params, final HttpCallback callBack) {
        StringBuffer requestUrlBuf = new StringBuffer(url);
        if (params != null) {
            Set<String> keys = params.keySet();
            if (keys.size() > 0) {
                Iterator<String> keyIt = keys.iterator();
                requestUrlBuf.append("?");
                while (keyIt.hasNext()) {
                    String key = keyIt.next();
                    requestUrlBuf.append(key)
                            .append("=")
                            .append(params.get(key))
                            .append("&");
                }
            }
        }

        String requestUrl = requestUrlBuf.toString();
        if (requestUrl.endsWith("&")) {
            requestUrl = requestUrl.substring(0, requestUrl.length() - 1);
        }

        LogUtil.debug("GET-URL--"+requestUrl);


        //去请求
        Request request = new Request.Builder()
                .url(requestUrl)
                .get()
                .build();
        HuDongApplication.getHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (callBack != null){
                    callBack.onError(ResultCode.KNET_NETWORK_ERROR.getCode(),ResultCode.KNET_NETWORK_ERROR.getMessage());
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseJson = response.body().string();
                LogUtil.debug("GET-RESPONSE--"+responseJson);
                if (response.isSuccessful()) {
                    callBack.convertData(responseJson);
                }else {
                    if (callBack != null){
                        callBack.onError(ResultCode.KNET_NETWORK_ERROR.getCode(),ResultCode.KNET_NETWORK_ERROR.getMessage());
                    }
                }
            }
        });
    }



    public void post(String url, HashMap<String, String> params, final HttpCallback callBack) {
        FormBody.Builder builder = new FormBody.Builder();
        if (params != null) {
            Set<String> keys = params.keySet();
            if (keys.size() > 0) {
                Iterator<String> keyIt = keys.iterator();
                while (keyIt.hasNext()) {
                    String key = keyIt.next();
                    builder.addEncoded(key, params.get(key));
                }
            }
        }

        LogUtil.debug("POST-URL--"+url);
        LogUtil.debug("POST-PARAMS--"+params.toString());

        //去构建
        FormBody formBody = builder.build();
        //去请求
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        HuDongApplication.getHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (callBack != null){
                    callBack.onError(ResultCode.KNET_NETWORK_ERROR.getCode(),ResultCode.KNET_NETWORK_ERROR.getMessage());
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseJson = response.body().string();
                LogUtil.debug("POST-RESPONSE--"+responseJson);
                if (response.isSuccessful()) {
                    callBack.convertData(responseJson);
                }else {
                    if (callBack != null){
                        callBack.onError(ResultCode.KNET_NETWORK_ERROR.getCode(),ResultCode.KNET_NETWORK_ERROR.getMessage());
                    }
                }
            }
        });
    }
}
