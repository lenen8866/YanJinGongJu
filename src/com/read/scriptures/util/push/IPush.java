package com.read.scriptures.util.push;

import android.content.Context;

import java.util.List;

/**
 * Created with Android Studio. User : Lim Email: lgmshare@gmail.com Datetime : 2015/3/30 14:33 To
 * change this template use File | Settings | File Templates.
 */
public interface IPush {

    public void init(Context context);

    public void setAlais(Context context, String alais);

    public void addTags(Context context, List<String> tags);

    public void deleteTags(Context context, List<String> tags);

    /**
     * 暂停push接口
     * @param context
     */
    public void pausePush(Context context);

    /**
     * 恢复push接口
     * @param context
     */
    public void resumePush(Context context);
}
