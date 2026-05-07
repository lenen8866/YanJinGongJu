package com.read.scriptures.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.github.stuxuhai.jpinyin.ChineseHelper;
import com.music.player.lib.util.XToast;
import com.read.scriptures.EIUtils.EIBaseHolderAdapter;
import com.read.scriptures.EIUtils.ExpandGridView;
import com.read.scriptures.EIUtils.ViewHolder;
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
        if (!HomeDataManager.getInstance().isInitVolumeInfos()) {
            return false;
        }
        mVvolumeMaps.clear();
        mCategories.clear();

        boolean isSortNormal = SharedUtil.getBoolean(Preference_home_sort_type, true);
        if (isSortNormal) {
            // 按分类分组展示
            for (Category category : categories) {
                List<Volume> volumes = HomeDataManager.getInstance().getAllVolumesMap().get(String.valueOf(category.getId()));
                if (volumes == null) volumes = new ArrayList<>();
                bookCount += volumes.size();
                category.setVolCount(volumes.size());
                mVvolumeMaps.put(category, volumes);
            }
            mCategories.addAll(categories);
        } else {
            // 按拼音首字母分组展示
            List<Volume> allVolumeList = new ArrayList<>();
            for (Category category : categories) {
                List<Volume> volumes = HomeDataManager.getInstance().getAllVolumesMap().get(String.valueOf(category.getId()));
                if (volumes != null) allVolumeList.addAll(volumes);
            }
            Collections.sort(allVolumeList, new Comparator<Volume>() {
                @Override
                public int compare(Volume o1, Volume o2) {
                    return o1.getPinyin().compareTo(o2.getPinyin());
                }
            });
            LinkedHashMap<String, List<Volume>> groupByLetter = new LinkedHashMap<>();
            for (Volume v : allVolumeList) {
                List<Volume> group = groupByLetter.get(v.getFirstLetter());
                if (group == null) {
                    group = new ArrayList<>();
                    groupByLetter.put(v.getFirstLetter(), group);
                }
                group.add(v);
            }
            name = categories.get(0).getCateName();
            for (String firstLetter : groupByLetter.keySet()) {
                List<Volume> group = groupByLetter.get(firstLetter);
                Category category = new Category();
                category.setCateName(firstLetter);
                category.setVolCount(group.size());
                bookCount += group.size();
                mVvolumeMaps.put(category, group);
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
        List<Volume> volumeList = mVvolumeMaps.get(item);
        if (volumeList == null) {
            return;
        }
        int numColumn = mIsList ? 1 : 3;

        final ExpandGridView gridView = helper.getView(R.id.view_list);

        // 复用已有的 VolumeGridAdapter，避免每次滚动都重新 setAdapter
        Object tagObj = gridView.getTag(R.id.view_list);
        Integer cachedCategoryId = (Integer) gridView.getTag(R.id.tv_title);
        boolean isSameCategoryAndMode = cachedCategoryId != null
                && cachedCategoryId.equals(item.getId())
                && gridView.getNumColumns() == numColumn;

        final VolumeGridAdapter volumeGridAdapter;
        if (isSameCategoryAndMode && tagObj instanceof VolumeGridAdapter) {
            volumeGridAdapter = (VolumeGridAdapter) tagObj;
        } else {
            volumeGridAdapter = new VolumeGridAdapter(mContext, volumeList, mIsList);
            gridView.setTag(R.id.view_list, volumeGridAdapter);
            gridView.setTag(R.id.tv_title, item.getId());
            gridView.setVerticalSpacing(1);
            if (!mIsList) gridView.setHorizontalSpacing(1);
            gridView.setNumColumns(numColumn);
            gridView.invalidateMeasureCache();
            gridView.setAdapter(volumeGridAdapter);

            // adapter 首次绑定时设置两个 click事件
            volumeGridAdapter.setOnItemLongClickListener(new VolumeGridAdapter.OnItemOnLongClickListener() {
                @Override
                public void onItemLongClick(AdapterView<?> parent, View v, int position, String intro, String introVideo) {
                    showIntroDialog(mContext, intro, introVideo);
                }
            });
            volumeGridAdapter.setOnItemClickListener(new VolumeGridAdapter.OnItemOnClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View v, int position) {
                    Volume volume = volumeGridAdapter.getItem(position);
                    String categoryName = ChineseHelper.containsChinese(item.getCateName())
                            ? currenCategoryName + "-" + item.getCateName()
                            : currenCategoryName + "-" + name;
                    Bundle bd = new Bundle();
                    bd.putParcelable(BundleConstants.PARAM_VOLUME, volume);
                    bd.putString("categoryName", categoryName);
                    Intent intent = new Intent(mContext, ChaptersListActivity.class);
                    intent.putExtras(bd);
                    ((Activity) mContext).startActivityForResult(intent, -1);
                }
            });
        }

        helper.setText(R.id.tv_title, item.getCateName().replaceAll("^\\d{1,}-", ""));
        helper.setText(R.id.tv_count, volumeList.isEmpty() ? "暂无书籍" : "共" + volumeList.size() + "本");
    }

    private void showIntroDialog(final Context context, String intro, String introVideo) {
        if (StringUtil.isEmpty(intro) && StringUtil.isEmpty(introVideo)) {
            XToast.showToast(context, "暂无简介");
            return;
        }
        IntroDialogFragment.getInstance(intro, introVideo).show(mActivity.getSupportFragmentManager(), "intro");
    }

    public List<Category> getCategories() {
        return mCategories;
    }
}
