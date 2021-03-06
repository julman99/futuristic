package io.futuristic;

import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * @autor: julio
 */
public final class Futures {

    private static final Executor EXECUTOR = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private Futures(){

    }

    /**
     * Returns an object that has a trigger and a future. The future will be "known" when the trigger gets fired
     * @param <T>
     * @return
     */
    public static <T> FutureWithTrigger<T> withTrigger(){
        return new FutureWithTrigger<T>();
    };

    /**
     * Returns a Future object that is triggered by the {@link Callback} passed to the
     * {@link java.util.function.Consumer}
     * @param consumer
     * @param <T>
     * @return
     */
    public static <T> Future<T> withCallback(Consumer<Callback<T>> consumer){
        FutureWithTrigger<T> futureWithTrigger = new FutureWithTrigger<>();
        consumer.accept(futureWithTrigger.getTrigger());
        return futureWithTrigger.getFuture();
    };

    /**
     * Returns a future which value is already known
     * @param value
     * @param <T>
     * @return
     */
    public static <T> Future<T> withValue(T value){
        return new FutureWithValue<>(value);
    }

    /**
     * Returns a future which {@link java.lang.Exception} is already known.
     * @param exception
     * @param <T>
     * @return
     */
    public static <T> Future<T> withException(Exception exception){
        return new FutureWithException<>(exception);
    }

    /**
     * Executes the {@link java.util.concurrent.Callable} in a separate thread and the result will be exposed on the
     * future that is returned from this function.
     * @param callable
     * @param <T>
     * @return
     */
    public static <T> Future<T> withCallable(Callable<T> callable){
        return withCallable(EXECUTOR, callable);
    }

    /**
     * Executes the {@link java.util.concurrent.Callable} in a separate thread and the result will be exposed on the
     * future that is returned from this function.
     * @param executor Executor to run the callable
     * @param callable
     * @param <T>
     * @return
     */
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

    /**
     * Returns a future that will be known when all of the futures passed as arguments are known
     * @param futures
     * @param <T>
     * @return a {@link java.util.Set} with the result of all the futures
     */
    public static <T> Future<Set<T>> all(Future<T>... futures) {
        FuturePool<T> pool = new FuturePool<>();
        for(Future<T> future: futures) {
            pool.listen(future);
        }
        return pool.all();
    }

    /**
     * Returns a future that will be known when all of the futures passed as arguments are known
     * @param futures
     * @param <T>
     * @return a {@link java.util.Set} with the result of all the futures
     */
    public static <T> Future<Set<T>> all(Iterable<Future<T>> futures) {
        FuturePool<T> pool = new FuturePool<>();
        for(Future<T> future: futures) {
            pool.listen(future);
        }
        return pool.all();
    }

    /**
     * Returns a future that will be known when any of the futures passed as arguments are known
     * @param futures
     * @param <T>
     * @return the result of the first future to be known
     */
    public static <T> Future<T> any(Future<T>... futures) {
        FuturePool<T> pool = new FuturePool<>();
        for(Future<T> future: futures) {
            pool.listen(future);
        }
        return pool.first();
    }

    /**
     * Returns a future that will be known when any of the futures passed as arguments are known
     * @param futures
     * @param <T>
     * @return the result of the first future to be known
     */
    public static <T> Future<T> any(Iterable<Future<T>> futures) {
        FuturePool<T> pool = new FuturePool<>();
        for(Future<T> future: futures) {
            pool.listen(future);
        }
        return pool.first();
    }
}
