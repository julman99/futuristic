package io.futuristic;

import io.futuristic.util.DummyExceptions;
import io.futuristic.util.Triggerer;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

/**
 * @autor: julio
 */
public class FutureWithTriggerTest {

    @Test
    public void testGetSync() throws Exception {
        FutureWithTrigger futureWithTrigger = new FutureWithTrigger();

        Object test = new Object();
        Triggerer.triggerValue(test, futureWithTrigger);

        Object result = futureWithTrigger.getFuture().await();
        assertEquals(test, result);
    }

    @Test
    public void testGetAsync() throws Exception {
        FutureWithTrigger futureWithTrigger = new FutureWithTrigger();

        Object test = new Object();
        Triggerer.triggerValueAsync(10, test, futureWithTrigger);

        Object result = futureWithTrigger.getFuture().await();
        assertEquals(test, result);
    }

    @Test
    public void testConsumeSync(){
        FutureWithTrigger<AtomicBoolean> futureWithTrigger = new FutureWithTrigger<>();
        futureWithTrigger.getFuture().consume(b->b.set(true));

        AtomicBoolean test = new AtomicBoolean(false);
        Triggerer.triggerValue(test, futureWithTrigger);

        assertTrue(test.get());
    }

    @Test
    public void testConsumeAsync() throws Exception {
        FutureWithTrigger<AtomicBoolean> futureWithTrigger = new FutureWithTrigger<>();
        futureWithTrigger.getFuture().consume(b->b.set(true));

        AtomicBoolean test = new AtomicBoolean(false);
        Triggerer.triggerValueAsync(10, test, futureWithTrigger);
        AtomicBoolean result = futureWithTrigger.getFuture().consume(b->b.set(true)).await();

        assertEquals(test, result);
        assertTrue(test.get());
    }

    @Test
    public void testMapSync() throws Exception {
        FutureWithTrigger<Integer> futureWithTrigger = new FutureWithTrigger<>();

        int test = 1;
        Triggerer.triggerValue(test, futureWithTrigger);
        int result = futureWithTrigger.getFuture().map(i->i+1).await();

        assertEquals(test + 1, result);
    }

    @Test
    public void testMapAsync() throws Exception {
        FutureWithTrigger<Integer> futureWithTrigger = new FutureWithTrigger<>();

        int test = 1;
        Triggerer.triggerValueAsync(10, test, futureWithTrigger);
        int result = futureWithTrigger.getFuture().map(i->i+1).await();

        assertEquals(test + 1, result);
    }

    @Test
    public void testMapFutureSync() throws Exception {
        FutureWithTrigger<Integer> futureWithTrigger = new FutureWithTrigger<>();

        int test = 1;
        Triggerer.triggerValue(test, futureWithTrigger);
        int result = futureWithTrigger.getFuture().mapFuture(i -> {
            FutureWithTrigger<Integer> next = new FutureWithTrigger<>();
            Triggerer.triggerValue(test + 1, next);
            return next.getFuture();
        }).await();

        assertEquals(test + 1, result);
    }

    @Test
    public void testMapFutureAsync() throws Exception {
        FutureWithTrigger<Integer> futureWithTrigger = new FutureWithTrigger<>();

        int test = 1;
        Triggerer.triggerValueAsync(10, test, futureWithTrigger);
        int result = futureWithTrigger.getFuture().mapFuture(i -> {
            FutureWithTrigger<Integer> next = new FutureWithTrigger<>();
            Triggerer.triggerValueAsync(10, test + 1, next);
            return next.getFuture();
        }).await();

        assertEquals(test + 1, result);
    }

    @Test
    public void testChaining() throws Exception {
        FutureWithTrigger<Integer> futureWithTrigger = new FutureWithTrigger<>();

        int initialValue = 1;
        Triggerer.triggerValueAsync(10, initialValue, futureWithTrigger);
        String result = futureWithTrigger.getFuture().mapFuture(i -> {
            //This will increase by one the test variable, after 10 milliseconds
            //Result should be 2
            FutureWithTrigger<Integer> next = new FutureWithTrigger<>();
            Triggerer.triggerValueAsync(10, initialValue + 1, next);
            return next.getFuture();
        }).map(
            //This will create a StringBuilder with the integer returned from the previous step
            //Result should be "2"
            i -> new StringBuilder(Integer.toString(i))
        ).consume(
            //This appends a "c" to the builder
            //Result should be "2c"
            s -> s.append("c")
        ).await().toString();

        //Proof that all was executed in sequence
        assertEquals("2c", result);
    }

    @Test
    public void testErrorCatching() {
        FutureWithTrigger<Object> futureWithTrigger = new FutureWithTrigger<>();
        RuntimeException exception = new RuntimeException();
        AtomicReference<RuntimeException> exceptionReference = new AtomicReference<>();

        Triggerer.triggerValue(new Object(), futureWithTrigger);

        try {
            futureWithTrigger.getFuture().consume(v -> {
                throw exception;
            }).trap(RuntimeException.class, e -> {
                exceptionReference.set(e);
                throw e;
            }).await();

            fail("Exception should have been thrown");
        } catch (Exception catchedException) {
            assertEquals(exception, exceptionReference.get());
            assertEquals(exception, catchedException);
        }
    }

