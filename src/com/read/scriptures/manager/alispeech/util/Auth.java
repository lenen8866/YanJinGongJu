package com.read.scriptures.manager.alispeech.util;

import com.alibaba.fastjson.JSONObject;
import com.read.scriptures.manager.alispeech.token.AccessToken;

import java.io.IOException;

public class Auth {
    public static JSONObject getAliYunTicket() {
        JSONObject object = new JSONObject();
        final AccessToken token;
        //From Aliyun 请根据相关文档获取并填入
        String app_key = "TkBeJ8ixAp7nVM83";
        String accessKeyId = "REMOVED_KEY_ID";
        String accessKeySecret = "REMOVED_KEY_SECRET";


        token = new AccessToken(accessKeyId, accessKeySecret);
        Thread th = new Thread(){
            @Override
            public void run() {
                try {
                    token.apply();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        };
        th.start();
        try {
            th.join(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String token_txt = token.getToken();
        long expired_time = token.getExpireTime();

        object.put("app_key",app_key);
        object.put("token",token_txt);
        object.put("device_id",Utils.getDeviceId());
        object.put("url","wss://nls-gateway.cn-shanghai.aliyuncs.com:443/ws/v1");
        return object;
    }
}
