package com.read.scriptures.audio;

import android.os.Bundle;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.music.player.lib.bean.BaseAudioInfo;
import com.music.player.lib.listener.MusicPlayerInfoListener;
import com.music.player.lib.manager.MusicPlayerManager;
import com.read.scriptures.EIUtils.DateUtil;
import com.read.scriptures.R;
import com.read.scriptures.bean.CollectAudioBean;
import com.read.scriptures.bean.DateBean;
import com.read.scriptures.bean.NewAudioChapterData;
import com.read.scriptures.config.ZConfig;
import com.read.scriptures.manager.AccountManager;
import com.read.scriptures.ui.activity.base.BaseActivity;
import com.music.player.lib.util.NetUtil;
import com.read.scriptures.util.CalendarUtil;
import com.read.scriptures.util.DateUtils;
import com.read.scriptures.util.StatusBarUtils;
import com.read.scriptures.util.TimeUtils;
import com.read.scriptures.view.AudioPlayingView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class CollectAudioListActivity extends BaseActivity {

    private AudioPlayingView fl_view;

    private MusicPlayerManager musicPlayerManager;
    private MusicPlayerInfoListener musicPlayerInfoListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_collect_audio);
        StatusBarUtils.initMainColorStatusBar(this);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        initView();
        initData();
    }

    private CollectDateAdapter collectDateAdapter;
    private ImageView iv_show;
    private ImageView iv_month_left;
    private ImageView iv_month_right;
    private TextView tv_current_days;
    private LinearLayout ll_top;
    private AllDayAdapter allDayAdapter;

    private List<DateBean> nowDate = new ArrayList<>();
    private ArrayList<DateBean> monthDate;
    private RecyclerView rcv_collect_list;
    private LinearLayoutManager linearLayoutManager;

    private int year;
    private int month;

    private void initView() {
        fl_view = findViewById(R.id.fl_view);
        TextView tv_cate = findViewById(R.id.tv_cate);
        tv_cate.setText("收藏列表");

        ll_top = findViewById(R.id.ll_top);
        iv_month_left = findViewById(R.id.iv_month_left);
        iv_month_right = findViewById(R.id.iv_month_right);
        iv_show = findViewById(R.id.iv_show);
        tv_current_days = findViewById(R.id.tv_current_days);
        rcv_collect_list = findViewById(R.id.rcv_collect_list);
        linearLayoutManager = new LinearLayoutManager(this);
        rcv_collect_list.setLayoutManager(linearLayoutManager);


        collectDateAdapter = new CollectDateAdapter();
        rcv_collect_list.setAdapter(collectDateAdapter);
        year = parseInt(TimeUtils.getYearStr());
        month = parseInt(TimeUtils.getMonth());
        tv_current_days.setText(year + "年" + month + "月");


        RecyclerView rcv_date_list = findViewById(R.id.rcv_date_list);
        rcv_date_list.setLayoutManager(new GridLayoutManager(this, 7));
        allDayAdapter = new AllDayAdapter();
        rcv_date_list.setAdapter(allDayAdapter);

        allDayAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                for (DateBean item : allDayAdapter.getData()) {
                    item.isOnClick = false;
                }
                DateBean item = allDayAdapter.getItem(position);
                if (item != null) {
                    item.isOnClick = true;
                    if (item.data == null) {
                        showToast("没有数据...");
                        return;
                    } else {
                        allDayAdapter.notifyDataSetChanged();
                        scrollToPosition(item);
                    }
                }
            }
        });

        iv_show.setOnClickListener(this);
        iv_month_left.setOnClickListener(this);
        iv_month_right.setOnClickListener(this);

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private CollectAudioBean collectAudioBean;

    private void initNetData(CollectAudioBean collectAudioBean) {
        this.collectAudioBean = collectAudioBean;
        ArrayList<DateBean> dateBeans = formatCollectData(collectAudioBean);
        collectDateAdapter.setNewData(dateBeans);
        long currentTime = TimeUtils.getTargetTime(year, month);
        monthDate = CalendarUtil.getMonthDate(currentTime, collectAudioBean.data.rows);
        String toDay = TimeUtils.getTimeKey(System.currentTimeMillis());

        int line = 0;
        for (int i = 0; i < monthDate.size(); i++) {
            DateBean item = monthDate.get(i);
            item.isOnClick = false;
            if (TextUtils.equals(toDay, item.timeStr)) {
                line = i / 7;
            }
        }
        nowDate = monthDate.subList(line * 7, Math.min((line + 1) * 7, monthDate.size()));
        allDayAdapter.setNewData(nowDate);
    }

    private void scrollToPosition(DateBean timeStr) {
        List<DateBean> data = collectDateAdapter.getData();
        int index = data.indexOf(timeStr);
        if (index != -1) {
            linearLayoutManager.scrollToPositionWithOffset(index, 0);
        }
    }

    private void playAudio(List<BaseAudioInfo> data, int position) {
        collectDateAdapter.setCurrentAudio(data.get(Math.max(position, 0)));
        musicPlayerManager.startPlayMusic(data, Math.max(position, 0));
        musicPlayerManager.setCurrentAuthor(data.get(Math.max(position, 0)).author);
    }

    private void initData() {
        musicPlayerManager = MusicPlayerManager.getInstance().init(getApplicationContext());
        if (musicPlayerManager.isPlaying()) {
            BaseAudioInfo currentPlayerMusic = musicPlayerManager.getCurrentPlayerMusic();
            collectDateAdapter.setCurrentAudio(currentPlayerMusic);
        }
        initMusicPlayerManager();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCollectData();
    }

    private void initMusicPlayerManager() {
        musicPlayerInfoListener = new MusicPlayerInfoListener() {
            @Override
            public void onPlayMusiconInfo(BaseAudioInfo musicInfo, int position) {
                collectDateAdapter.setCurrentAudio(musicInfo);
            }
        };
        //应用播放器配置
        musicPlayerManager.addPlayInfoListener(musicPlayerInfoListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        musicPlayerManager.removePlayInfoListener(musicPlayerInfoListener);
        musicPlayerInfoListener = null;
        fl_view.onActivityDestroy();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void reStart(String tag) {
        switch (tag) {
            case "audio_chapter_no_cache":
                if (fl_view != null) {
                    fl_view.stopAnimation();
//                    fl_view.hide();
                }
                break;
            case "on_audio_stop":
                if (fl_view != null) {
                    fl_view.hide();
                }
                break;
        }
    }


    private void getCollectData() {
        HashMap<String, String> map = new HashMap<>();
        map.put("token", AccountManager.getInstance().getUserInfo().getToken());
        map.put("type", "1");
        map.put("offset", "1");
        map.put("limit", "200");
        NetUtil.getNoCache(ZConfig.SERVICE_URL + "/api/v1/user/collectlist", map, new NetUtil.CallBack() {
            @Override
            public void onSuccess(String t) {
                CollectAudioBean collectAudioBean = new Gson().fromJson(t, CollectAudioBean.class);
                if (collectAudioBean == null || collectAudioBean.data == null || collectAudioBean.data.rows == null || collectAudioBean.data.rows.isEmpty()) {
                    return;
                }
                initNetData(collectAudioBean);

            }
        });
    }


    private ArrayList<DateBean> formatCollectData(CollectAudioBean collectAudioBean) {
        if (collectAudioBean == null || collectAudioBean.data == null || collectAudioBean.data.rows == null) {
            return new ArrayList<>();
        }
        Map<String, ArrayList<CollectAudioBean.DataBean.RowsBean>> array = new TreeMap<>(new Comparator<String>() {
            public int compare(String obj1, String obj2) {
                // 降序排序
                return obj2.compareTo(obj1);
            }
        });
        for (CollectAudioBean.DataBean.RowsBean item : collectAudioBean.data.rows) {//20020202
            String key = TimeUtils.getTimeKey(item.create_time * 1000);
            ArrayList<CollectAudioBean.DataBean.RowsBean> rowsBeans = array.get(key);
            if (rowsBeans == null) {
                rowsBeans = new ArrayList<>();
            }
            rowsBeans.add(item);
            array.put(key, rowsBeans);
        }
        ArrayList<DateBean> dateBeans = new ArrayList<>();
        if (!array.isEmpty()) {
            for (String key : array.keySet()) {
                DateBean dateBean = new DateBean();
                dateBean.monthDay = TimeUtils.getMonthDay(key);
                dateBean.timeStr = key;
                dateBean.data = array.get(key);
                dateBeans.add(dateBean);
            }
        }
        return dateBeans;
    }

    public static int parseInt(String str) {
        if (!TextUtils.isEmpty(str)) {
            return Integer.parseInt(str);
        } else {
            return 0;
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_show:
                showAllDate();
                break;
            case R.id.iv_month_left:
                monthLeft();
                break;
            case R.id.iv_month_right:
                monthRight();
                break;
        }
    }

    private void monthRight() {
        if (year > 2050) {
            return;
        }
        if (month >= 12) {
            month = 1;
            year = year + 1;
        } else {
            month = month + 1;
        }
        long targetTime = TimeUtils.getTargetTime(year, month);
        tv_current_days.setText(year + "年" + month + "月");
        formatData(targetTime);
    }

    private void formatData(long targetTime) {
        if (collectAudioBean == null || collectAudioBean.data == null || collectAudioBean.data.rows == null) {
            return;
        }
        monthDate = CalendarUtil.getMonthDate(targetTime, collectAudioBean.data.rows);
        allDayAdapter.setNewData(monthDate);
    }

    private void monthLeft() {
        if (year < 1970) {
            return;
        }
        if (month <= 1) {
            year = year - 1;
            month = 12;
        } else {
            month = month - 1;
        }
        tv_current_days.setText(year + "年" + month + "月");
        long targetTime = TimeUtils.getTargetTime(year, month);
        formatData(targetTime);
    }

    private void showAllDate() {
        if (isShow) {
            ll_top.setVisibility(View.GONE);
            isShow = false;
            iv_show.setImageResource(R.drawable.icon_date_show);
            allDayAdapter.setNewData(nowDate);
        } else {
            year = parseInt(TimeUtils.getYearStr());
            month = parseInt(TimeUtils.getMonth());
            long currentTime = TimeUtils.getTargetTime(year, month);
            tv_current_days.setText(year + "年" + month + "月");
            formatData(currentTime);

            ll_top.setVisibility(View.VISIBLE);
            isShow = true;
            iv_show.setImageResource(R.drawable.icon_date_hide);
            allDayAdapter.setNewData(monthDate);
        }
    }

    private boolean isShow = false;

    public void onAudioItemClick(CollectAudioBean.DataBean.RowsBean item) {
        if (item == null || item.datainfo == null) {
            showToast("数据异常，请稍后再试...");
            return;
        }
        showProgressDialog("加载中...");
        Map<String, String> map = new HashMap<>();
        map.put("token", AccountManager.getInstance().getUserInfo().getToken());
        map.put("book", String.valueOf(item.datainfo.cate_id));
        map.put("author", item.datainfo.author);
        NetUtil.post(ZConfig.SERVICE_URL + "/api/v1/multimedia3/audioList", map, new NetUtil.CallBack() {
            @Override
            public void onSuccess(String t) {
                dismissProgressDialog();
                NewAudioChapterData newAudioChapterData = new Gson().fromJson(t, NewAudioChapterData.class);
                if (newAudioChapterData == null || newAudioChapterData.rows == null || newAudioChapterData.rows.isEmpty()) {
                    showToast("章节无内容");
                    return;
                }
                int playIndex = 0;
                for (int i = 0; i < newAudioChapterData.rows.size(); i++) {
                    BaseAudioInfo baseAudioInfo = newAudioChapterData.rows.get(i);
                    if (TextUtils.equals(item.datainfo.id, String.valueOf(baseAudioInfo.id))) {
                        playIndex = i;
                        break;
                    }
                }
                playAudio(newAudioChapterData.rows, playIndex);
            }

            @Override
            public void onError(String t) {
                dismissProgressDialog();
            }
        });
    }
}
