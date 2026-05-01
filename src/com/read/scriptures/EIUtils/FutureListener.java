//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.read.scriptures.EIUtils;


import com.read.scriptures.util.PluginApi;

import java.util.concurrent.Future;

public interface FutureListener<T> {
    @PluginApi
    void onFutureBegin(Future<T> var1);

    @PluginApi
    void onFutureDone(Future<T> var1);
}
