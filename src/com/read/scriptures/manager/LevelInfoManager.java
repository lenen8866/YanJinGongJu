package com.read.scriptures.manager;

import com.read.scriptures.bean.LevelActiveInfo;
import com.read.scriptures.bean.RecommendPayType;
import com.read.scriptures.bean.RespInfo;
import com.read.scriptures.config.ZConfig;
import com.read.scriptures.http.okhttp.HttpCallback;
import com.read.scriptures.http.okhttp.OkHttpUtils;
import com.read.scriptures.http.okhttp.ResultCode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

/**
 * Time: 2020/9/1
 * Author: a123
 * Description:
 */
public class LevelInfoManager {
    private static LevelInfoManager instance;

    private LinkedHashMap<String,List<LevelActiveInfo>> levelPriceMap;
    private List<String> tabList;
    private RecommendPayType mRecommendPayType;

    public static LevelInfoManager getInstance() {
        if (instance == null) {
            instance = new LevelInfoManager();
        }
        return instance;
    }

    private LevelInfoManager() {
        queryLevelActiveListInfos(null);
        queryRecommendPayType(null);
    }

    public List<LevelActiveInfo> getLevelPriceLise(String key) {
        return levelPriceMap.get(key);
    }

    public List<String> getTabList() {
        return tabList;
    }

    public RecommendPayType getRecommendPayType() {
        return mRecommendPayType;
    }

    /**
     * 请求会员激活信息
     */
    public void queryLevelActiveListInfos(final HttpCallback<RespInfo<LinkedHashMap<String, List<LevelActiveInfo>>>> callback) {
        OkHttpUtils.getInstance().get(ZConfig.LEVEL_ACTIVE_AMOUNT_URL, new HashMap<>(), new HttpCallback<RespInfo<LinkedHashMap<String, List<LevelActiveInfo>>>>() {
            @Override
            public void onSuccess(final RespInfo<LinkedHashMap<String, List<LevelActiveInfo>>> result) {
                if (result != null && result.getData() != null) {
                    tabList = new ArrayList<>();
                    levelPriceMap = result.getData();
                    Set<String> keys = levelPriceMap.keySet();
                    Iterator<String> iterator = keys.iterator();
                    while (iterator.hasNext()){
                        String key = iterator.next();
                        tabList.add(key);
                        List<LevelActiveInfo> levelActiveInfos = levelPriceMap.get(key);
                        Collections.sort(levelActiveInfos, new Comparator<LevelActiveInfo>() {
                            @Override
                            public int compare(LevelActiveInfo o1, LevelActiveInfo o2) {
                                //升序
                                return o1.getDay() - (o2.getDay()) < 0 ? -1 : 1;
                            }
                        });
                    }
                    if (callback != null){
                        callback.onSuccess(result);
                    }
                }else{
                    if (callback != null){
                        callback.onError(ResultCode.KNET_GSON_EROR.getCode(),ResultCode.KNET_GSON_EROR.getMessage());
                    }
                }
            }

            @Override
            public void onError(int code, String errorMsg) {
                if (callback != null){
                    callback.onError(code,errorMsg);
                }
            }

            @Override
            public void onFinish() {

            }
        });
    }


    /**
     * 请求支付推荐方式
     */
    public void queryRecommendPayType(final HttpCallback<RespInfo<RecommendPayType>> callback) {
        OkHttpUtils.getInstance().get(ZConfig.RECOMMEND_PAY_TYPE_URL, new HashMap<String, String>(), new HttpCallback<RespInfo<RecommendPayType>>() {
            @Override
            public void onSuccess(final RespInfo<RecommendPayType> result) {
                if (result != null) {
                   mRecommendPayType = result.getData();
                    if (callback != null){
                        callback.onSuccess(result);
                    }
                }else{
                    if (callback != null){
                        callback.onError(ResultCode.KNET_GSON_EROR.getCode(),ResultCode.KNET_GSON_EROR.getMessage());
                    }
                }
            }

            @Override
            public void onError(int code, String errorMsg) {
                if (callback != null){
                    callback.onError(code,errorMsg);
                }
            }

            @Override
            public void onFinish() {

            }
        });
    }
}
