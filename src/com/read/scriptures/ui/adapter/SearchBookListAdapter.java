package com.read.scriptures.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.read.scriptures.EIUtils.EIBaseHolderAdapter;
import com.read.scriptures.R;
import com.read.scriptures.model.Bookmark;
import com.read.scriptures.model.Category;
import com.read.scriptures.util.CharUtils;
import com.read.scriptures.util.DisplayUtil;
import com.read.scriptures.EIUtils.ViewHolder;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator. Datetime: 2015/7/2. Email: lgmshare@mgail.com
 */
public class SearchBookListAdapter extends EIBaseHolderAdapter<Bookmark> {

    private int mSearchType = 1;
    private Map<Category, List<Category>> mRootCategoryMaps;

    public SearchBookListAdapter(Context context) {
        super(context, R.layout.adapter_search_book_item);
    }

    public SearchBookListAdapter(Context context, Map<Category, List<Category>> mRootCategoryMaps) {
        super(context, R.layout.adapter_search_book_item);
        this.mRootCategoryMaps = mRootCategoryMaps;
    }

    public void setSearchType(int searchType) {
        this.mSearchType = searchType;
    }

    public String getType(Bookmark item) {
        if (mRootCategoryMaps != null && mRootCategoryMaps.size() > 0) {
            for (Map.Entry<Category, List<Category>> entry : mRootCategoryMaps.entrySet()) {
                for (Category category : entry.getValue()) {
                    if (item.getCategroyId().equals(category.getId() + "")) {
                        return entry.getKey().getCateName() + "-" + category.getCateName();
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void convert(ViewHolder helper, Bookmark item) {
        LinearLayout linearRoot = helper.getView(R.id.linear_root);
        LinearLayout ll_top = helper.getView(R.id.ll_top);
        TextView tv_title = helper.getView(R.id.tv_title);
        ImageView ze_icon = helper.getView(R.id.ze_icon);
        TextView tv_year = helper.getView(R.id.tv_year);
        TextView tv_chapter = helper.getView(R.id.tv_chapter_name);
        TextView tv_time = helper.getView(R.id.tv_book_name);
        TextView tv_content = helper.getView(R.id.tv_content);
        TextView tv_book_type = helper.getView(R.id.tv_book_type);
        tv_title.setVisibility(View.VISIBLE);
        if (helper.getPosition() % 2 == 0) {
            linearRoot.setBackground(mContext.getResources().getDrawable(R.drawable.btn_vol_gray));
        } else {
            linearRoot.setBackground(mContext.getResources().getDrawable(R.drawable.btn_vol));
        }
        String result = item.getContent();
        ll_top.setVisibility(View.VISIBLE);
        tv_book_type.setVisibility(View.GONE);
        if (mSearchType == 1) {
            ll_top.setVisibility(View.GONE);
            if (result.endsWith("E")) {
                result = result.substring(0, result.lastIndexOf("E"));
                Drawable drawable = mContext.getResources().getDrawable(R.drawable.ic_tag_ze1);
                // 这一步必须要做,否则不会显示.
                drawable.setBounds(0, 0, (int) DisplayUtil.dp2px(mContext, 15), (int) DisplayUtil.dp2px(mContext, 15));
                tv_content.setCompoundDrawables(null, null, drawable, null);
            } else {
                tv_content.setCompoundDrawables(null, null, null, null);
            }
            String time = CharUtils.match("\\((.*?)\\)", result);
            if (time != null) {
                tv_time.setText(Html.fromHtml("<font color='#7e7e7e'>" + time + "</font>"));
            }

            String header = CharUtils.match("\\{(.*?)\\}", result);
            if (!TextUtils.isEmpty(header)) {
                if (header.length() > 2) {//说明不止{}
                    result = result.replaceAll("\\((.*?)\\)", "");
                } else {
                    result = result.replaceAll("\\((.*?)\\)", "").replaceAll("\\{(.*?)\\}", "");
                }
            }

            String year = CharUtils.match("\\((.*?)\\)", item.getVolumeName());
            if (!TextUtils.isEmpty(result))
                tv_content.setText(Html.fromHtml(result));
            if (!TextUtils.isEmpty(year)) {
                year = year.replaceAll("\\(", "").replaceAll("\\)", "");
                tv_time.setText(year);
            }
            tv_content.setLineSpacing(0.5f, 1.0f);
            tv_book_type.setVisibility(View.VISIBLE);
            tv_book_type.setText(getType(item));
        } else if (mSearchType == 3) {
            if (result != null) {
                result = "\t" + result;
            }
            String title = item.getVolumeName().replaceAll("\\{(.*?)\\}", "").replaceAll("\\((.*?)\\)", "");
            String year = CharUtils.match("\\((.*?)\\)", item.getVolumeName());
            if (!TextUtils.isEmpty(title)) {
                if (title.endsWith("E")) {
                    title = title.substring(0, title.lastIndexOf("E"));
                    ze_icon.setVisibility(View.VISIBLE);
                } else {
                    ze_icon.setVisibility(View.GONE);
                }
                tv_title.setText(title);
            }
            if (!TextUtils.isEmpty(year)) {
                year = year.replaceAll("\\(", "").replaceAll("\\)", "");
                tv_year.setText(year);
            }
            tv_chapter.setText(getType(item));
            tv_content.setText(Html.fromHtml(result));
            tv_content.setLineSpacing(0.5f, 1.0f);
        } else if (mSearchType == 9) {
            // 百科搜索显示
            if (result != null) {
                result = "\t" + result;
            }
            tv_title.setTextSize(18);
            tv_title.setText(Html.fromHtml("<b>" + item.getChapterName() + "</b>"));
            tv_content.setText(Html.fromHtml(result));
            tv_content.setLineSpacing(0.5f, 1.0f);
        } else {
            if (result != null) {
                result = "\t\t\t" + result;
            }
            String version = null;
            if (result.contains("〖中文〗") && result.contains("〖/中文〗")) {
                version = "中文";
            } else if (result.contains("〖英文〗") && result.contains("〖/英文〗")) {
                version = "英文";
            } else if (result.contains("〖和合本〗") && result.contains("〖/和合本〗")) {
                version = "和合本";
            } else if (result.contains("〖吕振中") && result.contains("〖/吕振中〗")) {
                version = "吕振中";
            } else if (result.contains("〖思高本〗") && result.contains("〖/思高本〗")) {
                version = "思高本";
            } else if (result.contains("〖现代本〗") && result.contains("〖/现代本〗")) {
                version = "现代本";
            } else if (result.contains("〖新译本〗") && result.contains("〖/新译本〗")) {
                version = "新译本";
            } else if (result.contains("〖当代版〗") && result.contains("〖/当代版〗")) {
                version = "当代版";
            } else if (result.contains("〖KJV〗") && result.contains("〖/KJV〗")) {
                version = "KJV";
            } else if (result.contains("〖NIV〗") && result.contains("〖/NIV〗")) {
                version = "NIV";
            } else if (result.contains("〖BBE〗") && result.contains("〖/BBE〗")) {
                version = "BBE";
            } else if (result.contains("〖ASV〗") && result.contains("〖/ASV〗")) {
                version = "ASV";
            }
            if (!TextUtils.isEmpty(version)) {
                result = result.replace("〖" + version + "〗", "");
                result = result.replace("〖/" + version + "〗", "");
                result += ("<font color='#E0E0E0'> " + version + "</font>");
            }
            if (mSearchType == 2 && result.contains("<b>")) {
                //标题搜索只显示标题高亮
                String front = result.substring(0, result.indexOf("<b>") + "<b>".length());
                String title = result.substring(result.indexOf("<b>") + "<b>".length(), result.indexOf("</b>"));
                String end = result.substring(result.indexOf("</b>"), result.length());
                result = front.replaceAll("<font color='#ff0000'>", "").replaceAll("</font>", "")
                        + title
                        + end.replaceAll("<font color='#ff0000'>", "").replaceAll("</font>", "");
            } else {
                result = result.replaceAll("<font color='#ff0000'>", "<b><font color='#ff0000'>");
                result = result.replaceAll("</font>", "</font></b>");
            }
            String contain = CharUtils.match("<[\\u4e00-\\u9fa5]{1,2}\\d+\\.\\d+>", result);
            String title = item.getVolumeName().replaceAll("\\{(.*?)\\}", "").replaceAll("\\((.*?)\\)", "");
            String year = CharUtils.match("\\((.*?)\\)", item.getVolumeName());
            String chapterName = item.getChapterName();
            if (!TextUtils.isEmpty(title)) {
                if (title.endsWith("E")) {
                    title = title.substring(0, title.lastIndexOf("E"));
                    ze_icon.setVisibility(View.VISIBLE);
                } else {
                    ze_icon.setVisibility(View.GONE);
                }
                tv_title.setText(title);
            }
            if (!TextUtils.isEmpty(year)) {
                year = year.replaceAll("\\(", "").replaceAll("\\)", "");
                tv_year.setText(year);
            }

            if (!TextUtils.isEmpty(chapterName)) {
                tv_chapter.setText(chapterName);
            }
            if (!TextUtils.isEmpty(contain) && result != null) {
                String after = contain.replace("<", "&lt;").replace(">", "&gt;");
                result = result.replace(contain, "<font color='#7b2f2f'>" + after + "</font>");
            }
            if (result.contains("<h3>") && result.contains("</h3>")) {
                result = result.replaceAll("<h3>", "").replaceAll("</h3>", "");
            }
            tv_content.setText(Html.fromHtml(result));
            tv_content.setLineSpacing(0.5f, 1.5f);
        }
    }
}
