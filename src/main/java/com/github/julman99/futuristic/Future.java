package com.github.julman99.futuristic;

import java.util.function.Consumer;
import java.util.function.Function;

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
    public Future<T> consume(Consumer<T> consumer);

    /**
     * Called when the future value is available. The mapper should map the result of the future to a different
     * object
     * @param mapper
     * @param <R>
     * @return
     */
    public <R> Future<R> map(Function<T, R> mapper);

    /**
     * Called when the future value is available. The mapper should map the result of the future to a different
     * {@link com.github.julman99.futuristic.Future} object.
     * object
     * @param mapper
     * @param <R>
     * @return
     */
    public <R> Future<R> mapFuture(FutureFunction<T, R> mapper);

    /**
     * Called when there is an {@link java.lang.Exception}. The trapper will only be called if the Exception of
     * the type specified as the first parameter of the class
     * @param exceptionClass The class of the Exception to trap
     * @param trapper
     * @param <E>
     * @return
     */
    public <E extends Exception> Future<T> trap(Class<E> exceptionClass, ExceptionTrapper<E, T> trapper);

}
