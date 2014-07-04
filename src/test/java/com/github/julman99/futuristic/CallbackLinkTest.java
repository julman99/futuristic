package com.github.julman99.futuristic;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
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
        callbackLink.addTo(new Callback<AtomicBoolean>() {
            @Override
            public void completed(AtomicBoolean result) {
                result.set(true);
            }

            @Override
            public void failed(Exception throwable) {

            }
        });
        AtomicBoolean test = new AtomicBoolean(false);
        callbackLink.getFrom().completed(test);
        assertTrue(test.get());
    }

    @Test
    public void testLinkFailFirstTo(){
        AtomicReference<Exception> exceptionReference = new AtomicReference<>();
        CallbackLink callbackLink = new CallbackLink<>();
        callbackLink.addTo(new Callback() {
            @Override
            public void completed(Object result) {

            }

            @Override
            public void failed(Exception throwable) {
                exceptionReference.set(throwable);
            }
        });

        Exception exception = new RuntimeException();
        callbackLink.getFrom().failed(exception);
        assertEquals(exception, exceptionReference.get());
    }

    @Test
    public void testLinkSuccessFirstFrom(){
        CallbackLink<AtomicBoolean> callbackLink = new CallbackLink<>();
        AtomicBoolean test = new AtomicBoolean(false);
        callbackLink.getFrom().completed(test);
        callbackLink.addTo(new Callback<AtomicBoolean>() {
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
        callbackLink.getFrom().failed(exception);
        callbackLink.addTo(new Callback() {
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

    @Test
    public void testSuccessMultipleTo(){
        CallbackLink<AtomicInteger> callbackLink = new CallbackLink<>();
        AtomicInteger test = new AtomicInteger(0);
        Callback<AtomicInteger> callback = new Callback<AtomicInteger>() {
            @Override
            public void completed(AtomicInteger result) {
                result.incrementAndGet();
            }

            @Override
            public void failed(Exception throwable) {

            }
        };

        //Register one callback before triggering link
        callbackLink.addTo(callback);

        //Trigger the link
        callbackLink.getFrom().completed(test);

        //Register two more callbacks
        callbackLink.addTo(callback);
        callbackLink.addTo(callback);

        assertEquals(3, test.get());
    }

    @Test
    public void testErrorMultipleTo(){
        CallbackLink<AtomicInteger> callbackLink = new CallbackLink<>();
        List<Exception> test = new ArrayList<>(0);
        Callback<AtomicInteger> callback = new Callback<AtomicInteger>() {
            @Override
            public void completed(AtomicInteger result) {

            }

            @Override
            public void failed(Exception throwable) {
                test.add(throwable);
            }
        };

        //Register one callback before triggering link
        callbackLink.addTo(callback);

        //Trigger the link
        callbackLink.getFrom().failed(new Exception());

        //Register two more callbacks
        callbackLink.addTo(callback);
        callbackLink.addTo(callback);

        assertEquals(3, test.size());
        assertEquals(1, new HashSet<>(test).size());
    }


}
