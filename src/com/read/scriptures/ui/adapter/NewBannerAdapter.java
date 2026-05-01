package com.read.scriptures.ui.adapter;

import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.music.player.lib.util.XToast;
import com.read.scriptures.R;
import com.read.scriptures.model.Advertisement;
import com.read.scriptures.util.DensityUtil;
import com.read.scriptures.util.MyVideoPlay;
import com.read.scriptures.util.NetSocietyShare;
import com.read.scriptures.util.PicassoUtils;
import com.read.scriptures.util.StringUtil;
import com.read.scriptures.util.SystemUtils;
import com.shuyu.gsyvideoplayer.utils.CommonUtil;
import com.shuyu.gsyvideoplayer.utils.GSYVideoHelper;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.List;

public class NewBannerAdapter extends RecyclerView.Adapter {
    public final static String TAG = "NewBannerAdapter";

    private GSYVideoHelper smallVideoHelper;
    private GSYVideoHelper.GSYVideoHelperBuilder gsySmallVideoHelperBuilder;

    public void setDataBeans(List<Advertisement> dataBeans) {
        this.dataBeans = dataBeans;
        notifyDataSetChanged();
    }

    List<Advertisement> dataBeans = new ArrayList<>();

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0: //图片
                return new NewBannerImageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_banner_image, parent, false));
            case 1://视频
                return new NewBannerVideoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_banner_video, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int p) {
        int position = p % dataBeans.size();
        if (holder instanceof NewBannerImageViewHolder) {
            NewBannerImageViewHolder h = (NewBannerImageViewHolder) holder;
            if (TextUtils.isEmpty(dataBeans.get(position).getCover())) {
                PicassoUtils.loadImage(h.iv_image, R.drawable.img_banner_default, DensityUtil.getScreenWidth(holder.itemView.getContext()), CommonUtil.getScreenWidth(holder.itemView.getContext()) * 9 / 16);
            } else {
                PicassoUtils.loadBannerImage(h.iv_image, dataBeans.get(position).getCover(), R.drawable.img_banner_default, DensityUtil.getScreenWidth(holder.itemView.getContext()), CommonUtil.getScreenWidth(holder.itemView.getContext()) * 9 / 16);
            }
            h.tv_title.setText(dataBeans.get(position).getName());
            h.tv_title.setVisibility(TextUtils.isEmpty(dataBeans.get(position).getName()) || dataBeans.get(position).getType() == 0 ? View.GONE : View.VISIBLE);

            h.tv_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onTitleClick(v.getContext(), dataBeans.get(position).getType(), dataBeans.get(position).getUrl(), dataBeans.get(position).getImage());
                }
            });
            if (bannerListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bannerListener.OnBannerClick(position);
                    }
                });
            }
        } else if (holder instanceof NewBannerVideoViewHolder) {
            NewBannerVideoViewHolder h = (NewBannerVideoViewHolder) holder;
            onBindVideo(h, position);
        } else {
        }
    }

    private void onTitleClick(final Context context, int type, final String url, final String userName) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        try {
            if (url.contains("http") && type == 1) {
                //弹框
                final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AppTheme_Dialog_Alert);
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
                        SystemUtils.jumpToUrl(context, url);
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(true);
                dialog.setCancelable(true);
                dialog.show();
            } else if (type == 3) {//小程序
                //微信小程序
                if (StringUtil.isEmpty(userName)) {
                    XToast.showToast(context, "暂无可打开信息");
                    return;
                }
                String strPackageName = "com.tencent.mm";
                if (context == null) {
                    return;
                }
                if (!NetSocietyShare.goWechatMini(context, strPackageName, userName, url)) {
                    XToast.showToast(context, "未安装此应用");
                }
            }
        } catch (final Exception e) {

        }
    }

    private void onTitleVideoClick(final Context context, int type, final String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        try {
            if (url.contains("http") && type == 1) {
                //弹框
                final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AppTheme_Dialog_Alert);
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
                        SystemUtils.jumpToUrl(context, url);
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(true);
                dialog.setCancelable(true);
                dialog.show();
            }
        } catch (final Exception e) {

        }
    }

    private void onBindVideo(NewBannerVideoViewHolder h, final int position) {
        h.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        h.tv_title.setText(dataBeans.get(position).getName());
        h.tv_title.setVisibility(TextUtils.isEmpty(dataBeans.get(position).getName()) ? View.GONE : View.VISIBLE);
        smallVideoHelper.addVideoPlayer(position, h.imageView, TAG, h.list_item_container, h.list_item_btn);
        StandardGSYVideoPlayer gsyVideoPlayer = smallVideoHelper.getGsyVideoPlayer();
        if (gsyVideoPlayer instanceof MyVideoPlay) {
            ((MyVideoPlay) gsyVideoPlayer).setCover(h.list_item_container, h.imageView, h.list_item_btn, dataBeans.get(position).getCover());
        }
        h.tv_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTitleVideoClick(v.getContext(), dataBeans.get(position).getType(), dataBeans.get(position).getUrl());
            }
        });
        h.list_item_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                smallVideoHelper.setPlayPositionAndTag(position, TAG);
                notifyDataSetChanged();
                gsySmallVideoHelperBuilder.setVideoTitle("").setUrl(dataBeans.get(position).getImage());
//                gsySmallVideoHelperBuilder.setVideoTitle("").setUrl("http://58.255.174.37:7782/list_3459636928_2467694288_0.m3u8");
                smallVideoHelper.startPlay();
                smallVideoHelper.getGsyVideoPlayer().getBackButton().setImageResource(R.drawable.common_back_selector);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataBeans == null || dataBeans.isEmpty() ? 0 : dataBeans.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (dataBeans.size() == 0) {
            return -1;
        }
        return dataBeans.get(position % dataBeans.size()).getIs_image();
    }

    class NewBannerImageViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_image;
        TextView tv_title;
        View rl_main;

        public NewBannerImageViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_image = itemView.findViewById(R.id.iv_image);
            tv_title = itemView.findViewById(R.id.tv_title);
            rl_main = itemView.findViewById(R.id.rl_main);
            ViewGroup.LayoutParams layoutParams = rl_main.getLayoutParams();
            layoutParams.height = CommonUtil.getScreenWidth(itemView.getContext()) * 9 / 16;
            rl_main.setLayoutParams(layoutParams);
        }
    }

    class NewBannerVideoViewHolder extends RecyclerView.ViewHolder {

        View rl_main;
        FrameLayout list_item_container;
        ImageView list_item_btn;
        TextView tv_title;
        ImageView imageView;


        public NewBannerVideoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = new ImageView(itemView.getContext());
            //增加封面
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            list_item_container = itemView.findViewById(R.id.list_item_container);
            list_item_btn = itemView.findViewById(R.id.list_item_btn);
            tv_title = itemView.findViewById(R.id.tv_title);
            rl_main = itemView.findViewById(R.id.rl_main);

            ViewGroup.LayoutParams layoutParams = rl_main.getLayoutParams();
            layoutParams.height = CommonUtil.getScreenWidth(itemView.getContext()) * 9 / 16;
            rl_main.setLayoutParams(layoutParams);
        }
    }

    public void setVideoHelper(GSYVideoHelper smallVideoHelper, GSYVideoHelper.GSYVideoHelperBuilder gsySmallVideoHelperBuilder) {
        this.smallVideoHelper = smallVideoHelper;
        this.gsySmallVideoHelperBuilder = gsySmallVideoHelperBuilder;
    }

    OnBannerListener bannerListener;

    public void setOnBannerListener(OnBannerListener bannerListener) {
        this.bannerListener = bannerListener;
    }
}
