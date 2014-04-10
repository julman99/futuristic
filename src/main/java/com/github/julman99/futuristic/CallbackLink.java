package com.github.julman99.futuristic;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @autor: julio
 */
public class CallbackLink<T> {

    private final AtomicBoolean done = new AtomicBoolean(false);
    private final AtomicBoolean forwarded = new AtomicBoolean(false);

    private T result = null;
    private Exception error = null;

    private Callback<T> callbackTo;

    public CallbackLink() {
    }

    public Callback<T> getCallbackFrom(){
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

    public void setCallbackTo(Callback<T> callbackTo){
        this.callbackTo = callbackTo;
        forward();
    }

    private void forward(){
        if(done.get() && callbackTo != null && !forwarded.getAndSet(true) ){
            if(error == null){
                callbackTo.completed(result);
            } else {
                callbackTo.failed(error);
            }
        }
    }
}
