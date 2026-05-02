package com.read.scriptures.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.music.player.lib.util.XToast;
import com.read.scriptures.R;
import com.read.scriptures.config.PreferenceConfig;
import com.read.scriptures.config.SystemConfig;
import com.read.scriptures.constants.SystemConstants;
import com.read.scriptures.control.BaiduSpeechManager;
import com.read.scriptures.event.PlayEvent;
import com.read.scriptures.manager.XunFeiSpeechManager;
import com.read.scriptures.util.SharedUtil;

import org.greenrobot.eventbus.EventBus;

public class SpeechPopupWindow extends PopupWindow
        implements View.OnClickListener, XunFeiSpeechManager.PlayTimeChangeListener, BaiduSpeechManager.PlayTimeChangeListener {

    private Context mContext;
    private View mRootView;

    private SeekBar seekBarSpeed;

    private int engineType = 0;//SystemConstants.SPEECH_TYPE
    private static Button mLocalButton;

    private TextView btn_clock;
    private View ll_time_list;

    private View.OnClickListener mOnClickListener;

    private XunFeiSpeechManager mXunFeiSpeechManager;
    private BaiduSpeechManager mBaiduSpeechManager;
    RadioButton mBaiduButton;
    RadioButton mXFButton;

    RadioButton xiaoyan;
    RadioButton xiaoyu;
    RadioButton xiaomei;
    RadioButton xiaolin;
    RadioButton xiaorong;
    RadioButton xiaoqian;
    RadioButton xiaokun;
    RadioButton xiaoqiang;
    RadioButton vixying;
    RadioButton nannan;
    RadioButton xiaoxin;
    RadioButton vils;

    RadioButton soft;
    RadioButton affine;
    RadioButton sweet;
    RadioButton lolita;
    RadioButton natural;
    RadioButton serious;
    RadioButton zhejiang;
    RadioButton manTwo;
    RadioButton manThree;
    RadioButton manFour;

    CheckBox cbSpeakTitle;
    RadioButton mLastRadioButton = xiaoyan;

    RadioButton mLastSpeachModeRadioButton;

    private String chanageSuccess = "切换成功";

    private boolean mSpeechModel = true;

    public SpeechPopupWindow(final Context context, final XunFeiSpeechManager xunFeiSpeechManager, final BaiduSpeechManager baiduSpeechManager, SeekBar.OnSeekBarChangeListener listener) {
        super(context);
        mContext = context;
        mXunFeiSpeechManager = xunFeiSpeechManager;
        mXunFeiSpeechManager.setPlayTimeChangeListener(this);
        mBaiduSpeechManager = baiduSpeechManager;
        if (mBaiduSpeechManager != null) {
            mBaiduSpeechManager.setPlayTimeChangeListener(this);
        }

        mRootView = LayoutInflater.from(mContext).inflate(R.layout.read_ui_layout, null);
        mRootView.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        seekBarSpeed = (SeekBar) mRootView.findViewById(R.id.seekBarSpeed);
        seekBarSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if (engineType == 0) {
                        if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_XF) {
                            mXunFeiSpeechManager.setSpeechSpeed(progress);
                        } else if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_BAIDU
                                && mBaiduSpeechManager != null) {
                            mBaiduSpeechManager.setSpeechSpeed(progress);
                        }
                        return;
                    }
                    if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_XF) {
                        mXunFeiSpeechManager.setSpeechSpeed(progress);
                        mXunFeiSpeechManager.resetSpeaking();
                    } else if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_BAIDU
                            && mBaiduSpeechManager != null) {
                        mBaiduSpeechManager.setSpeechSpeed(progress);
                        mBaiduSpeechManager.resetSpeaking();
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        mBaiduButton = (RadioButton) mRootView.findViewById(R.id.baidu_engine);
        mXFButton = (RadioButton) mRootView.findViewById(R.id.radio_engine);

        final RadioGroup radioGroupPlayer = (RadioGroup) mRootView.findViewById(R.id.radioGroup);
        xiaoyan = (RadioButton) mRootView.findViewById(R.id.radio_woman);
        xiaoyu = (RadioButton) mRootView.findViewById(R.id.radio_man);
        xiaomei = (RadioButton) mRootView.findViewById(R.id.radio_yueyu);
        xiaolin = (RadioButton) mRootView.findViewById(R.id.radio_taiwan);
        xiaorong = (RadioButton) mRootView.findViewById(R.id.radio_sichuan);
        xiaoqian = (RadioButton) mRootView.findViewById(R.id.radio_dongbei);
        xiaokun = (RadioButton) mRootView.findViewById(R.id.radio_henan);
        xiaoqiang = (RadioButton) mRootView.findViewById(R.id.radio_hunan);
        vixying = (RadioButton) mRootView.findViewById(R.id.radio_shanxi);
        nannan = (RadioButton) mRootView.findViewById(R.id.radio_girl);
        xiaoxin = (RadioButton) mRootView.findViewById(R.id.radio_boy);
        vils = (RadioButton) mRootView.findViewById(R.id.radio_old);

        soft = (RadioButton) mRootView.findViewById(R.id.radio_soft);
        affine = (RadioButton) mRootView.findViewById(R.id.radio_affine);
        sweet = (RadioButton) mRootView.findViewById(R.id.radio_sweet);
        lolita = (RadioButton) mRootView.findViewById(R.id.radio_lolita);
        natural = (RadioButton) mRootView.findViewById(R.id.radio_natural);
        serious = (RadioButton) mRootView.findViewById(R.id.radio_serious);
        zhejiang = (RadioButton) mRootView.findViewById(R.id.radio_zhejiang);
        manTwo = (RadioButton) mRootView.findViewById(R.id.radio_man_two);
        manThree = (RadioButton) mRootView.findViewById(R.id.radio_man_three);
        manFour = (RadioButton) mRootView.findViewById(R.id.radio_man_four);

        cbSpeakTitle = (CheckBox) mRootView.findViewById(R.id.cb_speak_title);

        switch (PreferenceConfig.getSpeech(context)) {
            case "xiaoyan": xiaoyan.setChecked(true); break;
            case "xiaoyu": xiaoyu.setChecked(true); break;
            case "xiaomei": xiaomei.setChecked(true); break;
            case "xiaolin": xiaolin.setChecked(true); break;
            case "xiaorong": xiaorong.setChecked(true); break;
            case "xiaoqian": xiaoqian.setChecked(true); break;
            case "xiaokun": xiaokun.setChecked(true); break;
            case "xiaoqiang": xiaoqiang.setChecked(true); break;
            case "vixying": vixying.setChecked(true); break;
            case "nannan": nannan.setChecked(true); break;
            case "xiaoxin": xiaoxin.setChecked(true); break;
            case "vils": vils.setChecked(true); break;
            case "soft": soft.setChecked(true); break;
            case "affine": affine.setChecked(true); break;
            case "sweet": sweet.setChecked(true); break;
            case "lolita": lolita.setChecked(true); break;
            case "natural": natural.setChecked(true); break;
            case "serious": serious.setChecked(true); break;
            case "zhejiang": zhejiang.setChecked(true); break;
            case "manTwo": manTwo.setChecked(true); break;
            case "manThree": manThree.setChecked(true); break;
            case "manFour": manFour.setChecked(true); break;
        }

        // 始终使用模式B（讯飞），模式A已彻底隐藏
        SystemConfig.Speech_Model = SystemConfig.SPEECH_MODEL_XF;
        seekBarSpeed.setProgress(mXunFeiSpeechManager.getSpeechSpeed());
        mXFButton.setChecked(true);
        mLastSpeachModeRadioButton = mXFButton;

        // 隐藏百度（模式A）专属音色
        soft.setVisibility(View.GONE);
        affine.setVisibility(View.GONE);
        sweet.setVisibility(View.GONE);
        lolita.setVisibility(View.GONE);
        natural.setVisibility(View.GONE);
        serious.setVisibility(View.GONE);
        zhejiang.setVisibility(View.GONE);
        manTwo.setVisibility(View.GONE);
        manThree.setVisibility(View.GONE);
        manFour.setVisibility(View.GONE);

        radioGroupPlayer.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mLastRadioButton = mRootView.findViewById(checkedId);
                if (checkedId == R.id.radio_woman) {
                    mXunFeiSpeechManager.setSpeechVoicer("xiaoyan");
                    PreferenceConfig.saveSpeech(context, "xiaoyan");
                } else if (checkedId == R.id.radio_man) {
                    mXunFeiSpeechManager.setSpeechVoicer("xiaoyu");
                    PreferenceConfig.saveSpeech(context, "xiaoyu");
                } else if (checkedId == R.id.radio_yueyu) {
                    mXunFeiSpeechManager.setSpeechVoicer("xiaomei");
                    PreferenceConfig.saveSpeech(context, "xiaomei");
                } else if (checkedId == R.id.radio_taiwan) {
                    mXunFeiSpeechManager.setSpeechVoicer("xiaolin");
                    PreferenceConfig.saveSpeech(context, "xiaolin");
                } else if (checkedId == R.id.radio_sichuan) {
                    mXunFeiSpeechManager.setSpeechVoicer("xiaorong");
                    PreferenceConfig.saveSpeech(context, "xiaorong");
                } else if (checkedId == R.id.radio_dongbei) {
                    mXunFeiSpeechManager.setSpeechVoicer("xiaoqian");
                    PreferenceConfig.saveSpeech(context, "xiaoqian");
                } else if (checkedId == R.id.radio_henan) {
                    mXunFeiSpeechManager.setSpeechVoicer("xiaokun");
                    PreferenceConfig.saveSpeech(context, "xiaokun");
                } else if (checkedId == R.id.radio_hunan) {
                    mXunFeiSpeechManager.setSpeechVoicer("xiaoqiang");
                    PreferenceConfig.saveSpeech(context, "xiaoqiang");
                } else if (checkedId == R.id.radio_shanxi) {
                    mXunFeiSpeechManager.setSpeechVoicer("vixying");
                    PreferenceConfig.saveSpeech(context, "vixying");
                } else if (checkedId == R.id.radio_girl) {
                    mXunFeiSpeechManager.setSpeechVoicer("nannan");
                    PreferenceConfig.saveSpeech(context, "nannan");
                } else if (checkedId == R.id.radio_boy) {
                    mXunFeiSpeechManager.setSpeechVoicer("xiaoxin");
                    PreferenceConfig.saveSpeech(context, "xiaoxin");
                } else if (checkedId == R.id.radio_old) {
                    mXunFeiSpeechManager.setSpeechVoicer("vils");
                    PreferenceConfig.saveSpeech(context, "vils");
                }
                XToast.showToast(context, chanageSuccess);
                if (engineType != 0) {
                    mXunFeiSpeechManager.resetSpeaking();
                }
            }
        });

        RadioGroup radioGroupClock = (RadioGroup) mRootView.findViewById(R.id.radioGroupClock);
        radioGroupClock.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int time = 0;
                if (checkedId == R.id.radio_15) {
                    time = 15;
                } else if (checkedId == R.id.radio_30) {
                    time = 30;
                } else if (checkedId == R.id.radio_60) {
                    time = 60;
                } else if (checkedId == R.id.radio_90) {
                    time = 90;
                } else if (checkedId == R.id.btn_clock_close) {
                    mXunFeiSpeechManager.stopAutoFlowTimer();
                    ll_time_list.setVisibility(View.GONE);
                    btn_clock.setText("定时");
                    return;
                }
                ll_time_list.setVisibility(View.GONE);
                mXunFeiSpeechManager.startAutoFlowTimer(time);
            }
        });

        RadioGroup radioGroupEngine = (RadioGroup) mRootView.findViewById(R.id.radio_group);
        radioGroupEngine.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                // 模式A已隐藏，此处逻辑不会被触发
            }
        });

        mLocalButton = (Button) mRootView.findViewById(R.id.radio_local);
        mLocalButton.setOnClickListener(this);

        btn_clock = (TextView) mRootView.findViewById(R.id.btn_clock);
        btn_clock.setOnClickListener(this);
        btn_clock.setText("定时");
        ll_time_list = mRootView.findViewById(R.id.layout_clock_setting);

        mRootView.findViewById(R.id.btn_clock_close).setOnClickListener(this);
        mRootView.findViewById(R.id.btn_clock_arrow_down).setOnClickListener(this);
        mRootView.findViewById(R.id.btn_previous_chapter).setOnClickListener(this);
        mRootView.findViewById(R.id.btn_next_chapter).setOnClickListener(this);
        mRootView.findViewById(R.id.btn_exit).setOnClickListener(this);
        mRootView.findViewById(R.id.space_bar).setOnClickListener(this);
        cbSpeakTitle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedUtil.putBoolean(PreferenceConfig.Preference_Speak_Title, isChecked);
                cbSpeakTitle.setText(isChecked ? "朗读" : "不朗读");
            }
        });

        this.setContentView(mRootView);
        this.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.setWidth(LayoutParams.MATCH_PARENT);
        this.setHeight(LayoutParams.MATCH_PARENT);
        this.setFocusable(true);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (mSpeechModel) {
            if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_XF
                    && mXunFeiSpeechManager != null) {
                mXunFeiSpeechManager.resumeSpeaking();
            } else if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_BAIDU
                    && mBaiduSpeechManager != null) {
                mBaiduSpeechManager.resume();
            }
            setButton(1);
        }
    }

    public void dismiss(boolean speechModel) {
        mSpeechModel = speechModel;
        dismiss();
    }

    public void setOnClickListener(View.OnClickListener onCancleClickListener) {
        this.mOnClickListener = onCancleClickListener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.radio_local:
                EventBus.getDefault().post(new PlayEvent(SystemConstants.SPEECH_TYPE, false));
                break;
            case R.id.btn_clock_arrow_down:
                ll_time_list.setVisibility(View.GONE);
                break;
            case R.id.btn_clock:
                ll_time_list.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_previous_chapter:
                mOnClickListener.onClick(v);
                break;
            case R.id.btn_next_chapter:
                mOnClickListener.onClick(v);
                break;
            case R.id.btn_exit:
                if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_XF) {
                    mXunFeiSpeechManager.stopSpeaking();
                } else if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_BAIDU
                        && mBaiduSpeechManager != null) {
                    mBaiduSpeechManager.stop();
                }
                setButton(1);
                mOnClickListener.onClick(v);
                break;
            case R.id.space_bar:
                dismiss();
                break;
        }
    }

    @Override
    public void onChange(long playTime) {
        if (!isShowing()) {
            return;
        }
        if (playTime == 0) {
            btn_clock.setText("定时");
            return;
        }
        if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_XF) {
            if (mXunFeiSpeechManager.isStopThread()) return;
        } else if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_BAIDU
                && mBaiduSpeechManager != null) {
            if (mBaiduSpeechManager.isStopThread()) return;
        }
        int second = (int) (playTime / 60);
        int mis = (int) (playTime % 60);
        String ss = second > 9 ? second + "" : "0" + second;
        ss = ss + ":" + (mis > 9 ? mis + "" : "0" + mis);
        btn_clock.setText(ss);
    }

    @Override
    public void onStop() {
        btn_clock.setText("定时");
    }

    public void setButton(int engineType) {
        if (engineType == 0) {
            mLocalButton.setText("设置");
        } else {
            mLocalButton.setText("暂停");
        }
        this.engineType = engineType;
    }

    public TextView getPlay() {
        return mLocalButton;
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
        engineType = 0;
        if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_XF) {
            mXunFeiSpeechManager.pauseSpeaking();
        } else if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_BAIDU
                && mBaiduSpeechManager != null) {
            mBaiduSpeechManager.pause();
        }
        boolean isSpeakTitle = SharedUtil.getBoolean(PreferenceConfig.Preference_Speak_Title, false);
        cbSpeakTitle.setChecked(isSpeakTitle);
        cbSpeakTitle.setText(isSpeakTitle ? "朗读" : "不朗读");
        XToast.showToast(mContext, "朗读暂停中...");
    }

    public void netUnAvailable() {
        if (mXFButton != null) {
            mXFButton.setEnabled(false);
        }
        if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_XF) {
            mXunFeiSpeechManager.stopSpeaking();
            XToast.showToast(mContext, "当前网络离线，朗读已停止");
        }
    }

    public void netAvailable() {
        if (mXFButton != null) {
            mXFButton.setEnabled(true);
        }
    }
}
