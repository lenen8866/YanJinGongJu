package com.read.scriptures.ui.adapter.baike;

import android.content.Context;
import android.text.Html;
import android.widget.TextView;

import com.read.scriptures.R;
import com.read.scriptures.model.Baike;
import com.read.scriptures.ui.adapter.BaseHolderAdapter;
import com.read.scriptures.util.StringUtil;
import com.read.scriptures.EIUtils.ViewHolder;

import java.util.List;

public class BaikeListAdapter extends BaseHolderAdapter<Baike> {

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    private String keyWord;

    public BaikeListAdapter(Context context, List<Baike> list) {
        super(context, list, R.layout.adapter_baike_list_item);
    }

    @Override
    public void convert(ViewHolder helper, int position, Baike item) {
        if (StringUtil.isNotEmpty(keyWord)) {
            ((TextView) helper.getView(R.id.baike_list_name)).setText(Html.fromHtml(item.getName()
                    .replace(keyWord, "<font color=\"#ff0000\">" + keyWord + "</font>")));
        } else {
            helper.setText(R.id.baike_list_name, item.getName());
        }
        String title = item.getContent();
        if (title != null) {
            String[] contents = title.split("\n");
            for (String content : contents) {
                if (StringUtil.isNotEmpty(content) && !content.startsWith(item.getShowName())) {
                    title = content;
                    break;
                }
            }
        }
        helper.setText(R.id.baike_list_title, title);
    }

}
