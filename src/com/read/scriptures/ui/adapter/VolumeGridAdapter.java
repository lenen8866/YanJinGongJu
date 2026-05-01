package com.read.scriptures.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
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
 * Created by Administrator.
 * Datetime: 2015/7/2.
 * Email: lgmshare@mgail.com
 * 主页下方的GridView展示
 */
public class VolumeGridAdapter extends EIBaseAdapter<Volume> {

    private boolean mIsList;


    public VolumeGridAdapter(Context context, List<Volume> volumeList, boolean isList) {
        super(context, volumeList);
        mIsList = isList;
    }

    @Override
    public int getCount() {
        if (mIsList) {
            return super.getCount();
        } else {
            if (getList().size() % 3 == 0) {
                return super.getCount();
            }
            return super.getCount() + (3 - getList().size() % 3);
        }
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

        String content = item.getVolName()
                .replaceAll("\\((.*?)\\)", "")
                .replaceAll("\\[(.*?)\\]", "")
                .replaceAll("\\{(.*?)\\}", "");

        viewHolder.tvTitle.setText(content);
        if (viewHolder.tvTitle.getText() != null && !TextUtils.isEmpty(viewHolder.tvTitle.getText().toString())) {
            String title = viewHolder.tvTitle.getText().toString();
            if (title.contains("E") && isContainChinese(title)) {
                title = title.replace(title.charAt(title.length() - 1) + "", "").trim();
                viewHolder.tvTitle.setText(title);
                if (mIsList) {
                    if (title.length() > 15) {
                        viewHolder.ze_icon_last.setVisibility(View.VISIBLE);
                    } else {
                        viewHolder.ze_icon.setVisibility(View.VISIBLE);
                    }
                    if (viewHolder.tvAuthor.getVisibility() != View.VISIBLE) {
                        viewHolder.tvAuthor.setVisibility(View.GONE);
                    }
                }
            }
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
        viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onItemClick((AdapterView<?>) parent, v, position);
            }
        });
        String introduction1 = item.getIntro();
        String introduction2 = item.getIntroVideoAdd();
        viewHolder.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (longClickListener != null)
                    longClickListener.onItemLongClick((AdapterView<?>) parent, view, position, introduction1, introduction2);
                return true;
            }
        });
        return convertView;
    }

    public static boolean isContainChinese(String str) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
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
