package com.read.scriptures.ui.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SynthesizerListener;
import com.read.scriptures.R;
import com.read.scriptures.adapter.CurrentAnswerStatusAdapter;
import com.read.scriptures.adapter.StartAnswerAdapter;
import com.read.scriptures.bean.AnswerPromptBean;
import com.read.scriptures.bean.AnswerTipBean;
import com.read.scriptures.bean.CommitAnswerBean;
import com.read.scriptures.bean.QuestionBean;
import com.read.scriptures.bean.StartAnswerAgainBean;
import com.read.scriptures.config.SystemConfig;
import com.read.scriptures.config.ZConfig;
import com.read.scriptures.control.BaiduSpeechManager;
import com.read.scriptures.manager.AccountManager;
import com.read.scriptures.manager.XunFeiSpeechManager;
import com.read.scriptures.manager.alispeech.AliSpeechManager;
import com.read.scriptures.net.NetworkUtils;
import com.read.scriptures.ui.activity.base.BaseActivity;
import com.read.scriptures.ui.fragment.LevelQuestionFragment;
import com.read.scriptures.util.DensityUtil;
import com.read.scriptures.util.DialogUtils;
import com.music.player.lib.util.NetUtil;
import com.read.scriptures.util.PreferencesUtils;
import com.read.scriptures.util.SharedUtil;
import com.read.scriptures.util.StatusBarUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

public class StartAnswerActivity extends BaseActivity {

    public static final String QUESTION_DATA = "QUESTION_DATA";
    public static final String QUESTION_LEVEL = "QUESTION_LEVEL";
    public static final String REMAIN_ANSWER_NUM = "REMAIN_ANSWER_NUM";

    private StartAnswerAdapter answerAdapter;
    private RecyclerView rcv_question_list;
    private LinearLayoutManager layoutManager;
    private QuestionBean.DataBean questionBean;

    private boolean isVoice = true;
    private String questionLevel;
    private SoundPool soundPool;

    private boolean audioIsPlay = false;
    private Dialog dialog;
    private boolean isAnswerNum = false;
    private boolean isAnswerComplete = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_start_answer);
        StatusBarUtils.initMainColorStatusBar(this);
