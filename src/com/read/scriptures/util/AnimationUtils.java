package com.read.scriptures.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.read.scriptures.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

/**
 * Created by thbpc on 2017/3/2 0002.
 */

public class AnimationUtils {

    static boolean isTest19Stop = true;

    public static void jumpAnimation(final View view) {
        //定义一个位移补间动画
        TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 0, -20);
        //设置动画结束后效果保留
        translateAnimation.setFillAfter(false);
        //设置动画持续时间
        translateAnimation.setDuration(200);
        translateAnimation.setRepeatCount(9);
        translateAnimation.setRepeatMode(TranslateAnimation.REVERSE);
        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isTest19Stop = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        //开启动画
        if (isTest19Stop) {
            isTest19Stop = false;
            view.startAnimation(translateAnimation);
        }
    }

    private static int mRandomWidth;
    private static int mRandomHeight;
    private static ArrayList<ImageView> imageViewsList = new ArrayList<>();
    private static ViewGroup mRootView;

    public static void throwingAnything(Activity context, final ViewGroup group, int resId, int[] position, int endY) {
        final ImageView mImg = new ImageView(context);
        mImg.setImageResource(resId);
        mImg.setScaleType(ImageView.ScaleType.MATRIX);
//        final ViewGroup rootView = (ViewGroup) context.getWindow().getDecorView();
        mRootView = group;
        group.addView(mImg, 4);//将view添加到viewgroup的第四层
        mImg.setPivotX(mImg.getWidth() / 2);
        mImg.setPivotY(mImg.getHeight() / 2);
        imageViewsList.add(mImg);

        Point startPosition = new Point(position[0], position[1]);
        mRandomWidth = new Random().nextInt((int) (DensityUtil.getScreenWidth(context) * 0.6)) + (int) (DensityUtil.getScreenWidth(context) * 0.2);
        mRandomHeight = endY;

        final Point endPosition = new Point(mRandomWidth, mRandomHeight);
        int pointX = startPosition.x;
        int pointY = endPosition.y / 3 * 2;
        Point controllPoint = new Point(pointX, pointY);

        ValueAnimator valueAnimator = ValueAnimator.ofObject(new BizierEvaluator(controllPoint), startPosition, endPosition);
        valueAnimator.setInterpolator(new LinearInterpolator());
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(mImg, "scaleX", 0.7f, 1.0f, 0.8f);
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(mImg, "scaleY", 0.7f, 1.0f, 0.8f);
        ObjectAnimator animator4 = ObjectAnimator.ofFloat(mImg, "rotation", 0f, new Random().nextBoolean() ? -new Random().nextInt(180) : new Random().nextInt(180));

        AnimatorSet mSet = new AnimatorSet();
        mSet.setDuration(1000);
        mSet.playTogether(animator1, animator3, valueAnimator, animator4);
        mSet.start();
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Point point = (Point) valueAnimator.getAnimatedValue();
                mImg.setX(point.x);
                mImg.setY(point.y);
            }
        });
//        valueAnimator.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                if (imageViewsList.size() > 50) {
//                    Iterator<ImageView> iterator = imageViewsList.iterator();
//                    while (iterator.hasNext() && imageViewsList.size() > 30) {
//                        group.removeView(iterator.next());
//                        iterator.remove();
//                    }
//                }
//            }
//        });
    }


    static float mPointx;
    static float mPointy;

