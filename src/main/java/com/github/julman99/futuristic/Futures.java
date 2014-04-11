package com.github.julman99.futuristic;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @autor: julio
 */
public class Futures {

    private static final Executor EXECUTOR = Executors.newCachedThreadPool();

    public static <T> FutureWithTrigger<T> withTrigger(){
        return new FutureWithTrigger<T>();
    };

    public static <T> Future<T> withValue(T value){
        return new FutureWithValue<>(value);
    }

    public static <T> Future<T> withException(Exception exception){
        return new FutureWithException<>(exception);
    }

    public static <T> Future<T> withCallable(Callable<T> callable){
        return withCallable(EXECUTOR, callable);
    }

    public static <T> Future<T> withCallable(Executor executor, Callable<T> callable){
        FutureWithTrigger<T> futureWithTrigger = new FutureWithTrigger<>();
        executor.execute(() -> {
            try {
                T res = callable.call();
                futureWithTrigger.getTrigger().completed(res);
            } catch (Exception ex) {
                futureWithTrigger.getTrigger().failed(ex);
            }
        });
        return futureWithTrigger.getFuture();
    }
}