//        MusicPlayerManager musicPlayerManager = MusicPlayerManager.getInstance();
//        if (musicPlayerManager.isPlaying()) {
//            audioIsPlay = true;
//            musicPlayerManager.playOrPause();
//        }
        initView();
        initData();
        initSpeechTts("");
        playBgMusic(R.raw.bg_music, -1);
    }

    private void playBgMusic(int resId, int loop) {
        if (soundPool == null) {
            //实例化SoundPool
            //sdk版本21是SoundPool 的一个分水岭
            SoundPool.Builder builder = new SoundPool.Builder();
            //传入最多播放音频数量,
            builder.setMaxStreams(2);
            //AudioAttributes是一个封装音频各种属性的方法
            AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
            //设置音频流的合适的属性
            attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);
            //加载一个AudioAttributes
            builder.setAudioAttributes(attrBuilder.build());
            soundPool = builder.build();
        }
        //声音ID 加载音频资源,这里用的是第二种，第三个参数为priority，声音的优先级*API中指出，priority参数目前没有效果，建议设置为1。
        final int voiceId = soundPool.load(StartAnswerActivity.this, resId, 1);
        //异步需要等待加载完成，音频才能播放成功
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                if (status == 0) {
                    //第一个参数soundID
                    //第二个参数leftVolume为左侧音量值（范围= 0.0到1.0）
                    //第三个参数rightVolume为右的音量值（范围= 0.0到1.0）
                    //第四个参数priority 为流的优先级，值越大优先级高，影响当同时播放数量超出了最大支持数时SoundPool对该流的处理
                    //第五个参数loop 为音频重复播放次数，0为值播放一次，-1为无限循环，其他值为播放loop+1次
                    //第六个参数 rate为播放的速率，范围0.5-2.0(0.5为一半速率，1.0为正常速率，2.0为两倍速率)
                    soundPool.play(sampleId, 1, 1, 1, loop, 1);
                }
            }
        });
    }

    private TextView bt_back;
    private TextView tv_time_down;
    private TextView tv_answer_tip;
    private TextView tv_answer_right;
    private TextView tv_answer_error;
    private TextView tv_answer_count;
    private TextView tv_tip_answer;
    private ImageView iv_voice;

    private int currentPosition = 1;

    private void initView() {
        rcv_question_list = findViewById(R.id.rcv_question_list);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }

            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        };


        rcv_question_list.setLayoutManager(layoutManager);
        answerAdapter = new StartAnswerAdapter();
        rcv_question_list.setAdapter(answerAdapter);

        PagerSnapHelper lineSnapHelper = new PagerSnapHelper();
        lineSnapHelper.attachToRecyclerView(rcv_question_list);

        bt_back = findViewById(R.id.bt_back);
        tv_time_down = findViewById(R.id.tv_time_down);
        tv_answer_tip = findViewById(R.id.tv_answer_tip);
        tv_answer_right = findViewById(R.id.tv_answer_right);
        tv_answer_error = findViewById(R.id.tv_answer_error);
        tv_answer_count = findViewById(R.id.tv_answer_count);
        tv_tip_answer = findViewById(R.id.tv_tip_answer);
        iv_voice = findViewById(R.id.iv_voice);

        bt_back.setOnClickListener(this);
        tv_answer_tip.setOnClickListener(this);
        iv_voice.setOnClickListener(this);
        tv_answer_count.setOnClickListener(this);

        answerAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.iv_repeat:
                        view.animate().scaleX(0.8f).scaleY(0.8f).withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                view.animate().scaleX(1.2f).scaleY(1.2f).start();
                            }
                        }).setDuration(100).start();
                        QuestionBean.DataBean.RowsBean item = answerAdapter.getItem(position);
                        initSpeechTts(item.title);
                        break;
                }
            }
        });
        GestureDetector gestureDetector = new GestureDetector(this, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                Log.d("apppp", "onScroll:" + distanceX + "," + distanceY);
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e1.getX() - e2.getX() > 150) {
                    scrollRight();
                    return true;
                }
                if (e2.getX() - e1.getX() > 150) {
                    scrollLeft();
                    return true;
                }
                return true;
            }
        });

        rcv_question_list.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });
    }

    private void scrollRight() {
        int currentVisiblePosition = layoutManager.findLastCompletelyVisibleItemPosition();
        if (currentVisiblePosition != -1) {
            int nextPosition = currentVisiblePosition + 1;
            if (nextPosition < answerAdapter.getItemCount()) {
                QuestionBean.DataBean.RowsBean item = answerAdapter.getItem(nextPosition);
                if (item != null && item.isLoad) {
                    currentPosition = nextPosition;
                    rcv_question_list.scrollToPosition(nextPosition);
                    String tip = answerTipMap.get(String.valueOf(item.id));
                    if (TextUtils.isEmpty(tip)) {
                        tv_tip_answer.setText("");
                    } else {
                        tv_tip_answer.setText("提示：\n" + tip);
                    }
                }
            }
        }
    }

    private void scrollLeft() {
        int currentVisiblePosition = layoutManager.findLastCompletelyVisibleItemPosition();
        if (currentVisiblePosition != -1) {
            int lastPosition = currentVisiblePosition - 1;
            if (lastPosition >= 0) {
                QuestionBean.DataBean.RowsBean item = answerAdapter.getItem(lastPosition);
                if (item != null) {
                    currentPosition = lastPosition;
                    rcv_question_list.scrollToPosition(lastPosition);
                    String tip = answerTipMap.get(String.valueOf(item.id));
                    if (TextUtils.isEmpty(tip)) {
                        tv_tip_answer.setText("");
                    } else {
                        tv_tip_answer.setText("提示：\n" + tip);
                    }
                }
            }
        }
    }

    private void showPrompt() {
        if (!isAnswerComplete) {
            DialogUtils.showNormalDialog(this, "提示", "您是否放弃本次答题？", "放弃", "继续答题", new DialogUtils.onDialogClickListener() {
                @Override
                public void onCancel(Dialog dialog) {
                    dialog.dismiss();
                    if (countDownTimer != null) {
                        countDownTimer.cancel();
                        countDownTimer = null;
                    }
                    finish();
                }

                @Override
                public void onOk(Dialog dialog) {
                    dialog.dismiss();
                }
            });
        } else {
            finish();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_back:
                showPrompt();
                break;
            case R.id.tv_answer_tip:
                getAnswerTip();
                break;
            case R.id.iv_voice:
                boolean voice = PreferencesUtils.getBoolean(StartAnswerActivity.this, "question_voice", true);
                if (voice) {
                    if (mXunFeiSpeechManager != null) {
                        mXunFeiSpeechManager.stopSpeaking();
                    }
                    isVoice = false;
                    showToast("声音已关闭");
                    iv_voice.setImageResource(R.drawable.icon_voice);
                    PreferencesUtils.putBoolean(StartAnswerActivity.this, "question_voice", false);
                } else {
                    isVoice = true;
                    showToast("声音已开启");
                    iv_voice.setImageResource(R.drawable.icon_voice1);
                    PreferencesUtils.putBoolean(StartAnswerActivity.this, "question_voice", true);
                }
                break;
            case R.id.tv_answer_count:
                showCurrentAnswerStatus();
                break;
        }
    }

    private void showCurrentAnswerStatus() {
        showCurrentAnswerDialog();
    }

    private void showCurrentAnswerDialog() {
        int height = answerAdapter.getItemCount() >= 50 ? DensityUtil.getScreenHeight(StartAnswerActivity.this) * 3 / 5 : (int) (DensityUtil.getScreenHeight(StartAnswerActivity.this) * 0.3);

        dialog = DialogUtils.showBottomDialog(this, R.layout.dialog_current_answer_layout, -1, height, new DialogUtils.InitViewsListener() {
            @Override
            public void setAction(Dialog dialog, View view) {
                TextView tv_answer_right2 = view.findViewById(R.id.tv_answer_right);
                TextView tv_answer_error2 = view.findViewById(R.id.tv_answer_error);
                TextView tv_answer_count2 = view.findViewById(R.id.tv_answer_count);
                View ll_main = view.findViewById(R.id.ll_main);

                tv_answer_right2.setText(tv_answer_right.getText());
                tv_answer_error2.setText(tv_answer_error.getText());
                tv_answer_count2.setText(tv_answer_count.getText());

                RecyclerView rcv_question_status = view.findViewById(R.id.rcv_question_status);
                rcv_question_status.setLayoutManager(new GridLayoutManager(view.getContext(), 7));
                CurrentAnswerStatusAdapter currentAnswerStatusAdapter = new CurrentAnswerStatusAdapter();
                currentAnswerStatusAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                        QuestionBean.DataBean.RowsBean item = currentAnswerStatusAdapter.getItem(position);
                        if (rl_main != null && isAnswerComplete) {
                            rl_main.setVisibility(View.GONE);
                        }
                        if (item.isLoad) {
                            currentPosition = position;
                            rcv_question_list.scrollToPosition(position);
                            String tip = answerTipMap.get(String.valueOf(item.id));
                            if (TextUtils.isEmpty(tip)) {
                                tv_tip_answer.setText("");
                            } else {
                                tv_tip_answer.setText("提示：\n" + tip);
                            }
                        }
                    }
                });
                rcv_question_status.setAdapter(currentAnswerStatusAdapter);
                currentAnswerStatusAdapter.setIsEnd(isAnswerComplete);
                currentAnswerStatusAdapter.setCurrentQuestion(answerAdapter.getItem(currentPosition));
                currentAnswerStatusAdapter.setNewData(answerAdapter.getData());
            }
        });
    }


    private Map<String, String> answerTipMap = new ArrayMap<>();
    private String loadAnswerId;
    private int promptCount = 0;

    /**
     * 获取答案提示
     */
    private void getAnswerTip() {
        String id = questionBean.rows.get(currentPosition).id + "";
        String answerTip = tv_tip_answer.getText().toString();
        if (!TextUtils.isEmpty(answerTip)) {
            initSpeechTts(answerTip);
            return;
        }
        showProgressDialog("");
        Map<String, String> map = new HashMap<>();
        map.put("token", AccountManager.getInstance().getUserInfo().getToken());
        map.put("id", id);
        loadAnswerId = id;
        if (promptCount <= 0) {
            showToast("提示次数已不足,请购买");
            return;
        }
        NetUtil.getNoCache(ZConfig.SERVICE_URL + "/api/v1/answer/prompt", map, new NetUtil.CallBack() {
            @Override
            public void onSuccess(String t) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                dismissProgressDialog();
                AnswerTipBean answerTipBean = new Gson().fromJson(t, AnswerTipBean.class);
                if (answerTipBean.data != null) {
                    answerTipMap.put(id, answerTipBean.data.tips);
                    promptCount = answerTipBean.data.prompt;
                    if (TextUtils.equals(loadAnswerId, id)) {
                        tv_tip_answer.setText("提示：\n" + answerTipBean.data.tips);
                        if (answerTipBean.data.prompt <= 0) {
                            tv_answer_tip.setText("提示：" + 0);
                            tv_answer_tip.setVisibility(View.INVISIBLE);
                        } else {
                            tv_answer_tip.setText("提示：" + answerTipBean.data.prompt);
                            tv_answer_tip.setVisibility(View.VISIBLE);
                        }
                        initSpeechTts(answerTipBean.data.tips);
                    }
                }
            }

            @Override
            public void onError(String t) {
                super.onError(t);
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                dismissProgressDialog();
                showToast(t);
            }
        });
    }

    private void initData() {
        questionBean = (QuestionBean.DataBean) getIntent().getSerializableExtra(QUESTION_DATA);
        if (questionBean == null || questionBean.rows == null || questionBean.rows.isEmpty()) {
            finish();
            return;
        }
        questionLevel = getIntent().getStringExtra(QUESTION_LEVEL);
        promptCount = questionBean.prompt;
        if (questionBean.prompt <= 0) {
            tv_answer_tip.setVisibility(View.INVISIBLE);
        } else {
            tv_answer_tip.setVisibility(View.VISIBLE);
            tv_answer_tip.setText("提示 " + questionBean.prompt);
        }
        String sort = questionBean.rows.get(0).sort;
        bt_back.setText(sort);
        answerAdapter.setNewData(questionBean.rows);
        isAnswerNum = getIntent().getBooleanExtra(REMAIN_ANSWER_NUM, false);
        isVoice = PreferencesUtils.getBoolean(StartAnswerActivity.this, "question_voice", true);
        iv_voice.setImageResource(isVoice ? R.drawable.icon_voice1 : R.drawable.icon_voice);
        int answerTime = getIntent().getIntExtra(LevelQuestionFragment.ANSWER_TIME, 30);
        countDownTimer = new CountDownTimer(answerTime * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String value = String.valueOf((int) (millisUntilFinished / 1000));
                tv_time_down.setText("倒计时：" + value + "s");
            }

            @Override
            public void onFinish() {
                QuestionBean.DataBean.RowsBean item = answerAdapter.getItem(currentPosition);
                if (item != null) {
                    item.selectedAnswer = "null";
                    item.isRightAnswer = false;
                }
                loadNextPage(item, currentPosition, false);
            }
        };
    }

    public void onNextPage(QuestionBean.DataBean.RowsBean item, int position) {
        initSpeechTts(item.title);
        currentPosition = position;
        tv_answer_count.setText((position + 1) + "/" + answerAdapter.getItemCount());
        String answerTip = answerTipMap.get(String.valueOf(item.id));
        tv_tip_answer.setText(answerTip);
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        countDownTimer.start();
    }

    private CountDownTimer countDownTimer;