//    public static void throwingEggs(final Activity context, final ViewGroup group, int resId, int[] position) {
//        final ImageView mImg = new ImageView(context);
//        mImg.setImageResource(resId);
//        mImg.setScaleType(ImageView.ScaleType.MATRIX);
////        final ViewGroup rootView = (ViewGroup) context.getWindow().getDecorView();
//        mRootView = group;
//        group.addView(mImg, 4);
//        mImg.setPivotX(mImg.getWidth() / 2);
//        mImg.setPivotY(mImg.getHeight() / 2);
//        Point startPosition = new Point(position[0], position[1]);
//        mRandomWidth = new Random().nextInt((int) (DensityUtil.getScreenWidth(context) * 0.8)) + (int) (DensityUtil.getScreenWidth(context) * 0.1);
//        mRandomHeight = new Random().nextInt(180) + 70;
//
//        final Point endPosition = new Point(mRandomWidth, DensityUtil.dip2px(context, mRandomHeight));
//        int pointX = startPosition.x;
//        int pointY = endPosition.y / 2;
//        Point controllPoint = new Point(pointX, pointY);
//
//        ValueAnimator valueAnimator = ValueAnimator.ofObject(new BizierEvaluator(controllPoint), startPosition, endPosition);
//        ObjectAnimator animator1 = ObjectAnimator.ofFloat(mImg, "scaleX", 0.8f, 1.1f, 0.9f);
//        ObjectAnimator animator3 = ObjectAnimator.ofFloat(mImg, "scaleY", 0.8f, 1.1f, 0.9f);
//        ObjectAnimator animator4 = ObjectAnimator.ofFloat(mImg, "rotation", 0f, new Random().nextBoolean() ? -new Random().nextInt(180) : new Random().nextInt(180));
//
//        AnimatorSet mSet = new AnimatorSet();
//        mSet.setDuration(1000);
//        mSet.playTogether(animator1, animator3, valueAnimator, animator4);
//        mSet.start();
//
//        valueAnimator.setInterpolator(new LinearInterpolator());
//        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator valueAnimator) {
//                Point point = (Point) valueAnimator.getAnimatedValue();
//                mImg.setX(point.x);
//                mImg.setY(point.y);
//
//            }
//        });
//
//        valueAnimator.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                super.onAnimationEnd(animation);
//                mPointx = mImg.getX();
//                mPointy = mImg.getY();
//                group.removeView(mImg);
//                final ImageView mIv = new ImageView(context);
//                mIv.setScaleType(ImageView.ScaleType.MATRIX);
//                group.addView(mIv, 8);
//                mIv.setX(mPointx - DensityUtil.dip2px(context, 80f));
//                mIv.setY(mPointy - DensityUtil.dip2px(context, 80f));
//
//                FramesSequenceAnimation framesSequenceAnimation = new FramesSequenceAnimation(context, mIv, R.array.eggs_aniation, 25);
//                framesSequenceAnimation.setFramesSequenceAnimationListener(new FramesSequenceAnimation.FramesSequenceAnimationListener() {
//                    @Override
//                    public void AnimationStopped() {
//                        group.removeView(mIv);
//                    }
//
//                    @Override
//                    public void AnimationStarted() {
//
//                    }
//                });
//                framesSequenceAnimation.start();
//            }
//        });
//    }

    public static AnimatorSet shake(View view) {
        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(width, height);
        view.setPivotX(view.getMeasuredWidth() / 2);
        view.setPivotY(view.getMeasuredHeight());

        ObjectAnimator animator3 = ObjectAnimator.ofFloat(view, "rotation", 0f, -8f);
        animator3.setDuration(1000);
        animator3.setRepeatCount(0);
        animator3.setStartDelay(new Random().nextInt(2500));

        ObjectAnimator animator4 = ObjectAnimator.ofFloat(view, "rotation", -8f, 8f);
        animator4.setDuration(2000);
        animator4.setRepeatCount(-1);
        animator4.setRepeatMode(ObjectAnimator.REVERSE);

        AnimatorSet mSet = new AnimatorSet();
        mSet.play(animator3).before(animator4);
        mSet.start();
        return mSet;
    }


