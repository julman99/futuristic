package io.futuristic;

import junit.framework.TestCase;
import org.junit.Test;

import java.io.InvalidClassException;
import java.util.InvalidPropertiesFormatException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class FutureWithExceptionTest extends TestCase {

    @Test
    public void testConsumeDoesNotGetCalled() {
        AtomicBoolean consumed = new AtomicBoolean(false);
        Exception expectedException = new RuntimeException();
        FutureWithException futureWithException = new FutureWithException(expectedException);
        try {
            futureWithException.consume(o -> consumed.set(true)).await();
            fail("An exception should have been thrown");
        } catch (Exception e) {
            assertEquals(expectedException, e);
            assertFalse(consumed.get());
        }
    }

    @Test
    public void testMapDoesNotGetCalled() {
        AtomicBoolean consumed = new AtomicBoolean(false);
        Exception expectedException = new RuntimeException();
        FutureWithException futureWithException = new FutureWithException(expectedException);
        try {
            futureWithException.map(o -> {
                consumed.set(true);
                return o;
            }).await();
            fail("An exception should have been thrown");
        } catch (Exception e) {
            assertEquals(expectedException, e);
            assertFalse(consumed.get());
        }
    }

    @Test
    public void testMapFutureDoesNotGetCalled() {
        AtomicBoolean consumed = new AtomicBoolean(false);
        Exception expectedException = new RuntimeException();
        FutureWithException futureWithException = new FutureWithException(expectedException);
        try {
            futureWithException.mapFuture(o -> {
                consumed.set(true);
                return Futures.withValue(o);
            }).await();
            fail("An exception should have been thrown");
        } catch (Exception e) {
            assertEquals(expectedException, e);
            assertFalse(consumed.get());
        }

    }

    @Test
    public void testTrapOnlyCallsOnce() {
        AtomicInteger trapCount = new AtomicInteger();
        Exception expectedException = new RuntimeException();
        FutureWithException futureWithException = new FutureWithException(expectedException);
        try {
            futureWithException
                .trap(RuntimeException.class, e -> {
                    trapCount.incrementAndGet();
                    return null;
                }).trap(RuntimeException.class, e -> {
                    trapCount.incrementAndGet();
                    return null;
                }).await();

            assertEquals(1, trapCount.get());
        } catch (Exception e) {
            fail("No exception should have been thrown");
        }
    }

    @Test
    public void testTrapCorrectType() {
        AtomicInteger trapCount = new AtomicInteger();
        Exception expectedException = new RuntimeException();
        FutureWithException futureWithException = new FutureWithException(expectedException);
        try {
            futureWithException
                .trap(InvalidClassException.class, e -> {
                    trapCount.decrementAndGet();
                    return null;
                })
                .trap(RuntimeException.class, e -> {
                    trapCount.incrementAndGet();
                    return null;
                }).await();

            assertEquals(1, trapCount.get());
        } catch (Exception e) {
            fail("No exception should have been thrown");
        }
    }

    @Test
    public void testTrapCorrectType_invertedChain() {
        AtomicInteger trapCount = new AtomicInteger();
        Exception expectedException = new RuntimeException();
        FutureWithException futureWithException = new FutureWithException(expectedException);
        try {
            futureWithException
                .trap(RuntimeException.class, e -> {
                    trapCount.incrementAndGet();
                    return null;
                })
                .trap(InvalidClassException.class, e -> {
                    trapCount.decrementAndGet();
                    return null;
                }).await();

            assertEquals(1, trapCount.get());
        } catch (Exception e) {
            fail("No exception should have been thrown");
        }
    }

    @Test
    public void testTrapCorrectType_noTrapping() {
        AtomicInteger trapCount = new AtomicInteger();
        Exception expectedException = new RuntimeException();
        FutureWithException futureWithException = new FutureWithException(expectedException);
        try {
            futureWithException
                .trap(InvalidPropertiesFormatException.class, e -> {
                    trapCount.incrementAndGet();
                    return null;
                })
                .trap(InvalidClassException.class, e -> {
                    trapCount.incrementAndGet();
                    return null;
                }).await();
            fail("An exception should have been thrown");
        } catch (Exception e) {
            assertEquals(expectedException, e);
            assertEquals(0, trapCount.get());
        }
    }

    @Test
    public void testTrapCorrectType_supperClass() {
        AtomicInteger trapCount = new AtomicInteger();
        Exception expectedException = new RuntimeException();
        FutureWithException futureWithException = new FutureWithException(expectedException);
        try {
            futureWithException
                .trap(Throwable.class, e -> {
                    trapCount.incrementAndGet();
                    return null;
                })
                .trap(InvalidClassException.class, e -> {
                    trapCount.decrementAndGet();
                    return null;
                }).await();

            assertEquals(1, trapCount.get());
        } catch (Exception e) {
            fail("No exception should have been thrown");
        }
    }

    @Test
    public void test_bigChain() {
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger trapCount = new AtomicInteger();
        Exception expectedException = new RuntimeException();
        FutureWithException futureWithException = new FutureWithException(expectedException);
        try {
            futureWithException
                .consume(v -> successCount.incrementAndGet())
                .consume(v -> successCount.incrementAndGet())
                .map(v -> successCount.incrementAndGet())
                .mapFuture(v -> Futures.withValue(successCount.incrementAndGet()))
                .trap(RuntimeException.class, e -> {
                    trapCount.incrementAndGet();
                    return null;
                })
                .trap(InvalidClassException.class, e -> {
                    trapCount.decrementAndGet();
                    return null;
                })
                .consume(v -> successCount.incrementAndGet())
                .map(v -> successCount.incrementAndGet())
                .await();

            assertEquals(2, successCount.get());
            assertEquals(1, trapCount.get());
        } catch (Exception e) {
            fail("No exception should have been thrown");
        }
    }

}