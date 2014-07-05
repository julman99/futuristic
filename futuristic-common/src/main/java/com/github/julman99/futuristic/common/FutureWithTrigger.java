package com.github.julman99.futuristic.common;

import com.github.julman99.futuristic.common.function.ConsumerWithException;
import com.github.julman99.futuristic.common.function.ExceptionTrapper;
import com.github.julman99.futuristic.common.function.FunctionWithException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @autor: julio
 */
public final class FutureWithTrigger<T> {

    private final CallbackLink<T> callbackLink;
    private final Callback<T> triggerCallback;
    private final Future<T> future;

    public FutureWithTrigger() {
        this.callbackLink = new CallbackLink<>();
        this.triggerCallback = this.callbackLink.getFrom();
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
                FutureWithTrigger.this.callbackLink.addTo(new Callback<T>() {
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
            public Future<T> consume(ConsumerWithException<T> consumer) {
                FutureWithTrigger<T> nextFuture = new FutureWithTrigger<>();
                FutureWithTrigger.this.callbackLink.addTo(new Callback<T>() {
                    @Override
                    public void completed(T result) {
                        try {
                            consumer.accept(result);
                        } catch (Exception e) {
                            this.failed(e);
                            return;
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
            public <R> Future<R> map(FunctionWithException<T, R> mapper) {
                return mapFuture(t -> new FutureWithValue<R>(mapper.apply(t)));
            }

            @Override
            public <R> Future<R> mapFuture(FunctionWithException<T, Future<R>> mapper) {
                FutureWithTrigger<R> nextFuture = new FutureWithTrigger<>();
                FutureWithTrigger.this.callbackLink.addTo(new Callback<T>() {
                    @Override
                    public void completed(T result) {
                        try {
                            Future<R> mapped = mapper.apply(result);
                            mapped.consume(nextFuture.getTrigger());
                        } catch (Exception ex) {
                            failed(ex);
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
            public <E extends Exception> Future<T> trap(Class<E> exceptionClass, ExceptionTrapper<E, T> trapper) {
                return trapFuture(exceptionClass, e -> new FutureWithValue<>(trapper.trap(e)));
            }

            @Override
            public <E extends Exception> Future<T> trapFuture(Class<E> exceptionClass, ExceptionTrapper<E, Future<T>> trapper) {
                FutureWithTrigger<T> nextFuture = new FutureWithTrigger<>();
                FutureWithTrigger.this.callbackLink.addTo(new Callback<T>() {
                    @Override
                    public void completed(T result) {

                    }

                    @Override
                    public void failed(Exception throwable) {
                        if (exceptionClass.isAssignableFrom(throwable.getClass())) {
                            try {
                                Future<T> res = trapper.trap((E) throwable);
                                res.consume(nextFuture.getTrigger());
                            } catch (Exception ex) {
                                nextFuture.getTrigger().failed(throwable);
                            }
                        } else {
                            nextFuture.getTrigger().failed(throwable);
                        }

                    }
                });
                return nextFuture.getFuture();
            }
        };
    }


}
