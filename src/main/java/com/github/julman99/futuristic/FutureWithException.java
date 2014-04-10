package com.github.julman99.futuristic;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @autor: julio
 */
public class FutureWithException<T> implements Future<T> {

    private final Exception exception;

    public FutureWithException(Exception exception) {
        this.exception = exception;
    }

    @Override
    public T get() throws Exception {
        throw exception;
    }

    @Override
    public Future<T> consume(Consumer<T> consumer) {
        return new FutureWithException<>(exception);
    }

    @Override
    public <R> Future<R> map(Function<T, R> mapper) {
        return new FutureWithException<>(exception);
    }

    @Override
    public <R> Future<R> mapFuture(FutureFunction<T, R> mapper) {
        return new FutureWithException<>(exception);
    }

    @Override
    public <E extends Exception> Future<T> trap(Class<E> throwableClass, Consumer<E> consumer) {
        if(throwableClass.isAssignableFrom(exception.getClass())){
            consumer.accept((E) exception);
        }
        return this;
    }
}