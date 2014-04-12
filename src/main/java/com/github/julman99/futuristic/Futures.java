package com.github.julman99.futuristic;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * @autor: julio
 */
public final class Futures {

    private static final Executor EXECUTOR = Executors.newCachedThreadPool();

    private Futures(){

    }

    public static <T> FutureWithTrigger<T> withTrigger(){
        return new FutureWithTrigger<T>();
    };

    public static <T> Future<T> withCallback(Consumer<Callback<T>> consumer){
        FutureWithTrigger<T> futureWithTrigger = new FutureWithTrigger<>();
        consumer.accept(futureWithTrigger.getTrigger());
        return futureWithTrigger.getFuture();
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
