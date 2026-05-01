package com.read.scriptures.ui.adapter;

import android.app.Activity;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.read.scriptures.EIUtils.EIBaseAdapter;
import com.read.scriptures.R;
import com.read.scriptures.model.Bookmark;
import com.read.scriptures.util.CharUtils;
import com.read.scriptures.util.MTextUtil;
import com.read.scriptures.util.StringUtil;
import com.read.scriptures.widget.HorizontalExpandMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 书签列表
 */
public class BookMarkListAdapter extends EIBaseAdapter<Bookmark> {


    private Activity mContext;
    //是否是操作模式
    private boolean isOperationModel = false;
    //当前点钟
    private List<String> selectIndexs = new ArrayList<>();
    //搜索的关键字
    private List<String> keyWordList = new ArrayList<>();

    private boolean isInit = true;

    private BookMarkListAdapter.CheckedChangeCallback checkedChangeCallback;

    public interface CheckedChangeCallback {
        void checkedChange();

        void jump(Bookmark bookmark);

        void deleteItem(Bookmark bookmark);
    }

    public BookMarkListAdapter(Activity context, List<Bookmark> list, BookMarkListAdapter.CheckedChangeCallback checkedChangeCallback) {
        super(context, list);
        mContext = context;
        this.checkedChangeCallback = checkedChangeCallback;
    }


    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        Holder mHolder;
        if (view == null) {
            mHolder = new Holder();
            view = LayoutInflater.from(mContext).inflate(R.layout.list_book_mark_item, null);
            mHolder.listItemLayout = view.findViewById(R.id.list_item_layout);
            mHolder.ze_icon_first = view.findViewById(R.id.ze_icon_first);
            mHolder.name = (TextView) view.findViewById(R.id.tv_name);
            mHolder.content = (TextView) view.findViewById(R.id.tv_content);
            mHolder.description = (TextView) view.findViewById(R.id.tv_description);
            mHolder.horizontalExpandMenu = (HorizontalExpandMenu) view.findViewById(R.id.expanded_menu);
            mHolder.tvCategory = (TextView) view.findViewById(R.id.tv_category);
            mHolder.tvChapter = (TextView) view.findViewById(R.id.tv_chapter);
            mHolder.tvTime = (TextView) view.findViewById(R.id.tv_book_name);
            mHolder.tvMsg = (TextView) view.findViewById(R.id.tv_msg);
            mHolder.tvCopy = (TextView) view.findViewById(R.id.tv_copy);
            mHolder.tvJump = (TextView) view.findViewById(R.id.tv_jump);
            mHolder.tvShare = (TextView) view.findViewById(R.id.tv_share);
            mHolder.tvDelete = (TextView) view.findViewById(R.id.tv_delete);
            mHolder.flCheck = (FrameLayout) view.findViewById(R.id.fl_check);
            mHolder.cbSelect = (CheckBox) view.findViewById(R.id.cb_select);
            view.setTag(mHolder);
        } else {
            mHolder = (Holder) view.getTag();
        }

        if (position % 2 == 0) {
            mHolder.listItemLayout.setBackground(mContext.getResources().getDrawable(R.drawable.btn_vol_gray));
        } else {
            mHolder.listItemLayout.setBackground(mContext.getResources().getDrawable(R.drawable.btn_vol));
        }


        final Bookmark bookmark = mList.get(position);
//        mHolder.name.setText("《" + bookmark.getVolumeName() + "》" + bookmark.getChapterName());
        mHolder.name.setText(bookmark.getVolumeName().replaceAll("\\((.*?)\\)", "").replaceAll("\\[(.*?)\\]", "")
                .replaceAll("\\{(.*?)\\}", ""));
        if (mHolder.name.getText().toString() != null && MTextUtil.isContainChinese(mHolder.name.getText().toString())) {
            if (mHolder.name.getText().toString().contains("E")) {
                String str = MTextUtil.changeEletter(mHolder.name.getText().toString().trim());
                if (str.length() != mHolder.name.getText().toString().length()) {
                    mHolder.ze_icon_first.setVisibility(View.VISIBLE);
                }
                mHolder.name.setText(str);
            }
        }
        mHolder.tvChapter.setText(bookmark.getChapterName());
        mHolder.tvCategory.setText(bookmark.getCategroyName());

        String desc = bookmark.getDescription();
        String content = bookmark.getContent();
        String contain = CharUtils.match("<[\\u4e00-\\u9fa5]{1,2}\\d+\\.\\d+>", content);
        if (!TextUtils.isEmpty(contain) && content != null) {
            String after = contain.replace("<", "&lt;").replace(">", "&gt;");
            content = content.replace(contain, after);//"<font color='#7b2f2f'>" +after+"</font>"
        }
        for (String keyWord : keyWordList) {
            if (StringUtil.isEmpty(keyWord)) {
                continue;
            }
            if ("<".equals(keyWord)) {
                keyWord = "&lt;";
            }
            if (">".equals(keyWord)) {
                keyWord = "&gt;";
            }
            content = content.replaceAll(Pattern.quote(keyWord), "<font color='#ff0000'>" + keyWord + "</font>");
            if (StringUtil.isNotEmpty(desc)) {
                desc = desc.replaceAll(Pattern.quote(keyWord), "<font color='#ff0000'>" + keyWord + "</font>");
            }
        }

        content = content.replaceAll("〖.*?〗", "");
        mHolder.content.setText(Html.fromHtml("\t\t" + content));
        if (StringUtil.isNotEmpty(bookmark.getDescription())) {
            mHolder.description.setText(Html.fromHtml(desc));
            mHolder.description.setVisibility(View.VISIBLE);
        } else {
            mHolder.description.setVisibility(View.GONE);
        }
        mHolder.tvTime.setText(bookmark.getCreateTime().substring(0, bookmark.getCreateTime().lastIndexOf(":")));

