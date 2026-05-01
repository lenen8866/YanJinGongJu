//package com.read.scriptures.view;
//
//import android.content.Context;
//import android.util.AttributeSet;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageButton;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.read.scriptures.R;
//
///**
// * 通用ActionBar
// *
// * @author Xun.Zhang
// * @ClassName: CommonActionBar
// * @date 2014-10-28 上午11:02:00
// */
//public class ActionBarLayout extends RelativeLayout {
//
//    /**
//     * 左边图标按钮
//     */
//    private ImageButton mBtnLeft;
//
//    /**
//     * 中间文字标题
//     */
//    private TextView mTvTitle;
//
//    /**
//     * 右边文字按钮
//     */
//    private Button mBtnRight;
//    /**
//     * 右边按钮
//     */
//    private ImageButton mBtnImgRight;
//
//    public ActionBarLayout(Context context, AttributeSet attrs, int defStyle) {
//        super(context, attrs, defStyle);
//        initViews(context);
//    }
//
//    public ActionBarLayout(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        initViews(context);
//    }
//
//    public ActionBarLayout(Context context) {
//        super(context);
//        initViews(context);
//    }
//
//    private void initViews(Context context) {
//        View rootLayout = LayoutInflater.from(context).inflate(R.layout.layout_actionbar, this);
//        mBtnLeft = (ImageButton) rootLayout.findViewById(R.id.btn_back);
//        mTvTitle = (TextView) rootLayout.findViewById(R.id.tv_title);
//        mBtnRight = (Button) rootLayout.findViewById(R.id.btn_right);
//        mBtnImgRight = (ImageButton) rootLayout.findViewById(R.id.btn_img_right);
//    }
//
//    /**
//     * 设置标题
//     *
//     * @param txtResId
//     */
//    public void setTitle(int txtResId) {
//        mTvTitle.setText(txtResId);
//    }
//
//    /**
//     * 设置标题
//     *
//     * @param title
//     */
//    public void setTitle(String title) {
//        mTvTitle.setText(title);
//    }
//
//    public void setTitle(String title, OnClickListener onClickListener) {
//        mTvTitle.setText(title);
//        mBtnLeft.setVisibility(View.VISIBLE);
//        mBtnLeft.setOnClickListener(onClickListener);
//    }
//
//    /**
//     * 设置左边图标按钮及事件
//     *
//     * @param imageResId      资源ID
//     * @param onClickListener 事件
//     */
//    public void setLeftBtn(int imageResId, OnClickListener onClickListener) {
//        mBtnLeft.setVisibility(View.VISIBLE);
//        mBtnLeft.setImageResource(imageResId);
//        mBtnLeft.setOnClickListener(onClickListener);
//    }
//
//    /**
//     * 设置右边按钮及事件
//     *
//     * @param imgResId        资源ID
//     * @param onClickListener 事件
//     */
//    public void setRighImagetBtn(int imgResId, OnClickListener onClickListener) {
//        mBtnImgRight.setVisibility(View.VISIBLE);
//        mBtnImgRight.setImageResource(imgResId);
//        mBtnImgRight.setOnClickListener(onClickListener);
//    }
//
//    /**
//     * 设置右边按钮及事件
//     *
//     * @param txtResId        资源ID
//     * @param onClickListener 事件
//     */
//    public void setRightBtn(int txtResId, OnClickListener onClickListener) {
//        mBtnRight.setVisibility(View.VISIBLE);
//        mBtnRight.setText(txtResId);
//        mBtnRight.setOnClickListener(onClickListener);
//    }
//
//    /**
//     * 设置右边按钮及事件
//     *
//     * @param txt             资源
//     * @param onClickListener 事件
//     */
//    public void setRightBtn(String txt, OnClickListener onClickListener) {
//        mBtnRight.setVisibility(View.VISIBLE);
//        mBtnRight.setText(txt);
//        mBtnRight.setOnClickListener(onClickListener);
//    }
//
//    public void setRightBtnTxt(int txtResId) {
//        mBtnRight.setText(txtResId);
//    }
//
//    public void setRightBtnTxt(String txt) {
//        mBtnRight.setText(txt);
//    }
//}
