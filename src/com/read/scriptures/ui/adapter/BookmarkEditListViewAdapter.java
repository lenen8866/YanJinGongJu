package com.read.scriptures.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.read.scriptures.EIUtils.EIBaseAdapter;
import com.read.scriptures.R;
import com.read.scriptures.model.Bookmark;
import com.read.scriptures.util.MTextUtil;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BookmarkEditListViewAdapter extends EIBaseAdapter<Bookmark> {

    private Context mContext;
    private Holder mHolder;

    public BookmarkEditListViewAdapter(Context context, List<Bookmark> list) {
        super(context, list);
        mContext = context;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if (view == null) {
            mHolder = new Holder();
            view = LayoutInflater.from(mContext).inflate(R.layout.adapter_bookmark_edit_item, null);
            mHolder.name = (TextView) view.findViewById(R.id.tv_name);
            mHolder.ze_icon_first = (ImageView) view.findViewById(R.id.ze_icon_first);
            mHolder.content = (TextView) view.findViewById(R.id.tv_content);
            mHolder.tv_chapter_name = (TextView) view.findViewById(R.id.tv_chapter_name);
            view.setTag(mHolder);
        } else {
            mHolder = (Holder) view.getTag();
        }

        Bookmark bookmark = mList.get(position);
        String bookName = matchSearchText(bookmark.getVolumeName());
        mHolder.name.setText(bookName);
        if (mHolder.name.getText().toString() != null && MTextUtil.isContainChinese(mHolder.name.getText().toString())) {
            if (mHolder.name.getText().toString().contains("E")) {
                String str = MTextUtil.changeEletter(mHolder.name.getText().toString().trim());
                if (str.length() != mHolder.name.getText().toString().length()) {
                    mHolder.ze_icon_first.setVisibility(View.VISIBLE);
                }
                mHolder.name.setText(str);
            }
        }
        mHolder.tv_chapter_name.setText(bookmark.getChapterName());
        String content = bookmark.getReplaceContent().replaceAll("〖.*?〗", "");
        mHolder.content.setText("\t\t" + content);
        return view;
    }

    private String matchSearchText(String str) {
        String regex1 = "\\{([^}])*\\}";
        Pattern P1 = Pattern.compile(regex1);
        Matcher matcher1 = P1.matcher(str);
        while (matcher1.find()) {
            str = str.replace(matcher1.group(), "");
        }
        String regex2 = "\\[([^}])*\\]";
        Pattern P2 = Pattern.compile(regex2);
        Matcher matcher2 = P2.matcher(str);
        while (matcher2.find()) {
            str = str.replace(matcher2.group(), "");
        }
        String regex3 = "\\(([^}])*\\)";
        Pattern P3 = Pattern.compile(regex3);
        Matcher matcher3 = P3.matcher(str);
        while (matcher3.find()) {
            str = str.replace(matcher3.group(), "");
        }
        return str;
    }

    private class Holder {
        TextView name;
        TextView content;
        TextView tv_chapter_name;
        ImageView ze_icon_first;
    }
}
