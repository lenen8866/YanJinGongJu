package com.read.scriptures.ui.fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.read.scriptures.R;
import com.read.scriptures.adapter.AnnTabAdapter;
import com.read.scriptures.bean.AnnItemInfo;
import com.read.scriptures.config.SystemConfig;
import com.read.scriptures.constants.BundleConstants;
import com.read.scriptures.util.DisplayUtil;
import com.read.scriptures.util.SearchTextUtil;
import com.read.scriptures.util.SharedUtil;
import com.read.scriptures.util.StringUtil;
import com.read.scriptures.widget.bottomSheet.BottomSheetBehavior2;
import com.read.scriptures.widget.bottomSheet.BottomSheetDialog2;
import com.read.scriptures.widget.bottomSheet.BottomSheetDialogFragment2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BaseFullBottomSheetFragment extends BottomSheetDialogFragment2 {

    private List<AnnItemInfo> tabList;
    private Context mContext;
    private int pickHeight = 0;
    private int mBottomSheetStatus;//底部注释弹框状态
    private int mNewHeight;
    private ViewGroup rootView;
    //    private BottomSheetListView annListView;
    private int listViewHeight;
    private int mTop;
    private int mDefaultTop;
    private boolean hasMeasured;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        mContext = this.getContext();
        BottomSheetDialog2 dialog = new BottomSheetDialog2(this.getContext());
        dialog.setContentView(R.layout.popup_ann_pop);

        return dialog;
    }


    @Override
    public void onStart() {
        super.onStart();

        int rootHeight = getArguments().getInt("rootHeight");
        rootHeight = rootHeight - (int) DisplayUtil.dp2px(getContext(), 45);
        pickHeight = rootHeight;
        mNewHeight = pickHeight;
        setDialogHeight(pickHeight);
    }

    private void setDialogHeight(final int height) {
        //获取dialog对象
        final BottomSheetDialog2 dialog = (BottomSheetDialog2) getDialog();
        //把windowsd的默认背景颜色去掉，不然圆角显示不见
        dialog.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        initViews(dialog);

        //获取diglog的根部局
        final FrameLayout bottomSheet = dialog.getDelegate().findViewById(R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            //获取根部局的LayoutParams对象
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomSheet.getLayoutParams();
            layoutParams.height = height;
            //修改弹窗的最大高度，不允许上滑（默认可以上滑）
            bottomSheet.setLayoutParams(layoutParams);

            final BottomSheetBehavior2<FrameLayout> behavior = BottomSheetBehavior2.from(bottomSheet);
            //peekHeight即弹窗的最大高度
            behavior.setPeekHeight(height);
            behavior.setContext(getActivity());
            behavior.setBottomSheetCallback(new BottomSheetBehavior2.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {

                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                    if (mDefaultTop == 0) {
                        mDefaultTop = bottomSheet.getTop();
                    }
                    if (listViewHeight == 0) {
                        listViewHeight = dialog.findViewById(R.id.rl_content).getHeight();
                    }
                    int listHeight = listViewHeight - bottomSheet.getTop() + mDefaultTop;
                    dialog.findViewById(R.id.rl_content).getLayoutParams().height = listHeight;
                    dialog.findViewById(R.id.rl_content).requestLayout();
                }
            });
            // 初始为展开状态
            behavior.setState(BottomSheetBehavior2.STATE_EXPANDED);

            ViewTreeObserver vto = dialog.findViewById(R.id.rl_content).getViewTreeObserver();
            vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    if (hasMeasured == false) {
                        int contentHeight = dialog.findViewById(R.id.rl_content).getMeasuredHeight();
                        int listHeight = contentHeight - (height / 3);
                        dialog.findViewById(R.id.rl_content).getLayoutParams().height = listHeight;
                        dialog.findViewById(R.id.rl_content).requestLayout();
                        hasMeasured = true;
                    }
                    return true;
                }
            });
