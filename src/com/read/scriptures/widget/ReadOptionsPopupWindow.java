package com.read.scriptures.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.read.scriptures.R;
import com.read.scriptures.config.PreferenceConfig;
import com.read.scriptures.EIUtils.EIBaseHolderAdapter;
import com.read.scriptures.EIUtils.ViewHolder;

import java.util.ArrayList;
import java.util.List;

public class ReadOptionsPopupWindow extends PopupWindow {

    private static final int TITLE_DURATION = 200;
    private static final int BOTTOM_DURATION = 200;

    private Context mContext;
    private View mRootView;
    private RelativeLayout mTitleLayout;
    private RelativeLayout mBottomLayout;
    private int mTitleHeight;
    private int mBottomHeight;

    private List<OptoinsMenuItem> mOptionMenuItems;
    private AdapterView.OnItemClickListener mOnItemClickListener;
    private View.OnClickListener mOnClickListener;

    private boolean isDismiss;
    private boolean isSelectedVolume;
    private boolean isSelectedChapter;

    private boolean isContainsATag;

    private String selectContent;

    private GridView gridView;

    MyMenuAdapter adapter;

    public ReadOptionsPopupWindow(Context context, String content, int removeIndex) {
        this(context, content);
        mOptionMenuItems.remove(removeIndex);
        adapter.setList(mOptionMenuItems);
        adapter.notifyDataSetChanged();
    }


    public ReadOptionsPopupWindow(Context context, String content) {
        super(context.getApplicationContext());
        mContext = context.getApplicationContext();
        selectContent = content;
        isSelectedVolume = PreferenceConfig.getCopyVolumae(mContext);
        isSelectedChapter = PreferenceConfig.getCopyChapter(mContext);
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.popup_chapter_read_options,
                null);
//        mRootView.setOnTouchListener(new OnTouchListener() {
//            public boolean onTouch(View v, MotionEvent event) {
//                int topHeight = mRootView.findViewById(R.id.layout_top).getBottom();
//                int bottomHeight = mRootView.findViewById(R.id.layout_bottom).getTop();
//                int y = (int) event.getY();
//                if (event.getAction() == MotionEvent.ACTION_UP) {
//                    if (topHeight < y && y < bottomHeight) {
//                        hideMenu();
//                    }
//                }
//                return true;
//            }
//        });

        mRootView.findViewById(R.id.btn_cancel_model).setOnClickListener(new View.OnClickListener
                () {
            @Override
            public void onClick(View v) {
                dismiss();
                if (mOnItemClickListener != null) {
                    mOnClickListener.onClick(v);
                }
            }
        });

