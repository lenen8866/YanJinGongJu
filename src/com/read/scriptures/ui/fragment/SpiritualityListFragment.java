package com.read.scriptures.ui.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.read.scriptures.EIUtils.DateUtil;
import com.read.scriptures.EIUtils.TextStyleUtil;
import com.read.scriptures.R;
import com.read.scriptures.app.HuDongApplication;
import com.read.scriptures.bean.UserInfo;
import com.read.scriptures.constants.BundleConstants;
import com.read.scriptures.db.SpiritualityCategoryDatabaseHepler;
import com.read.scriptures.event.RefreshDataEvent;
import com.read.scriptures.manager.HomeDataManager;
import com.read.scriptures.model.Spirituality;
import com.read.scriptures.model.SpiritualityCategory;
import com.read.scriptures.model.SpliTab;
import com.read.scriptures.ui.activity.MainActivity;
import com.read.scriptures.ui.activity.SpiritualityContentActivity;
import com.read.scriptures.ui.adapter.CategorySpiriGridAdapter;
import com.read.scriptures.ui.adapter.SpiritualityListAdapter;
import com.read.scriptures.ui.adapter.SpiritualityListRecntAdapter;
import com.read.scriptures.ui.adapter.SpiritualitySlidingAdapter;
import com.read.scriptures.EIUtils.ActivityUtil;
import com.read.scriptures.util.CommonUtil;
import com.read.scriptures.util.PreferencesUtils;
import com.read.scriptures.util.ThreadUtil;
import com.read.scriptures.util.TimeUtils;
import com.read.scriptures.widget.CustomViewPager;
import com.read.scriptures.widget.CustomViewPager.ScollAbleArea;
import com.read.scriptures.widget.sliding.SlidingLayout;
import com.read.scriptures.widget.sliding.slider.PageSlider;
import com.read.scriptures.widget.wheelview.DateWheelMain;
import com.read.scriptures.widget.wheelview.ScreenInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created with Android Studio. User : Lim Email: lgmshare@gmail.com Datetime :
 * 2015/4/28 16:00 To change getActivity() template use File | Settings | File
 * Templates.
 */
