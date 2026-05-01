package com.read.scriptures.audio;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.read.scriptures.R;
import com.read.scriptures.util.DensityUtil;
import com.read.scriptures.view.largeimage.LargeImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 *
 */
public class ShowImageDialog extends FG_Dialog_Base {

    private LargeImageView largeImageView;
//    private TextView tv_tip;
    private String img;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setData(String img) {
        this.img = img;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
//        StatusBarUtils.setColor(getActivity(),Color.RED);
        // 设置宽度为屏宽, 靠近屏幕底部。
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.CENTER; // 紧贴底部
        lp.width = WindowManager.LayoutParams.MATCH_PARENT; // 宽度持平
        lp.height = WindowManager.LayoutParams.MATCH_PARENT; // 宽度持平
        window.setAttributes(lp);
        return dialog;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    protected View dialogView() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.dialog_show_image, null);
        largeImageView = view.findViewById(R.id.liv_image);
//        tv_tip = view.findViewById(R.id.tv_tip);
        loadImg(img);

        largeImageView.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                dismiss();
            }
        });
        largeImageView.setScaleCallBack(new LargeImageView.ScaleCallBack() {
            @Override
            public void startScale() {
//                tv_tip.setVisibility(View.GONE);
            }
        });
        return view;
    }


    private void loadImg(String url) {
        Picasso.get().load(TextUtils.isEmpty(url) ? "1" : url).placeholder(R.drawable.icon_play_deault_bg).error(R.drawable.icon_play_deault_bg).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                if (isAdded()) {
                    largeImageView.setImage(bitmap);
                    int height = bitmap.getHeight() * DensityUtil.getScreenWidth(getContext()) / bitmap.getWidth();
                    int screenHeight = DensityUtil.getScreenHeight(getContext());
                    int topSpace = (screenHeight - height) / 2;
//                    tv_tip.setVisibility(View.VISIBLE);
//                    largeImageView.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) tv_tip.getLayoutParams();
//                            layoutParams.bottomMargin = topSpace;
//                            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//                            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//                            tv_tip.setLayoutParams(layoutParams);
//                        }
//                    });
                }
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                if (isAdded()) {
                    largeImageView.setImage(BitmapFactory.decodeResource(getResources(), R.drawable.icon_play_deault_bg));
                }
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
    }
}
