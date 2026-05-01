//package com.read.scriptures.widget;
//
//import android.app.Activity;
//import android.content.Context;
//import android.graphics.drawable.ColorDrawable;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.View.OnTouchListener;
//import android.view.ViewGroup.LayoutParams;
//import android.widget.AdapterView;
//import android.widget.ListView;
//import android.widget.PopupWindow;
//
//import com.read.scriptures.R;
//import com.read.scriptures.model.Chapter;
//import com.read.scriptures.model.Volume;
//import com.read.scriptures.ui.adapter.ChapterListViewAdapter;
//import com.lgmshare.eiframe.utils.DensityUtil;
//
//import java.util.List;
//
//
//public class ChaptersListPopupWindow extends PopupWindow {
//
//    private View mMenuView;
//
//    private ChapterListViewAdapter mAdapter;
//    private Volume mVolume;
//
//    public ChaptersListPopupWindow(Context context) {
//        super(context);
//    }
//
//    public ChaptersListPopupWindow(Activity context, List<Chapter> chapters, AdapterView.OnItemClickListener listener) {
//        super(context);
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        mMenuView = inflater.inflate(R.layout.popup_chapter_listview_layout, null);
//        mMenuView.setOnTouchListener(new OnTouchListener() {
//            public boolean onTouch(View v, MotionEvent event) {
//                int height = mMenuView.findViewById(R.id.pop_layout).getTop();
//                int y = (int) event.getY();
//                if (event.getAction() == MotionEvent.ACTION_UP) {
//                    if (y < height) {
//                        dismiss();
//                    }
//                }
//                return true;
//            }
//        });
//        mAdapter = new ChapterListViewAdapter(context, chapters,);
//        ListView mListView = (ListView) mMenuView.findViewById(R.id.chapter_listview);
//        mListView.setAdapter(mAdapter);
//        mListView.setOnItemClickListener(listener);
//
//        this.setContentView(mMenuView);
//        this.setBackgroundDrawable(new ColorDrawable(0x00000000));
//        this.setWidth(LayoutParams.MATCH_PARENT);
//        this.setHeight(DensityUtil.getScreenHeight(context) - 160);
//        this.setFocusable(true);
//        this.setAnimationStyle(R.style.AnimBottom);
//    }
//}
