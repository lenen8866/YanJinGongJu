package com.read.scriptures.widget;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.music.player.lib.util.XToast;
import com.read.scriptures.R;
import com.read.scriptures.util.DensityUtil;
import com.read.scriptures.util.PreferencesUtils;
import com.read.scriptures.view.color.ColorPicker;

import java.io.IOException;
import java.util.ArrayList;

public class ColorPickDialogFt extends DialogFragment implements ColorPicker.ColorSelectedListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.MyDialogFragment);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            Window window = getDialog().getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            params.gravity = Gravity.CENTER;
            params.width = DensityUtil.dip2px(280);
            params.height = -2;
            params.windowAnimations = R.style.DialogBottomInAnim;
            window.setAttributes(params);
            window.setBackgroundDrawableResource(R.drawable.ft_dialog_bg_shape);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(getLayout(), container, false);
    }

    private int getLayout() {
        return R.layout.dialog_color_layout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private SeekBar sb_bar;
    private View iv_color;
    private TextView tv_percentage;
    private RecyclerView rcv_color;
    private GradientDrawable drawable;

    private ColorListAdapter colorListAdapter;
    private ViewGroup mainView;

    private void initView(View view) {
        mainView = (ViewGroup) view;
        ColorPicker colorPicker = view.findViewById(R.id.baseColorPicker);
        sb_bar = view.findViewById(R.id.sb_bar);
        iv_color = view.findViewById(R.id.iv_color);
        rcv_color = view.findViewById(R.id.rcv_color);
        tv_percentage = view.findViewById(R.id.tv_percentage);

        ViewGroup.LayoutParams layoutParams = colorPicker.getLayoutParams();
        layoutParams.height = DensityUtil.dip2px(280);

        colorPicker.setGradientView(R.drawable.color_picker);
        colorPicker.setColorPicker(R.drawable.rectangle_color_circle);
        colorPicker.setColorSelectedListener(this);
        drawable = (GradientDrawable) ContextCompat.getDrawable(view.getContext(), R.drawable.icon_seekbar_bg1);

        rcv_color.setLayoutManager(new GridLayoutManager(view.getContext(), 5, GridLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        colorListAdapter = new ColorListAdapter();
        rcv_color.setAdapter(colorListAdapter);


        try {
            colorList = (ArrayList<Integer>) PreferencesUtils.getObject(getContext(), "cache_audio_tag_color");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (colorList == null) {
            colorList = new ArrayList<>();
            colorList.add(Color.BLACK);
            colorList.add(-1);
        }
        colorListAdapter.setNewData(colorList);

        sb_bar.setBackground(drawable);
        sb_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    tv_percentage.setText(i + "%");
                    alphaColor = mathColorAlpha();
                    iv_color.setBackgroundColor(alphaColor);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        colorListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                int color = colorListAdapter.getItem(position);
                if (color == -1) {
                    addColor();
                } else {
                    if (colorSetCallBack != null) {
                        colorSetCallBack.onColorSet(color);
                        dismissAllowingStateLoss();
                    }
                }
            }
        });
        iv_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (colorSetCallBack != null) {
                    colorSetCallBack.onColorSet(alphaColor);
                    dismissAllowingStateLoss();
                }
            }
        });
        colorListAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                showDeletePop(view, colorListAdapter.getItem(position));
                return true;
            }
        });
        if (defaultColor == 0) {
            orgColor = Color.BLACK;
        } else {
            orgColor = defaultColor;
        }
        colors[1] = orgColor;
        drawable.setColors(colors);
        sb_bar.setBackground(drawable);
        iv_color.setBackgroundColor(orgColor);
        alphaColor = mathColorAlpha();
    }

    private int defaultColor = 0;

    public void setDefaultColor(int color) {
        defaultColor = color;
    }

    private void showDeletePop(View view, Integer item) {
        View popView = getLayoutInflater().inflate(R.layout.pop_color_delete_layout, null);
        TextView tv_delete = popView.findViewById(R.id.tv_delete);
        int width = DensityUtil.dip2px(popView.getContext(), 42);
        PopupWindow popupWindow = new PopupWindow(popView, width, -2, true);
        tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                colorList.remove(item);
                colorListAdapter.setNewData(colorList);
                try {
                    PreferencesUtils.putObject(getContext(), "cache_audio_tag_color", colorList);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        int[] location = new int[2];
        view.getLocationInWindow(location);
        popView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        // 计算contentView的高宽
        int windowWidth = popView.getMeasuredWidth();
        int windowHeight = popView.getMeasuredHeight();
        popupWindow.showAtLocation(mainView, Gravity.NO_GRAVITY, location[0] - windowWidth + 3, location[1] - windowHeight);
    }


    private int colors[] = {Color.TRANSPARENT, Color.BLACK};

    private ArrayList<Integer> colorList;

    private int orgColor;//原始颜色
    private int alphaColor;//带透明度

    @Override
    public void onColorSelected(@ColorInt int color, boolean isTapUp) {
        orgColor = color;
        colors[1] = color;
        drawable.setColors(colors);
        sb_bar.setBackground(drawable);
        alphaColor = mathColorAlpha();
        iv_color.setBackgroundColor(alphaColor);
    }

    public void addColor() {
        if (colorList.size() >= 10) {
            XToast.showToast(getContext(), "最多添加9个");
            return;
        }
        colorList.add(colorList.size() - 1, alphaColor);
        colorListAdapter.setNewData(colorList);
        try {
            PreferencesUtils.putObject(getContext(), "cache_audio_tag_color", colorList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int mathColorAlpha() {
        //计算百分比
        int progress = sb_bar.getProgress();
        float percentage = progress / 100f;
        //计算透明度值
        int alpha = (int) (percentage * 255);
        //把计算的头目的值转16进制
        String hex = Integer.toHexString(alpha);
        if (hex.length() < 2) {//转成10进制可能只有一位
            hex = "0" + hex;
        }
        //把选中的颜色转16进制
        String noAlphaColorHex = Integer.toHexString(orgColor);
        //替换掉选中颜色的透明度
        String alphaColorHex = noAlphaColorHex.replaceFirst("ff", hex);
        //最后转为10进制
        return Color.parseColor("#" + alphaColorHex);
    }

    public void setCallBack(ColorSetCallBack callBack) {
        colorSetCallBack = callBack;
    }

    private ColorSetCallBack colorSetCallBack;

    public interface ColorSetCallBack {
        void onColorSet(int color);
    }
}
