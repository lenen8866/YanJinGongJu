package com.read.scriptures.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.read.scriptures.R;
import com.read.scriptures.app.HuDongApplication;
import com.read.scriptures.config.SystemConfig;
import com.read.scriptures.config.ZConfig;
import com.read.scriptures.constants.SystemConstants;
import com.read.scriptures.db.CategoryDatabaseHelper;
import com.read.scriptures.event.RefreshDataEvent;
import com.read.scriptures.manager.HomeDataManager;
import com.read.scriptures.model.Advertisement;
import com.read.scriptures.model.Category;
import com.read.scriptures.model.NewBannerBean;
import com.read.scriptures.model.ShareModel;
import com.read.scriptures.model.Volume;
import com.read.scriptures.ui.activity.MainActivity;
import com.read.scriptures.ui.activity.base.BaseActivity;
import com.read.scriptures.ui.adapter.CategoryOneGridAdapter;
import com.read.scriptures.ui.adapter.CategorySectionAdapter;
import com.read.scriptures.ui.adapter.CategoryTwoGridAdapter;
import com.read.scriptures.ui.adapter.NewBannerAdapter;
import com.read.scriptures.util.ActManager;
import com.read.scriptures.util.DialogUtils;
import com.read.scriptures.util.DisplayUtil;
import com.read.scriptures.util.DownloadFileUtils;
import com.read.scriptures.util.FileUtil;
import com.read.scriptures.util.MyVideoPlay;
import com.read.scriptures.util.NetConnectUtil;
import com.read.scriptures.util.PreferencesUtils;
import com.read.scriptures.util.SharedPreferencesUtils;
import com.read.scriptures.util.SharedUtil;
import com.read.scriptures.util.StringUtil;
import com.read.scriptures.util.SystemUtils;
import com.read.scriptures.util.ThreadUtil;
import com.read.scriptures.util.TimeUtils;
import com.read.scriptures.util.UmShareUtils;
import com.read.scriptures.view.QuickIndexBar;
import com.read.scriptures.widget.CustomViewPager.ScollAbleArea;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.utils.CommonUtil;
import com.shuyu.gsyvideoplayer.utils.Debuger;
import com.shuyu.gsyvideoplayer.utils.GSYVideoHelper;
import com.youth.banner.listener.OnBannerListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.read.scriptures.config.PreferenceConfig.Preference_home_sort_type;


/**
 * Created with Android Studio. User : Lim Email: lgmshare@gmail.com Datetime :
 * 2015/4/28 16:00 To change this template use File | Settings | File Templates.
 */
public class HomeFragment extends BaseFragment implements View.OnClickListener, ScollAbleArea {

    private View hoverItem;

    private RecyclerView banner;
    private View headView1;

    private Category mSearchNode;

    private List<Advertisement> mAdvertisments;

    //存放一级分类
    private List<Category> mRootCategory = new ArrayList<>();

    //存放二级分类
    private Map<Integer, List<Category>> mCategoryMaps;

    //第二行分类Adapter
    private CategoryTwoGridAdapter mCategoryTwoGridAdapter;
    private CategoryTwoGridAdapter fmCategoryTwoGridAdapter;

    private ListView mCategorySectionListView;
    private CategorySectionAdapter mCategorySectionAdapter;

    private FrameLayout videoFullContainer;
    //当前选中的一级tab的下标
    private int mIndexCategory = 1;

    private List<NewBannerBean.DataBean> bannerBeanList = new ArrayList<>();

    //第二行分类GrdiView
    private GridView mHomeCategoryTwoGridView;
    private GridView mCategoryOneGridView;
    private LinearLayout mLlFloatView;

    private GridView fmHomeCategoryTwoGridView;
    private GridView fmCategoryOneGridView;
    private LinearLayout fmLlFloatView;

    private CategoryOneGridAdapter mCategoryOneGridAdapter;
    private CategoryOneGridAdapter fmCategoryOneGridAdapter;
    private QuickIndexBar mQuickIndexBar;
    private TextView mTvTextDialog;
    private NewBannerAdapter newBannerAdapter;
    private PagerSnapHelper pagerSnapHelper;
    private String currenCategoryName;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    // 2.1 定义用来与外部activity交互，获取到宿主activity
    private FragmentInteraction listterner;

    public void setVideoFullContainer(FrameLayout videoFullContainer) {
        this.videoFullContainer = videoFullContainer;
    }


    // 1 定义了所有activity必须实现的接口方法
    public interface FragmentInteraction {
        void setRange(int range);

        void setNode(Category node);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            HomeDataManager.getInstance().updateSuccessRefreshHomeCategoryVolumes();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof FragmentInteraction) {
            listterner = (FragmentInteraction) activity; // 2.2 获取到宿主activity并赋值
        } else {
            throw new IllegalArgumentException("activity must implements FragmentInteraction");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        listterner = null;
    }

    @SuppressLint("UseSparseArrays")
    private void initData() {
    }

