package com.read.scriptures.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.music.player.lib.util.XToast;
import com.read.scriptures.manager.AccountManager;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMVideo;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.media.UMusic;

public class UmShareUtils {

    public static void shareMusic(Activity context, String musicUrl, String title, String content, String picUrl, String url) {
        UMusic music = new UMusic(musicUrl);//音乐的播放链接
        music.setTitle(title);//音乐的标题
        UMImage image = new UMImage(context, picUrl);
        music.setThumb(image);//音乐的缩略图
        music.setDescription(content);//音乐的描述
        String link = AccountManager.getInstance().getAudioShareLink();
        music.setmTargetUrl(TextUtils.isEmpty(link) ? "https://www.baidu.com" : link);//音乐的跳转链接
        new ShareAction(context)
                .withMedia(music)
                .setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ)
                .setCallback(new UMShareListener() {
                    @Override
                    public void onStart(SHARE_MEDIA share_media) {

                    }

                    @Override
                    public void onResult(SHARE_MEDIA share_media) {
                        XToast.showToast(context, "分享成功！");
                    }

                    @Override
                    public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                        XToast.showToast(context, throwable.getMessage());
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA share_media) {

                    }
                }).open();
    }

    public static void shareUrl(Activity context, String title, String content, String picUrl, String url) {
        UMWeb web = new UMWeb(url);
        web.setTitle(title);//标题
        UMImage image = new UMImage(context, picUrl);
        web.setThumb(image);//音乐的缩略图
        web.setDescription(content);//描述

        new ShareAction(context)
                .withMedia(web)
                .setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ)
                .setCallback(new UMShareListener() {
                    @Override
                    public void onStart(SHARE_MEDIA share_media) {

                    }

                    @Override
                    public void onResult(SHARE_MEDIA share_media) {
                        XToast.showToast(context, "分享成功");
                    }

                    @Override
                    public void onError(SHARE_MEDIA share_media, Throwable throwable) {

                    }

                    @Override
                    public void onCancel(SHARE_MEDIA share_media) {

                    }
                }).open();
    }

    public static void shareText(Activity context, String title) {
        title = title.replaceAll("〖(.*?)〗", "");
        title = title.replaceAll("(?<=\\[)(.*?)(?=])", "");
        title = title.replaceAll("(?<=\\{)[^}]*(?=\\})", "");
        title = title.replaceAll("\\[\\]", "");
        title = title.replaceAll("\\{\\}", "");
        String finalTitle = title;
        new ShareAction(context)
                .withText(title)
                .setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ)
                .setCallback(new UMShareListener() {
                    @Override
                    public void onStart(SHARE_MEDIA share_media) {

                    }

                    @Override
                    public void onResult(SHARE_MEDIA share_media) {
                        XToast.showToast(context, "分享成功");
                    }

                    @Override
                    public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                        if (throwable.getMessage().contains("该平台不支持纯文本分享")) {
                            shareQQ(context, finalTitle);
                        }
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA share_media) {

                    }
                }).open();
    }

    /**
     * @param mContext 上下文
     * @param content  要分享的文本
     */
    public static void shareQQ(Context mContext, String content) {
        if (PlatformUtil.isQQClientAvailable(mContext)) {
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
            intent.putExtra(Intent.EXTRA_TEXT, content);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(new ComponentName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.JumpActivity"));
            mContext.startActivity(intent);
        } else {
            Toast.makeText(mContext, "您需要安装QQ客户端", Toast.LENGTH_LONG).show();
        }
    }

    public static void shareVideo(Activity mContext,String title, String content, String url, String picUrl) {
        UMVideo web = new UMVideo(url);
        web.setTitle(title);//标题
        UMImage image = new UMImage(mContext, picUrl);
        web.setThumb(image);//音乐的缩略图
        web.setDescription(content);//描述

        new ShareAction(mContext)
                .withMedia(web)
                .setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ)
                .setCallback(new UMShareListener() {
                    @Override
                    public void onStart(SHARE_MEDIA share_media) {

                    }

                    @Override
                    public void onResult(SHARE_MEDIA share_media) {
                        XToast.showToast(mContext, "分享成功");
                    }

                    @Override
                    public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                        XToast.showToast(mContext, throwable.getMessage());
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA share_media) {

                    }
                }).open();

    }
}
