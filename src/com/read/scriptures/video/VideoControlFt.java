package com.read.scriptures.video;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.music.player.lib.util.XToast;
import com.read.scriptures.R;
import com.read.scriptures.app.HuDongApplication;
import com.read.scriptures.util.PreferencesUtils;

public class VideoControlFt extends DialogFragment {

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
            params.gravity = mGravity;
            params.width = width;
            params.height = height;
            params.windowAnimations = anim;
            window.setAttributes(params);
            window.setBackgroundDrawableResource(R.drawable.ft_dialog_bg_shape);
        }
    }

    int anim;
    int mGravity;
    int width;
    int height;
    boolean isFullScreen;

    public void setFullScreen(boolean b) {
        isFullScreen = b;
        if (b) {
            mGravity = Gravity.BOTTOM | Gravity.RIGHT;
            width = getScreenHeight();
            height = ViewGroup.LayoutParams.WRAP_CONTENT;
            anim = R.style.DialogRightInAnim;
        } else {
            mGravity = Gravity.BOTTOM;
            width = getScreenWidth();
            height = ViewGroup.LayoutParams.WRAP_CONTENT;
            anim = R.style.DialogBottomInAnim;
        }
    }

    private int getScreenHeight() {
        DisplayMetrics dm = new DisplayMetrics();
        dm = HuDongApplication.getContext().getResources().getDisplayMetrics();
        return dm.heightPixels; // 屏幕高（像素，如：1280px）
    }

    private int getScreenWidth() {
        DisplayMetrics dm = new DisplayMetrics();
        dm = HuDongApplication.getContext().getResources().getDisplayMetrics();
        return dm.widthPixels; // 屏幕高（像素，如：1280px）
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(getLayout(), container, false);
    }

    private int getLayout() {
        return R.layout.layout_video_more;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private boolean isLight;
    private boolean auto_play_next;
    private boolean auto_skip_start;

    private void initView(View view) {
        view.findViewById(R.id.tv_share_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callBack != null) {
                    dismiss();
                    callBack.shareVideo();
                }
            }
        });
        TextView tv_light = view.findViewById(R.id.tv_light);
        if (isFullScreen) {
            tv_light.setVisibility(View.GONE);
        } else {
            tv_light.setVisibility(View.VISIBLE);
        }
        isLight = PreferencesUtils.getBoolean(getContext(), "page_is_light", false);
        tv_light.setText(isLight ? matchText("模式 • 已开灯", 4) : "模式 • 已关灯");
        tv_light.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isLight = !isLight;
                tv_light.setText(isLight ? matchText("模式 • 已开灯", 4) : "模式 • 已关灯");
                PreferencesUtils.putBoolean(getContext(), "page_is_light", isLight);
                if (callBack != null) {
                    dismiss();
                    callBack.pageLight(isLight);
                }
            }
        });

        auto_play_next = PreferencesUtils.getBoolean(getContext(), "auto_play_next", true);
        TextView tv_auto_play_next = view.findViewById(R.id.tv_auto_play_next);
        tv_auto_play_next.setText(auto_play_next ? matchText("自动下集 • 已开启", 6) : "自动下集 • 已关闭");
        tv_auto_play_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auto_play_next = !auto_play_next;
                tv_auto_play_next.setText(auto_play_next ? matchText("自动下集 • 已开启", 6) : "自动下集 • 已关闭");
                XToast.showToast(getContext(), auto_play_next ? "自动下集 已开启" : "自动下集 已关闭");
                PreferencesUtils.putBoolean(getContext(), "auto_play_next", auto_play_next);
            }
        });

        auto_skip_start = PreferencesUtils.getBoolean(getContext(), "auto_skip_start", true);
        TextView tv_skip_start = view.findViewById(R.id.tv_skip_start);
        tv_skip_start.setText(auto_skip_start ? matchText("片头/片尾 • 已开启", 7) : "片头/片尾 • 已关闭");
        tv_skip_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auto_skip_start = !auto_skip_start;
                tv_skip_start.setText(auto_skip_start ? matchText("片头/片尾 • 已开启", 7) : "片头/片尾 • 已关闭");
                XToast.showToast(getContext(), auto_skip_start ? "片头/片尾 已开启" : "片头/片尾 已关闭");
                PreferencesUtils.putBoolean(getContext(), "auto_skip_start", auto_skip_start);
            }
        });
        float playSpeed = PreferencesUtils.getFloat(getContext(), "play_video_speed", 1.0f);
        TextView tv_play_speed = view.findViewById(R.id.tv_play_speed);
        tv_play_speed.setText("播放速度 • " + ((playSpeed == 1.0f) ? "正常" : playSpeed + ""));
        tv_play_speed.setText(matchText(tv_play_speed.getText().toString(), 6));
        tv_play_speed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferencesUtils.putFloat(getContext(), "play_video_speed", playSpeed);
                if (callBack != null) {
                    dismiss();
                    callBack.showSetSpeed();
                }
            }
        });
        view.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private GsyVideoCallBack callBack;

    public void setCallBack(GsyVideoCallBack callBack) {
        this.callBack = callBack;
    }


    private SpannableString matchText(String str, int start) {
        SpannableString sStr = new SpannableString(str);
        sStr.setSpan(new ForegroundColorSpan(Color.parseColor("#5677FC")), start, str.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        return sStr;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
