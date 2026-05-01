package com.read.scriptures.audio;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.music.player.lib.bean.BaseAudioInfo;
import com.music.player.lib.constants.MusicConstants;
import com.music.player.lib.listener.MusicPlayerInfoListener;
import com.music.player.lib.manager.MusicPlayerManager;
import com.music.player.lib.util.NetUtil;
import com.music.player.lib.util.SaltUtils;
import com.read.scriptures.R;
import com.read.scriptures.bean.AudioCollectBean;
import com.read.scriptures.bean.DownloadAudioEvent;
import com.read.scriptures.bean.NewAudioBean;
import com.read.scriptures.bean.NewAudioChapterData;
import com.read.scriptures.bean.NewBookData;
import com.read.scriptures.config.ZConfig;
import com.read.scriptures.manager.AccountManager;
import com.read.scriptures.ui.activity.base.BaseActivity;
import com.read.scriptures.util.AnimationUtils;
import com.read.scriptures.util.DensityUtil;
import com.read.scriptures.util.FileUtil;
import com.read.scriptures.util.PicassoUtils;
import com.read.scriptures.util.PreferencesUtils;
import com.read.scriptures.util.StatusBarUtils;
import com.read.scriptures.util.UmShareUtils;
import com.read.scriptures.view.AudioPlayingView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewAudioChapterActivity extends BaseActivity {

    public static final String BOOK_DATA = "BOOK_DATA";
    public static final String BOOK_AUTHOR = "BOOK_AUTHOR";

    private RecyclerView rcv_audio_list;
    private ImageView iv_cover;
    private TextView tv_book_name;
    private TextView tv_author;
    private TextView tv_cate;
    private TextView tv_book_detail;
    private TextView tv_book;
    private TextView tv_play_all;
    private NestedScrollView tv_no_data;
    private RelativeLayout rl_main;
    private View view_target;
    private TextView tv_no_data1;

    private AudioPlayingView apv_view;

    private NewAudioChapterAdapter newAudioChapterAdapter;

    private MusicPlayerManager musicPlayerManager;
    private MusicPlayerInfoListener musicPlayerInfoListener;
    private NewBookData.RowsBean bookData;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            int position = msg.arg1;
            PreferencesUtils.putString(NewAudioChapterActivity.this, "AUDIO_PLAY_END", bookData.id);
            playAudio(newAudioChapterAdapter.getData(), position);
        }
    };
    private ArrayList<NewAudioBean.RowsBean> cateIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_new_audio_chaper);
        StatusBarUtils.initMainColorStatusBar(this);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        iv_cover = findViewById(R.id.iv_cover);
        tv_book_name = findViewById(R.id.tv_book_name);
        tv_author = findViewById(R.id.tv_author);
        tv_book_detail = findViewById(R.id.tv_book_detail);
        apv_view = findViewById(R.id.apv_view);
        tv_play_all = findViewById(R.id.tv_play_all);
        rl_main = findViewById(R.id.rl_main);
        view_target = findViewById(R.id.view_target);
        tv_no_data = findViewById(R.id.tv_no_data);
        tv_book = findViewById(R.id.tv_book);
        tv_cate = findViewById(R.id.tv_cate);
        tv_no_data1 = findViewById(R.id.tv_no_data1);

        rcv_audio_list = findViewById(R.id.rcv_audio_list);
        rcv_audio_list.setLayoutManager(new LinearLayoutManager(this));

        newAudioChapterAdapter = new NewAudioChapterAdapter();
        rcv_audio_list.setAdapter(newAudioChapterAdapter);

        newAudioChapterAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                BaseAudioInfo item = newAudioChapterAdapter.getItem(position);
                if (apv_view.getVisibility() != View.VISIBLE) {
                    apv_view.setData(item);
                    apv_view.show();
                }
                BaseAudioInfo currentPlayerMusic = musicPlayerManager.getCurrentPlayerMusic();
                if (currentPlayerMusic != null && currentPlayerMusic.id == item.id) {
                    musicPlayerManager.playOrPause();
                    switch (musicPlayerManager.getPlayerState()) {
                        case MusicConstants.MUSIC_PLAYER_PAUSE:
                            showToast("暂停成功");
                            break;
                        case MusicConstants.MUSIC_PLAYER_PLAYING:
                            showToast("正在播放");
                            break;
                    }
                    return;
                }
                File file = new File(FileUtil.getDiskCachePath(NewAudioChapterActivity.this), String.valueOf(item.id));
                if (!file.exists() && !NetUtil.isNetWorkAvailable(NewAudioChapterActivity.this)) {
                    showToast(item.chapter + " 无缓存,请链接网络");
                    return;
                }
                newAudioChapterAdapter.setCurrentAudio(item.id);
                AnimationUtils.addShopCartAnimation(rl_main, view.findViewById(R.id.tv_chapter_time), view_target, 2);
                apv_view.setProgress(0, 0, 0);
                clickPlayAudio(view, position);
            }
        });

        newAudioChapterAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.iv_collect:
                        if (!NetUtil.isNetWorkAvailable(NewAudioChapterActivity.this)) {
                            showToast("请连接网络~");
                            return;
                        }
                        parseAudio(newAudioChapterAdapter.getItem(position), position);
                        break;
                    case R.id.iv_share:
                        BaseAudioInfo item = newAudioChapterAdapter.getItem(position);
                        UmShareUtils.shareMusic(NewAudioChapterActivity.this, SaltUtils.getUrl(item.audio_url), item.chapter, item.cate3_name + "-" + item.cate2_name + "-" + item.cate1_name, TextUtils.isEmpty(item.image) ? bookData.image : item.image, SaltUtils.getUrl(item.audio_url));
                        break;
                }
            }
        });

        findViewById(R.id.iv_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.iv_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewAudioChapterActivity.this, SearchAudioActivity.class);
                intent.putExtra("audio_cate_data", cateIds);
                startActivity(intent);
            }
        });
        tv_play_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newAudioChapterAdapter.getItemCount() == 0) {
                    return;
                }
                playAudio(newAudioChapterAdapter.getData(), 0);
            }
        });
        initAudioManager();
        initData();

    }

    private void clickPlayAudio(View view, int position) {
        if (apv_view.getVisibility() != View.VISIBLE) {
            Message message = handler.obtainMessage();
            message.arg1 = position;
            message.obj = view;
            handler.sendMessage(message);
            return;
        }
        handler.removeCallbacksAndMessages(null);
        Message message = handler.obtainMessage();
        message.arg1 = position;
        message.obj = view;
        handler.sendMessageDelayed(message, 500);
    }


    /**
     * 点赞
     */
    private void parseAudio(BaseAudioInfo item, int position) {
        Map<String, String> map = new HashMap<>();
        map.put("token", AccountManager.getInstance().getUserInfo().getToken());
        map.put("value", String.valueOf(item.id));
        map.put("type", "1");
        NetUtil.get(ZConfig.GET_ADD_COLLECT, map, new NetUtil.CallBack() {
            @Override
            public void onSuccess(String t) {
                AudioCollectBean bean = new Gson().fromJson(t, AudioCollectBean.class);
                item.collect = bean.data.status;
                newAudioChapterAdapter.notifyItemChanged(position);
                showToastMsg(bean.msg);
            }
        });
    }

    private void initAudioManager() {
        musicPlayerManager = MusicPlayerManager.getInstance().init(getApplicationContext());
        if (musicPlayerManager.isPlaying()) {
            BaseAudioInfo currentPlayerMusic = (BaseAudioInfo) musicPlayerManager.getCurrentPlayerMusic();
            newAudioChapterAdapter.setCurrentAudio(currentPlayerMusic.id);
        }
        musicPlayerInfoListener = new MusicPlayerInfoListener() {
            @Override
            public void onPlayMusiconInfo(BaseAudioInfo musicInfo, int position) {
                newAudioChapterAdapter.setCurrentAudio(musicInfo.id);
            }
        };
        musicPlayerManager.addPlayInfoListener(musicPlayerInfoListener);
    }

    private void playAudio(List<BaseAudioInfo> data, int position) {
        musicPlayerManager.startPlayMusic(data, Math.max(position, 0));
        musicPlayerManager.setCurrentAuthor(data.get(Math.max(position, 0)).author);
    }

    private void initData() {
        bookData = (NewBookData.RowsBean) getIntent().getSerializableExtra(BOOK_DATA);
        author = getIntent().getStringExtra(BOOK_AUTHOR);
        if (bookData == null) {
            finish();
            return;
        }
        cateIds = (ArrayList<NewAudioBean.RowsBean>) getIntent().getSerializableExtra("audio_cate_data");
        PicassoUtils.loadImage(iv_cover, bookData.image, R.drawable.icon_play_deault_bg, DensityUtil.dip2px(70), DensityUtil.dip2px(100));
        tv_book_name.setText(bookData.name);
        tv_author.setText("读者：" + ("全部".equals(author) ? "加载中..." : author));
        tv_cate.setText("分类：" + bookData.cate1_name + "-" + bookData.cate2_name);
        tv_book_detail.setText(bookData.content);
        tv_book.setText(bookData.name);
        bookId = bookData.id;
        getAudioChapterData();

    }

    private String bookId = "";
    private String author = "";

    private void getAudioChapterData() {
        HashMap<String, String> map = new HashMap<>();
        map.put("book", bookId);
        map.put("author", "全部".equals(author) ? "" : author);
        map.put("token", AccountManager.getInstance().getUserInfo().getToken());
        NetUtil.postCache(ZConfig.SERVICE_URL + "/api/v1/multimedia3/audioList", map, new NetUtil.CallBack() {
            @Override
            public void onSuccess(String t) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                NewAudioChapterData newAudioChapterData = new Gson().fromJson(t, NewAudioChapterData.class);
                if (newAudioChapterData.rows == null || newAudioChapterData.rows.isEmpty()) {
                    tv_play_all.setText("播放全部 0");
                    tv_no_data.setVisibility(View.VISIBLE);
                    tv_no_data1.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    rcv_audio_list.setVisibility(View.GONE);
                    return;
                }
                tv_no_data.setVisibility(View.GONE);
                rcv_audio_list.setVisibility(View.VISIBLE);
                tv_author.setText("读者：" + newAudioChapterData.rows.get(0).author);
                newAudioChapterAdapter.setNewData(newAudioChapterData.rows);
                tv_play_all.setText("播放全部 " + newAudioChapterAdapter.getItemCount());
            }

            @Override
            public void onError(String t) {
                super.onError(t);
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                if (!NetUtil.isNetWorkAvailable(NewAudioChapterActivity.this)) {
                    tv_no_data.setVisibility(View.VISIBLE);
                    tv_no_data1.setText("对不起，暂无网络");
                    tv_no_data1.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_no_network, 0, 0);
                    rcv_audio_list.setVisibility(View.GONE);
                    return;
                }
                tv_no_data.setVisibility(View.VISIBLE);
                rcv_audio_list.setVisibility(View.GONE);
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void reStart(String tag) {
        switch (tag) {
            case "audio_chapter_no_cache":
                if (apv_view != null) {
                    apv_view.stopAnimation();
//                    apv_view.hide();
                }
                break;
            case "on_audio_stop":
                if (apv_view != null) {
                    apv_view.hide();
                }
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void reStart(DownloadAudioEvent downloadAudioEvent) {
        newAudioChapterAdapter.setCurrentDownloadAudio();
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        musicPlayerManager.removePlayInfoListener(musicPlayerInfoListener);
        musicPlayerInfoListener = null;
        handler = null;
        musicPlayerManager = null;
        EventBus.getDefault().unregister(this);
        apv_view.onActivityDestroy();
        super.onDestroy();
    }
}