//            = new CountDownTimer(30 * 1000, 1000) {
//        @Override
//        public void onTick(long millisUntilFinished) {
//            String value = String.valueOf((int) (millisUntilFinished / 1000));
//            tv_time_down.setText("倒计时：" + value + "s");
//        }
//
//        @Override
//        public void onFinish() {
//            loadNextPage(answerAdapter.getItem(currentPosition), currentPosition, false);
//        }
//    };


    private int rightAnswerCount = 0;
    private int errorAnswerCount = 0;

    public void loadNextPage(QuestionBean.DataBean.RowsBean item, int position, boolean needDelay) {
        if (item != null) {
            if (item.isRightAnswer) {
                playBgMusic(R.raw.right, 0);
                rightAnswerCount++;
            } else {
                playBgMusic(R.raw.error, 0);
                errorAnswerCount++;
            }
            tv_answer_right.setText(String.valueOf(rightAnswerCount));
            tv_answer_error.setText(String.valueOf(errorAnswerCount));
            bt_back.setText(item.sort);
        }
        int pos = position + 1;
        if (pos >= answerAdapter.getItemCount()) {
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }
            tv_time_down.setText("倒计时：0s");
            answerAdapter.setAnswerComplete(true);
            onAnswerComplete();
            return;
        }
        //放到这里优先走答题完毕的逻辑
        if (errorAnswerCount >= 3) {
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }
            onAnswerComplete();
            return;
        }
        if (needDelay) {
            rcv_question_list.postDelayed(new Runnable() {
                @Override
                public void run() {
                    rcv_question_list.scrollToPosition(pos);
                }
            }, 600);
        } else {
            rcv_question_list.scrollToPosition(pos);
        }
    }

    private void onAnswerComplete() {
        if (mXunFeiSpeechManager != null) {
            mXunFeiSpeechManager.stopSpeaking();
        }
        isAnswerComplete = true;
        showProgressDialog("");
        String answerStr = parseAnswer();
        Map<String, String> map = new HashMap<>();
        map.put("token", AccountManager.getInstance().getUserInfo().getToken());
        map.put("checkId", questionBean.checkId);
        map.put("value", answerStr);
        NetUtil.getNoCache(ZConfig.SERVICE_URL + "/api/v1/answer/correct", map, new NetUtil.CallBack() {
            @Override
            public void onSuccess(String t) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                dismissProgressDialog();
                CommitAnswerBean commitAnswerBean = new Gson().fromJson(t, CommitAnswerBean.class);
                if (commitAnswerBean.data != null) {
                    initEndView(commitAnswerBean);
                }
            }

            @Override
            public void onError(String t) {
                super.onError(t);
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                dismissProgressDialog();
                showToast(t);
            }
        });
    }

    private View rl_main;

    private void initEndView(CommitAnswerBean commitAnswerBean) {
        if (dialog != null) {
            dialog.dismiss();
        }

        ViewStub viewStub = findViewById(R.id.vs_answer_end);
        if (viewStub == null) {
            finish();
            return;
        }
        viewStub.inflate();
        rl_main = findViewById(R.id.rl_main);
        rl_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //防止点击穿透
            }
        });
        TextView tv_title_1 = findViewById(R.id.tv_title_1);
        TextView tv_title_2 = findViewById(R.id.tv_title_2);
        TextView tv_title_3 = findViewById(R.id.tv_title_3);
        TextView tv_title_4 = findViewById(R.id.tv_title_4);
        TextView tv_title_5 = findViewById(R.id.tv_title_5);
        TextView tv_total_score = findViewById(R.id.tv_total_score);
        TextView tv_complete_score = findViewById(R.id.tv_complete_score);
        TextView tv_back_home = findViewById(R.id.tv_back_home);
        TextView tv_back_home1 = findViewById(R.id.tv_back_home1);
        View cv_2 = findViewById(R.id.cv_2);
        if (isAnswerNum) {
            cv_2.setVisibility(View.VISIBLE);
        } else {
            cv_2.setVisibility(View.GONE);
        }
        TextView tv_answer_tip1 = findViewById(R.id.tv_answer_tip1);
        TextView tv_answer_right1 = findViewById(R.id.tv_answer_right1);
        TextView tv_answer_error1 = findViewById(R.id.tv_answer_error1);
        TextView tv_answer_count1 = findViewById(R.id.tv_answer_count1);

        switch (commitAnswerBean.data.unlockLevel) {
            case 0:
                tv_title_1.setText("抱歉，本次答题失败。请再接再厉");
                tv_title_2.setVisibility(View.GONE);
                break;
            case 2:
                tv_title_1.setText("恭喜！完成本次答题，辛苦啦");
                EventBus.getDefault().post("refresh_init_url");
                tv_title_2.setText("已成功解锁“中级“问答");
                break;
            case 3:
                tv_title_1.setText("恭喜！完成本次答题，辛苦啦");
                EventBus.getDefault().post("refresh_init_url");
                tv_title_2.setText("已成功解锁“高级“问答");
                break;
        }