    @Test
    public void testErrorCatchingFuture() {
        FutureWithTrigger<Object> futureWithTrigger = new FutureWithTrigger<>();
        RuntimeException exception = new RuntimeException();
        AtomicReference<RuntimeException> exceptionReference = new AtomicReference<>();

        Triggerer.triggerValue(new Object(), futureWithTrigger);

        try {
            futureWithTrigger.getFuture().consume(v -> {
                throw exception;
            }).trapFuture(RuntimeException.class, e -> {
                exceptionReference.set(e);
                throw e;
            }).await();

            fail("Exception should have been thrown");
        } catch (Exception catchedException) {
            assertEquals(exception, exceptionReference.get());
            assertEquals(exception, catchedException);
        }
    }

    @Test
    public void testErrorCatchingRightType() {
        FutureWithTrigger<Object> futureWithTrigger = new FutureWithTrigger<>();
        DummyExceptions.DummyException1 exception = new DummyExceptions.DummyException1();
        AtomicReference<DummyExceptions.DummyException1> exceptionReference1 = new AtomicReference<>();
        AtomicReference<DummyExceptions.DummyException2> exceptionReference2 = new AtomicReference<>();

        Object test = new Object();
        Triggerer.triggerValue(test, futureWithTrigger);

        try {
            futureWithTrigger.getFuture().consume(v -> {
                throw exception;
            }).trap(DummyExceptions.DummyException2.class, e -> {
                exceptionReference2.set(e);  //This should never be called
                throw e;
            }).trap(DummyExceptions.DummyException1.class, e -> {
                exceptionReference1.set(e); //This should be called because of the type
                throw e;
            }).await();

            fail("Exception should have been thrown");
        } catch (Exception catchedException) {
            assertEquals(exception, exceptionReference1.get());
            assertNull(exceptionReference2.get());
            assertEquals(exception, catchedException);
        }
    }

    @Test
    public void testErrorThrowingSync() {
        FutureWithTrigger<Object> futureWithTrigger = new FutureWithTrigger<>();
        RuntimeException exception = new RuntimeException();
        AtomicReference<RuntimeException> exceptionReference = new AtomicReference<>();

        Triggerer.triggerError(exception, futureWithTrigger);

        try {
            futureWithTrigger.getFuture().trap(RuntimeException.class, e -> {
                exceptionReference.set(e);
                throw e;
            }).await();

            fail("Exception should have been thrown");
        } catch (Exception catchedException) {
            assertEquals(exception, exceptionReference.get());
            assertEquals(exception, catchedException);
        }
    }

    @Test
    public void testErrorThrowingAsync() {
        FutureWithTrigger<Object> futureWithTrigger = new FutureWithTrigger<>();
        RuntimeException exception = new RuntimeException();
        AtomicReference<RuntimeException> exceptionReference = new AtomicReference<>();

        Triggerer.triggerErrorAsync(10, exception, futureWithTrigger);

        try {
            futureWithTrigger.getFuture().trap(RuntimeException.class, e -> {
                exceptionReference.set(e);
                throw e;
            }).await();

            fail("Exception should have been thrown");
        } catch (Exception catchedException) {
            assertEquals(exception, exceptionReference.get());
            assertEquals(exception, catchedException);
        }
    }

    @Test
    public void testErrorRecovering() {
        FutureWithTrigger<Integer> futureWithTrigger = new FutureWithTrigger<>();
        RuntimeException exception = new RuntimeException();
        AtomicReference<RuntimeException> exceptionReference = new AtomicReference<>();

        Triggerer.triggerErrorAsync(10, exception, futureWithTrigger);

        try {
            int value = futureWithTrigger.getFuture().trap(RuntimeException.class, e -> {
                exceptionReference.set(e);
                return 1;
            }).await();

            assertEquals(1, value);
            assertEquals(exception, exceptionReference.get());
        } catch (Exception catchedException) {
            fail("No error should have been thrown");
        }
    }

    @Test
    public void testErrorRecoveringFuture() {
        FutureWithTrigger<Integer> futureWithTrigger = new FutureWithTrigger<>();
        RuntimeException exception = new RuntimeException();
        AtomicReference<RuntimeException> exceptionReference = new AtomicReference<>();

        Triggerer.triggerErrorAsync(10, exception, futureWithTrigger);

        try {
            int value = futureWithTrigger.getFuture().trapFuture(RuntimeException.class, e -> {
                exceptionReference.set(e);
                return Futures.withValue(1);
            }).await();

            assertEquals(1, value);
            assertEquals(exception, exceptionReference.get());
        } catch (Exception catchedException) {
            fail("No error should have been thrown");
        }
    }

}
