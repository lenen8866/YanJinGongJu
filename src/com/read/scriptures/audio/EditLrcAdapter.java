package com.read.scriptures.audio;

import android.app.Activity;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.read.scriptures.R;
import com.read.scriptures.view.lrc.EditLrcBean;
import com.read.scriptures.view.lrc.Lrc;

import java.util.List;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class EditLrcAdapter extends RecyclerView.Adapter<EditLrcAdapter.EditLrcViewHolder> {

    @Override
    public EditLrcViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_edit_lrc, parent, false);
        return new EditLrcViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EditLrcViewHolder helper, int position) {
        if (scrollPosition == position) {
            helper.tv_content.setTextColor(Color.parseColor("#FFFF00"));
            helper.tv_content.setTextSize(16);
            helper.et_content.setTextColor(Color.parseColor("#FFFF00"));
            helper.et_content.setTextSize(16);
        } else {
            helper.tv_content.setTextColor(Color.WHITE);
            helper.tv_content.setTextSize(14);
            helper.et_content.setTextColor(Color.WHITE);
            helper.et_content.setTextSize(14);
        }
        EditLrcBean item = lrcList.get(position);
        helper.et_content.setText(item.title);
        helper.tv_content.setText(item.title);
        TextWatcherListener textWatcherListener = new TextWatcherListener();
        helper.et_content.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    helper.et_content.post(new Runnable() {
                        @Override
                        public void run() {
                            showInput(helper.et_content);
                        }
                    });
                    helper.et_content.addTextChangedListener(textWatcherListener);
                } else {
                    helper.et_content.removeTextChangedListener(textWatcherListener);
                }
            }
        });
        helper.et_content.clearFocus();
        if (helper.getAdapterPosition() == currentPosition) {
            helper.et_content.setVisibility(View.VISIBLE);
            helper.tv_content.setVisibility(View.GONE);
            helper.et_content.requestFocus();
            helper.et_content.setSelection(helper.et_content.length());
            helper.et_content.performClick();
            helper.et_content.performClick();
            showInput(helper.et_content);
            tempStr.append(item.title);
        } else {
            helper.et_content.setVisibility(View.GONE);
            helper.tv_content.setVisibility(View.VISIBLE);
        }

        helper.view_line.setVisibility(position == currentPosition ? View.VISIBLE : View.GONE);
        helper.tv_cancel.setVisibility(position == currentPosition ? View.VISIBLE : View.GONE);
        helper.tv_save.setVisibility(position == currentPosition ? View.VISIBLE : View.GONE);

        helper.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEditing = true;
                scrollPosition = position;
                setCurrentIndex(position);
            }
        });

        helper.tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCurrentIndex(-1);
                isEditing = false;
            }
        });

        helper.tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEdit = true;
                isEditing = false;
                item.title = tempStr.toString();
                setCurrentIndex(-1);
                hideInput(v);
            }
        });
    }


    @Override
    public int getItemCount() {
        return lrcList == null ? 0 : lrcList.size();
    }

    public List<EditLrcBean> getLrcList() {
        return lrcList;
    }

    private List<EditLrcBean> lrcList;

    public void setNewData(List<EditLrcBean> lrcList) {
        this.lrcList = lrcList;
        notifyDataSetChanged();
    }

    private boolean isEdit = false;//编辑过

    public boolean isEditing() {
        return isEditing;
    }

    private boolean isEditing = false;//编辑中

    public boolean isEdit() {
        return isEdit;
    }

    private int scrollPosition = -1;

    public void setScrollToPosition(int scrollPosition) {
        this.scrollPosition = scrollPosition;
        notifyDataSetChanged();
    }

    class EditLrcViewHolder extends RecyclerView.ViewHolder {
        private EditText et_content;
        private TextView tv_content;
        private View view_line;
        private TextView tv_cancel;
        private TextView tv_save;

        public EditLrcViewHolder(View view) {
            super(view);
            et_content = view.findViewById(R.id.et_content);
            tv_content = view.findViewById(R.id.tv_content);
            view_line = view.findViewById(R.id.view_line);
            tv_cancel = view.findViewById(R.id.tv_cancel);
            tv_save = view.findViewById(R.id.tv_save);
        }
    }

    private StringBuilder tempStr = new StringBuilder();

    class TextWatcherListener implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            // no op
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            tempStr.setLength(0);
            tempStr.append(editable.toString());
        }
    }


    public int getCurrentPosition() {
        return currentPosition;
    }

    private int currentPosition = -1;

    public void setCurrentIndex(int position) {
        tempStr.setLength(0);
        this.currentPosition = position;
        notifyDataSetChanged();
    }

    /**
     * 显示键盘
     *
     * @param et 输入焦点
     */
    public void showInput(EditText et) {
        InputMethodManager imm = (InputMethodManager) et.getContext().getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(et, InputMethodManager.SHOW_FORCED);
    }

    /**
     * 隐藏键盘
     */
    public void hideInput(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(INPUT_METHOD_SERVICE);
        View v = ((Activity) view.getContext()).getWindow().peekDecorView();
        if (null != v) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

}