//        if (commitAnswerBean.data.prize == 0) {
//            tv_title_3.setVisibility(View.GONE);
//        } else {
//            tv_title_3.setText("本次，我们将送您" + commitAnswerBean.data.prize + "张蓝卡");
//        }
        if (commitAnswerBean.data.unlockLevel != 0) {
            tv_title_4.setText("恭喜过关!");
        } else {
            tv_title_4.setVisibility(View.GONE);
        }

        String errorCountStr = tv_answer_error.getText().toString();
        int errorCount = Integer.parseInt(errorCountStr);
        if (errorCount >= 3) {
            tv_title_4.setVisibility(View.VISIBLE);
            tv_title_5.setVisibility(View.VISIBLE);
            tv_title_4.setText("很抱歉！");
            tv_title_5.setText("本次已经错了3次了，\n希望您多巩固。");
        }

        tv_total_score.setText("总分数：" + commitAnswerBean.data.totalFraction + "分");
        tv_complete_score.setText("已完成：" + commitAnswerBean.data.fraction + "分");
        tv_back_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tv_back_home1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mXunFeiSpeechManager != null) {
                    mXunFeiSpeechManager.destroy();
                }
                finish();
                StartAnswerAgainBean answerAgainBean = new StartAnswerAgainBean();
                answerAgainBean.level = questionLevel;
                EventBus.getDefault().post(answerAgainBean);
            }
        });

        if (commitAnswerBean.data.prompt <= 0) {
            tv_answer_tip1.setVisibility(View.INVISIBLE);
        } else {
            tv_answer_tip1.setVisibility(View.VISIBLE);
            tv_answer_tip1.setText("提示 " + questionBean.prompt);
        }
        tv_answer_right1.setText(tv_answer_right.getText());
        tv_answer_error1.setText(tv_answer_error.getText());
        tv_answer_count1.setText(tv_answer_count.getText());

        tv_answer_count1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCurrentAnswerStatus();
            }
        });

    }

    private String parseAnswer() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < questionBean.rows.size(); i++) {
            QuestionBean.DataBean.RowsBean item = questionBean.rows.get(i);
            stringBuilder.append(item.selectedAnswer);
            if (i != questionBean.rows.size() - 1) {
                stringBuilder.append("#&");
            }
        }
        return stringBuilder.toString();
    }

    public void loadAnswerComplete() {
        onAnswerComplete();
    }

    /*--------------------------------------------下面是语音模块-------------------------------------------*/

    @SuppressLint("HandlerLeak")
    Handler mainHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
