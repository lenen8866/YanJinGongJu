//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.read.scriptures.EIUtils;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;

import com.read.scriptures.util.PreferencesUtils;

import org.litepal.LitePalApplication;

public abstract class EIApplication extends LitePalApplication {
    private static final String TAG = EIApplication.class.getSimpleName();
    private static EIApplication mInstance;
    private int mAppCurVersionCode;
    private String mAppCurVersionName;
    private int mAppLastVersionCode;
    private EIConfiguration mEIConfiguration;
    private boolean mNetworkAvailable = true;
    private EIActivityStack mStackManager;

    public EIApplication() {
    }

    public static <T extends EIApplication> T getInstance() {
        return (T) mInstance;
    }

    private void initActivityStack() {
        this.mStackManager = EIActivityStack.getInstance();
    }

    private void initAppVsersionInfo() {
        this.mAppLastVersionCode = PreferencesUtils.getInt(this,"app_version_code",1);

        try {
            PackageInfo var2 = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            this.mAppCurVersionCode = var2.versionCode;
            this.mAppCurVersionName = var2.versionName;
        } catch (NameNotFoundException var3) {
            this.mAppCurVersionCode = this.mAppLastVersionCode;
            this.mAppCurVersionName = "";
        }
    }

    public abstract EIConfiguration buildEIConfiguration();

    public void exitApp() {
        this.mStackManager.exit();
    }

    public EIActivityStack getActivityStackManager() {
        if (this.mStackManager == null) {
            this.mStackManager = EIActivityStack.getInstance();
        }

        return this.mStackManager;
    }

    public EIConfiguration getEIConfiguration() {
        if (this.mEIConfiguration == null) {
            this.mEIConfiguration = (new EIConfiguration.Builder(this)).build();
        }

        return this.mEIConfiguration;
    }

    public PackageInfo getPackageInfo() {
        PackageInfo var2;
        label16: {
            PackageInfo var3;
            try {
                var3 = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            } catch (NameNotFoundException var4) {
                var4.printStackTrace(System.err);
                var2 = null;
                break label16;
            }

            var2 = var3;
        }

        if (var2 == null) {
            var2 = new PackageInfo();
        }

        return var2;
    }

    public String getVersionName() {
        return this.mAppCurVersionName;
    }

    public boolean isNetworkAvailable() {
        return this.mNetworkAvailable;
    }

    public boolean isNewInstall() {
        return this.mAppLastVersionCode != this.mAppCurVersionCode;
    }

    public void onConfigurationChanged(Configuration var1) {
        super.onConfigurationChanged(var1);
    }

    public void onCreate() {
        super.onCreate();
        mInstance = this;
        this.mEIConfiguration = this.buildEIConfiguration();
        this.initActivityStack();
        this.initAppVsersionInfo();
    }

    public void onLowMemory() {
        super.onLowMemory();
    }

    public void onTerminate() {
        super.onTerminate();
    }

}
