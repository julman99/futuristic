package io.futuristic;

import io.futuristic.function.ConsumerWithException;
import io.futuristic.function.ExceptionTrapper;
import io.futuristic.function.FunctionWithException;

/**
 * @autor: julio
 */
final class FutureWithValue<T> implements Future<T> {

    private final T value;

    public FutureWithValue(T value) {
        this.value = value;
    }

    @Override
    public T await() throws Exception {
        return value;
    }

    @Override
    public Future<T> consume(ConsumerWithException<T> consumer) {
        try {
            consumer.accept(value);
            return this;
        } catch (Exception ex) {
            return new FutureWithException<>(ex);
        }
    }

    @Override
    public <R> Future<R> map(FunctionWithException<T, R> mapper) {
        try {
            R mapped = mapper.apply(value);
            return new FutureWithValue<>(mapped);
        } catch (Exception ex) {
            return new FutureWithException<>(ex);
        }
    }

    @Override
    public <R> Future<R> mapFuture(FunctionWithException<T, Future<R>> mapper) {
        try {
            Future<R> mapped = mapper.apply(value);
            return mapped;
        } catch (Exception ex) {
            return new FutureWithException<>(ex);
        }
    }

    @Override
    public <E extends Exception> Future<T> trap(Class<E> exceptionClass, ExceptionTrapper<E, T> trapper) {
        return this;
    }

    @Override
    public <E extends Exception> Future<T> trapFuture(Class<E> exceptionClass, ExceptionTrapper<E, Future<T>> trapper) {
        return this;
    }

}
