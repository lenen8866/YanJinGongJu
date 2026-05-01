package com.read.scriptures.http.okhttp;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.read.scriptures.bean.RespInfo;
import com.read.scriptures.manager.AccountManager;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Time: 2020/8/31
 * Author: a123
 * Description:
 */
public abstract class HttpCallback<T> {
    private TypeAdapter<T> mTypeAdapter;

    public HttpCallback() {
        Gson gson = new Gson();
        Type genType = this.getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        mTypeAdapter = (TypeAdapter<T>) gson.getAdapter(TypeToken.get(params[0]));
    }

    public T convertData(String responseJson) {
        T data = null;
        try {
            data = mTypeAdapter.fromJson(responseJson);
            if (data instanceof RespInfo ) {
                RespInfo respInfoObj = (RespInfo) data;
                if (Integer.valueOf(respInfoObj.getCode()) == ResultCode.KNET_SUCCESS.getCode()) {
                    onSuccess(data);
                } else {
                    if (respInfoObj.getMsg()!= null && (respInfoObj.getMsg().contains("令牌已过期") || respInfoObj.getMsg().contains("登录已过期") )){
                        //token失效了
                        AccountManager.getInstance().loginOut(true,"登录已过期，请重新登录！");
                        respInfoObj.setMsg("登录已过期，请重新登录！");
                    }else if (respInfoObj.getMsg()!= null && respInfoObj.getMsg().contains("禁用")){
                        //token失效了
                        AccountManager.getInstance().loginOut(true,respInfoObj.getMsg());
                    }
                    onError(respInfoObj.getCode(), respInfoObj.getMsg());
                }
            } else {
                onSuccess(data);
            }
        } catch (Exception e) {
            onError(ResultCode.KNET_GSON_EROR.getCode(), ResultCode.KNET_GSON_EROR.getMessage());
            e.printStackTrace();
        }

        return data;
    }

    public abstract void onSuccess(T data);

    public abstract void onError(int code, String errorMsg);

    public abstract void onFinish();

}