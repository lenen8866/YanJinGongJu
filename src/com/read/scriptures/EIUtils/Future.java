//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.read.scriptures.EIUtils;


import com.read.scriptures.util.PluginApi;

@PluginApi
public interface Future<T> {
    @PluginApi
    void cancel();

    @PluginApi
    T get();

    @PluginApi
    boolean isCancelled();

    @PluginApi
    boolean isDone();

    void setCancelListener(Future.CancelListener var1);

    @PluginApi
    void waitDone();

    public interface CancelListener {
        void onCancel();
    }
}
