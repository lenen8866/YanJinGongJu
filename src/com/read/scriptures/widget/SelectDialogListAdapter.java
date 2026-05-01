package com.read.scriptures.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.read.scriptures.R;
import com.read.scriptures.widget.QSelectDialog.SelectActions;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("InflateParams")
public class SelectDialogListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private Context context;
    private float fontSize;

    private List<SelectDialogShowItem> showValues = new ArrayList<SelectDialogShowItem>();

    public List<SelectDialogShowItem> getShowValues() {
        return showValues;
    }

    public void setShowValues(List<SelectDialogShowItem> showValues) {
        this.showValues = showValues;
    }

    private boolean noAddItem;
    private SelectActions action;

    public SelectDialogListAdapter(Context context, List<SelectDialogShowItem> showValues, SelectActions action) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.showValues = showValues;
        this.action = action;
    }

    @Override
    public int getCount() {
        if (noAddItem) {
            return showValues.size();
        }
        // 长度设置为站点列表的长度
        return showValues.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        if (position == 0) {
            return null;
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        TextView itemShow = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_select_dialog_list, null);
            itemShow = (TextView) convertView.findViewById(R.id.select_dialog_list_item_show);

            convertView.setTag(itemShow);
        } else {
            itemShow = (TextView) convertView.getTag();
            if (itemShow == null) {
                convertView = inflater.inflate(R.layout.item_select_dialog_list, null);
                itemShow = (TextView) convertView.findViewById(R.id.select_dialog_list_item_show);
                convertView.setTag(itemShow);
            }
        }
        ImageView delItem = (ImageView) convertView.findViewById(R.id.select_dialog_list_item_show_del);
        if (noAddItem) {
            delItem.setVisibility(View.GONE);
            SelectDialogShowItem item = showValues.get(position);
            if (fontSize != 0) {
                itemShow.setTextSize(fontSize);
            }
            itemShow.setText(Html.fromHtml(item.getName()));
            if (item.getImage() != -1 && item.isShowLeft()) {
                setLeftImage(itemShow, item.getImage());
            }
            return convertView;
        }
        if (position == 0) {
            itemShow.setText("新增选项");
            delItem.setVisibility(View.GONE);
        } else {
            delItem.setVisibility(View.VISIBLE);
            if (position == 1) {
                delItem.setVisibility(View.GONE);
            }
            SelectDialogShowItem item = showValues.get(position - 1);
            itemShow.setText(item.getName());
            if (item.getImage() != -1 && item.isShowLeft()) {
                setLeftImage(itemShow, item.getImage());
            }
            delItem.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    SelectDialogShowItem select = showValues.remove(position - 1);
                    SelectDialogListAdapter.this.notifyDataSetChanged();
                    action.removeItem(select);
                }
            });
        }
        return convertView;
    }

    /**
     * 设置左侧图片
     */
    public void setLeftImage(TextView itemShow, int resId) {
        Drawable img = context.getResources().getDrawable(resId);
        img.setBounds(0, 0, 40, 40);
        itemShow.setCompoundDrawables(img, null, null, null);
        itemShow.setCompoundDrawablePadding(15);
    }

    public boolean isNoAddItem() {
        return noAddItem;
    }

    public void setNoAddItem(boolean noAddItem) {
        this.noAddItem = noAddItem;
    }

    public float getFontSize() {
        return fontSize;
    }

    public void setFontSize(float fontSize) {
        this.fontSize = fontSize;
    }
}
