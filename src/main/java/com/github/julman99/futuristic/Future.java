package com.github.julman99.futuristic;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @autor: julio
 */
public interface Future<T> {

    public T get() throws Exception;
    public Future<T> consume(Consumer<T> consumer);
    public <R> Future<R> map(Function<T, R> mapper);
    public <R> Future<R> mapFuture(FutureFunction<T, R> mapper);

    public <E extends Exception> Future<T> trap(Class<E> throwableClass, Consumer<E> consumer);

}