//    public static void throwingEggsOnLive(final Activity context, int resId, int[] position) {
//        final ImageView mImg = new ImageView(context);
//        mImg.setImageResource(resId);
//        mImg.setScaleType(ImageView.ScaleType.MATRIX);
//        final ViewGroup rootView = (ViewGroup) context.getWindow().getDecorView();
//        rootView.addView(mImg);
//        Point startPosition = new Point(position[0], position[1]);
//        mRandomWidth = new Random().nextInt(DensityUtil.getScreenWidth(context) / 5 * 3) + DensityUtil.getScreenWidth(context) / 5;
//        mRandomHeight = new Random().nextInt(DensityUtil.getScreenHeight(context) / 5 * 3) + DensityUtil.getScreenHeight(context) / 5;
//
//        final Point endPosition = new Point(mRandomWidth, mRandomHeight);
//        int pointX = startPosition.x;
//        int pointY = endPosition.y / 2;
//        Point controllPoint = new Point(pointX, pointY);//控制点
//
//        ValueAnimator valueAnimator = ValueAnimator.ofObject(new BizierEvaluator(controllPoint), startPosition, endPosition);
//        valueAnimator.setInterpolator(new LinearInterpolator());
//        mImg.setPivotX(mImg.getWidth() / 2);
//        mImg.setPivotY(mImg.getHeight() / 2);
//        ObjectAnimator animator1 = ObjectAnimator.ofFloat(mImg, "scaleX", 0.5f, 1.1f, 1.2f);
//        ObjectAnimator animator3 = ObjectAnimator.ofFloat(mImg, "scaleY", 0.5f, 1.1f, 1.2f);
//        ObjectAnimator animator4 = ObjectAnimator.ofFloat(mImg, "rotation", 0f, new Random().nextBoolean() ? -new Random().nextInt(180) : new Random().nextInt(180));
//        AnimatorSet mSet = new AnimatorSet();
//        mSet.setDuration(1000);
//        mSet.playTogether(animator1, animator3, valueAnimator, animator4);
//        mSet.start();
//
//        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator valueAnimator) {
//                Point point = (Point) valueAnimator.getAnimatedValue();
//                mImg.setX(point.x);
//                mImg.setY(point.y);
//            }
//        });
//        valueAnimator.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                super.onAnimationEnd(animation);
//                mPointx = mImg.getX();
//                mPointy = mImg.getY();
//                rootView.removeView(mImg);
//                final ImageView mIv = new ImageView(context);
//                mIv.setScaleType(ImageView.ScaleType.MATRIX);
//                rootView.addView(mIv);
//                mIv.setX(mPointx - DensityUtil.dip2px(context, 80f));
//                mIv.setY(mPointy - DensityUtil.dip2px(context, 80f));
//
//                FramesSequenceAnimation framesSequenceAnimation = new FramesSequenceAnimation(context, mIv, R.array.eggs_aniation, 25);
//                framesSequenceAnimation.setFramesSequenceAnimationListener(new FramesSequenceAnimation.FramesSequenceAnimationListener() {
//                    @Override
//                    public void AnimationStopped() {
//                        rootView.removeView(mIv);
//                    }
//
//                    @Override
//                    public void AnimationStarted() {
//
//                    }
//                });
//                framesSequenceAnimation.start();
//            }
//        });
//    }

    /**
     * 清除view
     */
    public static void cleanView() {
        if (imageViewsList != null && mRootView != null) {
            Iterator<ImageView> iterator = imageViewsList.iterator();
            while (iterator.hasNext()) {
                mRootView.removeView(iterator.next());
                iterator.remove();
            }
        }
    }


    public static void throwingTicketOrStarOnLive(Activity context, int resId, int[] position) {
        final ImageView mImg = new ImageView(context);
        mImg.setImageResource(resId);
        mImg.setScaleType(ImageView.ScaleType.MATRIX);
        mRootView = (ViewGroup) context.getWindow().getDecorView();
        mRootView.addView(mImg);
        Point startPosition = new Point(position[0], position[1]);
        mRandomWidth = new Random().nextInt(DensityUtil.getScreenWidth(context) / 5 * 3) + DensityUtil.getScreenWidth(context) / 5;
        mRandomHeight = new Random().nextInt(DensityUtil.getScreenHeight(context) / 5 - 10) + DensityUtil.getScreenHeight(context) * 4 / 5;
        imageViewsList.add(mImg);

        final Point endPosition = new Point(mRandomWidth, mRandomHeight);
        int pointX = startPosition.x;
        int pointY = endPosition.y / 2;
        Point controllPoint = new Point(pointX, pointY);//控制点

        ValueAnimator valueAnimator = ValueAnimator.ofObject(new BizierEvaluator(controllPoint), startPosition, endPosition);
        valueAnimator.setInterpolator(new LinearInterpolator());
        mImg.setPivotX(mImg.getWidth() / 2);
        mImg.setPivotY(mImg.getHeight() / 2);
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(mImg, "scaleX", 0.5f, 1.0f, 1.1f);
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(mImg, "scaleY", 0.5f, 1.0f, 1.1f);
        ObjectAnimator animator4 = ObjectAnimator.ofFloat(mImg, "rotation", 0f, new Random().nextBoolean() ? -new Random().nextInt(180) : new Random().nextInt(180));
        AnimatorSet mSet = new AnimatorSet();
        mSet.setDuration(1000);
        mSet.playTogether(animator1, animator3, valueAnimator, animator4);
        mSet.start();

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Point point = (Point) valueAnimator.getAnimatedValue();
                mImg.setX(point.x);
                mImg.setY(point.y);
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (imageViewsList.size() > 50) {
                    Iterator<ImageView> iterator = imageViewsList.iterator();
                    while (iterator.hasNext() && imageViewsList.size() > 30) {
                        mRootView.removeView(iterator.next());
                        iterator.remove();
                    }
                }
            }
        });
    }

    public static int[] getPosition(Context context, View view) {
        int[] loc = new int[2];
        if (view != null) {
            view.getLocationInWindow(loc);
        }
        if (loc[0] != 0 && loc[1] != 0) {
            return loc;
        } else {
            loc[0] = new Random().nextBoolean() ? 0 : DensityUtil.getScreenWidth(context);
            loc[1] = new Random().nextInt(DensityUtil.getScreenHeight(context) / 2) + DensityUtil.getScreenHeight(context) / 2;
        }
        return loc;
    }

    /**
     * @param iv
     */
    public static void addShopCartAnimation(ViewGroup rl, View iv, View targetView,int viewIndex) {
        float[] mCurrentPosition = new float[2];
        final ImageView goods = new ImageView(rl.getContext());
        goods.setImageResource(R.drawable.icon_audio_logo);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(DensityUtil.dip2px(rl.getContext(), 30), DensityUtil.dip2px(rl.getContext(), 30));
        rl.addView(goods, viewIndex, params);
        int[] parentLocation = new int[2];
        rl.getLocationInWindow(parentLocation);
        int startLoc[] = new int[2];
        iv.getLocationInWindow(startLoc);
        float startX = startLoc[0] - parentLocation[0] + iv.getWidth() / 2;
        float startY = startLoc[1] - parentLocation[1] + iv.getHeight() / 2;

        int[] index = new int[2];
        targetView.getLocationInWindow(index);
        int width = targetView.getWidth();
        index[0] = index[0] + width / 2;
        float toX = index[0] - parentLocation[0];
        float toY = index[1] - parentLocation[1];
        //开始绘制贝塞尔曲线
        Path path = new Path();
        path.moveTo(startX, startY);
        path.quadTo((startX + toX) / 2, startY, toX, toY);
        // 如果是true，path会形成一个闭环
        PathMeasure mPathMeasure = new PathMeasure(path, false);
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, mPathMeasure.getLength());
        valueAnimator.setDuration(1000);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                mPathMeasure.getPosTan(value, mCurrentPosition, null);//mCurrentPosition此时就是中间距离点的坐标值
                // 移动的商品图片（动画图片）的坐标设置为该中间点的坐标
                goods.setTranslationX(mCurrentPosition[0]);
                goods.setTranslationY(mCurrentPosition[1]);

            }
        });
        valueAnimator.start();
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                rl.removeView(goods);
            }
        });
    }
}
