package com.newland.intelligentserver.sdk;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
/**
 * Created by Administrator on 2019/6/20.
 */
public class SdkExecutors {

    public static void setThreadPoolRunning(boolean threadPoolRunning) {
        SdkExecutors.threadPoolRunning = threadPoolRunning;
    }
    public static boolean isThreadPoolRunning() {
        return threadPoolRunning;
    }
    private static boolean threadPoolRunning = true;

    private SdkExecutors(){}
    private static final ExecutorService mThreadPool = SdkExecutors.newCachedThreadPool(5);
    public static ExecutorService getThreadPoolInstance(){
        return mThreadPool;
    }

    private static final ExecutorService mFixedThreadPool = Executors.newFixedThreadPool(5);
    public static ExecutorService getFixedThreadPoolInstance(){
        return mFixedThreadPool;
    }

    private static ThreadPoolExecutor newCachedThreadPool(int corePoolSize) {
        if(corePoolSize < 0)
            return null;
        return new ThreadPoolExecutor(corePoolSize,Integer.MAX_VALUE,
                3L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>());
    }
}
