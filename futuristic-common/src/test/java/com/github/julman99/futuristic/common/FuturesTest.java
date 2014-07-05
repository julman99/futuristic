package com.github.julman99.futuristic.common;

import com.github.julman99.futuristic.common.util.DummyExceptions;
import org.junit.Test;

import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @autor: julio
 */
public class FuturesTest {

    @Test
    public void testWithCallback() throws Exception {
        Future<Integer> future = Futures.withCallback(c -> c.completed(1));
        int result = future.get();
        assertEquals(1, result);
    }

    @Test
    public void testWithCallable() throws Exception {
        Future<Integer> future = Futures.withCallable(() -> 1);
        int result = future.get();
        assertEquals(1, result);
    }

    @Test
    public void testWithCallableAndExecutor() throws Exception {
        Future<Integer> future = Futures.withCallable(Executors.newCachedThreadPool(), () -> 1);
        int result = future.get();
        assertEquals(1, result);
    }

    @Test
    public void testWithValue() throws Exception {
        Future<Integer> future = Futures.withValue(1);
        int result = future.get();
        assertEquals(1, result);
    }

    @Test
    public void testWithException() {
        Exception exception = new DummyExceptions.DummyException1();
        Future<Integer> future = Futures.withException(exception);
        try {
            future.get();
            fail("Exception should have been thrown");
        } catch (DummyExceptions.DummyException1 dex1){
            assertEquals(exception, dex1);
        } catch (Exception ex){
            fail("Should have got a DummyException1");
        }
    }

}