public class SpiritualityListFragment extends BaseFragment
        implements View.OnClickListener, AdapterView.OnItemClickListener, ScollAbleArea {
    private boolean mIsInit;

    public static SpiritualityListFragment newInstance() {
        return new SpiritualityListFragment();
    }

    /**
     * 自定义Dialog
     */
    private Dialog mDateDialog;
    private DateWheelMain mDateWheelMain;

    private TextView mTitleTextView;
    private TextView tv_nodata;
    private ImageButton mTodayButton;

    private SlidingLayout mSlidingLayout;
    private SpiritualitySlidingAdapter mSpiritualitySlidingAdapter;
    private SpiritualityListRecntAdapter mSpiritualityListAdapter;
    private List<Spirituality> mRecentReadList = new ArrayList<Spirituality>();

    private String mDayDate;
    private String mCurrentDate;
    private TextStyleUtil textStyleUtil;
    private List<SpliTab> rootCategorys = new ArrayList<>();
    boolean isUpdate = false;

    /**
     * 分类选中项
     */
    private int selectIndex = 0;

    private int selectCateGoryId = 0;


    public int getScollY() {
        int[] location = new int[2];
        if (mSlidingLayout != null)
            mSlidingLayout.getLocationInWindow(location);
        int temp = location[1];
        if (mTodayButton != null)
            mTodayButton.getLocationInWindow(location);
        return temp - location[1];
    }

    @Override
    protected void initWidget() {
        init();
    }

    @Override
    protected int onObtainLayoutResId() {
        return R.layout.activity_spirituality_list_fragment;
    }

    private void init(){
        mIsInit = false;
        if (!HomeDataManager.getInstance().isInitVolumeInfos()){
            //防止数据库被锁死
            return;
        }
        long start = TimeUtils.getNow();
        initData();

        GridView classifyGridView = (GridView) findViewById(R.id.baike_classify_gridview);

        tv_nodata = (TextView )findViewById(R.id.tv_no_data);
        CategorySpiriGridAdapter gridAdapter = new CategorySpiriGridAdapter(getActivity(), rootCategorys);
        gridAdapter.setIndex(0);
        classifyGridView.setNumColumns(rootCategorys.size());
        classifyGridView.setAdapter(gridAdapter);
        classifyGridView.setOnItemClickListener(this);
        mSpiritualityListAdapter = new SpiritualityListRecntAdapter(getActivity());
        mSpiritualityListAdapter.setList(mRecentReadList);
        GridView gridView = (GridView) findViewById(R.id.gridview);
        gridView.setAdapter(mSpiritualityListAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!HuDongApplication.getInstance().isAppNormalLevelActivate()) {
                    CommonUtil.showActivateDialog(getActivity(), UserInfo.VIP_NORMAL);
                    return;
                }
                Spirituality spirituality = mSpiritualityListAdapter.getItem(position);
                Bundle bd = new Bundle();
                bd.putParcelable(BundleConstants.PARAM_SPIRITUALITY, spirituality);
                ActivityUtil.next(getActivity(), SpiritualityContentActivity.class, bd, -1);
            }
        });
        mSpiritualitySlidingAdapter = new SpiritualitySlidingAdapter(getActivity());
        mSpiritualitySlidingAdapter.setDataType(rootCategorys.get(selectIndex).getCateName());
        mSpiritualitySlidingAdapter.setCurrentDate(mCurrentDate);
        mSpiritualitySlidingAdapter.setOnItemClickListener(this);
        mSpiritualitySlidingAdapter.updateUi(new SpiritualitySlidingAdapter.updateUi() {
            @Override
            public void updateUi(final boolean show) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (show)
                            tv_nodata.setVisibility(View.VISIBLE);
                        else
                            tv_nodata.setVisibility(View.GONE);
                    }
                });
            }
        });
        mSlidingLayout = (SlidingLayout) findViewById(R.id.sliding_layout);
        mSlidingLayout.setAdapter(mSpiritualitySlidingAdapter);
        mSlidingLayout.setOnTapListener(new SlidingLayout.OnTapListener() {

            @Override
            public void onSingleTap(MotionEvent event) {
                int screenWidth = getResources().getDisplayMetrics().widthPixels;
                int x = (int) event.getX();
                if (x > screenWidth / 2) {
                    mSlidingLayout.slideNext();
                } else if (x <= screenWidth / 2) {
                    mSlidingLayout.slidePrevious();
                }
            }
        });

        mSlidingLayout.setOnSlideChangeListener(new SlidingLayout.OnSlideChangeListener() {
            @Override
            public void onSlideScrollStateChanged(int touchResult) {
            }

            @Override
            public void onSlideSelected(Object obj) {
                mCurrentDate = mSpiritualitySlidingAdapter.getCurrentShowDate();
                if (mDayDate.equals(mCurrentDate)) {
                    mTodayButton.setVisibility(View.INVISIBLE);
                } else {
                    mTodayButton.setVisibility(View.VISIBLE);
                }
                String datetime = getDateTitle(mCurrentDate);
                getRecentReadList();
                mSpiritualityListAdapter.setList(mRecentReadList);
                mSpiritualityListAdapter.notifyDataSetChanged();
                textStyleUtil.setString(datetime);
                // mTitleTextView.setText(textStyleUtil.setUnderlineSpan(0,
                // datetime.length()).getSpannableString());
                mTitleTextView.setText(datetime);
            }
        });
        initActionBar();
        // 默认为左右平移模式
        ThreadUtil.doOnOtherThread(new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mSlidingLayout.setSlider(new PageSlider());
                    }
                });
            }
        });
        long end = TimeUtils.getNow();
        long diff = TimeUtils.diffTime(start, end);
        Log.e("ASDASDASDAD", "SpiritualityListFragment: " + diff);
        mIsInit = true;
    }

    private void initData() {
        textStyleUtil = new TextStyleUtil();
        mDayDate = DateUtil.getStringDateShort();
        mCurrentDate = DateUtil.getStringDateShort();
        SpiritualityCategoryDatabaseHepler spiritualityCategoryDatabaseHepler = new SpiritualityCategoryDatabaseHepler(getActivity());
        List<SpiritualityCategory> list = spiritualityCategoryDatabaseHepler.getSpiritualityCategoryList();
        for (int i = 0; i < list.size();i++){
            SpliTab spliTab = new SpliTab();
            spliTab.setCateName(list.get(i).getCateName());
            spliTab.setId(list.get(i).getId());
            rootCategorys.add(spliTab);
        }
        if (rootCategorys != null && rootCategorys.size() > 0) {
            selectCateGoryId = rootCategorys.get(0).getId();
        }
    }

    private void initActionBar() {
        findViewById(R.id.time_pick)
                // .setVisibility(View.INVISIBLE);
                .setOnClickListener(this);
        // findViewById(R.id.tv_title).setOnClickListener(this);
        mTitleTextView = (TextView) findViewById(R.id.tv_title);
        mTitleTextView.setOnClickListener(this);
        String datetime = getDateTitle(mCurrentDate);
        textStyleUtil.setString(datetime);
        // mTitleTextView.setText(textStyleUtil.setUnderlineSpan(0,
        // datetime.length()).getSpannableString());
        mTitleTextView.setText(datetime);
        mTitleTextView.setOnClickListener(this);
        mTodayButton = (ImageButton) findViewById(R.id.btn_today);
        mTodayButton.setVisibility(View.INVISIBLE);
        mTodayButton.setOnClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.baike_classify_gridview){
            selectIndex = position;
            mSpiritualitySlidingAdapter.setDataType(rootCategorys.get(selectIndex).getCateName());
            mCurrentDate = DateUtil.getStringDateShort();
            refreshSpirituality(mCurrentDate);
            CategorySpiriGridAdapter adapter = (CategorySpiriGridAdapter) parent.getAdapter();
            selectCateGoryId = adapter.getItem(selectIndex).getId();
            adapter.setIndex(parent, position);
//            mSpiritualitySlidingAdapter.setCurrentDate(mCurrentDate);
        }else {
            if (!HuDongApplication.getInstance().isAppNormalLevelActivate()) {
                CommonUtil.showActivateDialog(getActivity(),UserInfo.VIP_NORMAL);
                return;
            }
            SpiritualityListAdapter adapter = mSpiritualitySlidingAdapter.getCurrentSpiritualityListAdapter();
            Spirituality spirituality = adapter.getItem(position);
            boolean isNone = true;
            for (int i = 0; i < mRecentReadList.size(); i++) {
                if (mRecentReadList.get(i).getShowBook().equals(spirituality.getShowBook())) {
                    isNone = false;
                    break;
                }
            }
            if (isNone) {
                mRecentReadList.add(0, spirituality);
                if (mRecentReadList.size() > 3) {
                    mRecentReadList.remove(3);
                }
                try {
                    PreferencesUtils.putObject(getActivity(),"recent_spirituality", mRecentReadList);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Bundle bd = new Bundle();
            bd.putParcelable(BundleConstants.PARAM_SPIRITUALITY, spirituality);
            ActivityUtil.next(getActivity(), SpiritualityContentActivity.class, bd, -1);
        }
    }

    private String getDateTitle(String date) {
        return DateUtil.format(date, "yyyy-MM-dd", "yyyy年MM月dd日") + " " + DateUtil.getWeekStrFormat(date) + " "
                + getDateWeekSeq(date);
    }

    /**
     * 产生周序列,即得到当前时间所在的年度是第几周
     *
     * @return
     */
    public static String getDateWeekSeq(String date) {
        Calendar c = Calendar.getInstance(Locale.getDefault());
//        c.setFirstDayOfWeek(Calendar.MONDAY); // 设置每周的第一天为星期一
//        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);// 每周从周一开始
//        c.setMinimalDaysInFirstWeek(7); // 设置每周最少为7天
        c.setTime(DateUtil.strToDate(date, "yyyy-MM-dd"));
        String week = Integer.toString(c.get(Calendar.WEEK_OF_YEAR));
        if (week.length() == 1)
            week = "0" + week;
        return "第" + week + "周";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_back:
            // finish();
            break;
        case R.id.tv_title:
        case R.id.time_pick:
            showDateTimePicker();
            break;
        case R.id.btn_today:
            mCurrentDate = DateUtil.getStringDateShort();
            refreshSpirituality(mCurrentDate);
            // ActivityUtil.next(getActivity(), SpiritualityListActivity.class);
            break;
        }
    }

    public void refreshSpirituality(String date) {//设置偏移量，不动原本所在位置
        mSpiritualitySlidingAdapter.setCurrentDate(TimeUtils.getDateSp());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date start = null;
        Date end = null;
        try {
            start = sdf.parse(TimeUtils.getDateSp());
            end = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int index = TimeUtils.getGapCount(start,end);
        mSpiritualitySlidingAdapter.setPageIndex(index);//需要设置距今相差多少天
        mSpiritualitySlidingAdapter.notifyDataSetChanged();
        if (mDayDate.equals(mCurrentDate)) {
            mTodayButton.setVisibility(View.INVISIBLE);
        } else {
            mTodayButton.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 获取最近阅读列表
     */
    @SuppressWarnings("unchecked")
    private void getRecentReadList() {
        // 初始化搜索历史关键字数据
        List<Spirituality> recents = null;
        try {
            recents = (List<Spirituality>) PreferencesUtils.getObject(getActivity(),"recent_spirituality");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        mRecentReadList.clear();

        if (recents != null) {
            for (int i = 0; i < recents.size(); i++) {
                mRecentReadList.add(recents.get(i));
            }
        }
    }

    /**
     * 时间滚动器
     */
    public void showDateTimePicker() {
        View timepickerview = LayoutInflater.from(getActivity()).inflate(R.layout.datepicker_layout, null);
        timepickerview.setMinimumWidth(getActivity().getWindowManager().getDefaultDisplay().getWidth());
        timepickerview.findViewById(R.id.btn_ensure).setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                mCurrentDate = mDateWheelMain.getTime();
                refreshSpirituality(mCurrentDate);
                String datetime = getDateTitle(mCurrentDate);
                getRecentReadList();
                mSpiritualityListAdapter.setList(mRecentReadList);
                mSpiritualityListAdapter.notifyDataSetChanged();
                textStyleUtil.setString(datetime);
                // mTitleTextView.setText(textStyleUtil.setUnderlineSpan(0,
                // datetime.length()).getSpannableString());

                mTitleTextView.setText(datetime);
                mDateDialog.dismiss();
            }
        });
        timepickerview.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                mDateDialog.dismiss();
            }
        });
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateUtil.strToDate(mCurrentDate, "yyyy-MM-dd"));// 设置指定时间
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        ScreenInfo screenInfo = new ScreenInfo(getActivity());
        mDateWheelMain = new DateWheelMain(timepickerview);
        mDateWheelMain.screenheight = screenInfo.getHeight();
        mDateWheelMain.setTime(year, month, day);
        mDateDialog = new Dialog(getActivity(), R.style.ActionSheetDialogStyle);
        mDateDialog.setContentView(timepickerview);
        mDateDialog.show();
        Window window = mDateDialog.getWindow();
        window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
    }

    @Override
    protected void lazyLoad() {
//        initWidget();
//        initData();
//        if (isUpdate){
//            return;
//        }
        if (!mIsInit) {
            init();
        }else {
            getRecentReadList();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mSpiritualityListAdapter.setList(mRecentReadList);
                    mSpiritualityListAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void UpdateUI(RefreshDataEvent refreshDataEvent) {
        if ("初始化".equals(refreshDataEvent.getMessage()) && !mIsInit){
            init();
            return;
        }
        isUpdate = true;
        rootCategorys.clear();
        initWidget();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
        isUpdate = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
//        getRecentReadList();
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                mSpiritualityListAdapter.setList(mRecentReadList);
//                mSpiritualityListAdapter.notifyDataSetChanged();
//            }
//        });

//        if (isVisible){
//            ThreadUtil.doOnOtherThread(new Runnable() {
//                @Override
//                public void run() {
//                    getRecentReadList();
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            mSpiritualityListAdapter.setList(mRecentReadList);
//                            mSpiritualityListAdapter.notifyDataSetChanged();
//                        }
//                    });
//
//                }
//            });
//        }
    }




}
