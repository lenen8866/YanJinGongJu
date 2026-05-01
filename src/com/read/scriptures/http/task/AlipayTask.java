//package com.read.scriptures.http.task;
//
//import com.alibaba.fastjson.JSON;
//import com.read.scriptures.bean.AlipayBean;
//import com.read.scriptures.config.ZConfig;
//import com.read.scriptures.http.core.HttpRequestTask;
//import com.read.scriptures.util.LogUtil;
//
//public class AlipayTask extends HttpRequestTask<AlipayBean>{
//    public AlipayTask() {
//    }
//
//    @Override
//    public String getUrl() {
//        return ZConfig.PAY_URL;
//    }
//
//    @Override
//    public int getMethod() {
//        return Method.POST;
//    }
//
//    @Override
//    public AlipayBean parse(String data) {
//        LogUtil.test(data);
//        return JSON.parseObject(data, AlipayBean.class);
//    }
//}
