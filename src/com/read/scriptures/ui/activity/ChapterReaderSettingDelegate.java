package com.read.scriptures.ui.activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.read.scriptures.R;
import com.read.scriptures.app.HuDongApplication;
import com.read.scriptures.config.SystemConfig;
import com.read.scriptures.ui.adapter.BackgroundColorAdapter;
import com.read.scriptures.ui.adapter.ChapterReadSlidingAdapter;
import com.read.scriptures.ui.adapter.TextColorAdapter;
import com.read.scriptures.util.StatusBarUtils;
import com.read.scriptures.widget.SpacesItemDecoration;
import com.read.scriptures.widget.colorpicker.ColorPickerDialog;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 阅读设置模块（从 ChapterReaderActivity 抽取）
 *
 * 职责：
 *   - 字体大小调节（长按加减）
 *   - 背景颜色 / 文字颜色切换
 *   - 行距 / 段距 / 左右边距调节
 *   - 自定义颜色选择器
 *   - 繁简切换显示状态
 *
 * 使用方式（在 ChapterReaderActivity 中）：
 *   mSettingDelegate = new ChapterReaderSettingDelegate(this, mChapterReadSlidingAdapter);
 *   mSettingDelegate.initSettingPop();   // 初始化设置弹窗
 *   mSettingDelegate.initMarginPop();    // 初始化边距弹窗
 *   mSettingDelegate.initColorPop();     // 初始化颜色弹窗
 *   mSettingDelegate.getPopSetting()     // 获取弹窗实例供主文件使用
 */
public class ChapterReaderSettingDelegate {

    // 弱引用 Activity，防止内存泄漏
    private final WeakReference<ChapterReaderActivity> mRef;
    // Adapter 引用，设置变化时通知刷新
    private final ChapterReadSlidingAdapter mAdapter;

    // 设置弹窗
    public PopupWindow popSetting;
    public PopupWindow popMarginSetting;
    public PopupWindow popColorSetting;

    // 字体设置控件
    private ImageView tv_size_small;
    private ImageView iv_font;
    private ImageView tv_size_big;
    private TextView  tv_size;
    private TextView  tv_traditional;
    private ImageView iv_2_hor, iv_3_hor, iv_4_hor, iv_full_4_hor, iv_3_ver;
    private ImageView iv_more_margin;
    private RadioGroup  rg_background;
    private RadioButton iv_text_white, iv_text_yellow, iv_text_grey;
    private RadioButton iv_text_green, iv_text_blue, iv_more_color;

    // 边距设置控件
    private ImageView iv_back_margin;
    private SeekBar   sb_line, sb_section, sb_left_right;
    private TextView  tv_left_right, tv_line, tv_section;
    public  TextView  return_default; // 主文件 initSpeechTts 会调用此控件

    // 颜色设置控件
    private ImageView   iv_back_color;
    private RecyclerView rv_text, rv_background;

    // 字号长按定时器
    private ScheduledExecutorService scheduledExecutor;

    // 字号调节通过 Handler 发消息给主文件处理
    private final Handler mHandler;

    public ChapterReaderSettingDelegate(ChapterReaderActivity activity,
                                        ChapterReadSlidingAdapter adapter,
                                        Handler handler) {
        mRef     = new WeakReference<>(activity);
        mAdapter = adapter;
        mHandler = handler;
    }

    private ChapterReaderActivity getActivity() {
        return mRef.get();
    }

    // ===================== 初始化弹窗 =====================

