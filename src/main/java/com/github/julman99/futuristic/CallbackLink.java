package com.github.julman99.futuristic;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @autor: julio
 */
public final class CallbackLink<T> {

    private final AtomicBoolean done = new AtomicBoolean(false);
    private final AtomicBoolean forwarded = new AtomicBoolean(false);

    private T result = null;
    private Exception error = null;

    private Queue<Callback<T>> callbacksTo = new ConcurrentLinkedQueue<>();

    public CallbackLink() {
    }

    public Callback<T> getFrom(){
        return new Callback<T>() {
            @Override
            public void completed(T result) {
                CallbackLink.this.done.set(true);
                CallbackLink.this.result = result;
                CallbackLink.this.forward();
            }

            @Override
            public void failed(Exception exception) {
                CallbackLink.this.done.set(true);
                CallbackLink.this.error = exception;
                CallbackLink.this.forward();
            }
        };
    }

    public void addTo(Callback<T> callbackTo){
        this.callbacksTo.add(callbackTo);
        forward();
    }

    private void forward(){
        if(done.get()){
            while(true) {
                Callback<T> next = callbacksTo.poll();
                if(next == null){
                    break;
                } else {
                    if(error == null){
                        next.completed(result);
                    } else {
                        next.failed(error);
                    }
                }
            }

        }
    }
}
