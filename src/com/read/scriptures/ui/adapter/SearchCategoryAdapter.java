package com.read.scriptures.ui.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import androidx.fragment.app.FragmentActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.music.player.lib.util.XToast;
import com.read.scriptures.R;
import com.read.scriptures.app.HuDongApplication;
import com.read.scriptures.bean.IntroBean;
import com.read.scriptures.config.PreferenceConfig;
import com.read.scriptures.db.ChapterDatabaseHepler;
import com.read.scriptures.db.VolumeDatabaseHepler;
import com.read.scriptures.model.Category;
import com.read.scriptures.model.Volume;
import com.read.scriptures.ui.fragment.IntroDialogFragment;
import com.read.scriptures.util.SharedUtil;
import com.read.scriptures.util.StringUtil;
import com.read.scriptures.EIUtils.ViewHolder;
import com.read.scriptures.widget.Video.WxMediaController;
import com.read.scriptures.widget.Video.WxPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchCategoryAdapter extends BaseHolderAdapter<Category> {
    private static final String TAG = "SearchCategoryAdapter";

    private final boolean mIsList;
    private FragmentActivity mActivity;

    private VolumeDatabaseHepler mVolumeHepler;
    private List<Category> mCategories = new ArrayList<>();
    private Map<Category, List<Volume>> mVvolumeMaps;
    private ChapterDatabaseHepler chapterDatabaseHepler;
    private AlertDialog alertDialog;
    private AdapterView.OnItemClickListener itemClickListener;

    // 缓存KEY
    private static final String getIntroListCacheKey = "Key_getIntroList";

    public SearchCategoryAdapter(FragmentActivity context) {
        super(context, SharedUtil.getBoolean(PreferenceConfig.Preference_home_list_type, true) ? R.layout.adapter_home_category_section : R.layout.adapter_home_category_section_gv);
        mActivity = context;
        mCategories = new ArrayList<Category>();
        mVvolumeMaps = new HashMap<Category, List<Volume>>();
        mVolumeHepler = new VolumeDatabaseHepler(context);
        chapterDatabaseHepler = new ChapterDatabaseHepler(context);
        mIsList = SharedUtil.getBoolean(PreferenceConfig.Preference_home_list_type, true);
    }

    public void setNodeCategorys(List<Category> categories) {
        // 性能优化
        // List<IntroBean> introBeans = chapterDatabaseHepler.getIntroList();
        List<IntroBean> introBeans = null;
        String cache = HuDongApplication.getInstance().getaCache().getAsString(getIntroListCacheKey);
        // Log.d(TAG, "allIntroBeans cache:" + cache);
        if (TextUtils.isEmpty(cache)) {
            cache = HuDongApplication.getInstance().getIntroListAssets();
        }

        if(!TextUtils.isEmpty(cache)){
            try {
                // 加载缓存
                introBeans = JSON.parseArray(cache, IntroBean.class);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, e.getMessage());
                introBeans = null;
            }
        }

        for (int i = 0; i < categories.size(); i++) {
            List<Volume> volumes = mVolumeHepler.getVolumes(categories.get(i).getId());
            for (Volume volume : volumes) {
                volume.setVolName(volume.getVolName());
                for (IntroBean introBean : introBeans) {
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
            mVvolumeMaps.put(categories.get(i), volumes);
        }
        mCategories.clear();
        mCategories.addAll(categories);
    }

    @Override
    public int getCount() {
        return mCategories.size();
    }

    @Override
    public Category getItem(int position) {
        return mCategories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public void convert(ViewHolder helper, int position, final Category item) {
        List<Volume> volumeList = mVvolumeMaps.get(item);
        int numColumn = 1;
        if (!mIsList) {
            numColumn = 3;
        }
        helper.setText(R.id.tv_title, item.getCateName());
        final VolumeGridAdapter volumeGridAdapter = new VolumeGridAdapter(mContext,mVvolumeMaps.get(item), mIsList);
        if (!mIsList) {
            GridView gridView = helper.getView(R.id.view_list);
            gridView.setTag(position);
            gridView.setNumColumns(numColumn);
            gridView.setHorizontalSpacing(1);
            gridView.setVerticalSpacing(1);
            gridView.setAdapter(volumeGridAdapter);
            gridView.setOnItemClickListener(itemClickListener);
        } else {
            ListView listView = helper.getView(R.id.view_list);
            listView.setTag(position);
            listView.setAdapter(volumeGridAdapter);
            listView.setOnItemClickListener(itemClickListener);
        }
        volumeGridAdapter.notifyDataSetChanged();
        volumeGridAdapter.setOnItemClickListener(new VolumeGridAdapter.OnItemOnClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position) {
                itemClickListener.onItemClick(parent, v, position, position);
            }
        });
        volumeGridAdapter.setOnItemLongClickListener(new VolumeGridAdapter.OnItemOnLongClickListener() {
            @Override
            public void onItemLongClick(AdapterView<?> parent, View v, int position, String intro, String introVideo) {
//                showIntroDialog(mContext, intro, introVideo);
                if (position >= volumeGridAdapter.getList().size()){
                    return;
                }
                if (StringUtil.isEmpty(intro) && StringUtil.isEmpty(intro)){
                    XToast.showToast(mContext, "暂无简介");
                    return;
                }
                IntroDialogFragment.getInstance(intro, introVideo).show(mActivity.getSupportFragmentManager(), "intro");
            }
        });

    }

    public AdapterView.OnItemClickListener getItemClickListener() {
        return itemClickListener;
    }

    public void setItemClickListener(AdapterView.OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    private void showIntroDialog(Context context, String intro, String introVideo) {
        View view = LayoutInflater.from(context).inflate(R.layout.no_cache_hint, null);
        TextView tvJoin = view.findViewById(R.id.tv_join);
        TextView tvClose = view.findViewById(R.id.tv_close);
        final WxPlayer wxPlayer = view.findViewById(R.id.wx_player);

        if (introVideo != null && !introVideo.trim().equals("")) {
            wxPlayer.setVisibility(View.VISIBLE);
            wxPlayer.setVideoPath(introVideo);
            wxPlayer.setMediaController(new WxMediaController(context));
        }
        tvJoin.setText(intro);
        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (wxPlayer != null) {
                    wxPlayer.release();
                }
            }
        });
        alertDialog.show();

        Window window = alertDialog.getWindow();
        DisplayMetrics dm2 = context.getResources().getDisplayMetrics();
        WindowManager.LayoutParams p = window.getAttributes(); // 获取对话框当前的参数值
        p.height = (int) (dm2.heightPixels * 0.8); // 改变的是dialog框在屏幕中的位置而不是大小
        p.width = (int) (dm2.widthPixels * 0.9); // 宽度设置为屏幕的0.65
        window.setAttributes(p);
    }
}
