package com.read.scriptures.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.github.stuxuhai.jpinyin.ChineseHelper;
import com.music.player.lib.util.XToast;
import com.read.scriptures.EIUtils.EIBaseHolderAdapter;
import com.read.scriptures.R;
import com.read.scriptures.config.PreferenceConfig;
import com.read.scriptures.constants.BundleConstants;
import com.read.scriptures.manager.HomeDataManager;
import com.read.scriptures.model.Category;
import com.read.scriptures.model.Volume;
import com.read.scriptures.ui.activity.ChaptersListActivity;
import com.read.scriptures.ui.fragment.IntroDialogFragment;
import com.read.scriptures.util.SharedUtil;
import com.read.scriptures.util.StringUtil;
import com.read.scriptures.EIUtils.ViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

import static com.read.scriptures.config.PreferenceConfig.Preference_home_sort_type;


/**
 * 主页下方的list展示 对号的一整块
 */
public class CategorySectionAdapter extends EIBaseHolderAdapter<Category> {

    private boolean mIsList;
    private FragmentActivity mActivity;
    private String currenCategoryName;
    private String name = null;

    public void setCurrenCategoryName(String currenCategoryName) {
        this.currenCategoryName = currenCategoryName;
        System.out.println("-------------------------currenCategoryName-----------" + currenCategoryName);
    }

    private List<Category> mCategories;
    private LinkedHashMap<Category, List<Volume>> mVvolumeMaps;


    public CategorySectionAdapter(FragmentActivity context) {
        super(context, R.layout.adapter_home_category_section_gv_only);
        mContext = context;
        mActivity = context;
        mCategories = new ArrayList<>();
        mVvolumeMaps = new LinkedHashMap<>();
    }

    private int bookCount;

    public int getBookCount() {
        return bookCount;
    }

    public boolean setNodeCategorys(List<Category> categories) {
        bookCount = 0;

        boolean isSortNormal = SharedUtil.getBoolean(Preference_home_sort_type, true);
        if (!HomeDataManager.getInstance().isInitVolumeInfos()) {
            //未初始化
            return false;
        }
        mVvolumeMaps.clear();
        //数据已初始化
        if (isSortNormal) {//按分类分组
            for (int i = 0; i < categories.size(); i++) {
                List<Volume> volumes = HomeDataManager.getInstance().getAllVolumesMap().get(String.valueOf(categories.get(i).getId()));
                if (volumes == null) {
                    volumes = new ArrayList<>();
                }

                bookCount = bookCount + volumes.size();
                categories.get(i).setVolCount(volumes.size());
                mVvolumeMaps.put(categories.get(i), volumes);
            }
            mCategories.clear();
            mCategories.addAll(categories);
        } else {
            List<Volume> allVolumeList = new ArrayList<>();
            for (int i = 0; i < categories.size(); i++) {
                List<Volume> volumes = HomeDataManager.getInstance().getAllVolumesMap().get(String.valueOf(categories.get(i).getId()));
                if (volumes == null) {
                    volumes = new ArrayList<>();
                }
                allVolumeList.addAll(volumes);
            }
            //重新组数据
            LinkedHashMap<String, List<Volume>> linkedHashMap = new LinkedHashMap<>();
            Collections.sort(allVolumeList, new Comparator<Volume>() {
                @Override
                public int compare(Volume o1, Volume o2) {
                    return o1.getPinyin().compareTo(o2.getPinyin());
                }
            });
            for (Volume v : allVolumeList) {
                if (linkedHashMap.containsKey(v.getFirstLetter())) {
                    linkedHashMap.get(v.getFirstLetter()).add(v);
                } else {
                    List<Volume> volumePinyins = new ArrayList<>();
                    volumePinyins.add(v);
                    linkedHashMap.put(v.getFirstLetter(), volumePinyins);
                }
            }
            mCategories.clear();
            name = categories.get(0).getCateName();
            for (String firstPinyin : linkedHashMap.keySet()) {
                Category category = new Category();
                category.setCateName(firstPinyin);
                List<Volume> volumePinyins = linkedHashMap.get(firstPinyin);
                mVvolumeMaps.put(category, volumePinyins);
                bookCount = bookCount + volumePinyins.size();
                category.setVolCount(volumePinyins.size());
                mCategories.add(category);
            }
        }
        return true;
    }

    @Override
    public int getCount() {
        return mCategories.size();
    }

    @Override
    public Category getItem(int position) {
        if (position >= mCategories.size()) return null;
        return mCategories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public void convert(ViewHolder helper, final Category item) {
        mIsList = SharedUtil.getBoolean(PreferenceConfig.Preference_home_list_type, true);
        if (mVvolumeMaps == null) {
            return;
        }
        List<Volume> volumeList = mVvolumeMaps.get(item);
        if (volumeList == null) {
            return;
        }
        int numColumn = 1;
        int count = volumeList.size();
        if (!mIsList) {
            numColumn = 3;
        }
        final GridView gridView = helper.getView(R.id.view_list);
        VolumeGridAdapter volumeGridAdapter;
        volumeGridAdapter = new VolumeGridAdapter(mContext, volumeList, mIsList);
        gridView.setTag(volumeGridAdapter);
        gridView.setVerticalSpacing(1);
        gridView.setAdapter(volumeGridAdapter);
        gridView.setNumColumns(numColumn);
        if (!mIsList) {
            gridView.setHorizontalSpacing(1);
        }
        volumeGridAdapter.notifyDataSetChanged();
        helper.setText(R.id.tv_title, item.getCateName().replaceAll("^\\d{1,}-", ""));
        helper.setText(R.id.tv_count, volumeList.isEmpty() ? "暂无书籍" : "共" + count + "本");

        //设置每本书长按事件-弹出播放视频对话框
        volumeGridAdapter.setOnItemLongClickListener(new VolumeGridAdapter.OnItemOnLongClickListener() {
            @Override
            public void onItemLongClick(AdapterView<?> parent, View v, int position, String intro, String introVideo) {
                if (position >= volumeGridAdapter.getList().size()) {
                    return;
                }
                showIntroDialog(mContext, intro, introVideo);
            }
        });
        //设置每本书点击事件-跳转到章节
        volumeGridAdapter.setOnItemClickListener(new VolumeGridAdapter.OnItemOnClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position) {
                if (position >= volumeGridAdapter.getList().size()) {
                    return;
                }

                VolumeGridAdapter adapter = (VolumeGridAdapter) parent.getAdapter();
                Volume volume = adapter.getItem(position);
                String categoryName;
                if (ChineseHelper.containsChinese(item.getCateName())) {
                    categoryName = currenCategoryName + "-" + item.getCateName();
                } else {
                    categoryName = currenCategoryName + "-" + name;
                }
                Bundle bd = new Bundle();
                bd.putParcelable(BundleConstants.PARAM_VOLUME, volume);
                bd.putString("categoryName", categoryName);
                Intent intent=new Intent();
                intent.setClass(mContext, ChaptersListActivity.class);
                intent.putExtras(bd);
                ((Activity) mContext).startActivityForResult(intent,-1);
            }
        });

    }

    private void showIntroDialog(final Context context, String intro, String introVideo) {
        if (StringUtil.isEmpty(intro) && StringUtil.isEmpty(intro)) {
            XToast.showToast(context, "暂无简介");
            return;
        }
        IntroDialogFragment.getInstance(intro, introVideo).show(mActivity.getSupportFragmentManager(), "intro");
    }

    public List<Category> getCategories() {
        return mCategories;
    }

    public int getVolumes() {
        return mVvolumeMaps.size();
    }
}
