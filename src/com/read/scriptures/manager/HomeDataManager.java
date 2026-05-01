package com.read.scriptures.manager;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.read.scriptures.app.HuDongApplication;
import com.read.scriptures.bean.IntroBean;
import com.read.scriptures.db.CategoryDatabaseHelper;
import com.read.scriptures.db.ChapterDatabaseHepler;
import com.read.scriptures.db.SpiritualityCategoryDatabaseHepler;
import com.read.scriptures.db.VolumeDatabaseHepler;
import com.read.scriptures.event.RefreshDataEvent;
import com.read.scriptures.model.Category;
import com.read.scriptures.model.Chapter;
import com.read.scriptures.model.SpiritualityCategory;
import com.read.scriptures.model.Volume;
import com.read.scriptures.util.ThreadUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class HomeDataManager {
    private static final String TAG = "HomeDataManager";

    //单例
    private static HomeDataManager mInstance;
    //表中所有的简介
    private List<IntroBean> mAllIntroBeans = new ArrayList<>();

    //保存一级分类和二级分类，key:一级分类id,value:二级分类
    public LinkedHashMap<String, List<Category>> twoCategoriesMap = new LinkedHashMap<>();
    //保存二级分类和对应的书籍信息，key:二级分类id,value:书籍列表
    public LinkedHashMap<String, List<Volume>> allVolumesMap = new LinkedHashMap<>();

    public LinkedHashMap<Integer, List<Chapter>> chapterMap = new LinkedHashMap<>();

    private CategoryDatabaseHelper categoryHepler;

    private ChapterDatabaseHepler chapterDatabaseHepler;

    private VolumeDatabaseHepler volumeDatabaseHepler;

    //是否初始化完成
    private boolean isInitVolumeInfos = false;

    // 缓存KEY
    private static final String getIntroListCacheKey = "Key_getIntroList";

    private Timer timer;

    private HomeDataManager() {
    }

    /**
     * 初始化
     *
     * @return
     */
    public static HomeDataManager getInstance() {
        if (mInstance == null) {
            mInstance = new HomeDataManager();
        }
        return mInstance;
    }


    /**
     * 主线程查询介绍信息
     *
     * @return
     */
    public List<IntroBean> getAllIntroBeansMainThread() {
        return chapterDatabaseHepler.getIntroList();
    }

    /**
     * 获取介绍信息
     *
     * @return
     */
    public List<IntroBean> getAllIntroBeans() {
        return mAllIntroBeans;
    }

    //===================================书库信息===============================
    public void updateSuccessRefreshHomeCategoryVolumes() {
        isInitVolumeInfos = false;
        chapterMap.clear();
        refreshHomeCategoryVolumes();

    }

    /**
     * 刷新首页消息
     */
    public void refreshHomeCategoryVolumes() {
        if (isInitVolumeInfos) {
            RefreshDataEvent refreshDataEvent = new RefreshDataEvent();
            refreshDataEvent.setMessage("初始化");
            EventBus.getDefault().post(new RefreshDataEvent());
            return;
        }
        isInitVolumeInfos = false;
        ThreadUtil.doOnOtherThread(new Runnable() {
            @Override
            public void run() {
                try {
                    getHomeVolumesInfo();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getHomeVolumesInfo() {
        if (chapterDatabaseHepler == null) {
            chapterDatabaseHepler = new ChapterDatabaseHepler(HuDongApplication.getInstance());
        }
        if (categoryHepler == null) {
            categoryHepler = new CategoryDatabaseHelper(HuDongApplication.getInstance());
        }
        if (volumeDatabaseHepler == null) {
            volumeDatabaseHepler = new VolumeDatabaseHepler(HuDongApplication.getInstance());
        }
        if (mAllIntroBeans.isEmpty()) {
            // 性能优化
            // List<IntroBean> allIntroBeans = chapterDatabaseHepler.getIntroList();
            List<IntroBean> allIntroBeans = null;
            String cache = HuDongApplication.getInstance().getaCache().getAsString(getIntroListCacheKey);
            // Log.d(TAG, "allIntroBeans cache:" + cache);
            if (TextUtils.isEmpty(cache)) {
                cache = HuDongApplication.getInstance().getIntroListAssets();
            }

            if (!TextUtils.isEmpty(cache)) {
                try {
                    // 加载缓存
                    allIntroBeans = JSON.parseArray(cache, IntroBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                    allIntroBeans = null;
                }
            }

            if (allIntroBeans == null) {
                // 读取数据库
                allIntroBeans = chapterDatabaseHepler.getIntroList();

                // 异步刷新缓存,下次启动APP时就会看到最新数据
                List<IntroBean> finalAllIntroBeans = allIntroBeans;
                ThreadUtil.doOnOtherThread(
                        new Runnable() {
                            @Override
                            public void run() {
                                HuDongApplication.getInstance().getaCache().put(getIntroListCacheKey, JSON.toJSONString(finalAllIntroBeans));
                            }
                        }
                );
            } else {
                // 异步刷新缓存,下次启动APP时就会看到最新数据(等待20秒后再执行,而且不能跨线程,否则被锁住)
                this.timer = new Timer();
                this.timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        List<IntroBean> introList = chapterDatabaseHepler.getIntroList();
                        HuDongApplication.getInstance().getaCache().put(getIntroListCacheKey, JSON.toJSONString(introList));
                        timer.cancel();
                    }
                }, 20, 60 * 1000); // 20秒后执行1次 然后退出
            }
            // Log.d(TAG, "allIntroBeans:" + allIntroBeans.toString());

            if (allIntroBeans.isEmpty()) {
                isInitVolumeInfos = false;
            } else {
                mAllIntroBeans.clear();
                mAllIntroBeans.addAll(allIntroBeans);
            }
        }
        getHomeCategoryVolumes();

        if (isInitVolumeInfos) {
            //数据查询成功,通知首页更新
            RefreshDataEvent refreshDataEvent = new RefreshDataEvent();
            refreshDataEvent.setMessage("初始化");
            EventBus.getDefault().post(new RefreshDataEvent());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //默认调用一次查询，缓解首次调用耗时较长的情况
                        getChapterListByVolumeId(148);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        refreshSpirituality();
    }

    private void getHomeCategoryVolumes() {
        try {
            twoCategoriesMap.clear();
            allVolumesMap.clear();
            //获取分类信息
            //获取一级分类
            List<Category> rootCategories = categoryHepler.getCategroyList(0);
            for (Category rootCategory : rootCategories) {
                //根据一级分类获取二级分类
                List<Category> twoCategories = categoryHepler.getCategroyList(rootCategory.getId());
                selectBookListWithCategory(twoCategories);
                twoCategoriesMap.put(String.valueOf(rootCategory.getId()), twoCategories);
            }
            isInitVolumeInfos = true;
        } catch (Exception e) {
            e.printStackTrace();
            isInitVolumeInfos = false;
        }

    }

    /**
     * 查询并处理书籍信息
     *
     * @param twoCategories 二级分类列表
     */
    private void selectBookListWithCategory(List<Category> twoCategories) {
        for (Category twoCategory : twoCategories) {
            List<Volume> volumes = volumeDatabaseHepler.getVolumes(twoCategory.getId());
            for (Volume volume : volumes) {
                volume.setVolName(volume.getVolName());
                //转换拼音
                volume.pinyin(getName(volume.getVolName()));

                for (IntroBean introBean : mAllIntroBeans) {
                    if (introBean.getId() == volume.getId()) {
                        if (introBean.getIntro() != null && introBean.getIntro().contains(".mp4")) {
                            volume.setIntro(introBean.getIntro().substring(introBean.getIntro().indexOf(".mp4") + 4));
                            volume.setIntroVideoAdd(introBean.getIntro().substring(0, introBean.getIntro().indexOf(".mp4") + 4));
                        } else {
                            volume.setIntro(introBean.getIntro());
                            volume.setIntroVideoAdd("");
                        }
                        break;
                    }
                }
            }
            allVolumesMap.put(String.valueOf(twoCategory.getId()), volumes);
        }
    }

    private String getName(String volName) {
        String name = volName.replaceAll("\\((.*?)\\)", "").replaceAll("\\[(.*?)\\]", "")
                .replaceAll("\\{(.*?)\\}", "");
        return name;
    }

    public boolean isInitVolumeInfos() {
        return isInitVolumeInfos;
    }

    public LinkedHashMap<String, List<Volume>> getAllVolumesMap() {
        return allVolumesMap;
    }

    //=================================书库信息查询完毕===========================================


    //=================================灵修====================================================
    private List<SpiritualityCategory> refreshSpirituality() {
        SpiritualityCategoryDatabaseHepler spiritualityCategoryDatabaseHepler = new SpiritualityCategoryDatabaseHepler(HuDongApplication.getInstance());
        List<SpiritualityCategory> list = spiritualityCategoryDatabaseHepler.getSpiritualityCategoryList();
        return list;

    }

    //记录已查过的章节列表
    public List<Chapter> getChapterListByVolumeId(int volumeId) {
        if (chapterMap.containsKey(volumeId)) {
            return chapterMap.get(volumeId);
        }
        if (chapterDatabaseHepler == null) {
            chapterDatabaseHepler = new ChapterDatabaseHepler(HuDongApplication.getInstance());
        }
        List<Chapter> list = chapterDatabaseHepler.getChapterList(volumeId);
        Iterator<Chapter> iterator = list.iterator();
        while (iterator.hasNext()) {
            Chapter chapter = iterator.next();
            if (chapter.getShowName().contains("jieshao")) {
                iterator.remove();
            }
        }
        for (Chapter chapter : list) {
            chapter.setIndexId(chapter.getIndexId());
        }
        chapterMap.put(volumeId, list);
        return list;
    }
}
