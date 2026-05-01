//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.read.scriptures.EIUtils;

import android.os.Looper;


import com.facebook.imagepipeline.core.PriorityThreadFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;

public class ThreadPool {
    private static final AtomicLong SEQ = new AtomicLong(0L);
    private ThreadPool.ResourceCounter mCpuCounter;
    private final Executor mExecutor;
    private ThreadPool.ResourceCounter mNetworkCounter;

    public ThreadPool() {
        this("ThreadPool", 4, 8);
    }

    public ThreadPool(String var1, int var2, int var3) {
        this(var1, var2, var3, new PriorityBlockingQueue());
    }

    public ThreadPool(String var1, int var2, int var3, BlockingQueue<Runnable> var4) {
        this.mCpuCounter = new ThreadPool.ResourceCounter(2);
        this.mNetworkCounter = new ThreadPool.ResourceCounter(2);
        if (var2 <= 0) {
            var2 = 1;
        }

        if (var3 <= var2) {
            var3 = var2;
        }

        PriorityThreadFactory var5 = new PriorityThreadFactory(10);
        TimeUnit var6 = TimeUnit.SECONDS;
        this.mExecutor = new ThreadPoolExecutor(var2, var3, 10L, var6, var4, var5);
    }

    private <T> ThreadPool.Worker<T> generateWorker(ThreadPool.Job<T> var1, FutureListener<T> var2, ThreadPool.Priority var3) {
        switch(var3) {
        case HIGH:
            return new ThreadPool.PriorityWorker(var1, var2, var3.priorityInt, false);
        case LOW:
            return new ThreadPool.PriorityWorker(var1, var2, var3.priorityInt, false);
        case NORMAL:
            return new ThreadPool.PriorityWorker(var1, var2, var3.priorityInt, true);
        default:
            return new ThreadPool.PriorityWorker(var1, var2, var3.priorityInt, false);
        }
    }

    public static ThreadPool getInstance() {
        return ThreadPool.InstanceHolder.INSTANCE;
    }

