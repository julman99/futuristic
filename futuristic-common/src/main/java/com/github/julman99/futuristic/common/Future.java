package com.github.julman99.futuristic.common;

import com.github.julman99.futuristic.common.function.ConsumerWithException;
import com.github.julman99.futuristic.common.function.ExceptionTrapper;
import com.github.julman99.futuristic.common.function.FunctionWithException;

/**
 * @autor: julio
 */
public interface Future<T> {

    /**
     * Blocks the call until the future value is available.
     * @return
     * @throws Exception
     */
    public T get() throws Exception;

    /**
     * Called when the future value is available.
     * @param consumer
     * @return
     */
    public Future<T> consume(ConsumerWithException<T> consumer);

    /**
     * Called when the future value is available.
     * @param callback
     * @return
     */
    default void consume(Callback<T> callback){
        this
            .consume(r -> callback.completed(r))
            .trap(Exception.class, e -> {
                callback.failed(e);
                throw e;
            });
    }

    /**
     * Called when the future value is available. The mapper should map the result of the future to a different
     * object
     * @param mapper
     * @param <R>
     * @return
     */
    public <R> Future<R> map(FunctionWithException<T, R> mapper);

    /**
     * Called when the future value is available. The mapper should map the result of the future to a different
     * {@link Future} object.
     * object
     * @param mapper
     * @param <R>
     * @return
     */
    public <R> Future<R> mapFuture(FunctionWithException<T, Future<R>> mapper);

    /**
     * Called when there is an {@link java.lang.Exception}. The trapper will only be called if the Exception of
     * the type specified as the first parameter of the class. The trapper should return a value to recover from the
     * {@link java.lang.Exception} or should rethrow the exception
     * @param exceptionClass The class of the Exception to trap
     * @param trapper
     * @param <E>
     * @return
     */
    public <E extends Exception> Future<T> trap(Class<E> exceptionClass, ExceptionTrapper<E, T> trapper);

    /**
     * Called when there is an {@link java.lang.Exception}. The trapper will only be called if the Exception of
     * the type specified as the first parameter of the class. The trapper should return a future value to recover from
     * the {@link java.lang.Exception} or should rethrow the exception
     * @param exceptionClass The class of the Exception to trap
     * @param trapper
     * @param <E>
     * @return
     */
    public <E extends Exception> Future<T> trapFuture(Class<E> exceptionClass, ExceptionTrapper<E, Future<T>> trapper);
}