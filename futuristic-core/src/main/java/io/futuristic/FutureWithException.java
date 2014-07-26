package io.futuristic;

import io.futuristic.function.ConsumerWithException;
import io.futuristic.function.ExceptionTrapper;
import io.futuristic.function.FunctionWithException;

/**
 * @autor: julio
 */
final class FutureWithException<T> implements Future<T> {

    private final Exception exception;

    public FutureWithException(Exception exception) {
        this.exception = exception;
    }

    @Override
    public T await() throws Exception {
        throw exception;
    }

    @Override
    public Future<T> consume(ConsumerWithException<T> consumer) {
        return new FutureWithException<>(exception);
    }

    @Override
    public <R> Future<R> map(FunctionWithException<T, R> mapper) {
        return new FutureWithException<>(exception);
    }

    @Override
    public <R> Future<R> mapFuture(FunctionWithException<T, Future<R>> mapper) {
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
