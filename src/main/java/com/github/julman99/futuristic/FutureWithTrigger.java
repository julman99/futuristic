package com.github.julman99.futuristic;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @autor: julio
 */
public class FutureWithTrigger<T> implements Future<T> {

    private final CallbackLink<T> callbackLink;
    private final Callback<T> triggerCallback;

    public FutureWithTrigger() {
        this.callbackLink = new CallbackLink<>();
        this.triggerCallback = this.callbackLink.getCallbackFrom();
    }

    public Callback<T> getTrigger(){
        return this.triggerCallback;
    }

    @Override
    public T get() throws Exception{
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<T> resultReference = new AtomicReference<>();
        final AtomicReference<Exception> errorReference = new AtomicReference<>();
        this.callbackLink.setCallbackTo(new Callback<T>() {
            @Override
            public void completed(T result) {
                resultReference.set(result);
                latch.countDown();
            }

            @Override
            public void failed(Exception throwable) {
                errorReference.set(throwable);
                latch.countDown();
            }
        });

        latch.await();
        if(errorReference.get() == null){
            return resultReference.get();
        } else {
            throw errorReference.get();
        }
    }

    @Override
    public Future<T> consume(Consumer<T> consumer) {
        FutureWithTrigger<T> nextFuture = new FutureWithTrigger<>();
        this.callbackLink.setCallbackTo(new Callback<T>() {
            @Override
            public void completed(T result) {
                try {
                    consumer.accept(result);
                } catch (Exception e) {
                    this.failed(e);
                }

                nextFuture.getTrigger().completed(result);
            }

            @Override
            public void failed(Exception throwable) {
                nextFuture.getTrigger().failed(throwable);
            }
        });

        return nextFuture;
    }

    @Override
    public <R> Future<R> map(Function<T, R> mapper) {
        FutureWithTrigger<R> nextFuture = new FutureWithTrigger<>();
        this.callbackLink.setCallbackTo(new Callback<T>() {
            @Override
            public void completed(T result) {
                try {
                    R mapped = mapper.apply(result);
                    nextFuture.getTrigger().completed(mapped);
                } catch (Exception e) {
                    this.failed(e);
                }

            }

            @Override
            public void failed(Exception throwable) {
                nextFuture.getTrigger().failed(throwable);
            }
        });
        return nextFuture;
    }

    @Override
    public <R> Future<R> mapFuture(FutureFunction<T, R> mapper) {
        FutureWithTrigger<R> nextFuture = new FutureWithTrigger<>();
        this.callbackLink.setCallbackTo(new Callback<T>() {
            @Override
            public void completed(T result) {
                Future<R> mapped = mapper.apply(result);
                mapped.consume(r -> nextFuture.getTrigger().completed(r));
            }

            @Override
            public void failed(Exception throwable) {
                nextFuture.getTrigger().failed(throwable);
            }
        });
        return nextFuture;
    }

    @Override
    public <E extends Exception> Future<T> trap(Class<E> throwableClass, Consumer<E> consumer) {
        FutureWithTrigger<T> nextFuture = new FutureWithTrigger<>();
        this.callbackLink.setCallbackTo(new Callback<T>() {
            @Override
            public void completed(T result) {

            }

            @Override
            public void failed(Exception throwable) {
                if(throwableClass.isAssignableFrom(throwable.getClass())){
                    consumer.accept((E) throwable);
                }
                nextFuture.getTrigger().failed(throwable);
            }
        });
        return nextFuture;
    }
}
