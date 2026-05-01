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
import com.read.scriptures.manager.alispeech.AliSpeechManager;
import com.read.scriptures.net.NetworkUtils;
import com.read.scriptures.util.SharedUtil;

import org.greenrobot.eventbus.EventBus;

public class SpeechPopupWindow extends PopupWindow
        implements View.OnClickListener, XunFeiSpeechManager.PlayTimeChangeListener, BaiduSpeechManager.PlayTimeChangeListener, AliSpeechManager.PlayTimeChangeListener {

    private Context mContext;
    private View mRootView;

    private SeekBar seekBarSpeed;

    private int engineType = 0;//SystemConstants.SPEECH_TYPE
    //    private Button mBaiduButton;
//    private Button mEngineButton;
    private static Button mLocalButton;

    private TextView btn_clock;
    private View ll_time_list;

    private View.OnClickListener mOnClickListener;

    private XunFeiSpeechManager mXunFeiSpeechManager;
    private BaiduSpeechManager mBaiduSpeechManager;
    private AliSpeechManager mAliSpeechManager;
    RadioButton mBaiduButton;
    RadioButton mXFButton;
    RadioButton mAliButton;

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

//    public String getReadContent() {
//        return readContent;
//    }
//
//    public void setReadContent(String readContent) {
//        this.readContent = readContent;
//    }


    public SpeechPopupWindow(final Context context, final XunFeiSpeechManager xunFeiSpeechManager, final BaiduSpeechManager baiduSpeechManager, final AliSpeechManager aliSpeechManager, SeekBar.OnSeekBarChangeListener listener) {
        super(context);
        mContext = context;
        mXunFeiSpeechManager = xunFeiSpeechManager;
        mXunFeiSpeechManager.setPlayTimeChangeListener(this);
        mBaiduSpeechManager = baiduSpeechManager;
        mBaiduSpeechManager.setPlayTimeChangeListener(this);
        mAliSpeechManager = aliSpeechManager;
        mAliSpeechManager.setPlayTimeChangeListener(this);
//        if (SystemConfig.Speech_Model == 1){
//            mSpeechManager = speechManager;
//            mSpeechManager.setPlayTimeChangeListener(this);
//        }else {
//            mBaiduSpeechManager = baiduSpeechManager;
//            mBaiduSpeechManager.setPlayTimeChangeListener(this);
//        }

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
                //progress 1-100
                if (fromUser) {
                    if (engineType == 0) {
                        if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_XF) {
                            mXunFeiSpeechManager.setSpeechSpeed(progress);
                        } else if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_BAIDU) {
                            mBaiduSpeechManager.setSpeechSpeed(progress);
                        } else if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_ALI) {
                            //ali
                            mAliSpeechManager.setSpeechSpeed(progress);
                        }
                        return;
                    }
                    if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_XF) {
                        mXunFeiSpeechManager.setSpeechSpeed(progress);
                        mXunFeiSpeechManager.resetSpeaking();
                    } else if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_BAIDU) {
                        mBaiduSpeechManager.setSpeechSpeed(progress);
                        mBaiduSpeechManager.resetSpeaking();
                    } else if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_ALI) {
                        if (mAliSpeechManager.isExecuteFinish()) {
                            //微软
                            mAliSpeechManager.setSpeechSpeed(progress);
                            mAliSpeechManager.resetSpeaking();
                        } else {
                            seekBarSpeed.setProgress(mAliSpeechManager.getSpeechSpeed());
                        }
                    }

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mBaiduButton = (RadioButton) mRootView.findViewById(R.id.baidu_engine);
        mXFButton = (RadioButton) mRootView.findViewById(R.id.radio_engine);
        mAliButton = (RadioButton) mRootView.findViewById(R.id.radio_ali);

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

        cbSpeakTitle = (CheckBox)mRootView.findViewById(R.id.cb_speak_title);


        switch (PreferenceConfig.getSpeech(context)) {
            case "xiaoyan":
                xiaoyan.setChecked(true);
                break;
            case "xiaoyu":
                xiaoyu.setChecked(true);
                break;
            case "xiaomei":
                xiaomei.setChecked(true);
                break;
            case "xiaolin":
                xiaolin.setChecked(true);
                break;
            case "xiaorong":
                xiaorong.setChecked(true);
                break;
            case "xiaoqian":
                xiaoqian.setChecked(true);
                break;
            case "xiaokun":
                xiaokun.setChecked(true);
                break;
            case "xiaoqiang":
                xiaoqiang.setChecked(true);
                break;
            case "vixying":
                vixying.setChecked(true);
                break;
            case "nannan":
                nannan.setChecked(true);
                break;
            case "xiaoxin":
                xiaoxin.setChecked(true);
                break;
            case "vils":
                vils.setChecked(true);
                break;
            case "soft":
                soft.setChecked(true);
                break;
            case "affine":
                affine.setChecked(true);
                break;
            case "sweet":
                sweet.setChecked(true);
                break;
            case "lolita":
                lolita.setChecked(true);
                break;
            case "natural":
                natural.setChecked(true);
                break;
            case "serious":
                serious.setChecked(true);
                break;
            case "zhejiang":
                zhejiang.setChecked(true);
                break;
            case "manTwo":
                manTwo.setChecked(true);
                break;
            case "manThree":
                manThree.setChecked(true);
                break;
            case "manFour":
                manFour.setChecked(true);
                break;

        }


        if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_XF) {
            //所有都有
            seekBarSpeed.setProgress(mXunFeiSpeechManager.getSpeechSpeed());
            mXFButton.setChecked(true);
            mLastSpeachModeRadioButton = mXFButton;
        } else if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_BAIDU) {
            seekBarSpeed.setProgress(mBaiduSpeechManager.getSpeechSpeed());
            String speaker = PreferenceConfig.getSpeech(context);
            if (!"xiaoyan".equals(speaker)
                    && !"xiaoyu".equals(speaker)
                    && !"nannan".equals(speaker)
            ) {
                xiaoyan.setChecked(true);
                PreferenceConfig.saveSpeech(context, "xiaoyan");
            }
            mBaiduButton.setChecked(true);
            mLastSpeachModeRadioButton = mBaiduButton;
        } else if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_ALI) {
            seekBarSpeed.setProgress(mAliSpeechManager.getSpeechSpeed());
            String speaker = PreferenceConfig.getSpeech(context);
            if (!mAliSpeechManager.hasSpeaker(speaker)) {
                xiaoyan.setChecked(true);
                PreferenceConfig.saveSpeech(context, "xiaoyan");
            }
            mAliButton.setChecked(true);
            mLastSpeachModeRadioButton = mAliButton;
        }

        if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_XF) {
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
        } else if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_BAIDU) {
            //百度
            xiaomei.setVisibility(View.GONE);
            xiaolin.setVisibility(View.GONE);
            xiaorong.setVisibility(View.GONE);
            xiaoqian.setVisibility(View.GONE);
            xiaokun.setVisibility(View.GONE);
            xiaoqiang.setVisibility(View.GONE);
            vixying.setVisibility(View.GONE);
            xiaoxin.setVisibility(View.GONE);
            vils.setVisibility(View.GONE);

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
        } else if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_ALI) {
            //阿里
            xiaokun.setVisibility(View.GONE);
            vixying.setVisibility(View.GONE);
            xiaoxin.setVisibility(View.GONE);
            xiaoxin.setVisibility(View.GONE);
            vils.setVisibility(View.GONE);
        }

        radioGroupPlayer.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                if ((SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_ALI && !aliSpeechManager.isExecuteFinish() && mLastRadioButton != null)
//                        || mLastRadioButton == mRootView.findViewById(checkedId)) {
//                    //还有任务未执行完毕
//                    mLastRadioButton.setChecked(true);
//                    return;
//                }
                mLastRadioButton = mRootView.findViewById(checkedId);
                if (checkedId == R.id.radio_woman) {
                    if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_XF)
                        mXunFeiSpeechManager.setSpeechVoicer("xiaoyan");
                    else if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_BAIDU) {
                        mBaiduSpeechManager.setSpeaker("xiaoyan");
                    } else if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_ALI) {
                        aliSpeechManager.setSpeaker("xiaoyan");
                    }
                    PreferenceConfig.saveSpeech(context, "xiaoyan");
                } else if (checkedId == R.id.radio_man) {
                    if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_XF) {
                        mXunFeiSpeechManager.setSpeechVoicer("xiaoyu");
                    } else if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_BAIDU) {
                        mBaiduSpeechManager.setSpeaker("xiaoyu");
                    } else if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_ALI) {
                        aliSpeechManager.setSpeaker("xiaoyu");
                    }
                    PreferenceConfig.saveSpeech(context, "xiaoyu");
                } else if (checkedId == R.id.radio_yueyu) {
                    if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_XF) {
                        mXunFeiSpeechManager.setSpeechVoicer("xiaomei");
                    } else if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_ALI) {
                        aliSpeechManager.setSpeaker("xiaomei");
                    }
                    PreferenceConfig.saveSpeech(context, "xiaomei");
                } else if (checkedId == R.id.radio_taiwan) {
                    if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_XF) {
                        mXunFeiSpeechManager.setSpeechVoicer("xiaolin");
                    } else if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_ALI) {
                        aliSpeechManager.setSpeaker("xiaolin");
                    }
                    PreferenceConfig.saveSpeech(context, "xiaolin");
                } else if (checkedId == R.id.radio_sichuan) {
                    if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_XF) {
                        mXunFeiSpeechManager.setSpeechVoicer("xiaorong");
                    } else if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_ALI) {
                        aliSpeechManager.setSpeaker("xiaorong");
                    }
                    PreferenceConfig.saveSpeech(context, "xiaorong");
                } else if (checkedId == R.id.radio_dongbei) {
                    if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_XF) {
                        mXunFeiSpeechManager.setSpeechVoicer("xiaoqian");
                    } else if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_ALI) {
                        aliSpeechManager.setSpeaker("xiaoqian");
                    }
                    PreferenceConfig.saveSpeech(context, "xiaoqian");
                } else if (checkedId == R.id.radio_henan) {
                    mXunFeiSpeechManager.setSpeechVoicer("xiaokun");
                    PreferenceConfig.saveSpeech(context, "xiaokun");
                } else if (checkedId == R.id.radio_hunan) {
                    if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_XF) {
                        mXunFeiSpeechManager.setSpeechVoicer("xiaoqiang");
                    } else if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_ALI) {
                        aliSpeechManager.setSpeaker("xiaoqiang");
                    }
                    PreferenceConfig.saveSpeech(context, "xiaoqiang");
                } else if (checkedId == R.id.radio_shanxi) {
                    mXunFeiSpeechManager.setSpeechVoicer("vixying");
                    PreferenceConfig.saveSpeech(context, "vixying");
                } else if (checkedId == R.id.radio_girl) {
                    if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_XF) {
                        mXunFeiSpeechManager.setSpeechVoicer("nannan");
                    } else if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_BAIDU) {
                        mBaiduSpeechManager.setSpeaker("nannan");
                    } else if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_ALI) {
                        aliSpeechManager.setSpeaker("nannan");
                    }
                    PreferenceConfig.saveSpeech(context, "nannan");
                } else if (checkedId == R.id.radio_boy) {
                    mXunFeiSpeechManager.setSpeechVoicer("xiaoxin");
                    PreferenceConfig.saveSpeech(context, "xiaoxin");
                } else if (checkedId == R.id.radio_old) {
                    mXunFeiSpeechManager.setSpeechVoicer("vils");
                    PreferenceConfig.saveSpeech(context, "vils");
                } else if (checkedId == R.id.radio_soft) {
                    mAliSpeechManager.setSpeaker("soft");
                    PreferenceConfig.saveSpeech(context, "soft");
                } else if (checkedId == R.id.radio_affine) {
                    mAliSpeechManager.setSpeaker("affine");
                    PreferenceConfig.saveSpeech(context, "affine");
                } else if (checkedId == R.id.radio_sweet) {
                    mAliSpeechManager.setSpeaker("sweet");
                    PreferenceConfig.saveSpeech(context, "sweet");
                } else if (checkedId == R.id.radio_lolita) {
                    mAliSpeechManager.setSpeaker("lolita");
                    PreferenceConfig.saveSpeech(context, "lolita");
                } else if (checkedId == R.id.radio_natural) {
                    mAliSpeechManager.setSpeaker("natural");
                    PreferenceConfig.saveSpeech(context, "natural");
                } else if (checkedId == R.id.radio_serious) {
                    mAliSpeechManager.setSpeaker("serious");
                    PreferenceConfig.saveSpeech(context, "zhejiang");
                } else if (checkedId == R.id.radio_zhejiang) {
                    mAliSpeechManager.setSpeaker("zhejiang");
                    PreferenceConfig.saveSpeech(context, "zhejiang");
                } else if (checkedId == R.id.radio_man_two) {
                    mAliSpeechManager.setSpeaker("manTwo");
                    PreferenceConfig.saveSpeech(context, "manTwo");
                } else if (checkedId == R.id.radio_man_three) {
                    mAliSpeechManager.setSpeaker("manThree");
                    PreferenceConfig.saveSpeech(context, "manThree");
                } else if (checkedId == R.id.radio_man_four) {
                    mAliSpeechManager.setSpeaker("manFour");
                    PreferenceConfig.saveSpeech(context, "manFour");
                }
                XToast.showToast(context, chanageSuccess);
                if (engineType == 0) {
                    return;
                }
                if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_XF) {
                    mXunFeiSpeechManager.resetSpeaking();
                } else if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_BAIDU) {
                    mBaiduSpeechManager.resetSpeaking();
                } else if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_ALI) {
                    aliSpeechManager.resetSpeaking();
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
                    if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_XF) {
                        mXunFeiSpeechManager.stopAutoFlowTimer();
                    } else if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_BAIDU) {
                        mBaiduSpeechManager.stopAutoFlowTimer();
                    } else if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_ALI) {
                        aliSpeechManager.stopAutoFlowTimer();
                    }
                    ll_time_list.setVisibility(View.GONE);
                    btn_clock.setText("定时");
                    return;
                }
                ll_time_list.setVisibility(View.GONE);
                if (SystemConfig.Speech_Model == 1) {
                    mXunFeiSpeechManager.startAutoFlowTimer(time);
                } else if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_BAIDU) {
                    mBaiduSpeechManager.startAutoFlowTimer(time);
                } else if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_ALI) {
                    aliSpeechManager.startAutoFlowTimer(time);
                }

            }
        });
        RadioGroup radioGroupEngine = (RadioGroup) mRootView.findViewById(R.id.radio_group);
        radioGroupEngine.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.baidu_engine:
                        xiaoyan.setVisibility(View.VISIBLE);
                        xiaoyu.setVisibility(View.VISIBLE);
                        xiaomei.setVisibility(View.GONE);
                        xiaolin.setVisibility(View.GONE);
                        xiaorong.setVisibility(View.GONE);
                        xiaoqian.setVisibility(View.GONE);
                        xiaokun.setVisibility(View.GONE);
                        xiaoqiang.setVisibility(View.GONE);
                        vixying.setVisibility(View.GONE);
                        nannan.setVisibility(View.VISIBLE);
                        xiaoxin.setVisibility(View.GONE);
                        vils.setVisibility(View.GONE);
                        //阿里特有的全部隐藏
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

                        String speaker = PreferenceConfig.getSpeech(context);
                        if (!"xiaoyan".equals(speaker)
                                && !"xiaoyu".equals(speaker)
                                && !"nannan".equals(speaker)
                        ) {
                            xiaoyan.setChecked(true);
                        }

                        if (engineType == 1 && SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_XF) {
                            mXunFeiSpeechManager.stopSpeaking();
                        } else if (engineType == 1 && SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_ALI) {
                            aliSpeechManager.stopOnOtherThread();
                        }
                        SystemConfig.Speech_Model = SystemConfig.SPEECH_MODEL_BAIDU;
                        SharedUtil.putInt(SystemConfig.SP_SPEACH_MODEL_KEY,SystemConfig.Speech_Model);
                        if (mLastSpeachModeRadioButton != mBaiduButton) {
                            XToast.showToast(mContext, chanageSuccess);
                        }
                        if (engineType == 0) {
                            return;
                        }
                        mBaiduSpeechManager.setSpeechSpeed(mBaiduSpeechManager.getSpeechSpeed());
                        mBaiduSpeechManager.speak(SystemConfig.readContent);
                        break;
                    case R.id.radio_engine:
                        if (!NetworkUtils.isNetAvailable(mContext)) {
                            mBaiduButton.setChecked(true);
                            XToast.showToast(mContext,  "本模式暂不支持，离线播放");
                            return;
                        }

                        xiaoyan.setVisibility(View.VISIBLE);
                        xiaoyu.setVisibility(View.VISIBLE);
                        xiaomei.setVisibility(View.VISIBLE);
                        xiaolin.setVisibility(View.VISIBLE);
                        xiaorong.setVisibility(View.VISIBLE);
                        xiaoqian.setVisibility(View.VISIBLE);
                        xiaokun.setVisibility(View.VISIBLE);
                        xiaoqiang.setVisibility(View.VISIBLE);
                        vixying.setVisibility(View.VISIBLE);
                        nannan.setVisibility(View.VISIBLE);
                        xiaoxin.setVisibility(View.VISIBLE);
                        vils.setVisibility(View.VISIBLE);

                        //阿里特有的全部隐藏
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

                        String currentSpeaker = PreferenceConfig.getSpeech(context);
                        if ("soft".equals(currentSpeaker)
                                || "affine".equals(currentSpeaker)
                                || "sweet".equals(currentSpeaker)
                                || "lolita".equals(currentSpeaker)
                                || "natural".equals(currentSpeaker)
                                || "serious".equals(currentSpeaker)
                                || "zhejiang".equals(currentSpeaker)
                                || "manTwo".equals(currentSpeaker)
                                || "manThree".equals(currentSpeaker)
                                || "manFour".equals(currentSpeaker)
                                || "manFive".equals(currentSpeaker)
                        ) {
                            xiaoyan.setChecked(true);
                        }

                        if (engineType == 1 && SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_BAIDU) {
                            mBaiduSpeechManager.stop();
                        } else if (engineType == 1 && SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_ALI) {
                            aliSpeechManager.stopOnOtherThread();
                        }
                        SystemConfig.Speech_Model = SystemConfig.SPEECH_MODEL_XF;
                        SharedUtil.putInt(SystemConfig.SP_SPEACH_MODEL_KEY,SystemConfig.Speech_Model);
                        XToast.showToast(mContext,  chanageSuccess);
                        if (engineType == 0) {
                            return;
                        }
