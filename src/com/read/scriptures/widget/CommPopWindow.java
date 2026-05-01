package com.read.scriptures.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.read.scriptures.R;


public class CommPopWindow
{
	private Context context;
	private View view;
	private TextView tv;
	private ImageView img;
	private PopupWindow pop;
	private RotateAnimation rotateAnimation;
	private static String tvContent;

	public CommPopWindow(Context context, int id)
	{
		this.context = context;
		tvContent = context.getResources().getString(id);
		initPop(context);

	}

	public Context getContext()
	{
		return context;
	}

	public View getView()
	{
		return view;
	}

	public TextView getTv()
	{
		return tv;
	}

	public ImageView getImg()
	{
		return img;
	}

	public PopupWindow getPop()
	{
		return pop;
	}

	public boolean isShowing()
	{
		return pop.isShowing();
	}

	public RotateAnimation getRotateAnimation()
	{
		return rotateAnimation;
	}

	private void initPop(Context context)
	{
		view = LayoutInflater.from(context).inflate(R.layout.pop_loading, null);
		tv = (TextView) view.findViewById(R.id.tv_tag);
		img =(ImageView) view.findViewById(R.id.image_loading);
		pop = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
		pop.setTouchable(true);
		pop.setFocusable(true);
		ColorDrawable draw = new ColorDrawable(0x00000000);
		pop.setBackgroundDrawable(draw);
		rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		rotateAnimation.setDuration(1000);
		rotateAnimation.setRepeatCount(-1);
		rotateAnimation.setInterpolator(new LinearInterpolator());
		pop.setOnDismissListener(new PopupWindow.OnDismissListener()
		{
			@Override
			public void onDismiss()
			{
				img.clearAnimation();
				if(listener != null)
					listener.onDismiss();
			}
		});
	}

	public void showPop(View view)
	{
		tv.setText(tvContent);
		pop.showAtLocation(view, Gravity.CENTER, 0, 0);
		img.startAnimation(rotateAnimation);
	}

	public static void setContent(String content)
	{
		tvContent = content;
	}
	public void dismissPop()
	{
		if(pop.isShowing())
		{
			pop.dismiss();
			if(listener != null)
				listener.onDismiss();
		}
	}

	public void setOnDismissListener(setOnDismissListener lintener)
	{
		this.listener = lintener;
	}

	private setOnDismissListener listener;

	public interface setOnDismissListener
	{
		void onDismiss();
	}
}
