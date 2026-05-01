package com.read.scriptures.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.read.scriptures.R;
import com.read.scriptures.app.HuDongApplication;
import com.read.scriptures.config.SystemConfig;

import java.util.List;

import io.reactivex.annotations.NonNull;


/**
 * Created by CaiMi
 * date On 2018/4/25
 */
public class VersionSettingDialog extends Dialog implements View.OnClickListener {

    TextView tv_hehe;
    TextView tv_lvzhz;
    TextView tv_sigao;
    TextView tv_xiandai;
    TextView tv_xinyi;
    TextView tv_dangdai;
    TextView tv_kjv;
    TextView tv_niv;
    TextView tv_bbe;
    TextView tv_asv;
    ImageView btnChoose;
    int hzFlag = 0;

    private OnChoiceClickListener listener;

    public VersionSettingDialog(@NonNull Context context, int flag) {
        super(context, R.style.MyDialogStyle);
        this.hzFlag = flag;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_version_setting);
        initParam();
        initView();
        if (hzFlag == 2) {
            tv_hehe.setText("中文");
            tv_lvzhz.setText("英文");
            dismissTextView(tv_sigao, tv_xiandai, tv_xinyi, tv_dangdai, tv_kjv, tv_niv, tv_bbe, tv_asv);
        } else {
            tv_hehe.setText("和合本");
            tv_lvzhz.setText("吕振中");
            showTextView(tv_sigao, tv_xiandai, tv_xinyi, tv_dangdai, tv_kjv, tv_niv, tv_bbe, tv_asv);
        }
    }

    private void dismissTextView(TextView... vList) {
        if (vList == null || vList.length == 0) {
            return;
        }
        for (View view : vList) {
            if (view.getVisibility() == View.VISIBLE) {
                view.setVisibility(View.INVISIBLE);
                view.setEnabled(false);
            }
        }
    }

    private void showTextView(TextView... vList) {
        if (vList == null || vList.length == 0) {
            return;
        }
        for (View view : vList) {
            if (view.getVisibility() == View.GONE) {
                view.setVisibility(View.VISIBLE);
                view.setEnabled(true);
            }
        }
    }

    private void initParam() {
        Window window = getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        window.setAttributes(lp);
    }

    private void initView() {
        tv_hehe = (TextView) findViewById(R.id.tv_hehe);
        tv_lvzhz = (TextView) findViewById(R.id.tv_lvzhz);
        tv_sigao = (TextView) findViewById(R.id.tv_sigao);
        tv_xiandai = (TextView) findViewById(R.id.tv_xiandai);
        tv_xinyi = (TextView) findViewById(R.id.tv_xinyi);
        tv_dangdai = (TextView) findViewById(R.id.tv_dangdai);
        tv_kjv = (TextView) findViewById(R.id.tv_kjv);
        tv_niv = (TextView) findViewById(R.id.tv_niv);
        tv_bbe = (TextView) findViewById(R.id.tv_bbe);
        tv_asv = (TextView) findViewById(R.id.tv_asv);
        btnChoose = (ImageView) findViewById(R.id.btnChoose);

        tv_hehe.setOnClickListener(this);
        tv_lvzhz.setOnClickListener(this);
        tv_sigao.setOnClickListener(this);
        tv_xiandai.setOnClickListener(this);
        tv_xinyi.setOnClickListener(this);
        tv_dangdai.setOnClickListener(this);
        tv_kjv.setOnClickListener(this);
        tv_niv.setOnClickListener(this);
        tv_bbe.setOnClickListener(this);
        tv_asv.setOnClickListener(this);
        btnChoose.setOnClickListener(this);

        List<String> versions = HuDongApplication.mVersions;
        if (hzFlag == 2) {
            versions = HuDongApplication.mVersions_HZ;
        }


        if (versions != null && !versions.isEmpty()) {
            for (String version : versions) {
                switch (version) {
                    case "中文":
                        tv_hehe.setBackgroundResource(R.drawable.circle_normal_pressed);
                        break;
                    case "英文":
                        tv_lvzhz.setBackgroundResource(R.drawable.circle_normal_pressed);
                        break;
                    case "和合本":
                        tv_hehe.setBackgroundResource(R.drawable.circle_normal_pressed);
                        break;
                    case "吕振中":
                    case "呂振中":
                        tv_lvzhz.setBackgroundResource(R.drawable.circle_normal_pressed);
                        break;
                    case "思高本":
                        tv_sigao.setBackgroundResource(R.drawable.circle_normal_pressed);
                        break;
                    case "现代本":
                    case "現代本":
                        tv_xiandai.setBackgroundResource(R.drawable.circle_normal_pressed);
                        break;
                    case "新译本":
                    case "新譯本":
                        tv_xinyi.setBackgroundResource(R.drawable.circle_normal_pressed);
                        break;
                    case "当代版":
                    case "當代版":
                        tv_dangdai.setBackgroundResource(R.drawable.circle_normal_pressed);
                        break;
                    case "KJV":
                        tv_kjv.setBackgroundResource(R.drawable.circle_normal_pressed);
                        break;
                    case "NIV":
                        tv_niv.setBackgroundResource(R.drawable.circle_normal_pressed);
                        break;
                    case "BBE":
                        tv_bbe.setBackgroundResource(R.drawable.circle_normal_pressed);
                        break;
                    case "ASV":
                        tv_asv.setBackgroundResource(R.drawable.circle_normal_pressed);
                        break;
                }
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public VersionSettingDialog setListener(OnChoiceClickListener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_hehe:
                if (hzFlag == 2) {
                    selectVersion(tv_hehe, "中文");
                } else {
                    selectVersion(tv_hehe, "和合本");
                }
                break;
            case R.id.tv_lvzhz:
                if (hzFlag == 2) {
                    selectVersion(tv_lvzhz, "英文");
                } else {
                    if (HuDongApplication.getInstance().getTextModel() == SystemConfig.TEXT_MODEL_FANTI) {
                        selectVersion(tv_lvzhz, "呂振中");
                    } else {
                        selectVersion(tv_lvzhz, "吕振中");
                    }
                }
                break;
            case R.id.tv_sigao:
                selectVersion(tv_sigao, "思高本");
                break;
            case R.id.tv_xiandai:
                if (HuDongApplication.getInstance().getTextModel() == SystemConfig.TEXT_MODEL_FANTI) {
                    selectVersion(tv_xiandai, "現代本");
                } else {
                    selectVersion(tv_xiandai, "现代本");
                }
                break;
            case R.id.tv_xinyi:
                if (HuDongApplication.getInstance().getTextModel() == SystemConfig.TEXT_MODEL_FANTI) {
                    selectVersion(tv_xinyi, "新譯本");
                } else {
                    selectVersion(tv_xinyi, "新译本");
                }
                break;
            case R.id.tv_dangdai:
                if (HuDongApplication.getInstance().getTextModel() == SystemConfig.TEXT_MODEL_FANTI) {
                    selectVersion(tv_dangdai, "當代版");
                } else {
                    selectVersion(tv_dangdai, "当代版");
                }
                break;
            case R.id.tv_kjv:
                selectVersion(tv_kjv, "KJV");
                break;
            case R.id.tv_niv:
                selectVersion(tv_niv, "NIV");
                break;
            case R.id.tv_bbe:
                selectVersion(tv_bbe, "BBE");
                break;
            case R.id.tv_asv:
                selectVersion(tv_asv, "ASV");
                break;
            case R.id.btnChoose:
                if (listener != null) {
                    listener.versionSelect(HuDongApplication.mVersions);
                }
                dismiss();
                break;
        }
    }

    private void selectVersion(TextView view, String versionName) {
        if (!TextUtils.isEmpty(versionName)) {
            if (2 != hzFlag) {
                if (HuDongApplication.mVersions.contains(versionName)) {
                    if (HuDongApplication.mVersions.size() != 1) {
                        HuDongApplication.mVersions.remove(versionName);
                        view.setBackgroundResource(R.drawable.bg_translate);
                    }
                } else {
                    HuDongApplication.mVersions.add(versionName);
                    view.setBackgroundResource(R.drawable.circle_normal_pressed);
                }
            } else {
                if (HuDongApplication.mVersions_HZ.contains(versionName)) {
                    if (HuDongApplication.mVersions_HZ.size() != 1) {
                        HuDongApplication.mVersions_HZ.remove(versionName);
                        view.setBackgroundResource(R.drawable.bg_translate);
                    }
                } else {
                    HuDongApplication.mVersions_HZ.add(versionName);
                    view.setBackgroundResource(R.drawable.circle_normal_pressed);
                }
            }
            if (listener != null) {
                if (2 != hzFlag) {
                    listener.versionSelect(HuDongApplication.mVersions);
                } else {
                    listener.versionSelect(HuDongApplication.mVersions_HZ);
                }
            }
        }
    }

    public interface OnChoiceClickListener {
        void versionSelect(List<String> versions);
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }


}
