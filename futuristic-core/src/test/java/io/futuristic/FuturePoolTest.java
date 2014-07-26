package io.futuristic;

import org.junit.Test;

import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @autor: julio
 */
public class FuturePoolTest {

    @Test
    public void testFirst() throws Exception {
        FuturePool<Integer> pool = new FuturePool<>();

        long start = System.currentTimeMillis();
        pool.listen(delayed(40, 3));
        pool.listen(delayed(10, 2));
        int result = pool.first().await();
        long end = System.currentTimeMillis();
        long duration = end - start;

        assertEquals(2, result);
        assertTrue(duration < 40);
    }

    @Test
    public void testAll() throws Exception {
        FuturePool<Integer> pool = new FuturePool<>();

        long start = System.currentTimeMillis();
        pool.listen(delayed(40, 3));
        pool.listen(delayed(10, 2));
        Set<Integer> result = pool.all().await();
        long end = System.currentTimeMillis();
        long duration = end - start;

        assertEquals(2, result.size());
        assertTrue(result.contains(2));
        assertTrue(result.contains(3));
        assertTrue(duration >= 40);
    }

    private static <T> Future<T> delayed(long millis, T value){
        return Futures.withCallback(c -> {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    c.completed(value);
                }
            }, millis);
        });
    }

}
