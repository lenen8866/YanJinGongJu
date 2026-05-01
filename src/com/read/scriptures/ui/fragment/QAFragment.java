package com.read.scriptures.ui.fragment;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.text.TextUtils;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.gson.Gson;
import com.music.player.lib.manager.MusicPlayerManager;
import com.read.scriptures.R;
import com.read.scriptures.bean.AnswerInitBean;
import com.read.scriptures.bean.QAConfigBean;
import com.read.scriptures.config.ZConfig;
import com.read.scriptures.manager.AccountManager;
import com.music.player.lib.util.NetUtil;
import com.read.scriptures.widget.CustomViewPager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class QAFragment extends BaseFragment implements CustomViewPager.ScollAbleArea {

    @Override
    public int onObtainLayoutResId() {
        return R.layout.ft_qa;
    }

    public void lazyLoad() {

    }

    /**
     * 切换tab显示
     */
    public void setTabSelection(int index) {
        // 开启一个Fragment事务
        FragmentManager mfm = getChildFragmentManager();
        FragmentTransaction transaction = mfm.beginTransaction();
        findFragment(mfm);
        hidden(transaction);
        switch (index) {
            case 1:
                if (levelQuestionFragment1 == null) {
                    levelQuestionFragment1 = LevelQuestionFragment.getInstance("1", answerNumList,primaryAnswerTime);
                    //设置图标状态
                    transaction.add(R.id.fl_main, levelQuestionFragment1, "levelQuestionFragment1");
                } else {
                    //设置图标状态
                    transaction.show(levelQuestionFragment1);
                }
                if (answerInitBean != null && answerInitBean.data != null) {
                    levelQuestionFragment1.setAnswerCount(answerInitBean.data.frequency);
                    levelQuestionFragment1.setPromptNum(answerInitBean.data.prompt);
                }
                break;
            case 2:
                if (levelQuestionFragment2 == null) {
                    levelQuestionFragment2 = LevelQuestionFragment.getInstance("2", answerNumList,intermediateAnswerTime);
                    transaction.add(R.id.fl_main, levelQuestionFragment2, "levelQuestionFragment2");
                } else {
                    transaction.show(levelQuestionFragment2);
                }
                if (answerInitBean != null && answerInitBean.data != null) {
                    levelQuestionFragment2.setAnswerCount(answerInitBean.data.frequency);
                    levelQuestionFragment2.setPromptNum(answerInitBean.data.prompt);
                }
                break;
            case 3:
                if (levelQuestionFragment3 == null) {
                    levelQuestionFragment3 = LevelQuestionFragment.getInstance("3", answerNumList,seniorAnswerTime);
                    //设置图标状态
                    transaction.add(R.id.fl_main, levelQuestionFragment3, "levelQuestionFragment3");
                } else {
                    //设置图标状态
                    transaction.show(levelQuestionFragment3);
                }
                if (answerInitBean != null && answerInitBean.data != null) {
                    levelQuestionFragment3.setAnswerCount(answerInitBean.data.frequency);
                    levelQuestionFragment3.setPromptNum(answerInitBean.data.prompt);
                }
                break;
        }
        transaction.commitAllowingStateLoss();
    }

    private void findFragment(FragmentManager mfm) {
        if (levelQuestionFragment1 == null) {
            levelQuestionFragment1 = (LevelQuestionFragment) mfm.findFragmentByTag("levelQuestionFragment1");
        }
        if (levelQuestionFragment2 == null) {
            levelQuestionFragment2 = (LevelQuestionFragment) mfm.findFragmentByTag("levelQuestionFragment2");
        }
        if (levelQuestionFragment3 == null) {
            levelQuestionFragment3 = (LevelQuestionFragment) mfm.findFragmentByTag("levelQuestionFragment3");
        }
    }


    private void hidden(FragmentTransaction ft) {
        if (levelQuestionFragment1 != null && levelQuestionFragment1.isVisible()) {
            ft.hide(levelQuestionFragment1);
        }
        if (levelQuestionFragment2 != null && levelQuestionFragment2.isVisible()) {
            ft.hide(levelQuestionFragment2);
        }
        if (levelQuestionFragment3 != null && levelQuestionFragment3.isVisible()) {
            ft.hide(levelQuestionFragment3);
        }
    }

    private RadioGroup rg_root;
    private RadioButton rb_primary;
    private RadioButton rb_intermediate;
    private RadioButton rb_senior;

    private LevelQuestionFragment levelQuestionFragment1;
    private LevelQuestionFragment levelQuestionFragment2;
    private LevelQuestionFragment levelQuestionFragment3;

    private AnswerInitBean answerInitBean;

    private int lastClickId;

    @Override
    public void initWidget() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        rg_root = (RadioGroup) findViewById(R.id.rg_root);
        rb_primary = (RadioButton) findViewById(R.id.rb_primary);
        rb_intermediate = (RadioButton) findViewById(R.id.rb_intermediate);
        rb_senior = (RadioButton) findViewById(R.id.rb_senior);

        rg_root.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_primary:
                        if (answerInitBean != null && answerInitBean.data != null && answerInitBean.data.level >= 1) {
                            lastClickId = checkedId;
                            setTabSelection(1);
                        } else {
                            showToast("等级未到达");
                        }
                        break;
                    case R.id.rb_intermediate:
                        if (answerInitBean != null && answerInitBean.data != null && answerInitBean.data.level >= 2) {
                            lastClickId = checkedId;
                            setTabSelection(2);
                        } else {
                            rg_root.check(lastClickId);
                            if (TextUtils.isEmpty(intermediateLockScore)) {
                                showToast("等级未到达");
                            } else {
                                showToast("需要" + intermediateLockScore + "分解锁");
                            }
                        }
                        break;
                    case R.id.rb_senior:
                        if (answerInitBean != null && answerInitBean.data != null && answerInitBean.data.level >= 3) {
                            lastClickId = checkedId;
                            setTabSelection(3);
                        } else {
                            rg_root.check(lastClickId);
                            if (TextUtils.isEmpty(seniorLockScore)) {
                                showToast("等级未到达");
                            } else {
                                showToast("需要" + seniorLockScore + "分解锁");
                            }
                        }
                        break;
                }
            }
        });
        setTabSelection(1);
        initQAConfig();
        initData();
    }

    String intermediateLockScore;
    String seniorLockScore;

    private int primaryAnswerTime;//初级答题时间
    private int intermediateAnswerTime;//中级答题时间
    private int seniorAnswerTime;//高级答题时间

    public void initQAConfig() {
        NetUtil.get(ZConfig.SERVICE_URL + "/api/v1/answer/answerConfig", new NetUtil.CallBack() {
            @Override
            public void onSuccess(String t) {
                QAConfigBean qaConfigBean = new Gson().fromJson(t, QAConfigBean.class);
                if (qaConfigBean == null || qaConfigBean.data == null || qaConfigBean.data.isEmpty()) {
                    return;
                }
                for (QAConfigBean.DataBean item : qaConfigBean.data) {
                    switch (item.name) {
                        case "duration"://等级对应的答题时长
                            parseAnswerTime(item);
                            break;
                        case "unlock"://解锁分数
                            parseLockScore(item);
                            break;
                        case "frequency"://答题次数

                            break;
                        case "numAnswer"://可选答题次数
                            parseAnswerNum(item);
                            break;
                        case "fraction"://每次分数

                            break;
                    }
                }
            }

        });
    }

    private void parseAnswerTime(QAConfigBean.DataBean item) {
        if (item == null || item.value == null) {
            return;
        }
        ArrayList<String> data = (ArrayList<String>) item.value;
        if (data.size() != 3) {
            return;
        }
        primaryAnswerTime = parseInt(data.get(0));
        intermediateAnswerTime = parseInt(data.get(1));
        seniorAnswerTime = parseInt(data.get(2));

        if (levelQuestionFragment1 != null) {
            levelQuestionFragment1.setAnswerTime(primaryAnswerTime);
        }
        if (levelQuestionFragment2 != null) {
            levelQuestionFragment2.setAnswerTime(intermediateAnswerTime);
        }
        if (levelQuestionFragment3 != null) {
            levelQuestionFragment3.setAnswerTime(seniorAnswerTime);
        }
    }

    private int parseInt(String str) {
        if (TextUtils.isEmpty(str)) {
            return 30;
        }
        return Integer.parseInt(str);
    }


    /**
     * 解锁分
     *
     * @param item
     */
    private void parseLockScore(QAConfigBean.DataBean item) {
        if (item == null || item.value == null) {
            return;
        }
        ArrayList<String> data = (ArrayList<String>) item.value;
        if (data.size() != 3) {
            return;
        }
        intermediateLockScore = data.get(1);
        seniorLockScore = data.get(2);
    }

    ArrayList<String> answerNumList;

    /**
     * 答题次数配置
     *
     * @param item
     */
    private void parseAnswerNum(QAConfigBean.DataBean item) {
        if (item == null || item.value == null) {
            return;
        }
        answerNumList = (ArrayList<String>) item.value;
        if (levelQuestionFragment1 != null) {
            levelQuestionFragment1.setAnswerNumConfig(answerNumList);
        }
        if (levelQuestionFragment2 != null) {
            levelQuestionFragment2.setAnswerNumConfig(answerNumList);
        }
        if (levelQuestionFragment3 != null) {
            levelQuestionFragment3.setAnswerNumConfig(answerNumList);
        }
    }

    public void initData() {
        Map<String, String> map = new HashMap<>();
        map.put("token", AccountManager.getInstance().getUserInfo().getToken());
        NetUtil.getNoCache(ZConfig.SERVICE_URL + "/api/v1/answer/answerInit", map, new NetUtil.CallBack() {
            @Override
            public void onSuccess(String t) {
                answerInitBean = new Gson().fromJson(t, AnswerInitBean.class);
                if (answerInitBean != null && answerInitBean.data != null) {
                    if (levelQuestionFragment1 != null) {
                        levelQuestionFragment1.setAnswerCount(answerInitBean.data.frequency);
                        levelQuestionFragment1.setPromptNum(answerInitBean.data.prompt);
                    }
                    switch (answerInitBean.data.level) {
                        case 3:
                            rb_senior.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        case 2:
                            rb_intermediate.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        case 1:
                            rb_primary.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                            break;
                    }
                }
            }
        });
    }

    @Override
    public int getScollY() {
        return 0;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void reStart(String tag) {
        switch (tag) {
            case "refresh_init_url":
                initData();
                break;
        }
    }

    public void setAnswerCount(int frequency) {
        if (answerInitBean != null && answerInitBean.data != null) {
            answerInitBean.data.frequency = frequency;
            if (levelQuestionFragment1 != null) {
                levelQuestionFragment1.setAnswerCount(answerInitBean.data.frequency);
            }
            if (levelQuestionFragment2 != null) {
                levelQuestionFragment2.setAnswerCount(answerInitBean.data.frequency);
            }
            if (levelQuestionFragment3 != null) {
                levelQuestionFragment3.setAnswerCount(answerInitBean.data.frequency);
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onVisible() {
        super.onVisible();
        MusicPlayerManager instance = MusicPlayerManager.getInstance();
        if (instance != null) {
            if (instance.getCurrentPlayerMusic() != null) {
                instance.pause();
                EventBus.getDefault().post("audio_chapter_no_cache");
            }
        }
    }

    @Override
    protected void onInvisible() {
        super.onInvisible();
//        MusicPlayerManager instance = MusicPlayerManager.getInstance();
//        if (instance != null) {
//            BaseAudioInfo currentPlayerMusic = instance.getCurrentPlayerMusic();
//            if (currentPlayerMusic!=null){
//
//            }
//        }
    }
}