//                        mBaiduSpeechManager.stop();
//                        aliSpeechManager.stop();

                        mXunFeiSpeechManager.setSpeechSpeed(mXunFeiSpeechManager.getSpeechSpeed());
                        if (!SystemConfig.readContent.contains("行(xing2)"))
                            mXunFeiSpeechManager.startSpeaking(SystemConfig.readContent, mXunFeiSpeechManager.getTtsListener());
                        else
                            mXunFeiSpeechManager.startSpeaking(SystemConfig.readContent.replaceAll("行(xing2)", "行"), mXunFeiSpeechManager.getTtsListener());
                        break;
                    case R.id.radio_ali:
                        if (!NetworkUtils.isNetAvailable(mContext)) {
                            mBaiduButton.setChecked(true);
                            XToast.showToast(mContext,  "本模式暂不支持，离线播放");
                            return;
                        }
                        xiaoyan.setVisibility(View.VISIBLE);
                        xiaoyu.setVisibility(View.VISIBLE);
                        xiaomei.setVisibility(View.VISIBLE);
                        xiaolin.setVisibility(View.VISIBLE);
                        xiaorong.setVisibility(View.VISIBLE);
                        xiaoqian.setVisibility(View.VISIBLE);
                        xiaokun.setVisibility(View.GONE);
                        xiaoqiang.setVisibility(View.VISIBLE);
                        vixying.setVisibility(View.GONE);
                        nannan.setVisibility(View.VISIBLE);
                        xiaoxin.setVisibility(View.GONE);
                        vils.setVisibility(View.GONE);

                        //阿里特有的全部显示
                        soft.setVisibility(View.VISIBLE);
                        affine.setVisibility(View.VISIBLE);
                        sweet.setVisibility(View.VISIBLE);
                        lolita.setVisibility(View.VISIBLE);
                        natural.setVisibility(View.VISIBLE);
                        serious.setVisibility(View.VISIBLE);
                        zhejiang.setVisibility(View.VISIBLE);
                        manTwo.setVisibility(View.VISIBLE);
                        manThree.setVisibility(View.VISIBLE);
                        manFour.setVisibility(View.VISIBLE);

                        //判断是否有上次选中的语言
                        String checkedSpeakerName = PreferenceConfig.getSpeech(context);
                        if (!aliSpeechManager.hasSpeaker(checkedSpeakerName)) {
                            //没有该音效，指定默认音效
                            xiaoyan.setChecked(true);
                            PreferenceConfig.saveSpeech(mContext, "xiaoyan");
                        }
                        if (engineType == 1 && SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_BAIDU) {
                            mBaiduSpeechManager.stop();
                        } else if (engineType == 1 && SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_XF) {
                            xunFeiSpeechManager.stopSpeaking();
                        }
                        SystemConfig.Speech_Model = SystemConfig.SPEECH_MODEL_ALI;
                        SharedUtil.putInt(SystemConfig.SP_SPEACH_MODEL_KEY,SystemConfig.Speech_Model);
                        XToast.showToast(mContext,  chanageSuccess);
                        if (engineType == 0) {
                            return;
                        }

                        aliSpeechManager.setSpeechSpeed(aliSpeechManager.getSpeechSpeed());
                        if (!SystemConfig.readContent.contains("行(xing2)"))
                            aliSpeechManager.speak(SystemConfig.readContent);
                        else
                            aliSpeechManager.speak(SystemConfig.readContent.replaceAll("行(xing2)", "行"));
                        break;
                }
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
        mRootView.findViewById(R.id.btn_clock_arrow_down).setOnClickListener(this);
        mRootView.findViewById(R.id.btn_previous_chapter).setOnClickListener(this);
        mRootView.findViewById(R.id.btn_next_chapter).setOnClickListener(this);
        mRootView.findViewById(R.id.btn_exit).setOnClickListener(this);
        mRootView.findViewById(R.id.space_bar).setOnClickListener(this);
        cbSpeakTitle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedUtil.putBoolean(PreferenceConfig.Preference_Speak_Title,isChecked);
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
        if (engineType == 0 && mSpeechModel) {
            if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_XF) {
                mXunFeiSpeechManager.resetSpeaking();
            } else if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_BAIDU) {
                mBaiduSpeechManager.resetSpeaking();
            } else if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_ALI) {
                mAliSpeechManager.resetSpeaking();
            }
            setButton(1);
        }
        XToast.showToast(mContext,  "朗读加载中...");
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
//            if (engineType == 1) {
//                engineType = 0;
//                mLocalButton.setText("开始");
//                mSpeechManager.pauseSpeaking();
//            } else {
//                engineType = 1;
//                mLocalButton.setText("暂停");
//                mSpeechManager.resumeSpeaking();
//            }
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
                } else if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_BAIDU) {
                    mBaiduSpeechManager.stop();
                } else if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_ALI) {
                    mAliSpeechManager.stopOnOtherThread();
                }
                setButton(1);
                mOnClickListener.onClick(v);
                break;
            case R.id.space_bar:
                dismiss();
                break;
