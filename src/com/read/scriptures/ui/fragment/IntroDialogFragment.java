package com.read.scriptures.ui.fragment;

import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.read.scriptures.R;
import com.read.scriptures.app.HuDongApplication;
import com.read.scriptures.util.DisplayUtil;
import com.read.scriptures.widget.Video.WxMediaController;
import com.read.scriptures.widget.Video.WxPlayer;

public class IntroDialogFragment extends DialogFragment implements View.OnClickListener, WxPlayer.IUScreenChanageListener {
    RelativeLayout rootView;
    RelativeLayout rlContent;
    TextView tvJoin;
    TextView tvClose;
    WxPlayer wxPlayer;
    String intro;
    String introVideo;

    public static IntroDialogFragment getInstance(String intro, String introVideo) {
        IntroDialogFragment introDialogFragment = new IntroDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("intro", intro);
        bundle.putString("introVideo", introVideo);
        introDialogFragment.setArguments(bundle);
        return introDialogFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#55000000")));

        final ViewGroup view = (ViewGroup) inflater.inflate(R.layout.no_cache_hint, container);
        rootView = view.findViewById(R.id.rl_root_view);
        rlContent = view.findViewById(R.id.rl_content);
        tvJoin = view.findViewById(R.id.tv_join);
        tvClose = view.findViewById(R.id.tv_close);
        wxPlayer = view.findViewById(R.id.wx_player);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initBundleData();
        initListener();
        setViewSize();
        initVideo();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void setViewSize() {
        DisplayMetrics dm2 = getResources().getDisplayMetrics();
        int height = (int) (dm2.heightPixels * 0.8); // 改变的是dialog框在屏幕中的位置而不是大小
        int width = (int) (dm2.widthPixels * 0.8); // 宽度设置为屏幕的0.65
        rlContent.getLayoutParams().height = height;
        rlContent.getLayoutParams().width = width;
    }

    private void initBundleData() {
        intro = getArguments().getString("intro");
        introVideo = getArguments().getString("introVideo");
    }

    private void initListener() {
        tvClose.setOnClickListener(this);
        tvJoin.setOnClickListener(this);
        initKeyListener();
    }

    private void initVideo() {
        if (introVideo != null && !introVideo.trim().equals("") && HuDongApplication.getInstance().isAppNormalLevelActivate()) {
            WxMediaController wxMediaController = new WxMediaController(getActivity());
            wxPlayer.setVisibility(View.VISIBLE);
            wxPlayer.setVideoPath(introVideo);
            wxPlayer.setMediaController(wxMediaController);
            wxPlayer.setIuScreenChanageListener(this);
            wxMediaController.setWxPlayer(wxPlayer);
        }
        tvJoin.setText(intro);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (wxPlayer != null && wxPlayer.isPause()) {
            wxPlayer.restart();
        }
    }

    @Override
    public void onPause() {
        if (wxPlayer != null && wxPlayer.isPlaying()) {
            wxPlayer.pause();
        }
        super.onPause();
    }

    @Override
    public void onStop() {
        if (wxPlayer != null && wxPlayer.isPlaying()) {
            wxPlayer.pause();
        }
        super.onStop();
    }


    @Override
    public void screenChange(boolean isSmallScreen) {
        if (isSmallScreen) {
            WindowManager.LayoutParams attrs = getDialog().getWindow().getAttributes();
            attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getDialog().getWindow().setAttributes(attrs);
            getDialog().getWindow().clearFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

            //小屏
            rootView.removeView(wxPlayer);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    (int) DisplayUtil.dp2px(getActivity(), 200));
            rlContent.addView(wxPlayer, params);

            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            WindowManager.LayoutParams attrs = getDialog().getWindow().getAttributes();
            attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getDialog().getWindow().setAttributes(attrs);
            getDialog().getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

            //全屏
            rlContent.removeView(wxPlayer);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            rootView.addView(wxPlayer, params);
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);


        }
    }

    @Override
    public void onClick(View v) {
        if (v == tvClose) {
            dismissAllowingStateLoss();
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (wxPlayer != null) {
            wxPlayer.release();
        }
        super.onDismiss(dialog);
    }


    private void initKeyListener() {
        this.getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent arg2) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (wxPlayer != null && !wxPlayer.isSmallScreen()) {
                        wxPlayer.chanageScreen();
                        return true;
                    }
                }
                return false;
            }
        });
    }
}
