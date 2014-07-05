package com.github.julman99.futuristic.common;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @autor: julio
 */
final class FutureWithException<T> implements Future<T> {

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
    public <R> Future<R> mapFuture(Function<T, Future<R>> mapper) {
        return new FutureWithException<>(exception);
    }

    @Override
    public <E extends Exception> Future<T> trap(Class<E> exceptionClass, ExceptionTrapper<E, T> trapper) {
        if(exceptionClass.isAssignableFrom(exception.getClass())){
            try {
                T trapped = trapper.trap((E) exception);
                return new FutureWithValue<>(trapped);
            } catch (Exception ex){
                return new FutureWithException<>(ex);
            }
        }
        return this;
    }

    @Override
    public <E extends Exception> Future<T> trapFuture(Class<E> exceptionClass, ExceptionTrapper<E, Future<T>> trapper) {
        if(exceptionClass.isAssignableFrom(exception.getClass())){
            try {
                return trapper.trap((E) exception);
            } catch (Exception ex){
                return new FutureWithException<>(ex);
            }
        }
        return this;
    }

}