//        case R.id.btn_clock_close:
//            if (SystemConfig.Speech_Model == 1){
//                mSpeechManager.stopAutoFlowTimer();
//            }else{
//                mBaiduSpeechManager.stopAutoFlowTimer();
//            }
//            ll_time_list.setVisibility(View.GONE);
//            btn_clock.setText("定时");
//            break;
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
            if (mXunFeiSpeechManager.isStopThread())
                return;
        } else if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_BAIDU) {
            if (mBaiduSpeechManager.isStopThread())
                return;
        } else if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_ALI) {
            if (mAliSpeechManager.isStopThread())
                return;
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
        } else if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_BAIDU) {
            mBaiduSpeechManager.pause();
        } else if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_ALI) {
            mAliSpeechManager.stopOnOtherThread();
        }
        boolean isSpeakTitle = SharedUtil.getBoolean(PreferenceConfig.Preference_Speak_Title,false);
        cbSpeakTitle.setChecked(isSpeakTitle);
        cbSpeakTitle.setText(isSpeakTitle ? "朗读" : "不朗读");
        XToast.showToast(mContext,  "朗读暂停中...");
    }

    /**
     * 网络不可用
     */
    public void netUnAvailable() {
        if (mXFButton != null) {
            mXFButton.setEnabled(false);
        }
        if (mAliButton != null) {
            mAliButton.setEnabled(false);
        }

        if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_ALI) {
            mAliSpeechManager.stopOnOtherThread();
            //切换到模式A
            mBaiduButton.setChecked(true);
            XToast.showToast(mContext,  "当前网络离线，已切换到“模式A”");
        } else if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_XF) {
            mXunFeiSpeechManager.stopSpeaking();
            //切换到模式A
            mBaiduButton.setChecked(true);
            XToast.showToast(mContext,  "当前网络离线，已切换到“模式A”");
        }

    }

    public void netAvailable() {
        if (mXFButton != null) {
            mXFButton.setEnabled(true);
        }
        if (mAliButton != null) {
            mAliButton.setEnabled(true);
        }
    }
}
