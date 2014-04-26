package com.github.julman99.futuristic;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @autor: julio
 */
public final class FutureWithTrigger<T> {

    private final CallbackLink<T> callbackLink;
    private final Callback<T> triggerCallback;
    private final Future<T> future;

    public FutureWithTrigger() {
        this.callbackLink = new CallbackLink<>();
        this.triggerCallback = this.callbackLink.getCallbackFrom();
        this.future = createFuture();
    }

    public Callback<T> getTrigger(){
        return this.triggerCallback;
    }

    public Future<T> getFuture(){
        return this.future;
    }

    private Future<T> createFuture(){
        return new Future<T>() {
            @Override
            public T get() throws Exception{
                final CountDownLatch latch = new CountDownLatch(1);
                final AtomicReference<T> resultReference = new AtomicReference<>();
                final AtomicReference<Exception> errorReference = new AtomicReference<>();
                FutureWithTrigger.this.callbackLink.setCallbackTo(new Callback<T>() {
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
                FutureWithTrigger.this.callbackLink.setCallbackTo(new Callback<T>() {
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
                return nextFuture.getFuture();
            }

            @Override
            public <R> Future<R> map(Function<T, R> mapper) {
                FutureWithTrigger<R> nextFuture = new FutureWithTrigger<>();
                FutureWithTrigger.this.callbackLink.setCallbackTo(new Callback<T>() {
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
                return nextFuture.getFuture();
            }

            @Override
            public <R> Future<R> mapFuture(FutureFunction<T, R> mapper) {
                FutureWithTrigger<R> nextFuture = new FutureWithTrigger<>();
                FutureWithTrigger.this.callbackLink.setCallbackTo(new Callback<T>() {
                    @Override
                    public void completed(T result) {
                        Future<R> mapped = mapper.apply(result);
                        mapped.consume(nextFuture.getTrigger());
                    }

                    @Override
                    public void failed(Exception throwable) {
                        nextFuture.getTrigger().failed(throwable);
                    }
                });
                return nextFuture.getFuture();
            }

            @Override
            public <E extends Exception> Future<T> trap(Class<E> exceptionClass, ExceptionTrapper<E, T> trapper) {
                FutureWithTrigger<T> nextFuture = new FutureWithTrigger<>();
                FutureWithTrigger.this.callbackLink.setCallbackTo(new Callback<T>() {
                    @Override
                    public void completed(T result) {

                    }

                    @Override
                    public void failed(Exception throwable) {
                        if(exceptionClass.isAssignableFrom(throwable.getClass())){
                            try {
                                T res = trapper.trap((E) throwable);
                                nextFuture.getTrigger().completed(res);
                            } catch (Exception ex){
                                nextFuture.getTrigger().failed(throwable);
                            }
                        }

                    }
                });
                return nextFuture.getFuture();
            }
        };
    }


}