    @Override
    protected int onObtainLayoutResId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initWidget() {
        initView();
        initBannerView();
        //设置一级分类tab
        mCategoryOneGridAdapter = new CategoryOneGridAdapter(getActivity());
        fmCategoryOneGridAdapter = new CategoryOneGridAdapter(getActivity());
        mCategoryOneGridAdapter.setOnItemClickListener(new CategoryOneGridAdapter.OnItemOnClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position) {
                if (mIndexCategory == position) {
                    return;
                }
                mIndexCategory = position;
                listterner.setRange(mIndexCategory + 1);
                CategoryOneGridAdapter adapter = (CategoryOneGridAdapter) parent.getAdapter();
                currenCategoryName = adapter.getItem(mIndexCategory).getCateName();
                mCategorySectionAdapter.setCurrenCategoryName(currenCategoryName);
                // 更新颜色数据
                adapter.setIndex(parent, position);
                // 更新另一个的数据
                fmCategoryOneGridAdapter.setIndex(fmCategoryOneGridView, position);
                mCategoryOneGridAdapter.setIndex(mCategoryOneGridView, position);

                mCategoryOneGridAdapter.notifyDataSetChanged();
                fmCategoryOneGridAdapter.notifyDataSetChanged();

                mCategoryTwoGridAdapter.setList(mCategoryMaps.get(mIndexCategory));
                mCategoryTwoGridAdapter.setIndex(mHomeCategoryTwoGridView, 0);
                mCategoryTwoGridAdapter.notifyDataSetChanged();

                fmCategoryTwoGridAdapter.setList(mCategoryMaps.get(mIndexCategory));
                fmCategoryTwoGridAdapter.setIndex(fmHomeCategoryTwoGridView, 0);
                fmCategoryTwoGridAdapter.notifyDataSetChanged();

                mCategorySectionAdapter.setNodeCategorys(mCategoryMaps.get(mIndexCategory));

                mCategorySectionAdapter.notifyDataSetChanged();

//                if (mCategorySectionListView != null) {
//                    mCategorySectionListView.setSelection(0);
//                }

                chanageQuickBar();
            }
        });

        fmCategoryOneGridAdapter.setOnItemClickListener(new CategoryOneGridAdapter.OnItemOnClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position) {
                if (mIndexCategory == position) {
                    return;
                }
                mIndexCategory = position;
                listterner.setRange(mIndexCategory + 1);
                CategoryOneGridAdapter adapter = (CategoryOneGridAdapter) parent.getAdapter();
                currenCategoryName = adapter.getItem(mIndexCategory).getCateName();
                mCategorySectionAdapter.setCurrenCategoryName(currenCategoryName);
                adapter.setIndex(parent, position);

                mCategoryOneGridAdapter.setIndex(mCategoryOneGridView, position);
                fmCategoryOneGridAdapter.setIndex(fmCategoryOneGridView, position);

                fmCategoryTwoGridAdapter.setList(mCategoryMaps.get(mIndexCategory));
                fmCategoryTwoGridAdapter.setIndex(fmHomeCategoryTwoGridView, 0);
                fmCategoryTwoGridAdapter.notifyDataSetChanged();

                mCategoryTwoGridAdapter.setList(mCategoryMaps.get(mIndexCategory));
                mCategoryTwoGridAdapter.setIndex(mHomeCategoryTwoGridView, 0);
                mCategoryTwoGridAdapter.notifyDataSetChanged();

                mCategoryOneGridAdapter.notifyDataSetChanged();
                fmCategoryOneGridAdapter.notifyDataSetChanged();

                mCategorySectionAdapter.setNodeCategorys(mCategoryMaps.get(mIndexCategory));
                mCategorySectionAdapter.notifyDataSetChanged();

                mCategorySectionListView.setSelection(0);
//                if (mCategorySectionListView != null) {
//                    if (!flag) {
//                        Log.w("TTT", "onItemClick fmCategoryOneGridAdapter 44444");
//                        mCategorySectionListView.setSelection(1);
//                    } else {
//                        Log.w("TTT", "onItemClick fmCategoryOneGridAdapter 55555");
//                        mCategorySectionListView.setSelection(0);
//                    }
//                }

                chanageQuickBar();
            }
        });

        //设置二级分类tab
        mCategoryTwoGridAdapter = new CategoryTwoGridAdapter(getActivity());
        fmCategoryTwoGridAdapter = new CategoryTwoGridAdapter(getActivity());
        mCategoryTwoGridAdapter.setOnItemClickListener(new CategoryTwoGridAdapter.OnItemOnClickListener() {
            /**
             * @param parent
             * @param v
             * @param position
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position) {
                mCategoryTwoGridAdapter.setIndex(mHomeCategoryTwoGridView, position);
                fmCategoryTwoGridAdapter.setIndex(fmHomeCategoryTwoGridView, position);
                if (position == 0) {
                    mSearchNode = null;
                    listterner.setNode(mSearchNode);
                    mCategorySectionAdapter.setNodeCategorys(mCategoryMaps.get(mIndexCategory));
                    mCategorySectionAdapter.notifyDataSetChanged();
//                    if (mCategorySectionListView != null) {
//                        mCategorySectionListView.setSelection(0);
//                    }
                    chanageQuickBar();
                } else {
                    mSearchNode = mCategoryTwoGridAdapter.getItem(position);
                    listterner.setNode(mSearchNode);
                    ArrayList<Category> categories = new ArrayList<Category>();
                    categories.add(mCategoryTwoGridAdapter.getItem(position));
                    mCategorySectionAdapter.setNodeCategorys(categories);
                    mCategorySectionAdapter.notifyDataSetChanged();
//                    if (mCategorySectionListView != null) {
//                        mCategorySectionListView.setSelection(0);
//                    }
                    chanageQuickBar();
                }
            }
        });

        fmCategoryTwoGridAdapter.setOnItemClickListener(new CategoryTwoGridAdapter.OnItemOnClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position) {
                mCategoryTwoGridAdapter.setIndex(mHomeCategoryTwoGridView, position);
                fmCategoryTwoGridAdapter.setIndex(fmHomeCategoryTwoGridView, position);
                if (position == 0) {
                    mSearchNode = null;
                    listterner.setNode(mSearchNode);
                    mCategorySectionAdapter.setNodeCategorys(mCategoryMaps.get(mIndexCategory));
                    mCategorySectionAdapter.notifyDataSetChanged();
                } else {
                    mSearchNode = fmCategoryTwoGridAdapter.getItem(position);
                    listterner.setNode(mSearchNode);
                    ArrayList<Category> categories = new ArrayList<Category>();
                    categories.add(fmCategoryTwoGridAdapter.getItem(position));
                    mCategorySectionAdapter.setNodeCategorys(categories);
                    mCategorySectionAdapter.notifyDataSetChanged();
                }
                mCategorySectionListView.setSelection(0);
                chanageQuickBar();
            }
        });

        mCategorySectionAdapter = new CategorySectionAdapter(getActivity());
        mCategoryOneGridView.setAdapter(mCategoryOneGridAdapter);
        fmCategoryOneGridView.setAdapter(fmCategoryOneGridAdapter);
        mHomeCategoryTwoGridView.setAdapter(mCategoryTwoGridAdapter);
        fmHomeCategoryTwoGridView.setAdapter(fmCategoryTwoGridAdapter);
        mCategorySectionListView.setAdapter(mCategorySectionAdapter);

        showQuickIndexBar();
        showProgressDialog("等待加载…");
        //耗时1s
        ThreadUtil.doOnOtherThread(new Runnable() {
            @Override
            public void run() {
                initDatabaseData();
            }
        });
    }

    private int getBookCount() {
        if (mCategorySectionAdapter == null) {
            return 0;
        }
        return mCategorySectionAdapter.getBookCount();
    }

    View headView2;
    View ll_main;
    RelativeLayout ll_book_category_tag;//分类三层
    TextView tv_book_category_tag_title;
    TextView tv_book_category_tag_count;

    private void initView() {
        hoverItem = findViewById(R.id.hoverItem);

        fmHomeCategoryTwoGridView = (GridView) findViewById(R.id.gridview_22);
        fmCategoryOneGridView = (GridView) findViewById(R.id.gridview_11);
        fmLlFloatView = (LinearLayout) findViewById(R.id.ll_float);

        //banner
        headView1 = LayoutInflater.from(getActivity()).inflate(R.layout.header_home_banner, null);
        headView2 = LayoutInflater.from(getActivity()).inflate(R.layout.header_home_item, null);

        tv_book_category_tag_title = (TextView) findViewById(R.id.tv_title);
        tv_book_category_tag_count = (TextView) findViewById(R.id.tv_count);
        ll_book_category_tag = (RelativeLayout) findViewById(R.id.ll_book_category_tag);

        ll_main = headView1.findViewById(R.id.ll_main);
        banner = headView1.findViewById(R.id.banner);
        linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false) {
            @Override
            public boolean canScrollHorizontally() {
                return mAdvertisments != null && mAdvertisments.size() > 1;
            }
        };
        banner.setLayoutManager(linearLayoutManager);


        mCategoryOneGridView = headView2.findViewById(R.id.gridview_11);
        mHomeCategoryTwoGridView = headView2.findViewById(R.id.gridview_22);
        mLlFloatView = headView2.findViewById(R.id.ll_float);

        mCategorySectionListView = (ListView) findViewById(R.id.listview);
        mCategorySectionListView.addHeaderView(headView1);
        mCategorySectionListView.addHeaderView(headView2);

        mQuickIndexBar = (QuickIndexBar) findViewById(R.id.quick_index_bar);
        mTvTextDialog = (TextView) findViewById(R.id.text_dialog);


        mQuickIndexBar.setTextView(mTvTextDialog);
        mQuickIndexBar.setOnLetterChangedListener(new QuickIndexBar.OnLetterChangedListener() {
            @Override
            public void onLetterChanged(String letter) {
                if ("↑".equals(letter)) {
                    // 滚动到顶
//                    rootScrollView.scrollTo(0, 0);
                    return;
                }
                for (int i = 0; i < mCategorySectionAdapter.getCount(); i++) {
                    Category category = mCategorySectionAdapter.getItem(i);
                    if (category.getCateName().equals(letter)) {
                        int floatViewHeight = fmLlFloatView.getHeight();
                        if (!flag) {
                            mCategorySectionListView.setSelectionFromTop(i, floatViewHeight);
                        } else {
                            mCategorySectionListView.setSelectionFromTop(i + 1, floatViewHeight);
                        }

                        break;
                    }
                }
            }

            @Override
            public void onLetterGone() {
            }
        });

        pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(banner);
        newBannerAdapter = new NewBannerAdapter();
        banner.setAdapter(newBannerAdapter);


        MyVideoPlay player = new MyVideoPlay(getActivity());

        smallVideoHelper = new GSYVideoHelper(getActivity(), player);
        MainActivity activity = (MainActivity) getActivity();
        activity.smallVideoHelper = smallVideoHelper;
        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        FrameLayout frameLayout = new FrameLayout(getContext());
        frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        decorView.addView(frameLayout);

        smallVideoHelper.setFullViewContainer(frameLayout);
        //配置
        gsySmallVideoHelperBuilder = new GSYVideoHelper.GSYVideoHelperBuilder();
        gsySmallVideoHelperBuilder
                .setHideActionBar(true)
                .setHideStatusBar(true)
                .setNeedLockFull(true)
                .setCacheWithPlay(true)
                .setAutoFullWithSize(true)
                .setShowFullAnimation(true)
                .setLockLand(true)
                .setVideoAllCallBack(new GSYSampleCallBack() {
                    @Override
                    public void onPrepared(String url, Object... objects) {
                        super.onPrepared(url, objects);
                        Debuger.printfLog("Duration " + smallVideoHelper.getGsyVideoPlayer().getDuration() + " CurrentPosition " + smallVideoHelper.getGsyVideoPlayer().getCurrentPositionWhenPlaying());
                    }


                    @Override
                    public void onQuitSmallWidget(String url, Object... objects) {
                        super.onQuitSmallWidget(url, objects);
                        //大于0说明有播放,//对应的播放列表TAG
                        if (smallVideoHelper.getPlayPosition() >= 0 && smallVideoHelper.getPlayTAG().equals(NewBannerAdapter.TAG)) {
                            //当前播放的位置
                            int position = smallVideoHelper.getPlayPosition();
                            //不可视的是时候
                            if ((position < firstVisibleItem || position > lastVisibleItem)) {
                                //释放掉视频
                                smallVideoHelper.releaseVideoPlayer();
                                newBannerAdapter.notifyDataSetChanged();
                            } else {
                                if (player.isCompletion) {
                                    player.showCover();
                                } else {
                                }
                            }
                        } else {
                            player.showCover();
                        }
                    }
                });

        smallVideoHelper.setGsyVideoOptionBuilder(gsySmallVideoHelperBuilder);
        smallVideoHelper.getGsyVideoPlayer().getBackButton();

        newBannerAdapter.setVideoHelper(smallVideoHelper, gsySmallVideoHelperBuilder);
        banner.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {//如果滚动结束
                    View snapView = pagerSnapHelper.findSnapView(linearLayoutManager);
                    currentItem = linearLayoutManager.getPosition(snapView);

                    Advertisement advertisement = mAdvertisments.get(currentItem % mAdvertisments.size());
                    if (advertisement != null &&
                            advertisement.getPlay() == 1 && //是否自动播放
                            !smallVideoHelper.getGsyVideoPlayer().isInPlayingState() && //是否正在播放
                            !SharedPreferencesUtils.getIsTodayPlay(getActivity(), advertisement.getId()) //今天是否已经播放过
                    ) {
                        SharedPreferencesUtils.setIsTodayPlay(getActivity(), advertisement.getId());
                        smallVideoHelper.setPlayPositionAndTag(currentItem, NewBannerAdapter.TAG);
                        newBannerAdapter.notifyDataSetChanged();
                        gsySmallVideoHelperBuilder.setVideoTitle("").setUrl(advertisement.getImage());
                        gsySmallVideoHelperBuilder.setFullHideStatusBar(true);
                        gsySmallVideoHelperBuilder.setFullHideActionBar(true);
                        smallVideoHelper.startPlay();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition() % mAdvertisments.size();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition() % mAdvertisments.size();
                //大于0说明有播放,//对应的播放列表TAG
                if (smallVideoHelper.getPlayPosition() >= 0 && smallVideoHelper.getPlayTAG().equals(NewBannerAdapter.TAG)) {
                    //当前播放的位置
                    int position = smallVideoHelper.getPlayPosition();
                    //不可视的是时候
                    if ((position < firstVisibleItem || position > lastVisibleItem)) {
                        //如果是小窗口就不需要处理
                        if (!smallVideoHelper.isSmall() && !smallVideoHelper.isFull()) {
                            //小窗口
                            int height = CommonUtil.dip2px(getActivity(), 110);
                            int width = CommonUtil.dip2px(getActivity(), 190);
                            //actionbar为true才不会掉下面去
                            smallVideoHelper.showSmallVideo(new Point(width, height), false, false);
                        }
                    } else {
                        if (smallVideoHelper.isSmall()) {
                            smallVideoHelper.smallVideoToNormal();
                        }
                    }
                }
            }
        });
        banner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        stopAutoLoop();
                        break;
                    case MotionEvent.ACTION_UP:
                        startLoop();
                        break;
                }
                return false;
            }
        });
        //设置listview的滑动监听
        mCategorySectionListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem1, int visibleItemCount, int totalItemCount) {
//                //当滑动到第一个是，将悬停部分设置为显示
                View viewItemNext = view.getChildAt(1);//下一个的top位置

                int diffTop = -1; //移动的top

                if (viewItemNext != null && visibleItemCount >= 2) {
                    diffTop = viewItemNext.getTop();
                }
                if (diffTagFlag - diffTop > 0) {
                    MOVE_MODEL = 2;
                } else if (diffTagFlag == diffTop) {
                    MOVE_MODEL = 0;
                } else {
                    MOVE_MODEL = 1;
                }
                diffTagFlag = diffTop;
                if (!flag) {
                    if (firstVisibleItem1 >= 1) {
                        hoverItem.setVisibility(View.VISIBLE);
                        showSmallTag(firstVisibleItem1, flag, visibleItemCount, diffTop);
                    } else {
                        hoverItem.setVisibility(View.GONE);
                    }
                } else {
                    if (firstVisibleItem1 >= 0) {
                        hoverItem.setVisibility(View.VISIBLE);
                        //因为加了广告那么前面两个item为空
                        showSmallTag(firstVisibleItem1, flag, visibleItemCount, diffTop);
                    } else {
                        hoverItem.setVisibility(View.GONE);
                    }
                }
                if (mCategoryTwoGridAdapter != null && mCategoryTwoGridAdapter.getIndexCategory() != mIndexCategory && mCategoryMaps != null ) {
                    mCategoryTwoGridAdapter.setList(mCategoryMaps.get(mIndexCategory));
                    mCategoryTwoGridAdapter.setIndexCategory(mIndexCategory);
                    mCategoryTwoGridAdapter.setIndex(mHomeCategoryTwoGridView, 0);
                    mCategoryTwoGridAdapter.notifyDataSetChanged();
                }
                if (firstVisibleItem1 > 0) {
                    stopAutoLoop();
                    if (smallVideoHelper.getPlayPosition() >= 0 && smallVideoHelper.getPlayTAG().equals(NewBannerAdapter.TAG)) {
                        //如果是小窗口就不需要处理
                        if (!smallVideoHelper.isSmall() && !smallVideoHelper.isFull()) {
                            //小窗口
                            int height = CommonUtil.dip2px(getActivity(), 110);
                            int width = CommonUtil.dip2px(getActivity(), 190);
                            //actionbar为true才不会掉下面去
                            smallVideoHelper.showSmallVideo(new Point(width, height), false, false);
                        }
                    }
                }

                if (firstVisibleItem1 == 0) {
                    startLoop();
                    int position = smallVideoHelper.getPlayPosition();
                    if (position >= firstVisibleItem && position <= lastVisibleItem) {
                        //释放掉视频
                        if (smallVideoHelper.isSmall()) {
                            smallVideoHelper.smallVideoToNormal();
                        }
                    }
                }
            }
        });


        boolean is_guide_show = PreferencesUtils.getBoolean(getActivity(), "is_guide_show", false);
        if (is_guide_show) {//已经显示过了
            ThreadUtil.doOnOtherThread(new Runnable() {
                @Override
                public void run() {
                    String json = NetConnectUtil.getWelContent(getActivity(), ZConfig.GETSHARE);
                    ShareModel shareModel = JSONObject.parseObject(json, ShareModel.class);
                    if (shareModel != null && shareModel.data != null && shareModel.data.tip == 1) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showDialog(shareModel);
                            }
                        });
                    }
                }
            });
        }
    }

    int currentvisibleCount;
    int diffTagFlag = -1; //标记滑动的方向
    int MOVE_MODEL = 0; //0不动 UP2   DOWN1

    //全部分类下展示小的tag
    private void showSmallTag(int firstVisibleItem1, boolean flag, int visibleItemCount, int diffTop) {
        //首先前两个的tag flag为true广告开启
        if (flag) {
            if (mCategorySectionAdapter != null && mCategoryTwoGridAdapter != null && mCategoryTwoGridAdapter.getIndex() == 0) { //全部的情况
                showSmallTag();
                float headerH = hoverItem.getHeight();
                float h12 = fmLlFloatView.getHeight();
                float h3 = ll_book_category_tag.getHeight();
                float fixedH = h12 + h3;//fixed
                int HRADER_COUNT = 1;//header的个数为两个
                if (firstVisibleItem1 >= HRADER_COUNT) {
                    int index = 0;
                    //修改index
                    if (h12 >= diffTop && diffTop != -1) { //触发到12顶部 index应该是对的
                        index = firstVisibleItem1 - HRADER_COUNT + 1;
                    } else {
                        index = firstVisibleItem1 - HRADER_COUNT;
                    }
                    //动画
                    if (fixedH >= diffTop && diffTop != -1 && diffTop >= h12) { //开始触发。此时的index= +1
                        ll_book_category_tag.setTranslationY(-(fixedH - diffTop));
                    } else {
                        if (h12 != ll_book_category_tag.getY()) {
                            ll_book_category_tag.setY(h12);
                        }
                    }
                    if (mCategorySectionAdapter != null && mCategorySectionAdapter.getCount() > 0) {
                        Category cate = mCategorySectionAdapter.getItem(index);
                        if (cate == null) return;
                        tv_book_category_tag_title.setText(cate.getCateName());
                        tv_book_category_tag_count.setText("共" + cate.getVolCount() + "本");
                    }
                } else {
                    if (h12 != ll_book_category_tag.getY()) {
                        ll_book_category_tag.setY(h12);
                    }
                    if (mCategorySectionAdapter != null && mCategorySectionAdapter.getCount() > 0) {
                        Category cate = mCategorySectionAdapter.getItem(0);
                        if (!tv_book_category_tag_title.getText().toString().equals(cate.getCateName())) {
                            tv_book_category_tag_title.setText(cate.getCateName());
                            tv_book_category_tag_count.setText("共" + cate.getVolCount() + "本");
                        }
                    }
                }
                diffTagFlag = diffTop;
            } else {
                dismissSmallTag();
            }
        } else {//其次前一个个的tag flag为false无广告开启
            if (mCategorySectionAdapter != null && mCategoryTwoGridAdapter != null && mCategoryTwoGridAdapter.getIndex() == 0) { //全部的情况
                showSmallTag();
                float headerH = hoverItem.getHeight();
                float h12 = fmLlFloatView.getHeight();
                float h3 = ll_book_category_tag.getHeight();
                float fixedH = h12 + h3;//fixed
                int HRADER_COUNT = 2;//header的个数为2个 因为remove header_banner
                if (firstVisibleItem1 >= HRADER_COUNT) {
                    int index = 0;
                    //修改index
                    if (h12 >= diffTop && diffTop != -1) { //触发到12顶部 index应该是对的
                        index = firstVisibleItem1 - HRADER_COUNT + 1;
                    } else {
                        index = firstVisibleItem1 - HRADER_COUNT;
                    }
                    //动画
                    if (fixedH >= diffTop && diffTop != -1 && diffTop >= h12) { //开始触发。此时的index= +1
                        ll_book_category_tag.setTranslationY(-(fixedH - diffTop));
                    } else {
                        if (h12 != ll_book_category_tag.getY()) {
                            ll_book_category_tag.setY(h12);
                        }
                    }
                    if (mCategorySectionAdapter != null && mCategorySectionAdapter.getCount() > 0) {
                        Category cate = mCategorySectionAdapter.getItem(index);
                        tv_book_category_tag_title.setText(cate.getCateName());
                        tv_book_category_tag_count.setText("共" + cate.getVolCount() + "本");
                    }
                } else {
                    if (h12 != ll_book_category_tag.getY()) {
                        ll_book_category_tag.setY(h12);
                    }
                    if (mCategorySectionAdapter != null && mCategorySectionAdapter.getCount() > 0) {
                        Category cate = mCategorySectionAdapter.getItem(0);
                        if (!tv_book_category_tag_title.getText().toString().equals(cate.getCateName())) {
                            tv_book_category_tag_title.setText(cate.getCateName());
                            tv_book_category_tag_count.setText("共" + cate.getVolCount() + "本");
                        }
                    }
                }
                diffTagFlag = diffTop;
            } else {
                dismissSmallTag();
            }
        }
        currentvisibleCount = visibleItemCount;
    }

    //全部分类下展示小的tag消失
    private void dismissSmallTag() {
        if (mCategorySectionAdapter != null) {
            Category cate = mCategorySectionAdapter.getItem(0);
            if (cate != null) {
                tv_book_category_tag_title.setText(cate.getCateName());
                tv_book_category_tag_count.setText("共" + cate.getVolCount() + "本");
            }
        }
//        if (ll_book_category_tag != null && ll_book_category_tag.getVisibility() == View.VISIBLE) {
//            ll_book_category_tag.setVisibility(View.GONE);
//        }
    }

    //全部分类下展示小的tag
    private void showSmallTag() {
        if (ll_book_category_tag != null && ll_book_category_tag.getVisibility() == View.GONE) {
            ll_book_category_tag.setVisibility(View.VISIBLE);
        }
    }

    private void showDialog(ShareModel shareModel) {
        boolean isShow = SharedPreferencesUtils.getIsTodayShow(getActivity());
        if (isShow) {
            return;
        }
        DialogUtils.showNormalDialog(getActivity(), shareModel.data.title, shareModel.data.content, "分享朋友", "分享朋友圈", new DialogUtils.onDialogClickListener() {
            @Override
            public void onCancel(Dialog dialog) {
                share(shareModel);
                dialog.dismiss();
            }

            @Override
            public void onOk(Dialog dialog) {
                share(shareModel);
                dialog.dismiss();
            }
        });
        SharedPreferencesUtils.setIsTodayShow(getActivity());
    }

    private void share(ShareModel shareModel) {
//        ShareSDK.initSDK(getActivity());
//        OnekeyShare oks = new OnekeyShare();
//        if (!TextUtils.isEmpty(platForm)) {
//            oks.setPlatform(platForm);
//        }
//// title标题，微信、QQ和QQ空间等平台使用
//        oks.setTitle(shareModel.data.title);
//// text是分享文本，所有平台都需要这个字段
//        oks.setText(shareModel.data.subtitle);
//// setImageUrl是网络图片的url
//        oks.setImageUrl(shareModel.data.image);
//// url在微信、Facebook等平台中使用
//        oks.setDialogMode();
//        oks.setUrl(shareModel.data.link);
//        oks.addHiddenPlatform(TencentWeibo.NAME);
//        oks.addHiddenPlatform(ShortMessage.NAME);
//        oks.addHiddenPlatform(QZone.NAME);
//        oks.addHiddenPlatform(QQ.NAME);
//// 启动分享GUI
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                oks.show(getActivity());
//            }
//        });

        UmShareUtils.shareUrl(getActivity(), shareModel.data.title, shareModel.data.content, shareModel.data.image, shareModel.data.link);
    }

    private void showDatabaseDataFailDialog() {
        dismissProgressDialog();
        DialogUtils.showSureDialog(getActivity(), "温馨提示", "加载失败，是否重启应用再次加载？", "确定", new DialogUtils.onDialogClickListener() {
            @Override
            public void onCancel(Dialog dialog) {
                dialog.dismiss();
            }

            @Override
            public void onOk(Dialog dialog) {
                dialog.dismiss();
                FileUtil.delAllFile(HuDongApplication.getInstance().getDBDir());
                ActManager.finishAllActivityAndRestart(getActivity());
                FragmentActivity activity = getActivity();
                if (activity instanceof BaseActivity) {
                    ((BaseActivity) activity).exit();
                }
            }
        });
    }

    private void initDatabaseData() {
        if (!HomeDataManager.getInstance().isInitVolumeInfos()) {
            //未初始化完成 1秒后重新监控
            handler.removeMessages(INIT_DATA_BASE_DATA_MSG);
            handler.sendEmptyMessageDelayed(INIT_DATA_BASE_DATA_MSG, 1000);
            return;
        }
        handler.removeMessages(INIT_DATA_BASE_DATA_MSG);
        CategoryDatabaseHelper categoryHepler = new CategoryDatabaseHelper(getActivity());
        mRootCategory = categoryHepler.getCategroyList(0);
        mCategoryMaps = new HashMap<>();
        final Integer[] labelCounts = new Integer[mRootCategory.size()];
        for (int i = 0; i < mRootCategory.size(); i++) {
            List<Category> categorys = categoryHepler.getCategroyList(mRootCategory.get(i).getId());
            mCategoryMaps.put(i, categorys);
            int volCount = 0;
            if (categorys != null && !categorys.isEmpty()) {
                for (Category category : categorys) {
                    List<Volume> volumes = HomeDataManager.getInstance().getAllVolumesMap().get(String.valueOf(category.getId()));
                    int itemCount = volumes == null ? 0 : volumes.size();
                    volCount = volCount + itemCount;
                }
            }
            labelCounts[i] = volCount;
        }

        if (getActivity() == null) {
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCategoryOneGridAdapter.setList(mRootCategory);
                fmCategoryOneGridAdapter.setList(mRootCategory);
                mCategoryOneGridAdapter.setIndex(mIndexCategory);
                currenCategoryName = mCategoryOneGridAdapter.getItem(mIndexCategory).getCateName();
                mCategorySectionAdapter.setCurrenCategoryName(currenCategoryName);
                fmCategoryOneGridAdapter.setIndex(mIndexCategory);
                mCategoryOneGridView.setNumColumns(mRootCategory.size());
                fmCategoryOneGridView.setNumColumns(mRootCategory.size());

                mCategoryTwoGridAdapter.setList(mCategoryMaps.get(mIndexCategory));
                fmCategoryTwoGridAdapter.setList(mCategoryMaps.get(mIndexCategory));
                mHomeCategoryTwoGridView.setNumColumns(7);
                fmHomeCategoryTwoGridView.setNumColumns(7);
                listterner.setRange(mIndexCategory + 1);

                final ArrayList<Category> categories = new ArrayList<>();
                categories.add(mCategoryTwoGridAdapter.getItem(mCategoryTwoGridAdapter.getIndex()));
                boolean isLoadSuccess = false;
                if (mCategoryTwoGridAdapter.getIndex() == 0) {
                    isLoadSuccess = mCategorySectionAdapter.setNodeCategorys(mCategoryMaps.get(mIndexCategory));
                } else {
                    isLoadSuccess = mCategorySectionAdapter.setNodeCategorys(categories);
                }
                if (!isLoadSuccess) {
                    return;
                }
                dismissProgressDialog();

                fmCategoryTwoGridAdapter.notifyDataSetChanged();
                mCategoryOneGridAdapter.notifyDataSetChanged();
                fmCategoryOneGridAdapter.notifyDataSetChanged();
                mCategoryTwoGridAdapter.notifyDataSetChanged();
                mCategorySectionAdapter.notifyDataSetChanged();
                mCategorySectionListView.setSelection(0);

                mCategoryOneGridAdapter.setLabelCount(labelCounts);
                fmCategoryOneGridAdapter.setLabelCount(labelCounts);

                chanageQuickBar();
            }
        });
    }

    private void chanageQuickBar() {
        if (!SharedUtil.getBoolean(Preference_home_sort_type, true) && mQuickIndexBar != null) {
            List<String> letters = new ArrayList<>();
            for (Category category : mCategorySectionAdapter.getCategories()) {
                letters.add(category.getCateName());
            }
            mQuickIndexBar.setLetters(letters);
            mQuickIndexBar.setVisibility(View.VISIBLE);
        }
    }

    GSYVideoHelper smallVideoHelper;

    GSYVideoHelper.GSYVideoHelperBuilder gsySmallVideoHelperBuilder;

    LinearLayoutManager linearLayoutManager;

    int lastVisibleItem;
    int firstVisibleItem;

    private void initBannerView() {
        if (SystemUtils.isOnline(HuDongApplication.getInstance())) {
            getNetBanner();
        } else {
//            getLocBanner();
        }
    }

    /**
     * 初始化广告header
     */
    private boolean flag = true;

    private void initHeaderAdvView(final List<Advertisement> requipments) {
        mAdvertisments = requipments;
        if (requipments == null || mAdvertisments == null || mAdvertisments.size() == 0) {
            if (headView1 != null) {
                ll_main.setVisibility(View.VISIBLE);
                mCategorySectionListView.removeHeaderView(headView1);
                flag = true;
            }

        } else {
            ll_main.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(CommonUtil.getScreenWidth(getActivity()), CommonUtil.getScreenWidth(getActivity()) * 9 / 16);
            ll_main.setLayoutParams(layoutParams);
            flag = false;
        }

        //banner
        if (mAdvertisments != null && mAdvertisments.size() == 1 && mAdvertisments.get(0).getIs_image() == 1 && mAdvertisments.get(0).getPlay() == 1 && !SharedPreferencesUtils.getIsTodayPlay(getActivity(), mAdvertisments.get(0).getId())) {
            SharedPreferencesUtils.setIsTodayPlay(getActivity(), mAdvertisments.get(0).getId());
            smallVideoHelper.setPlayPositionAndTag(currentItem, NewBannerAdapter.TAG);
            newBannerAdapter.notifyDataSetChanged();
            gsySmallVideoHelperBuilder.setVideoTitle("").setUrl(mAdvertisments.get(0).getImage());
//            gsySmallVideoHelperBuilder.setFullHideStatusBar(true);
//            gsySmallVideoHelperBuilder.setFullHideActionBar(true);
            smallVideoHelper.startPlay();
        }

        newBannerAdapter.setDataBeans(mAdvertisments);
        newBannerAdapter.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                if (mAdvertisments == null || mAdvertisments.size() == 0) {
                    return;
                }

                final String url = mAdvertisments.get(position).getUrl();
                try {
                    if (mAdvertisments.get(position).getType() == mAdvertisments.get(position).TYPE_WEB) {
                        if (url.contains("http")) {
                            //弹框
                            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppTheme_Dialog_Alert);
                            builder.setTitle("提示");
                            builder.setMessage("您是否跳出到浏览器？");
                            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.setPositiveButton("跳出", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    SystemUtils.jumpToUrl(getActivity(), url);
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.setCanceledOnTouchOutside(true);
                            dialog.setCancelable(true);
                            dialog.show();
                        }
                    }
                } catch (final Exception e) {
//                    showToastMsg("网页地址未提供，或者无需提供！");
                }
            }
        });
        if (mAdvertisments != null && mAdvertisments.size() > 1) {
            startLoop();
        }
    }

    private void startLoop() {
        handler.removeCallbacks(task);
        handler.postDelayed(task, 5000);
    }

    private void stopAutoLoop() {
        handler.removeCallbacks(task);
    }

    int currentItem = 0;
    private static final int INIT_DATA_BASE_DATA_MSG = 1001;
    //5秒内加载不出来弹窗提示
    private static final int INIT_DATA_BASE_DATA_MAX_TIME = 1000 * 5;
    private int curDelayTime = 0;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == INIT_DATA_BASE_DATA_MSG) {
                curDelayTime += 1000;
                //5秒内加载不出来弹窗提示
                if (curDelayTime > INIT_DATA_BASE_DATA_MAX_TIME) {
                    showDatabaseDataFailDialog();
                } else {
                    //继续初始化数据库
                    initDatabaseData();
                }
            }
        }
    };

    private final Runnable task = new Runnable() {
        @Override
        public void run() {
            if (mAdvertisments == null) {
                return;
            }
            if (mAdvertisments.size() <= 1) {
                return;
            }
            if (smallVideoHelper.isFull()) {
                handler.postDelayed(task, 5000);
                return;
            }
            if (smallVideoHelper.getGsyVideoPlayer().isInPlayingState() &&
                    smallVideoHelper.getPlayPosition() >= firstVisibleItem &&
                    smallVideoHelper.getPlayPosition() <= lastVisibleItem) {//正在播放不要动
                handler.postDelayed(task, 5000);
                return;
            }
            if (currentItem < mAdvertisments.size() - 1) {
                currentItem++;
            } else {
                currentItem = 0;
            }


            banner.smoothScrollToPosition(currentItem);
            handler.postDelayed(task, 5000);
        }
    };

    /**
     * 跳转微信小程序
     */
