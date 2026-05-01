package com.music.player.lib.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpCallback;
import com.kymjs.rxvolley.client.HttpParams;
import com.kymjs.rxvolley.client.ProgressListener;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Set;

public class NetUtil {

    public static void getInviteData(String url, Map<String, String> params, CallBack callBack) {
        OkGo.<String>get(url)
                .cacheTime(-1)
                .cacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST)
                .params(params)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (callBack != null) {
                            if (TextUtils.isEmpty(response.body())) {
                                callBack.onError("接口异常");
                            } else {
                                callBack.onSuccess(response.body());
                            }
                        }
                    }

                    @Override
                    public void onCacheSuccess(Response<String> response) {
                        super.onCacheSuccess(response);
                        if (callBack != null) {
                            callBack.onSuccess(response.body());
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        if (callBack != null) {
                            callBack.onError(response.body());
                        }
                    }
                });
    }

    public static void get(String url, CallBack callBack) {
        get(url, null, callBack);
    }

    public static void get(String url, Map<String, String> params, CallBack callBack) {
        OkGo.<String>get(url)
                .params(params)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (callBack != null) {
                            if (TextUtils.isEmpty(response.body())) {
                                callBack.onError("接口异常");
                            } else {
                                callBack.onSuccess(response.body());
                            }
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        if (callBack != null) {
                            callBack.onError(response.body());
                        }
                    }
                });
    }

    public static void getCache(String url, CallBack callBack) {
        getCache(url, null, callBack);
    }

    public static void getCache(String url, Map<String, String> map, CallBack callBack) {
        HttpParams httpParams = null;
        if (map != null) {
            Set<String> strings = map.keySet();
            httpParams = new HttpParams();
            for (String key : strings) {
                httpParams.put(key, map.get(key));
            }
        }
        HttpCallback httpCallback = new HttpCallback() {
            @Override
            public void onSuccessInAsync(byte[] t) {
            }

            @Override
            public void onSuccess(String t) {
                if (callBack != null) {
                    callBack.onSuccess(t);
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                if (callBack != null) {
                    callBack.onError(strMsg);
                }
            }
        };
       startRequest(url,RxVolley.Method.GET,Integer.MAX_VALUE,httpParams,httpCallback);
    }

    private static void startRequest(String url, int RequestMethod, int cacheTime, HttpParams httpParams, HttpCallback callback) {
        new RxVolley.Builder()
                .url(url) //接口地址
                //请求类型，如果不加，默认为 GET 可选项：
                //RxVolley.Method.POST/PUT/DELETE/HEAD/OPTIONS/TRACE/PATCH
                .httpMethod(RequestMethod)
                //设置缓存时间: 默认是 get 请求 5 分钟, post 请求不缓存
                .cacheTime(cacheTime)
                .params(httpParams) //上文创建的HttpParams请求参数集
                .shouldCache(true)
                .callback(callback) //响应回调
                .doTask();  //执行请求操作
    }


    public static void getNoCache(String url, Map<String, String> params, CallBack callBack) {
        OkGo.<String>get(url)
                .params(params)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (callBack != null) {
                            if (TextUtils.isEmpty(response.body())) {
                                callBack.onError("接口异常");
                            } else {
                                callBack.onSuccess(response.body());
                            }
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        if (callBack != null) {
                            callBack.onError(response.body());
                        }
                    }
                });
    }


    public static void downloadAudio(String audioUrl, String filePath, String videoCacheTempPath, FileCallback fileCallback) {
        File tempFile = new File(videoCacheTempPath);
        if (tempFile.exists()) {//正在下载
            fileCallback.onError(null);
            return;
        }
        File file = new File(filePath);
        if (file.exists()) {//有缓存
            Response<File> objectResponse = new Response<>();
            objectResponse.setBody(file);
            fileCallback.onSuccess(objectResponse);
            return;
        }
        OkGo.<File>get(audioUrl)
                .execute(fileCallback);
    }

    public static void downloadLrc(Context context, String audioUrl, String audioId, String suffix, HttpCallback fileCallback) {
        File resultFile = new File(getDiskCachePath(context), audioId + "." + suffix);
        //存在直接返回
        if (resultFile.exists()) {
            if (fileCallback != null) {
                fileCallback.onSuccess(resultFile.getAbsolutePath());
            }
            return;
        }
        File tempFile = new File(getDiskCachePath(context), audioId + "_" + suffix + "_tmp");
        RxVolley.download(tempFile.getAbsolutePath(), audioUrl, null, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                File newFile = new File(getDiskCachePath(context), audioId + "." + suffix);
                if (!newFile.exists()) {
                    tempFile.renameTo(newFile);
                }
                fileCallback.onSuccess(newFile.getAbsolutePath());
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                fileCallback.onFailure(errorNo, strMsg);
            }
        });
    }


    public static void post(String url, Map<String, String> params, CallBack callBack) {
        OkGo.<String>post(url)
                .params(params)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (callBack != null) {
                            if (TextUtils.isEmpty(response.body())) {
                                callBack.onError("接口异常");
                            } else {
                                callBack.onSuccess(response.body());
                            }
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        if (callBack != null) {
                            callBack.onError(response.body());
                        }
                    }
                });
    }

    public static void post(String url, Map<String, String> params, File lrcFile, CallBack callBack) {
        OkGo.<String>post(url)
                .params(params)
                .params("file", lrcFile)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (callBack != null) {
                            if (TextUtils.isEmpty(response.body())) {
                                callBack.onError("接口异常");
                            } else {
                                callBack.onSuccess(response.body());
                            }
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        if (callBack != null) {
                            callBack.onError(response.body());
                        }
                    }
                });
    }

    public static void postCache(String url, Map<String, String> params, CallBack callBack) {
        OkGo.<String>post(url)
                .cacheTime(-1)
                .cacheMode(CacheMode.IF_NONE_CACHE_REQUEST)
                .params(params)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (callBack != null) {
                            if (TextUtils.isEmpty(response.body())) {
                                callBack.onError("接口异常");
                            } else {
                                callBack.onSuccess(response.body());
                            }
                        }
                    }

                    @Override
                    public void onCacheSuccess(Response<String> response) {
                        super.onCacheSuccess(response);
                        if (callBack != null) {
                            callBack.onSuccess(response.body());
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        if (callBack != null) {
                            callBack.onError(response.body());
                        }
                    }
                });
    }

    public abstract static class CallBack {
        public abstract void onSuccess(String t);

        public void onError(String t) {

        }

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

    /**
     * 判断是否打开网络
     *
     * @param context
     * @return
     */
    public static boolean isNetWorkAvailable(Context context) {
        boolean isAvailable = false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isAvailable()) {
            isAvailable = true;
        }
        return isAvailable;
    }
}
