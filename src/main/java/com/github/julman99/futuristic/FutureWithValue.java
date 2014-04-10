package com.github.julman99.futuristic;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @autor: julio
 */
public class FutureWithValue<T> implements Future<T> {

    private final T value;

    public FutureWithValue(T value) {
        this.value = value;
    }

    @Override
    public T get() throws Exception {
        return value;
    }

    @Override
    public Future<T> consume(Consumer<T> consumer) {
        try {
            consumer.accept(value);
            return this;
        } catch (Exception ex) {
            return new FutureWithException<>(ex);
        }
    }

    @Override
    public <R> Future<R> map(Function<T, R> mapper) {
        try {
            R mapped = mapper.apply(value);
            return new FutureWithValue<>(mapped);
        } catch (Exception ex) {
            return new FutureWithException<>(ex);
        }
    }

    @Override
    public <R> Future<R> mapFuture(FutureFunction<T, R> mapper) {
        try {
            Future<R> mapped = mapper.apply(value);
            return mapped;
        } catch (Exception ex) {
            return new FutureWithException<>(ex);
        }
    }

    @Override
    public <E extends Exception> Future<T> trap(Class<E> throwableClass, Consumer<E> consumer) {
        return this;
    }

}