    public static void runOnNonUIThread(final Runnable var0) {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            getInstance().submit(new ThreadPool.Job<Object>() {
                public Object run(ThreadPool.JobContext var1) {
                    var0.run();
                    return null;
                }
            });
        } else {
            var0.run();
        }
    }

    public <T> Future<T> submit(ThreadPool.Job<T> var1) {
        return this.submit(var1, (FutureListener)null, ThreadPool.Priority.NORMAL);
    }

    public <T> Future<T> submit(ThreadPool.Job<T> var1, FutureListener<T> var2) {
        return this.submit(var1, var2, ThreadPool.Priority.NORMAL);
    }

    public <T> Future<T> submit(ThreadPool.Job<T> var1, FutureListener<T> var2, ThreadPool.Priority var3) {
        ThreadPool.Worker var4 = this.generateWorker(var1, var2, var3);
        this.mExecutor.execute(var4);
        return var4;
    }

    public <T> Future<T> submit(ThreadPool.Job<T> var1, ThreadPool.Priority var2) {
        return this.submit(var1, (FutureListener)null, var2);
    }

    private static class InstanceHolder {
        public static final ThreadPool INSTANCE = new ThreadPool();

        private InstanceHolder() {
        }
    }

    public interface Job<T> {
        T run(ThreadPool.JobContext var1);
    }

    public interface JobContext {
        boolean isCancelled();

        boolean setMode(int var1);
    }

    private static class JobContextStub implements ThreadPool.JobContext {
        private JobContextStub() {
        }

        public boolean isCancelled() {
            return false;
        }

        public boolean setMode(int var1) {
            return true;
        }
    }

    public static enum Priority {
        HIGH(3),
        LOW(1),
        NORMAL(2);

        int priorityInt;

        static {
            ThreadPool.Priority[] var0 = new ThreadPool.Priority[]{LOW, NORMAL, HIGH};
        }

        private Priority(int var3) {
            this.priorityInt = var3;
        }
    }

    private class PriorityWorker<T> extends ThreadPool.Worker<T> implements Comparable<ThreadPool.PriorityWorker<T>> {
        private final boolean mFilo;
        private final int mPriority;
        private final long mSeqNum;

        public PriorityWorker(ThreadPool.Job<T> var2, FutureListener<T> var3, int var4, boolean var5) {
            super(var2, var3);
            this.mPriority = var4;
            this.mFilo = var5;
            this.mSeqNum = ThreadPool.SEQ.getAndIncrement();
        }

        private int subCompareTo(ThreadPool.PriorityWorker<T> var1) {
            int var2;
            if (this.mSeqNum > var1.mSeqNum) {
                var2 = 1;
            } else if (this.mSeqNum < var1.mSeqNum) {
                var2 = -1;
            } else {
                var2 = 0;
            }

            if (this.mFilo) {
                var2 = -var2;
            }

            return var2;
        }

        public int compareTo(ThreadPool.PriorityWorker<T> var1) {
            if (this.mPriority < var1.mPriority) {
                return 1;
            } else {
                return this.mPriority > var1.mPriority ? -1 : this.subCompareTo(var1);
            }
        }
    }

    private static class ResourceCounter {
        public int value;

        public ResourceCounter(int var1) {
            this.value = var1;
        }
    }

    private class Worker<T> implements Runnable, Future<T>, ThreadPool.JobContext {
        private com.read.scriptures.EIUtils.Future.CancelListener mCancelListener;
        private volatile boolean mIsCancelled;
        private boolean mIsDone;
        private ThreadPool.Job<T> mJob;
        private FutureListener<T> mListener;
        private int mMode;

        public Worker(ThreadPool.Job<T> var2, FutureListener<T> var3) {
            this.mJob = var2;
            this.mListener = var3;
        }

        private boolean acquireResource(ThreadPool.ResourceCounter param1) {
            // $FF: Couldn't be decompiled
            return false;
        }

        private ThreadPool.ResourceCounter modeToCounter(int var1) {
            if (var1 == 1) {
                return ThreadPool.this.mCpuCounter;
            } else {
                return var1 == 2 ? ThreadPool.this.mNetworkCounter : null;
            }
        }

        private void releaseResource(ThreadPool.ResourceCounter param1) {
            // $FF: Couldn't be decompiled
        }

        public void cancel() {
            // $FF: Couldn't be decompiled
        }

        public T get() {
            // $FF: Couldn't be decompiled
            return null;
        }

        @Override
        public T get(long timeout, TimeUnit unit) throws ExecutionException, InterruptedException, TimeoutException {
            return null;
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return false;
        }

        public boolean isCancelled() {
            return this.mIsCancelled;
        }

        public boolean isDone() {
            synchronized(this){}

            boolean var2;
            try {
                var2 = this.mIsDone;
            } finally {
                ;
            }

            return var2;
        }

        public void run() {
            // $FF: Couldn't be decompiled
        }

        public void setCancelListener(com.read.scriptures.EIUtils.Future.CancelListener var1) {
            synchronized(this){}

            try {
                this.mCancelListener = var1;
                if (this.mIsCancelled && this.mCancelListener != null) {
                    this.mCancelListener.onCancel();
                }
            } finally {
                ;
            }

        }

        public boolean setMode(int var1) {
            ThreadPool.ResourceCounter var2 = this.modeToCounter(this.mMode);
            if (var2 != null) {
                this.releaseResource(var2);
            }

            this.mMode = 0;
            ThreadPool.ResourceCounter var3 = this.modeToCounter(var1);
            if (var3 != null) {
                if (!this.acquireResource(var3)) {
                    return false;
                }

                this.mMode = var1;
            }

            return true;
        }

        public void waitDone() {
            this.get();
        }
    }
}
