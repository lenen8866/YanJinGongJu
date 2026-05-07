package com.read.scriptures.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.read.scriptures.EIUtils.EIBaseAdapter;
import com.read.scriptures.R;
import com.read.scriptures.model.Volume;
import com.read.scriptures.util.CharUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 主页下方的 GridView 展示，支持列表和网格两种模式
 */
public class VolumeGridAdapter extends EIBaseAdapter<Volume> {

    private final boolean mIsList;
    // 静态缓存正则，避免每次 getView 都重新编译
    private static final Pattern CHINESE_PATTERN = Pattern.compile("[\u4e00-\u9fa5]");
    // 用于删除书名中括号内容的正则，刘表/方括号、花括号
    private static final Pattern BRACKET_PATTERN = Pattern.compile("\\(.*?\\)|\\[.*?\\]|\\{.*?\\}");

    public VolumeGridAdapter(Context context, List<Volume> volumeList, boolean isList) {
        super(context, volumeList);
        mIsList = isList;
    }

    @Override
    public int getCount() {
        int size = mList.size();
        if (mIsList || size % 3 == 0) return size;
        // 网格模式下补齐空白格子至 3 的倍数
        return size + (3 - size % 3);
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            if (mIsList) {
                convertView = mLayoutInflater.inflate(R.layout.adapter_volume_gv_list_item, parent, false);
            } else {
                convertView = mLayoutInflater.inflate(R.layout.adapter_volume_gv_grid_item, parent, false);
            }
            viewHolder.linearLayout = convertView.findViewById(R.id.linear_root);
            viewHolder.tvHead = convertView.findViewById(R.id.tv_head);
            viewHolder.tvTitle = convertView.findViewById(R.id.tv_title);
            viewHolder.tvAuthor = convertView.findViewById(R.id.tv_cate);
            viewHolder.tvIntro = convertView.findViewById(R.id.tv_intro);
            if (mIsList) {
                viewHolder.ze_icon = convertView.findViewById(R.id.ze_icon);
                viewHolder.ze_icon_last = convertView.findViewById(R.id.ze_icon_last);
            }
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (position >= mList.size()) {
            //补得空数据
            viewHolder.tvHead.setText("");
            viewHolder.tvTitle.setText("");
            return convertView;
        }
        Volume item = getItem(position);
        viewHolder.tvHead.setText(item.getHeader());

        // 删除书名中括号内的内容（作者、备注等），用预编译的静态 Pattern
        String content = BRACKET_PATTERN.matcher(item.getVolName()).replaceAll("").trim();
        viewHolder.tvTitle.setText(content);

        // 如果书名包含英文字母 E 且有中文，表示末尾有特殊标记，删除并显示对应图标
        if (mIsList && content.contains("E") && isContainChinese(content)) {
            String title = content.substring(0, content.length() - 1).trim();
            viewHolder.tvTitle.setText(title);
            if (title.length() > 15) {
                viewHolder.ze_icon_last.setVisibility(View.VISIBLE);
            } else {
                viewHolder.ze_icon.setVisibility(View.VISIBLE);
            }
            viewHolder.tvAuthor.setVisibility(View.GONE);
        }
        String intro = item.getIntro();
        String author = CharUtils.match("\\((.*?)\\)", item.getVolName());
        if (!TextUtils.isEmpty(author)) {
            author = author.replaceAll("\\(", "").replaceAll("\\)", "");
            viewHolder.tvAuthor.setText(author);
        } else {
            viewHolder.tvAuthor.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(intro)) {
            intro = intro.replaceAll("\n", "").replaceAll(" ", "").replaceAll("　", "");
            viewHolder.tvIntro.setText(intro);
        } else {
            intro = "暂无简介";
            viewHolder.tvIntro.setText(intro);
        }
        // 只在第一次绑定时设置 listener，复用时无需重复 new 匿名类
        if (viewHolder.linearLayout.getTag(R.id.linear_root) == null) {
            viewHolder.linearLayout.setTag(R.id.linear_root, Boolean.TRUE);
            viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = (int) v.getTag(R.id.tv_title);
                    if (listener != null)
                        listener.onItemClick((AdapterView<?>) parent, v, pos);
                }
            });
            viewHolder.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int pos = (int) view.getTag(R.id.tv_title);
                    if (pos < mList.size() && longClickListener != null) {
                        Volume v2 = mList.get(pos);
                        longClickListener.onItemLongClick((AdapterView<?>) parent, view, pos, v2.getIntro(), v2.getIntroVideoAdd());
                    }
                    return true;
                }
            });
        }
        // 每次更新 position tag，让 listener 拿到正确的位置
        viewHolder.linearLayout.setTag(R.id.tv_title, position);
        return convertView;
    }

    public static boolean isContainChinese(String str) {
        // 使用静态缓存的 Pattern，避免重复编译
        Matcher m = CHINESE_PATTERN.matcher(str);
        return m.find();
    }

    private OnItemOnClickListener listener;

    public void setOnItemClickListener(OnItemOnClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemOnClickListener {
        void onItemClick(AdapterView<?> parent, View v, int position);
    }

    private OnItemOnLongClickListener longClickListener;

    public void setOnItemLongClickListener(OnItemOnLongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }

    public interface OnItemOnLongClickListener {
        void onItemLongClick(AdapterView<?> parent, View v, int position, String intro, String introVideo);
    }


    class ViewHolder {
        LinearLayout linearLayout;
        TextView tvHead;
        TextView tvTitle;
        TextView tvAuthor;
        TextView tvIntro;
        ImageView ze_icon;
        ImageView ze_icon_last;

    }
}
