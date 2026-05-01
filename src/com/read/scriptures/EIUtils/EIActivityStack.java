
package com.read.scriptures.EIUtils;

import android.app.Activity;
import android.os.Process;


import java.util.Iterator;
import java.util.Stack;

public class EIActivityStack {
    private static String TAG = EIActivityStack.class.getSimpleName();
    private static Stack<Activity> mActivityStack;
    private static final EIActivityStack mInstance = new EIActivityStack();

    private EIActivityStack() {
    }

    public static EIActivityStack getInstance() {
        return mInstance;
    }

    public void addActivity(Activity var1) {
        if (mActivityStack == null) {
            mActivityStack = new Stack();
        }

        mActivityStack.push(var1);
    }

    public void exit() {
        this.finishAllActivity();
        try {
            Process.killProcess(Process.myPid());
            System.exit(0);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void finishActivity() {
        this.removeActivity((Activity)mActivityStack.lastElement());
    }

    public void finishActivity(Activity var1) {
        if (var1 != null) {
            mActivityStack.remove(var1);
            var1.finish();
        }

    }

    public void finishActivity(Class<?> var1) {
        Iterator var2 = mActivityStack.iterator();

        while(var2.hasNext()) {
            Activity var3 = (Activity)var2.next();
            if (var3.getClass().equals(var1)) {
                this.finishActivity(var3);
            }
        }

    }

    public void finishAllActivity() {
        while(!mActivityStack.isEmpty()) {
            this.finishActivity((Activity)mActivityStack.pop());
        }

        mActivityStack.clear();
    }

    public Activity getCurrentActivity() {
        return (Activity)mActivityStack.lastElement();
    }

    public void removeActivity(Activity var1) {
        if (var1 != null) {
            mActivityStack.remove(var1);
        }

    }
}