//            handle(msg);
        }
    };

    private XunFeiSpeechManager mXunFeiSpeechManager;
    private BaiduSpeechManager baiduSpeechManager;
    private AliSpeechManager mAliSpeechManager;

    private void initSpeechTts(String str) {
        // 初始化合成对象
        if (mXunFeiSpeechManager == null) {
//            baiduSpeechManager = new BaiduSpeechManager(this, mainHandler);
            mXunFeiSpeechManager = new XunFeiSpeechManager(this);
            mXunFeiSpeechManager.setTtsListener(mTtsListener);
            mXunFeiSpeechManager.init(mTtsInitListener);
//            mAliSpeechManager = new AliSpeechManager(this, mainHandler);
//            mAliSpeechManager.setSpeechPopupWindow(mSpeechPopupWindow);
        } else {
            mXunFeiSpeechManager.stopSpeaking();
            startSpeech(str);
        }
    }

    private void startSpeech(String remarkTxt) {
        if (TextUtils.isEmpty(remarkTxt) || !isVoice) {
            return;
        }
        if (!NetworkUtils.isNetAvailable(getApplicationContext()) && SystemConfig.Speech_Model != SystemConfig.SPEECH_MODEL_BAIDU) {
            SystemConfig.Speech_Model = SystemConfig.SPEECH_MODEL_BAIDU;
            SharedUtil.putInt(SystemConfig.SP_SPEACH_MODEL_KEY, SystemConfig.Speech_Model);
        }
//        if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_XF) {
        mXunFeiSpeechManager.startSpeaking(remarkTxt, mTtsListener);
//        } else if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_BAIDU) {
//            baiduSpeechManager.speak(remarkTxt);
//        } else if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_ALI) {
//            mAliSpeechManager.speak(remarkTxt);
//        }
    }

    /**
     * 合成回调监听。
     */
    private final SynthesizerListener mTtsListener = new SynthesizerListener() {
        @Override
        public void onSpeakBegin() {
        }

        @Override
        public void onSpeakPaused() {
        }

        @Override
        public void onSpeakResumed() {
        }

        @Override
        public void onBufferProgress(final int percent, final int beginPos, final int endPos, final String info) {
            // 合成进度
        }

        @Override
        public void onSpeakProgress(final int percent, final int beginPos, final int endPos) {
            // 播放进度
//            if (percent > 99) {
//                this.onCompleted(null);
//            }
        }

        @Override
        public void onCompleted(final SpeechError error) {
        }

        @Override
        public void onEvent(final int eventType, final int arg1, final int arg2, final Bundle obj) {
        }
    };

    private final InitListener mTtsInitListener = new InitListener() {

        @Override
        public void onInit(final int code) {
            if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_XF) {
                dismissProgressDialog();
            }
            if (code == ErrorCode.SUCCESS) {
                // 初始化成功，之后可以调用startSpeaking方法
                // 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
                // 正确的做法是将onCreate中的startSpeaking调用移至这里
                if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_XF) {

                }
            } else {
                showToast("语音初始化失败,错误码：" + code);
            }
        }
    };

    @Override
    public void onBackPressed() {
        if (!isAnswerComplete) {
            DialogUtils.showNormalDialog(this, "提示", "您是否放弃本次答题？", "放弃", "继续答题", new DialogUtils.onDialogClickListener() {
                @Override
                public void onCancel(Dialog dialog) {
                    dialog.dismiss();
                    if (countDownTimer != null) {
                        countDownTimer.cancel();
                        countDownTimer = null;
                    }
                    StartAnswerActivity.super.onBackPressed();
                }

                @Override
                public void onOk(Dialog dialog) {
                    dialog.dismiss();
                }
            });
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (soundPool != null) {
            soundPool.autoResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (soundPool != null) {
            soundPool.autoPause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mXunFeiSpeechManager != null) {
            mXunFeiSpeechManager.stopSpeaking();
        }
        AnswerPromptBean answerPromptBean = new AnswerPromptBean();
        answerPromptBean.answerPromptNum = promptCount;
        EventBus.getDefault().post(answerPromptBean);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (soundPool != null) {
            soundPool.stop(0);
        }
        if (mXunFeiSpeechManager != null) {
            mXunFeiSpeechManager.destroy();
        }
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }

//        MusicPlayerManager musicPlayerManager = MusicPlayerManager.getInstance();
//        if (musicPlayerManager != null && audioIsPlay) {
//            musicPlayerManager.playOrPause();
//        }
    }
}
