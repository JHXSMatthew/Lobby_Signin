package com.mcndsj.lobby_signin.managers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Matthew on 3/07/2016.
 */
public class ThreadManager {



    private static ThreadManager manager;
    private ExecutorService pool;
    public ThreadManager(){
        pool = Executors.newCachedThreadPool();
    }

    public void runTask(Runnable runnable){
        pool.execute(runnable);
    }

    public static ThreadManager get(){
        if(manager == null){
            manager = new ThreadManager();
        }
        return manager;
    }
}
