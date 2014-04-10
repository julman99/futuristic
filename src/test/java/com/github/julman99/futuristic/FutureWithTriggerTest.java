package com.github.julman99.futuristic;

import org.junit.Test;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @autor: julio
 */
public class FutureWithTriggerTest {

    @Test
    public void testGetSync() throws Exception {
        FutureWithTrigger futureWithTrigger = new FutureWithTrigger();

        Object test = new Object();
        triggerValue(test, futureWithTrigger);

        Object result = futureWithTrigger.get();
        assertEquals(test, result);
    }

    @Test
    public void testGetAsync() throws Exception {
        FutureWithTrigger futureWithTrigger = new FutureWithTrigger();

        Object test = new Object();
        triggerValueAsync(10, test, futureWithTrigger);

        Object result = futureWithTrigger.get();
        assertEquals(test, result);
    }

    @Test
    public void testConsumeSync(){
        FutureWithTrigger<AtomicBoolean> futureWithTrigger = new FutureWithTrigger<>();
        futureWithTrigger.consume(b->b.set(true));

        AtomicBoolean test = new AtomicBoolean(false);
        triggerValue(test, futureWithTrigger);

        assertTrue(test.get());
    }

    @Test
    public void testConsumeAsync() throws Exception {
        FutureWithTrigger<AtomicBoolean> futureWithTrigger = new FutureWithTrigger<>();
        futureWithTrigger.consume(b->b.set(true));

        AtomicBoolean test = new AtomicBoolean(false);
        triggerValueAsync(10, test, futureWithTrigger);
        AtomicBoolean result = futureWithTrigger.consume(b->b.set(true)).get();

        assertEquals(test, result);
        assertTrue(test.get());
    }

    @Test
    public void testMapSync() throws Exception {
        FutureWithTrigger<Integer> futureWithTrigger = new FutureWithTrigger<>();

        int test = 1;
        triggerValue(test, futureWithTrigger);
        int result = futureWithTrigger.map(i->i+1).get();

        assertEquals(test + 1, result);
    }

    @Test
    public void testMapAsync() throws Exception {
        FutureWithTrigger<Integer> futureWithTrigger = new FutureWithTrigger<>();

        int test = 1;
        triggerValueAsync(10, test, futureWithTrigger);
        int result = futureWithTrigger.map(i->i+1).get();

        assertEquals(test + 1, result);
    }

    @Test
    public void testMapfSync() throws Exception {
        FutureWithTrigger<Integer> futureWithTrigger = new FutureWithTrigger<>();

        int test = 1;
        triggerValue(test, futureWithTrigger);
        int result = futureWithTrigger.mapFuture(i -> {
            FutureWithTrigger<Integer> next = new FutureWithTrigger<Integer>();
            triggerValue(test + 1, next);
            return next;
        }).get();

        assertEquals(test + 1, result);
    }

    @Test
    public void testMapfAsync() throws Exception {
        FutureWithTrigger<Integer> futureWithTrigger = new FutureWithTrigger<>();

        int test = 1;
        triggerValueAsync(10, test, futureWithTrigger);
        int result = futureWithTrigger.mapFuture(i -> {
            FutureWithTrigger<Integer> next = new FutureWithTrigger<Integer>();
            triggerValueAsync(10, test + 1, next);
            return next;
        }).get();

        assertEquals(test + 1, result);
    }


    private static <T> void triggerValue(T value, FutureWithTrigger<T> futureWithTrigger){
        futureWithTrigger.getTrigger().completed(value);
    }

    private static <T> void triggerValueAsync(long delay, T value, FutureWithTrigger<T> futureWithTrigger){
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                futureWithTrigger.getTrigger().completed(value);
            }
        }, delay);
    }

}