//    private void goWechatApplet(String url, String userName) {
//        String appId = SystemConfig.WX_KEY; // 填应用AppId
//        IWXAPI api = WXAPIFactory.createWXAPI(getContext(), appId);
//
//        WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
//        req.userName = userName/*"gh_4ffb59430a9d"*/; // 小程序原始id
//        req.path = url;                  //拉起小程序页面的可带参路径，不填默认拉起小程序首页
//        req.miniprogramType = WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE;
//        api.sendReq(req);
//
//    }

    @Override
    public int getScollY() {
        final int[] location = new int[2];
        if (banner != null)
            banner.getLocationInWindow(location);
        return (int) -(location[1] + DisplayUtil.dp2px(getContext(), 150));
    }

    /**
     * 加载默认广告
     *
     * @see [类、类#方法、类#成员]
     */

    private void loadDefaultGG() {
        if (mAdvertisments == null || mAdvertisments.size() == 0) {
            mAdvertisments = new ArrayList<>();
            if (bannerBeanList != null && bannerBeanList.size() > 0) {
                Collections.sort(bannerBeanList, new Comparator<NewBannerBean.DataBean>() {
                    @Override
                    public int compare(NewBannerBean.DataBean o1, NewBannerBean.DataBean o2) {
                        return o1.weigh < o2.weigh ? -1 : 0;
                    }
                });
                String addTime = "";
                String endTime = "";
                String guideUrl = "";
                String linkUrl = "";
                String type = "";
                int id = 0;

                for (int i = 0; i < bannerBeanList.size(); i++) {
                    if (SystemUtils.isOnline(getActivity())) {
                        if (bannerBeanList.get(i).type.val == 2) {
                            continue;
                        }
                    } else {
                        if (bannerBeanList.get(i).type.val == 2 || bannerBeanList.get(i).type.val == 3) {
                            continue;
                        }
                    }
                    int isImage = bannerBeanList.get(i).is_image;//0 图片 1视频
                    if (isImage == 0) {//只存图片
                        id = bannerBeanList.get(i).id;
                        if (i == bannerBeanList.size() - 1) {
                            if (bannerBeanList.get(i).date != null) {
                                addTime += bannerBeanList.get(i).date;
                            }
                            if (bannerBeanList.get(i).end_date != null && StringUtil.isNotEmpty(bannerBeanList.get(i).end_date.val)) {
                                endTime += bannerBeanList.get(i).end_date.val;
                            }
                            guideUrl += bannerBeanList.get(i).pic_url;
                            linkUrl += bannerBeanList.get(i).link;
                            type += bannerBeanList.get(i).type.val;
                        } else {
                            if (bannerBeanList.get(i).end_date != null && StringUtil.isNotEmpty(bannerBeanList.get(i).end_date.val)) {
                                endTime += bannerBeanList.get(i).end_date.val + ",";
                            }
                            if (bannerBeanList.get(i).date != null) {
                                addTime += bannerBeanList.get(i).date + ",";
                            }
                            guideUrl += bannerBeanList.get(i).pic_url + ",";
                            linkUrl += bannerBeanList.get(i).link + ",";
                            type += bannerBeanList.get(i).type.val + ",";
                        }
                        if (!SharedPreferencesUtils.containsKey(getActivity(), id)) {
                            File file = new File(SystemConstants.APP_BANNER_PATH + bannerBeanList.get(i).date + "--" + bannerBeanList.get(i).end_date.val + ".jpg");
                            if (file.exists()) {
                                file.delete();
                            }
                            final int now = i;
                            ThreadUtil.doOnOtherThread(new Runnable() {
                                public void run() {
                                    try {
                                        DownloadFileUtils.downLoadFromUrl(bannerBeanList.get(now).cover, bannerBeanList.get(now).date + "--" + bannerBeanList.get(now).end_date.val + ".jpg", SystemConstants.APP_BANNER_PATH);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                        SharedPreferencesUtils.saveBannerAddDate(getActivity(), addTime);
                        SharedPreferencesUtils.saveBannerEndDate(getActivity(), endTime);
                        SharedPreferencesUtils.saveBannerUrls(getActivity(), guideUrl);
                        SharedPreferencesUtils.saveBannerLink(getActivity(), linkUrl);
                        SharedPreferencesUtils.saveBannerType(getActivity(), type);
                        SharedPreferencesUtils.saveBannerId(getActivity(), id);

                    }
                    Advertisement advertisement = new Advertisement();
                    advertisement.setImage(bannerBeanList.get(i).pic_url);
                    advertisement.setUrl(bannerBeanList.get(i).link);
                    advertisement.setType(bannerBeanList.get(i).type.val);
                    advertisement.setIs_image(bannerBeanList.get(i).is_image);
                    advertisement.setCover(bannerBeanList.get(i).cover);
                    advertisement.setPlay(bannerBeanList.get(i).play);
                    advertisement.setName(bannerBeanList.get(i).pic_name);
                    advertisement.setId(bannerBeanList.get(i).id);
                    mAdvertisments.add(advertisement);
                }
            }
        }
        if (mAdvertisments.size() > 0) {
            if (getActivity() == null) {
                return;
            }
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                initHeaderAdvView(mAdvertisments);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        if (mAdvertisments != null && mAdvertisments.size() > 0) {
            startLoop();
        }
//        if (!MusicPlayerManager.getInstance().isPlaying()) {
//            smallVideoHelper.getGsyVideoPlayer().onVideoResume(false);
//        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        stopAutoLoop();
        smallVideoHelper.getGsyVideoPlayer().onVideoPause();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (smallVideoHelper == null || smallVideoHelper.getGsyVideoPlayer() == null) {
            return;
        }
        if (isVisibleToUser) {
//            if (!MusicPlayerManager.getInstance().isPlaying()) {
//                smallVideoHelper.getGsyVideoPlayer().onVideoResume(false);
//            }
        } else {
            if (smallVideoHelper.isSmall()) {
                smallVideoHelper.smallVideoToNormal();
            }
            smallVideoHelper.getGsyVideoPlayer().onVideoPause();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        smallVideoHelper.releaseVideoPlayer();
        GSYVideoManager.releaseAllVideos();
    }

    @Override
    protected void lazyLoad() {
    }

    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
        }
    }

    /**
     * 请求banner
     */
    public void getNetBanner() {
        ThreadUtil.doOnOtherThread(new Runnable() {
            public void run() {
                try {
                    String json = NetConnectUtil.getContent(getActivity(), ZConfig.GETBANNER, 3);
                    NewBannerBean result = JSONObject.parseObject(json, NewBannerBean.class);
                    if (result != null && result.data != null) {
                        bannerBeanList = result.data;
                        loadDefaultGG();
                    } else {
                        banner.post(new Runnable() {
                            @Override
                            public void run() {
                                initHeaderAdvView(mAdvertisments);
                            }
                        });
                    }
                } catch (Exception e) {
                    banner.post(new Runnable() {
                        @Override
                        public void run() {
                            initHeaderAdvView(mAdvertisments);
                        }
                    });
                    e.printStackTrace();
                }
            }

        });
    }

    /**
     *
     */
    private void getLocBanner() {
        ThreadUtil.doOnOtherThread(new Runnable() {
            public void run() {
                String imagePicUrl = SharedPreferencesUtils.getBannerUrls(getActivity());
                if (imagePicUrl.endsWith(",")) {
                    imagePicUrl = imagePicUrl.substring(0, imagePicUrl.lastIndexOf(","));
                }
                String[] list = imagePicUrl.split(",");
                String bannerStartTime = SharedPreferencesUtils.getBannerAddDate(getActivity());
                String bannerEndTime = SharedPreferencesUtils.getBannerEndDate(getActivity());
                String bannerType = SharedPreferencesUtils.getBannerType(HuDongApplication.getInstance());
                String bannerLink = SharedPreferencesUtils.getBannerLink(HuDongApplication.getInstance());
                String[] bannerTypeArr = bannerType.split(",");
                String[] bannerLinkArr = bannerLink.split(",");

                String[] bannerStartTimeArr = bannerStartTime.split(",");
                String[] bannerEndTimeArr = bannerEndTime.split(",");
                if (list.length > 0) {
                    mAdvertisments = new ArrayList<>();
                    for (int i = 0; i < list.length; i++) {
                        if (i < bannerStartTimeArr.length) {
                            File file = new File(SystemConstants.APP_BANNER_PATH + bannerStartTimeArr[i] + "--" + bannerEndTimeArr[i] + ".jpg");
                            if (file.exists()) {
                                try {
                                    Advertisement advertisement = new Advertisement();
                                    advertisement.setCover(SystemConstants.APP_BANNER_PATH + bannerStartTimeArr[i] + "--" + bannerEndTimeArr[i] + ".jpg");
                                    if (i < bannerLinkArr.length) {
                                        advertisement.setUrl(bannerLinkArr[i]);
                                    } else {
                                        advertisement.setUrl("");
                                    }
                                    if (i < bannerTypeArr.length) {
                                        advertisement.setType(Integer.valueOf(bannerTypeArr[i]));
                                    } else {
                                        advertisement.setType(-1);
                                    }
                                    mAdvertisments.add(advertisement);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    if (getActivity() == null) {
                        return;
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initHeaderAdvView(mAdvertisments);
                        }
                    });
                } else {
                    if (getActivity() == null) {
                        return;
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initHeaderAdvView(mAdvertisments);
                        }
                    });
                }
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void UpdateUI(RefreshDataEvent refreshDataEvent) {
        //移除加载不出来延迟通知

        initDatabaseData();
        showQuickIndexBar();
    }

    private void showQuickIndexBar() {
        boolean isSortCategory = SharedUtil.getBoolean(Preference_home_sort_type, true);
        if (isSortCategory) {
            mQuickIndexBar.setVisibility(View.INVISIBLE);
        } else {
            mQuickIndexBar.setVisibility(View.VISIBLE);
        }
    }


}
