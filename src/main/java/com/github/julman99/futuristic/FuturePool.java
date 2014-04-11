package com.github.julman99.futuristic;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @autor: julio
 */
public class FuturePool<T> {

    //Sets to store the futures that are being listened and completed
    private final Set<Future<T>> listenedFutures = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final Set<Future<T>> completedFutures = Collections.newSetFromMap(new ConcurrentHashMap<>());

    //Futures that will get triggered once any or all listened futures are finished
    private final FutureWithTrigger<Integer> allFinishedFuture = new FutureWithTrigger<>();
    private final FutureWithTrigger<T> anyFinishedFuture = new FutureWithTrigger<>();

    //Atomic structures to detect wether the first of the last Future is the one finishing
    private final AtomicInteger successCompletedCount = new AtomicInteger(0);
    private final AtomicBoolean isLastCompleted = new AtomicBoolean(true);


    public Future<T> listen(Future<T> future){
        listenedFutures.add(future);
        return
            future.consume(v -> {
                registerCompleted(future, v, null);
            }).trap(Exception.class, e -> {
                registerCompleted(future, null, e);
            });
    }

    public Future<Integer> all() {
        return allFinishedFuture.getFuture();
    }

    public Future<T> any() {
        return anyFinishedFuture.getFuture();
    }

    private void registerCompleted(Future<T> future, T result, Exception error) {
        int currentSuccessCompletedCount = successCompletedCount.incrementAndGet();

        //First finished?
        if(currentSuccessCompletedCount == 1) {
            if(error == null){
                anyFinishedFuture.getTrigger().completed(result);
            } else {
                anyFinishedFuture.getTrigger().failed(error);
            }
        }

        //All finished?
        completedFutures.add(future);
        if(completedFutures.equals(listenedFutures) && this.isLastCompleted.getAndSet(false)) {
            this.allFinishedFuture.getTrigger().completed(currentSuccessCompletedCount);
        }
    }

}
