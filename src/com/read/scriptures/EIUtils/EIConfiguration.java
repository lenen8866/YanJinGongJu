
package com.read.scriptures.EIUtils;

import android.content.Context;

public final class EIConfiguration {
    final String appCacheFilePath;
    final String appDownloadFilePath;
    final String appLoggerFilePath;
    final String dbAccount;
    final String dbPassword;
    final int dbVersion;
    final boolean debugLogs;
    final boolean debugMode;

    private EIConfiguration(Builder var1) {
        this.debugMode = var1.debugMode;
        this.debugLogs = var1.debugLogs;
        this.dbVersion = var1.dbVersion;
        this.dbAccount = var1.dbAccount;
        this.dbPassword = var1.dbPassword;
        this.appLoggerFilePath = var1.appLoggerFilePath;
        this.appCacheFilePath = var1.appCacheFilePath;
        this.appDownloadFilePath = var1.appDownloadFilePath;
    }

    public String getAppCacheFilePath() {
        return this.appCacheFilePath;
    }

    public String getAppDownloadFilePath() {
        return this.appDownloadFilePath;
    }

    public String getAppLoggerFilePath() {
        return this.appLoggerFilePath;
    }

    public String getDbAccount() {
        return this.dbAccount;
    }

    public String getDbPassword() {
        return this.dbPassword;
    }

    public int getDbVersion() {
        return this.dbVersion;
    }

    public boolean isDebugLogs() {
        return this.debugLogs;
    }

    public boolean isDebugMode() {
        return this.debugMode;
    }

    public static class Builder {
        private String appCacheFilePath;
        private String appDownloadFilePath;
        private String appLoggerFilePath;
        private Context context;
        private String dbAccount;
        private String dbPassword;
        private int dbVersion;
        private boolean debugLogs = true;
        private boolean debugMode = false;

        public Builder(Context var1) {
            this.debugMode = false;
            this.debugLogs = false;
            this.dbVersion = 1;
            this.dbAccount = null;
            this.dbPassword = null;
            this.appCacheFilePath = null;
            this.appDownloadFilePath = null;
            this.context = var1.getApplicationContext();
        }

        private void initEmptyFieldsWithDefaultValues() {
            if (this.dbAccount == null) {
                this.dbAccount = "65191514";
            }

            if (this.dbAccount == null) {
                this.dbPassword = "25376475";
            }

            if (this.appLoggerFilePath == null) {
                this.appLoggerFilePath = "/" + this.context.getPackageName() + "/log/";
            }

            if (this.appLoggerFilePath == null) {
                this.appCacheFilePath = "/" + this.context.getPackageName() + "/cache/";
            }

            if (this.appLoggerFilePath == null) {
                this.appDownloadFilePath = "/" + this.context.getPackageName() + "/download/";
            }

        }

        public Builder appCacheFilePath(String var1) {
            this.appCacheFilePath = var1;
            return this;
        }

        public Builder appDownloadFilePath(String var1) {
            this.appDownloadFilePath = var1;
            return this;
        }

        public Builder appLoggerFilePath(String var1) {
            this.appLoggerFilePath = var1;
            return this;
        }

        public EIConfiguration build() {
            this.initEmptyFieldsWithDefaultValues();
            return new EIConfiguration(this);
        }

        public Builder dbAccount(String var1) {
            this.dbAccount = var1;
            return this;
        }

        public Builder dbPassword(String var1) {
            this.dbPassword = var1;
            return this;
        }

        public Builder dbVersion(int var1) {
            this.dbVersion = var1;
            return this;
        }

        public Builder debugLogs() {
            this.debugLogs = true;
            return this;
        }

        public Builder debugMode() {
            this.debugMode = true;
            return this;
        }
    }
}

