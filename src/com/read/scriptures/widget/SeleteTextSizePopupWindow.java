package com.read.scriptures.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.read.scriptures.R;


public class SeleteTextSizePopupWindow extends PopupWindow {

    private View mMenuView;

    public SeleteTextSizePopupWindow(Context context) {
        super(context);
    }

    public SeleteTextSizePopupWindow(Activity context, int textSize, OnSeekBarChangeListener listener) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.popup_selete_text_size_layout, null);
        mMenuView.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                int height = mMenuView.findViewById(R.id.pop_layout).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });
        SeekBar seekBar = ((SeekBar) mMenuView.findViewById(R.id.seek));
        int progress = (textSize - 16) * 5;
        seekBar.setProgress(progress);
        seekBar.setOnSeekBarChangeListener(listener);

        this.setContentView(mMenuView);
        this.setBackgroundDrawable(new ColorDrawable(0x00000000));
        this.setWidth(LayoutParams.MATCH_PARENT);
        this.setHeight(LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setAnimationStyle(R.style.AnimBottom);
    }
}