    /** 初始化"阅读设置"弹窗（字体大小、背景色、边距入口） */
    @SuppressLint("InflateParams")
    public void initSettingPop() {
        ChapterReaderActivity act = getActivity();
        if (act == null) return;

        View view = LayoutInflater.from(act).inflate(R.layout.pop_read_setting, null);
        tv_size_small  = view.findViewById(R.id.tv_size_small);
        iv_font        = view.findViewById(R.id.iv_font);
        tv_size_big    = view.findViewById(R.id.tv_size_big);
        tv_size        = view.findViewById(R.id.tv_size);
        tv_traditional = view.findViewById(R.id.tv_traditional);
        iv_2_hor       = view.findViewById(R.id.iv_2_hor);
        iv_3_hor       = view.findViewById(R.id.iv_3_hor);
        iv_4_hor       = view.findViewById(R.id.iv_4_hor);
        iv_full_4_hor  = view.findViewById(R.id.iv_full_4_hor);
        iv_3_ver       = view.findViewById(R.id.iv_3_ver);
        iv_more_margin = view.findViewById(R.id.iv_more_margin);
        rg_background  = view.findViewById(R.id.rg_background);
        iv_text_white  = view.findViewById(R.id.iv_text_white);
        iv_text_yellow = view.findViewById(R.id.iv_text_yellow);
        iv_text_grey   = view.findViewById(R.id.iv_text_grey);
        iv_text_green  = view.findViewById(R.id.iv_text_green);
        iv_text_blue   = view.findViewById(R.id.iv_text_blue);
        iv_more_color  = view.findViewById(R.id.iv_more_color);

        // 背景色切换监听
        rg_background.setOnCheckedChangeListener((group, checkedId) -> {
            if (popColorSetting == null) return;
            int bg = -1, text = -1;
            if      (checkedId == R.id.iv_text_white)  { bg = R.color.color_read_white;  text = R.color.color_text_white; }
            else if (checkedId == R.id.iv_text_yellow) { bg = R.color.color_read_yellow; text = R.color.color_text_yellow; }
            else if (checkedId == R.id.iv_text_grey)   { bg = R.color.color_read_grey;   text = R.color.color_text_grey; }
            else if (checkedId == R.id.iv_text_green)  { bg = R.color.color_read_green;  text = R.color.color_text_green; }
            else if (checkedId == R.id.iv_text_blue)   { bg = R.color.color_read_blue;   text = R.color.color_text_blue; }
            else if (checkedId == R.id.iv_more_color)  {
                popColorSetting.showAtLocation(act.mParentView, Gravity.BOTTOM, 0, 0);
                return;
            }
            if (bg != -1) {
                HuDongApplication.getInstance().setBackgroudColor(act.getResources().getColor(bg));
                HuDongApplication.getInstance().setTextColor(act.getResources().getColor(text));
                setBackTint();
                mAdapter.setBackgroudColor(act.getResources().getColor(bg));
                mAdapter.setTextColor(act.getResources().getColor(text));
                mAdapter.notifyDataSetChanged();
            }
        });

        iv_more_color.setOnClickListener(v -> {
            if (popColorSetting != null)
                popColorSetting.showAtLocation(act.mParentView, Gravity.BOTTOM, 0, 0);
        });

        // 繁简状态
        tv_traditional.setText(HuDongApplication.getInstance().getTextModel()
                == SystemConfig.TEXT_MODEL_FANTI ? "简" : "繁");

        // 字号长按加减
        tv_size_small.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) updateAddOrSubtract(v.getId());
            else if (event.getAction() == MotionEvent.ACTION_UP) stopAddOrSubtract();
            return true;
        });
        tv_size_big.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) updateAddOrSubtract(v.getId());
            else if (event.getAction() == MotionEvent.ACTION_UP) stopAddOrSubtract();
            return true;
        });

        iv_font.setOnClickListener(v -> act.showToastPkg("正在开发中..."));

        // 其他控件点击交由主文件的 onClick 处理
        tv_size.setOnClickListener(act);
        tv_traditional.setOnClickListener(act);
        iv_2_hor.setOnClickListener(act);
        iv_3_hor.setOnClickListener(act);
        iv_4_hor.setOnClickListener(act);
        iv_full_4_hor.setOnClickListener(act);
        iv_3_ver.setOnClickListener(act);
        iv_more_margin.setOnClickListener(act);

        popSetting = new PopupWindow(view,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        popSetting.setTouchable(true);
        popSetting.setFocusable(true);
        popSetting.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popSetting.setAnimationStyle(R.style.pop_bottom);
        popSetting.setOnDismissListener(() ->
                StatusBarUtils.setBackgroundAlpha(act, 1.0f));

        if (HuDongApplication.getInstance().getReadModel() == SystemConfig.READ_MODEL_NORMAL) {
            setChecked();
        }
    }

    /** 初始化边距弹窗（行距/段距/左右边距） */
    @SuppressLint("InflateParams")
    public void initMarginPop() {
        ChapterReaderActivity act = getActivity();
        if (act == null) return;

        View view = LayoutInflater.from(act).inflate(R.layout.pop_magin_setting, null);
        iv_back_margin = view.findViewById(R.id.iv_back_margin);
        sb_line        = view.findViewById(R.id.line_seek_bar);
        sb_section     = view.findViewById(R.id.section_seek_bar);
        sb_left_right  = view.findViewById(R.id.left_right_seek_bar);
        tv_left_right  = view.findViewById(R.id.tv_left_right_margin);
        tv_line        = view.findViewById(R.id.tv_line_margin);
        tv_section     = view.findViewById(R.id.tv_section_margin);
        return_default = view.findViewById(R.id.return_default);

        sb_line.setProgress(HuDongApplication.getInstance().getmLineMargin());
        sb_section.setProgress(HuDongApplication.getInstance().getTextMagin());
        sb_left_right.setProgress(HuDongApplication.getInstance().getTextAround());
        tv_left_right.setText(HuDongApplication.getInstance().getTextAround() + "");
        tv_line.setText(HuDongApplication.getInstance().getmLineMargin() + "");
        tv_section.setText(HuDongApplication.getInstance().getTextMagin() + "");

        sb_line.setOnSeekBarChangeListener(act);
        sb_section.setOnSeekBarChangeListener(act);
        sb_left_right.setOnSeekBarChangeListener(act);
        iv_back_margin.setOnClickListener(act);
        return_default.setOnClickListener(act);

        popMarginSetting = new PopupWindow(view,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        popMarginSetting.setTouchable(true);
        popMarginSetting.setFocusable(true);
        popMarginSetting.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popMarginSetting.setAnimationStyle(R.style.pop_bottom);
        popMarginSetting.setOnDismissListener(() -> {
            HuDongApplication.getInstance().setmLineMargin(sb_line.getProgress());
            HuDongApplication.getInstance().setTextMagin(sb_section.getProgress());
            HuDongApplication.getInstance().setTextAround(sb_left_right.getProgress());
            mAdapter.setLineMargin(sb_line.getProgress());
            mAdapter.setTextMargin(sb_section.getProgress());
            mAdapter.setTextAroundMargin(sb_left_right.getProgress());
            mAdapter.notifyDataSetChanged();
            StatusBarUtils.setBackgroundAlpha(act, 1.0f);
        });
    }

    /** 初始化自定义颜色弹窗 */
    @SuppressLint("InflateParams")
    public void initColorPop() {
        ChapterReaderActivity act = getActivity();
        if (act == null) return;

        View view = LayoutInflater.from(act).inflate(R.layout.pop_color_setting, null);
        iv_back_color = view.findViewById(R.id.iv_back_color);
        rv_text       = view.findViewById(R.id.rv_text_color);
        rv_background = view.findViewById(R.id.rv_background_color);
        View view_text_color = view.findViewById(R.id.view_text_color);
        View view_bg_color   = view.findViewById(R.id.view_bg_color);

        int textColor = HuDongApplication.getInstance().getTextColor();
        int bgColor   = HuDongApplication.getInstance().getBackgroudColor();

        view_text_color.setOnClickListener(v -> showColorDialog(v, textColor));
        view_bg_color.setOnClickListener(v -> showBgColorDialog(v, bgColor));

        SpacesItemDecoration decoration = new SpacesItemDecoration(8);
        rv_text.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL));
        rv_background.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL));
        rv_text.addItemDecoration(decoration);
        rv_background.addItemDecoration(decoration);

        // 25 个颜色格，用空字符串数组占位，颜色值在 Adapter 内部定义
        String[] placeholder = new String[25];
        TextColorAdapter textColorAdapter = new TextColorAdapter(act, Arrays.asList(placeholder));
        BackgroundColorAdapter bgAdapter  = new BackgroundColorAdapter(act, Arrays.asList(placeholder));
        rv_text.setAdapter(textColorAdapter);
        rv_background.setAdapter(bgAdapter);

        textColorAdapter.setOnItemClickListener((v, pos) -> setTextColor(pos));
        bgAdapter.setOnItemClickListener((v, pos) -> setBackgroundColor(pos));

        iv_back_color.setOnClickListener(act);

        popColorSetting = new PopupWindow(view,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        popColorSetting.setTouchable(true);
        popColorSetting.setFocusable(true);
        popColorSetting.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popColorSetting.setAnimationStyle(R.style.pop_bottom);
        popColorSetting.setOnDismissListener(() ->
                StatusBarUtils.setBackgroundAlpha(act, 1.0f));
    }

    // ===================== 颜色设置 =====================

    /**
     * 设置文字颜色（position 0-24 对应 text_1 到 text_25）
     * 原来是 25 个 case 的 switch，精简为循环查表
     */
    public void setTextColor(int position) {
        ChapterReaderActivity act = getActivity();
        if (act == null) return;
        int[] colors = {
            R.color.text_1,  R.color.text_2,  R.color.text_3,  R.color.text_4,  R.color.text_5,
            R.color.text_6,  R.color.text_7,  R.color.text_8,  R.color.text_9,  R.color.text_10,
            R.color.text_11, R.color.text_12, R.color.text_13, R.color.text_14, R.color.text_15,
            R.color.text_16, R.color.text_17, R.color.text_18, R.color.text_19, R.color.text_20,
            R.color.text_21, R.color.text_22, R.color.text_23, R.color.text_24, R.color.text_25
        };
        if (position < 0 || position >= colors.length) return;
        int color = act.getResources().getColor(colors[position]);
        HuDongApplication.getInstance().setTextColor(color);
        setBackTint();
        mAdapter.setTextColor(color);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 设置背景颜色（position 0-24 对应 bac_1 到 bac_25）
     * 原来是 25 个 case 的 switch，精简为循环查表
     */
    public void setBackgroundColor(int position) {
        ChapterReaderActivity act = getActivity();
        if (act == null) return;
        int[] colors = {
            R.color.bac_1,  R.color.bac_2,  R.color.bac_3,  R.color.bac_4,  R.color.bac_5,
            R.color.bac_6,  R.color.bac_7,  R.color.bac_8,  R.color.bac_9,  R.color.bac_10,
            R.color.bac_11, R.color.bac_12, R.color.bac_13, R.color.bac_14, R.color.bac_15,
            R.color.bac_16, R.color.bac_17, R.color.bac_18, R.color.bac_19, R.color.bac_20,
            R.color.bac_21, R.color.bac_22, R.color.bac_23, R.color.bac_24, R.color.bac_25
        };
        if (position < 0 || position >= colors.length) return;
        int color = act.getResources().getColor(colors[position]);
        HuDongApplication.getInstance().setBackgroudColor(color);
        setBackTint();
        mAdapter.setBackgroudColor(color);
        mAdapter.notifyDataSetChanged();
    }

    /** 同步顶部栏、状态栏的文字颜色和背景色 */
    public void setBackTint() {
        ChapterReaderActivity act = getActivity();
        if (act == null) return;
        int textColor = HuDongApplication.getInstance().getTextColor();
        int bgColor   = HuDongApplication.getInstance().getBackgroudColor();

        Drawable up = ContextCompat.getDrawable(act, R.drawable.ic_back);
        Drawable wrapped = DrawableCompat.wrap(up);
        DrawableCompat.setTint(wrapped, textColor);
        act.iv_back.setImageDrawable(wrapped);

        act.dex.setBackgroundColor(textColor);
        act.mTitleVolumeTextView.setTextColor(textColor);
        act.mTitletvCategoryType.setTextColor(textColor);
        act.mTitleChapterTextView.setTextColor(textColor);
        act.mStatusTimeTextView.setTextColor(textColor);
        act.mStatusWeekTextView.setTextColor(textColor);
        act.mStatusBatteryTextView.setTextColor(textColor);
        act.mStatusLayout.setBackgroundColor(bgColor);
        act.mTitleLayout.setBackgroundColor(bgColor);
        StatusBarUtils.initColorStatusBar(act, bgColor);

        // 同步选中状态和电池图标颜色
        syncBatteryAndRadio(act, bgColor);
    }

    private void syncBatteryAndRadio(ChapterReaderActivity act, int bgColor) {
        int white  = act.getResources().getColor(R.color.color_read_white);
        int yellow = act.getResources().getColor(R.color.color_read_yellow);
        int grey   = act.getResources().getColor(R.color.color_read_grey);
        int green  = act.getResources().getColor(R.color.color_read_green);
        int blue   = act.getResources().getColor(R.color.color_read_blue);

        if (iv_text_white == null) return; // 弹窗未初始化时跳过

        if (bgColor == white)        { iv_text_white.setChecked(true);  act.mStatusBatteryView.setColor(0xFF2B2B2B); act.mStatusBatteryView.setFillColor(0xFFFFFFFF); }
        else if (bgColor == yellow)  { iv_text_yellow.setChecked(true); act.mStatusBatteryView.setColor(0xFF2B2B2B); act.mStatusBatteryView.setFillColor(0xFFFFFFFF); }
        else if (bgColor == grey)    { iv_text_grey.setChecked(true);   act.mStatusBatteryView.setColor(0xFFFFFFFF); act.mStatusBatteryView.setFillColor(0xFF2B2B2B); }
        else if (bgColor == green)   { iv_text_green.setChecked(true);  act.mStatusBatteryView.setColor(0xFF2B2B2B); act.mStatusBatteryView.setFillColor(0xFFFFFFFF); }
        else if (bgColor == blue)    { iv_text_blue.setChecked(true);   act.mStatusBatteryView.setColor(0xFF2B2B2B); act.mStatusBatteryView.setFillColor(0xFFFFFFFF); }
        else                         { iv_more_color.setChecked(true); }
    }

    /** 初始化时同步选中状态（布局和背景图标） */
    public void setChecked() {
        setBackTint();
        ChapterReaderActivity act = getActivity();
        if (act == null) return;
        int around = HuDongApplication.getInstance().getTextAround();
        int margin = HuDongApplication.getInstance().getTextMagin();
        if      (around == 20 && margin == 40) setBackground(R.id.iv_2_hor);
        else if (around == 20 && margin == 30) setBackground(R.id.iv_3_hor);
        else if (around == 20 && margin == 20) setBackground(R.id.iv_4_hor);
        else if (around == 0  && margin == 20) setBackground(R.id.iv_full_4_hor);
        else                                   setBackground(R.id.iv_more_margin);
    }

    /** 设置布局模式按钮的选中背景 */
    public void setBackground(int selectedId) {
        ChapterReaderActivity act = getActivity();
        if (act == null || iv_2_hor == null) return;
        int normal  = R.drawable.btn_border_normal;
        int pressed = R.drawable.btn_normal_pressed;
        int more    = R.drawable.ic_more_setting;
        int moreP   = R.drawable.ic_more_setting_pressed;

        iv_2_hor.setBackground(act.getResources().getDrawable(selectedId == R.id.iv_2_hor      ? pressed : normal));
        iv_3_hor.setBackground(act.getResources().getDrawable(selectedId == R.id.iv_3_hor      ? pressed : normal));
        iv_4_hor.setBackground(act.getResources().getDrawable(selectedId == R.id.iv_4_hor      ? pressed : normal));
        iv_full_4_hor.setBackground(act.getResources().getDrawable(selectedId == R.id.iv_full_4_hor ? pressed : normal));
        iv_3_ver.setBackground(act.getResources().getDrawable(selectedId == R.id.iv_3_ver      ? pressed : normal));
        iv_more_margin.setBackground(act.getResources().getDrawable(selectedId == R.id.iv_more_margin ? moreP : more));
    }

    // ===================== 颜色选择器 =====================

    private void showColorDialog(View v, int textColor) {
        ChapterReaderActivity act = getActivity();
        if (act == null) return;
        ColorPickerDialog dialog = new ColorPickerDialog(act, textColor == -1 ? Color.BLACK : textColor);
        dialog.setOnColorChangedListener(color -> {
            HuDongApplication.getInstance().setTextColor(color);
            setBackTint();
            mAdapter.setTextColor(color);
            mAdapter.notifyDataSetChanged();
        });
        dialog.show();
    }

    private void showBgColorDialog(View v, int bgColor) {
        ChapterReaderActivity act = getActivity();
        if (act == null) return;
        ColorPickerDialog dialog = new ColorPickerDialog(act, bgColor == -1 ? Color.WHITE : bgColor);
        dialog.setOnColorChangedListener(color -> {
            HuDongApplication.getInstance().setBackgroudColor(color);
            setBackTint();
            mAdapter.setBackgroudColor(color);
            mAdapter.notifyDataSetChanged();
        });
        dialog.show();
    }

    /** 显示边距弹窗，并同步当前数值到进度条 */
    public void showMarginPop(View parentView) {
        if (sb_line != null) sb_line.setProgress(HuDongApplication.getInstance().getmLineMargin());
        if (sb_section != null) sb_section.setProgress(HuDongApplication.getInstance().getTextMagin());
        if (sb_left_right != null) sb_left_right.setProgress(HuDongApplication.getInstance().getTextAround());
        if (tv_left_right != null) tv_left_right.setText(HuDongApplication.getInstance().getTextAround() + "");
        if (tv_line != null) tv_line.setText(HuDongApplication.getInstance().getmLineMargin() + "");
        if (tv_section != null) tv_section.setText(HuDongApplication.getInstance().getTextMagin() + "");
        if (popMarginSetting != null)
            popMarginSetting.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
    }

    /** 重置边距进度条到默认值 */
    public void resetMarginProgress(int line, int section, int leftRight) {
        if (sb_line != null) sb_line.setProgress(line);
        if (sb_section != null) sb_section.setProgress(section);
        if (sb_left_right != null) sb_left_right.setProgress(leftRight);
        if (tv_left_right != null) tv_left_right.setText(leftRight + "");
        if (tv_line != null) tv_line.setText(line + "");
        if (tv_section != null) tv_section.setText(section + "");
    }

    /** 更新字号显示 */
    public void updateTextSizeTv(int size) {
        if (tv_size != null) tv_size.setText(String.valueOf(size));
    }

    /** 更新行距显示 */
    public void updateLineTv(int progress) {
        if (tv_line != null) tv_line.setText(progress + "");
    }

    /** 更新段距显示 */
    public void updateSectionTv(int progress) {
        if (tv_section != null) tv_section.setText(progress + "");
    }

    /** 更新左右边距显示 */
    public void updateLeftRightTv(int progress) {
        if (tv_left_right != null) tv_left_right.setText(progress + "");
    }

    /** 更新繁简按钮文字（true=繁体，false=简体） */
    public void updateTraditionalTv(boolean isFanti) {
        if (tv_traditional != null) tv_traditional.setText(isFanti ? "简" : "繁");
    }

    // ===================== 字号长按定时器 =====================

    /** 开始长按计时器，每 200ms 向主文件 Handler 发一次消息 */
    public void updateAddOrSubtract(int viewId) {
        scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutor.scheduleWithFixedDelay(() -> {
            Message msg = new Message();
            msg.what = viewId;
            mHandler.sendMessage(msg);
        }, 0, 200, TimeUnit.MILLISECONDS);
    }

    /** 手指抬起，停止计时器 */
    public void stopAddOrSubtract() {
        if (scheduledExecutor != null) {
            scheduledExecutor.shutdownNow();
            scheduledExecutor = null;
        }
    }
}