//            int listHeight = height - (height / 3) - behavior.getmMinOffset() - (int) DisplayUtil.dp2px(getContext(), 2);
//            dialog.findViewById(R.id.rl_content).getLayoutParams().height = listHeight;
//            dialog.findViewById(R.id.rl_content).requestLayout();


        }
    }


    private void initViews(final BottomSheetDialog2 dialog) {
        String jsonStr = SharedUtil.getString(mContext, BundleConstants.ANN_LIST);
        if (!TextUtils.isEmpty(jsonStr)) {
            if (!TextUtils.isEmpty(jsonStr)) {
                tabList = new Gson().fromJson(jsonStr, new TypeToken<List<AnnItemInfo>>() {
                }.getType());
            }
        }
        rootView = dialog.findViewById(R.id.ll_root_view);
        GridView annGridView = dialog.findViewById(R.id.gridView_ann);
        TextView tvDefaultTitle = dialog.findViewById(R.id.tv_default_title);
        final RelativeLayout rlNoContent = dialog.findViewById(R.id.rl_no_content);
        final WebView tvContent = dialog.findViewById(R.id.webview);

//        annListView = dialog.findViewById(R.id.ann_listview);
        List<AnnItemInfo> needDeleteInfos = new ArrayList<>();
        if (tabList != null){
            Iterator<AnnItemInfo> annItemInfoIterator = tabList.iterator();
            while (annItemInfoIterator.hasNext()){
                AnnItemInfo info = annItemInfoIterator.next();
                if (StringUtil.isEmpty(info.getContent())
                        || StringUtil.isEmpty(info.getContent().replaceAll("\n","")
                        .replaceAll("\t","")
                        .trim())
                ){
                    needDeleteInfos.add(info);
                }
            }
        }
        if (!needDeleteInfos.isEmpty() && tabList != null){
            tabList.removeAll(needDeleteInfos);
        }

        if (tabList != null && tabList.size() > 0) {
            annGridView.setVisibility(View.VISIBLE);
            tvDefaultTitle.setVisibility(View.GONE);
            rlNoContent.setVisibility(View.GONE);
            dialog.findViewById(R.id.rl_content).setVisibility(View.VISIBLE);

            annGridView.setNumColumns(tabList.size());
            final AnnTabAdapter annTabAdapter = new AnnTabAdapter(mContext, tabList);
            annGridView.setAdapter(annTabAdapter);

            List<String> strList = SearchTextUtil.queryChaptreContentByContent(mContext, tabList.get(0).getContent(), SystemConfig.TEXT_MODEL_NORMAL);
            for (String s : strList) {
                Log.e("OkHttp", "s----" + s);
            }
            tvContent.getSettings().setDefaultTextEncodingName("utf-8");
            tvContent.loadDataWithBaseURL("",dealwithContent(strList), "text/html", "UTF-8",null);//,null,new SizeLabelHandler(getContext(),14,16)
//            final AnnAdapter annAdapter = new AnnAdapter(mContext, strList);
//            annListView.setAdapter(annAdapter);
            annGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (i == annTabAdapter.getSelectIndex()){
                        return;
                    }
                    annTabAdapter.setSelectIndex(i);
                    AnnItemInfo annItemInfo = tabList.get(i);
                    List<String> list = SearchTextUtil.queryChaptreContentByContent(mContext, annItemInfo.getContent(), SystemConfig.TEXT_MODEL_NORMAL);
                    if (list == null || list.isEmpty()){
                        rlNoContent.setVisibility(View.VISIBLE);
                        dialog.findViewById(R.id.rl_content).setVisibility(View.GONE);
                    }else {
                        rlNoContent.setVisibility(View.GONE);
                        dialog.findViewById(R.id.rl_content).setVisibility(View.VISIBLE);
                        tvContent.getSettings().setDefaultTextEncodingName("utf-8");
                        tvContent.loadDataWithBaseURL("",dealwithContent(list), "text/html", "UTF-8",null);//,null,new SizeLabelHandler(getContext(),14,16)
                    }
//                    boolean isCurrent = false;
//                    for (AnnItemInfo in : tabList) {
//                        if (in.isCheck()) {
//                            if (in.getTitle().equals(annItemInfo.getTitle())) {
//                                isCurrent = true;
//                            }
//                        }
//                    }
//                    if (!isCurrent) {
//                        for (AnnItemInfo info : tabList) {
//                            info.setCheck(false);
//                        }
//                        annItemInfo.setCheck(true);
//                        annTabAdapter.updateList(tabList);
//
//                        List<String> list = SearchTextUtil.queryChaptreContentByContent(mContext, annItemInfo.getContent(), SystemConfig.TEXT_MODEL_NORMAL);
////                        for (String s : list) {
////                            Log.e("OkHttp", "s----" + s);
////                        }
////                        annAdapter.updateList(list);
////                        annListView.setSelection(0);
//                        tvContent.loadData(dealwithContent(list), "text/html", "UTF-8");//,null,new SizeLabelHandler(getContext(),14,16)
//                    }
                }
            });

        } else {
            annGridView.setVisibility(View.GONE);
            tvDefaultTitle.setVisibility(View.VISIBLE);
            rlNoContent.setVisibility(View.VISIBLE);
            dialog.findViewById(R.id.rl_content).setVisibility(View.GONE);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) rlNoContent.getLayoutParams();
            layoutParams.height = pickHeight;
            rlNoContent.setLayoutParams(layoutParams);
        }
    }

    public String dealwithContent(List<String> lists) {
        int smallFontSize = 20;
        int normalFontSize = 20;
        StringBuffer contentBuff = new StringBuffer("<div style=\"font-size:"+normalFontSize+"px;color:#333333\">");
        for (String content : lists) {
            if (content == null)
                continue;
            //<b>1:1-2:3</b> 聖經以簡短的敘述開始。但這簡短的一句話,恰恰就是上帝莊嚴的宣告,若不是上帝以慈愛創造宇宙,人類存在的價值和意義又是什麼呢？人被自私的欲望、死亡的恐懼所壓迫,只能忍受邪惡勢力的支配。本文中上帝的宣告如同一束燦爛的啟示之光,給人類和世上萬物提供生命和存在的真正意義。因為上帝是萬物的開始(The Begining)、起因(The Cause)和根源(The Source)。上帝從無創造了有秩序的宇宙,他是世界的主宰。本文宣告了有關上帝的知識和宇宙的起源。從創造的順序中,我們也可以看到上帝周密的計劃。
            content = content.replaceAll("<b>", "<b style=\"font-size:"+smallFontSize+"px;color:#989898;font-weight:bold\">")
                    .replaceAll("<span>", "<b style=\"font-size:"+normalFontSize+"px;color:#333333;font-weight:bold;\">")
                    .replaceAll("</span>", "</b>");

            contentBuff
                    .append(content)
                    .append("<br/><br/>");
        }
        String newContent = contentBuff.append("</div>").toString();
        newContent = newContent.replaceAll("<h3>", "<center><b style=\"color:#333333;font-size:"+normalFontSize+"px;font-weight:bold\">")
                .replaceAll("</h3>", "</b></center>");

        return newContent;

    }
    public int sp2px(float spValue) {
        if (getActivity() == null){
            return (int)spValue;
        }
        final float fontScale = getActivity().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);

    }
}