        mHolder.flCheck.setVisibility(isOperationModel ? View.VISIBLE : View.GONE);
        if (selectIndexs.contains(String.valueOf(position))) {
            mHolder.cbSelect.setChecked(true);
        } else {
            mHolder.cbSelect.setChecked(false);
        }
        mHolder.flCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeChecked(position);
            }
        });
        mHolder.horizontalExpandMenu.setVisibility(View.INVISIBLE);
        if (mHolder.horizontalExpandMenu.isExpand()) {
            mHolder.horizontalExpandMenu.expandMenu(1);
        }
//        mHolder.horizontalExpandMenu.setVisibility(isOperationModel ? View.INVISIBLE : View.VISIBLE);
//
//        mHolder.tvMsg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //以短信方式发送
//                CommonUtil.callSystemSmsAction(mContext, "", bookmark.getContent());
//            }
//        });
//        mHolder.tvCopy.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //复制
//                StringBuffer copyTxt = new StringBuffer();
//                copyTxt.append("《" + bookmark.getVolumeName() + "》");
//                copyTxt.append(bookmark.getChapterName());
//                copyTxt.append("\n\t" + bookmark.getContent());
//                copyTxt.append("\n\t" + bookmark.getDescription());
//                CommonUtil.copy(mContext, copyTxt.toString());
//            }
//        });
//        mHolder.tvJump.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //跳转
//                if (checkedChangeCallback != null) {
//                    checkedChangeCallback.jump(bookmark);
//                }
//            }
//        });
//        mHolder.tvShare.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //分享
//                StringBuffer shareSb = new StringBuffer();
//                shareSb.append("《" + bookmark.getVolumeName() + "》");
//                shareSb.append(bookmark.getChapterName());
//                shareSb.append("\n  " + bookmark.getContent());
//                shareSb.append("\n  " + bookmark.getDescription());
//                Share share = new Share();
//                share.setText(shareSb.toString());
//                share.setTitle(bookmark.getVolumeName());
//                ShareUtil.showShare(mContext, share);
//            }
//        });
//        mHolder.tvDelete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //删除
//                if (checkedChangeCallback != null) {
//                    checkedChangeCallback.deleteItem(bookmark);
//                }
//            }
//        });


        return view;
    }


    public String getBookMarkContent(Bookmark bookmark) {
        String content = bookmark.getContent();
        String contain = CharUtils.match("<[\\u4e00-\\u9fa5]{1,2}\\d+\\.\\d+>", content);
        if (!TextUtils.isEmpty(contain) && content != null) {
            String after = contain.replace("<", "&lt;").replace(">", "&gt;");
            content = content.replace(contain, after);//"<font color='#7b2f2f'>" +after+"</font>"
        }
        for (String keyWord : keyWordList) {
            if (StringUtil.isEmpty(keyWord)) {
                continue;
            }
            if ("<".equals(keyWord)) {
                keyWord = "&lt;";
            }
            if (">".equals(keyWord)) {
                keyWord = "&gt;";
            }
            content = content.replaceAll(Pattern.quote(keyWord), "<font color='#ff0000'>" + keyWord + "</font>");
        }
        return content;
//        return Html.fromHtml("\t\t" + content);
    }

    ;

    public String getBookMarkDesc(Bookmark bookmark) {
        String desc = bookmark.getDescription();
        for (String keyWord : keyWordList) {
            if (StringUtil.isEmpty(keyWord)) {
                continue;
            }
            if ("<".equals(keyWord)) {
                keyWord = "&lt;";
            }
            if (">".equals(keyWord)) {
                keyWord = "&gt;";
            }
            if (StringUtil.isNotEmpty(desc)) {
                desc = desc.replaceAll(Pattern.quote(keyWord), "<font color='#ff0000'>" + keyWord + "</font>");
            }
        }

        return desc;
//        return Html.fromHtml(desc);
    }

    ;

    private class Holder {
        LinearLayout listItemLayout;
        TextView name;
        TextView tvCategory;
        TextView tvChapter;
        TextView tvTime;
        TextView content;
        TextView description;
        HorizontalExpandMenu horizontalExpandMenu;
        TextView tvMsg;
        TextView tvCopy;
        TextView tvJump;
        TextView tvShare;
        TextView tvDelete;
        FrameLayout flCheck;
        CheckBox cbSelect;
        ImageView ze_icon_first;
    }

    public List<String> getSelectIndexs() {
        return selectIndexs;
    }


    public void setOperationModel(boolean operationModel) {
        isOperationModel = operationModel;
        if (!isOperationModel) {
            selectIndexs.clear();
        }
        callbackChecked();
        notifyDataSetChanged();
    }

    public boolean isOperationModel() {
        return isOperationModel;
    }

    public void setCheckedAll(boolean isCheckedAll) {
        if (isCheckedAll) {
            selectIndexs.clear();
            for (int i = 0; i < getList().size(); i++) {
                selectIndexs.add(String.valueOf(i));
            }
        } else {
            selectIndexs.clear();
        }
        callbackChecked();
        notifyDataSetChanged();
    }

    public void changeChecked(int position) {
        if (selectIndexs.contains(position + "")) {
            selectIndexs.remove(position + "");
        } else {
            selectIndexs.add(position + "");
        }
        callbackChecked();
        notifyDataSetChanged();
    }


    private void callbackChecked() {
        if (checkedChangeCallback != null) {
            checkedChangeCallback.checkedChange();
        }
    }

    public void setKeyWord(List<String> keywordList) {
        keyWordList.clear();
        keyWordList.addAll(keywordList);
    }
}
