package com.read.scriptures.util;

import android.util.Log;
import android.widget.ProgressBar;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadUtil {
    /**
     * 可缓存线程池·
     */
    private static ExecutorService fixThreadPool = Executors.newFixedThreadPool(20);

    /**
     * 启动新线程
     *
     * @param runnable
     */
    public static void execute(Runnable runnable) {
        fixThreadPool.execute(runnable);
    }

    /**
     * 只是关闭了提交通道，用submit()是无效的；而内部该怎么跑还是怎么跑，跑完再停。
     */
    public static void exit() {
        fixThreadPool.shutdown();
    }

    public static void doOnOtherThread(final Runnable action) {
        fixThreadPool.execute(action);
    }

    public static class ProgressThread extends Thread {
        private boolean stop;
        private int wait;
        private ProgressBar progressBar;
        private int index;
        private int space = 10;

        public ProgressThread(int wait, ProgressBar progressBar) {
            super();
            this.wait = wait;
            this.progressBar = progressBar;
            stop = false;
            index = 0;
        }

        public ProgressThread(int wait, ProgressBar progressBar, int index, int space) {
            super();
            this.wait = wait;
            this.progressBar = progressBar;
            this.index = index;
            this.space = space;
        }

        public boolean isStop() {
            return stop;
        }

        public void setStop(boolean stop) {
            this.stop = stop;
        }

        public int getWait() {
            return wait;
        }

        public void setWait(int wait) {
            this.wait = wait;
        }

        @Override
        public void run() {
            final int max = progressBar.getMax();
            Log.i("test", "max:" + max);
            while (!stop && index < max) {
                final int progress = index;
                progressBar.post(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setProgress(progress);
                    }
                });
                // Log.i("test", "index:" + index);
                int add = max / (100 + index);
                add = Math.max(add, max / 1000);
                if (index < max - add - 1) {
                    index += add;
                }
                try {
                    Thread.sleep(space);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Log.i("test", "start stop index:" + index);
            int temp = index;
            while (index < max) {
                final int progress = index;
                progressBar.post(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setProgress(progress);
                    }
                });
                index += (max - temp) / (wait / 33 + 1) + 5;
                try {
                    Thread.sleep(33);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Log.i("test", "last index:" + index);
            progressBar.post(new Runnable() {
                @Override
                public void run() {
                    progressBar.setProgress(max);
                }
            });
        }

    }
}