        mRootView.findViewById(R.id.btn_selectall).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnClickListener.onClick(v);
                }
            }
        });

        mRootView.findViewById(R.id.btn_selectnone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnClickListener.onClick(v);
                }
            }
        });
        mRootView.findViewById(R.id.layout_empty).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideMenu();
            }
        });
        mTitleLayout = (RelativeLayout) mRootView.findViewById(R.id.layout_top);
        mBottomLayout = (RelativeLayout) mRootView.findViewById(R.id.layout_bottom);
        mTitleHeight = getViewHeight(mTitleLayout);
        mBottomHeight = getViewHeight(mBottomLayout);

        mOptionMenuItems = new ArrayList<OptoinsMenuItem>();
        mOptionMenuItems.add(new OptoinsMenuItem(R.drawable.ic_menu_1, "内容多选"));
        mOptionMenuItems.add(new OptoinsMenuItem(R.drawable.ic_menu_2, "加入书签"));
        mOptionMenuItems.add(new OptoinsMenuItem(R.drawable.ic_menu_3, "一键分享"));
        if (isSelectedVolume) {
            mOptionMenuItems.add(new OptoinsMenuItem(R.drawable.ic_menu_5, "复制书名"));
        } else {
            mOptionMenuItems.add(new OptoinsMenuItem(R.drawable.ic_menu_4, "复制书名"));
        }

        if (isSelectedChapter) {
            mOptionMenuItems.add(new OptoinsMenuItem(R.drawable.ic_menu_5, "复制目录"));
        } else {
            mOptionMenuItems.add(new OptoinsMenuItem(R.drawable.ic_menu_4, "复制目录"));
        }

        mOptionMenuItems.add(new OptoinsMenuItem(R.drawable.ic_menu_6, "复制内容"));

        adapter = new MyMenuAdapter(context);
        adapter.setList(mOptionMenuItems);
        gridView = (GridView) mBottomLayout.findViewById(R.id.popup_gv_option);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if ("复制书名".equals(adapter.getItem(position).getMenuName())) {
                    if (isSelectedVolume) {
                        isSelectedVolume = false;
                    } else {
                        isSelectedVolume = true;
                    }
                    PreferenceConfig.saveCopyVolumae(mContext, isSelectedVolume);
                    updateMenu();
                    return;
                } else if ("复制目录".equals(adapter.getItem(position).getMenuName())) {
                    if (isSelectedChapter) {
                        isSelectedChapter = false;
                    } else {
                        isSelectedChapter = true;
                    }
                    PreferenceConfig.saveCopyChapter(mContext, isSelectedChapter);
                    updateMenu();
                    return;
                }
                mOnItemClickListener.onItemClick(parent, view, position, id);
            }
        });

        this.setContentView(mRootView);
        this.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.setWidth(LayoutParams.MATCH_PARENT);
        this.setHeight(LayoutParams.MATCH_PARENT);
        this.setFocusable(true);
    }


    @Override
    public void setOnDismissListener(OnDismissListener onDismissListener) {
        super.setOnDismissListener(onDismissListener);
    }

    private int getViewHeight(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        return view.getMeasuredHeight();
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public void setOnClickListener(View.OnClickListener onCancleClickListener) {
        this.mOnClickListener = onCancleClickListener;
    }

    public boolean isSelectedVolume() {
        return isSelectedVolume;
    }

    public boolean isSelectedChapter() {
        return isSelectedChapter;
    }

    public void showMenu(View parent) {
        updateMenu();
        startAnimation(mBottomLayout, 0, 0, mBottomHeight << 1, 0, BOTTOM_DURATION);
        startAnimation(mTitleLayout, 0, 0, -mTitleHeight, 0, TITLE_DURATION);
        showAtLocation(parent, Gravity.LEFT | Gravity.TOP, 0, 0);
    }

    private void updateMenu() {
        OptoinsMenuItem menuVolumeItem = null;
        OptoinsMenuItem menuChapterItem = null;
        OptoinsMenuItem menuJumpLinkItem = null;
        for (OptoinsMenuItem menuItem :
                mOptionMenuItems) {
            if ("复制书名".equals(menuItem.getMenuName())) {
                menuVolumeItem = menuItem;
            } else if ("复制目录".equals(menuItem.getMenuName())) {
                menuChapterItem = menuItem;
            } else if ("跳转链接".equals(menuItem.getMenuName())) {
                menuJumpLinkItem = menuItem;
            }
        }
        if (isSelectedVolume) {
            menuVolumeItem.setIconID(R.drawable.ic_menu_5);
        } else {
            menuVolumeItem.setIconID(R.drawable.ic_menu_4);
        }

        if (isSelectedChapter) {
            menuChapterItem.setIconID(R.drawable.ic_menu_5);
        } else {
            menuChapterItem.setIconID(R.drawable.ic_menu_4);
        }

        if (selectContent != null && (selectContent.contains
                ("<a ") || selectContent.contains("<a>"))) {
            isContainsATag = true;
            if (menuJumpLinkItem == null) {
                menuJumpLinkItem = new OptoinsMenuItem(R.drawable.jumplink, "跳转链接");
                mOptionMenuItems.add(menuJumpLinkItem);
            }
        } else {
            isContainsATag = false;
            if (menuJumpLinkItem != null) {
                mOptionMenuItems.remove(menuJumpLinkItem);
            }
        }
        gridView.setNumColumns(mOptionMenuItems.size() / 2 + mOptionMenuItems.size() % 2);
        adapter.setList(mOptionMenuItems);
        adapter.notifyDataSetChanged();
    }

    public void hideMenu() {
        if (isShowing()) {
            isDismiss = true;
            startAnimation(mTitleLayout, 0, 0, 0, -mTitleHeight, TITLE_DURATION);
            startAnimation(mBottomLayout, 0, 0, 0, mBottomHeight, BOTTOM_DURATION);
        }
    }

    private void startAnimation(View view, float fromX, float toX, float fromY, float toY, long
            duration) {
        TranslateAnimation animation = new TranslateAnimation(fromX, toX, fromY, toY);
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(duration);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (isDismiss) {
                    isDismiss = false;
                    dismiss();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        view.startAnimation(animation);
    }

    class MyMenuAdapter extends EIBaseHolderAdapter<OptoinsMenuItem> {

        public MyMenuAdapter(Context context) {
            super(context, R.layout.adapter_menu_item);
        }

        @Override
        public void convert(ViewHolder viewHolder, OptoinsMenuItem optoinsMenuItem) {
            viewHolder.setViewBackground(R.id.iv_menu_icon, optoinsMenuItem.getIconID());
            viewHolder.setText(R.id.tv_menu_name, optoinsMenuItem.getMenuName());
        }
    }

    class OptoinsMenuItem {
        /**
         * 当前菜单项图标ID
         */
        private int iconID;
        /**
         * 当前菜单名称
         */
        private String menuName;

        public OptoinsMenuItem(int iconID, String menuName) {
            this.iconID = iconID;
            this.menuName = menuName;
        }

        public int getIconID() {
            return iconID;
        }

        public void setIconID(int iconID) {
            this.iconID = iconID;
        }

        public String getMenuName() {
            return menuName;
        }

        public void setMenuName(String menuName) {
            this.menuName = menuName;
        }

    }

    public String getSelectContent() {
        return selectContent;
    }

    public void setSelectContent(String selectContent) {
        this.selectContent = selectContent;
    }
}
