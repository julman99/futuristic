package com.github.julman99.futuristic;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @autor: julio
 */
public class CallbackLinkTest {

    @Test
    public void testLinkSuccessFirstTo(){
        CallbackLink<AtomicBoolean> callbackLink = new CallbackLink<>();
        callbackLink.setCallbackTo(new Callback<AtomicBoolean>() {
            @Override
            public void completed(AtomicBoolean result) {
                result.set(true);
            }

            @Override
            public void failed(Exception throwable) {

            }
        });
        AtomicBoolean test = new AtomicBoolean(false);
        callbackLink.getCallbackFrom().completed(test);
        assertTrue(test.get());
    }

    @Test
    public void testLinkFailFirstTo(){
        AtomicReference<Exception> exceptionReference = new AtomicReference<>();
        CallbackLink callbackLink = new CallbackLink<>();
        callbackLink.setCallbackTo(new Callback() {
            @Override
            public void completed(Object result) {

            }

            @Override
            public void failed(Exception throwable) {
                exceptionReference.set(throwable);
            }
        });

        Exception exception = new RuntimeException();
        callbackLink.getCallbackFrom().failed(exception);
        assertEquals(exception, exceptionReference.get());
    }

    @Test
    public void testLinkSuccessFirstFrom(){
        CallbackLink<AtomicBoolean> callbackLink = new CallbackLink<>();
        AtomicBoolean test = new AtomicBoolean(false);
        callbackLink.getCallbackFrom().completed(test);
        callbackLink.setCallbackTo(new Callback<AtomicBoolean>() {
            @Override
            public void completed(AtomicBoolean result) {
                result.set(true);
            }

            @Override
            public void failed(Exception throwable) {

            }
        });
        assertTrue(test.get());
    }

    @Test
    public void testLinkFailFirstFrom(){
        AtomicReference<Exception> exceptionReference = new AtomicReference<>();
        CallbackLink callbackLink = new CallbackLink<>();
        Exception exception = new RuntimeException();
        callbackLink.getCallbackFrom().failed(exception);
        callbackLink.setCallbackTo(new Callback() {
            @Override
            public void completed(Object result) {

            }

            @Override
            public void failed(Exception throwable) {
                exceptionReference.set(throwable);
            }
        });
        assertEquals(exception, exceptionReference.get());
    }



}
