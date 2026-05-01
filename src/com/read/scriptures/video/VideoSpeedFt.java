package com.read.scriptures.video;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.music.player.lib.util.XToast;
import com.read.scriptures.R;
import com.read.scriptures.app.HuDongApplication;
import com.read.scriptures.audio.SpeedAdapter;
import com.read.scriptures.util.PreferencesUtils;

import java.util.Arrays;

public class VideoSpeedFt extends DialogFragment {

    Float[] speedArray = {0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f, 2.25f, 2.5f, 2.75f, 3.0f};

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
        return R.layout.pop_play_speed;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private float playSpeed;

    private void initView(View view) {
        RecyclerView rcv_speed = view.findViewById(R.id.rcv_speed);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 4);
        rcv_speed.setLayoutManager(gridLayoutManager);
        playSpeed = PreferencesUtils.getFloat(getContext(), "play_video_speed", 1.0f);
        SpeedAdapter speedAdapter = new SpeedAdapter();
        speedAdapter.setCurrentSpeed(playSpeed);
        speedAdapter.setNewData(Arrays.asList(speedArray));
        rcv_speed.setAdapter(speedAdapter);

        speedAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                dismiss();
                playSpeed = (float) adapter.getItem(position);
                PreferencesUtils.putFloat(getContext(), "play_video_speed", playSpeed);
                if (callBack != null) {
                    callBack.setVideoSpeed(playSpeed);
                }
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
