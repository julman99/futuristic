package com.github.julman99.futuristic.common.util;

import com.github.julman99.futuristic.common.FutureWithTrigger;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @autor: julio
 */
public class Triggerer {

    public static <T> void triggerValue(T value, FutureWithTrigger<T> futureWithTrigger){
        futureWithTrigger.getTrigger().completed(value);
    }

    public static <T> void triggerValueAsync(long delay, T value, FutureWithTrigger<T> futureWithTrigger){
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                futureWithTrigger.getTrigger().completed(value);
            }
        }, delay);
    }

    public static <T> void triggerError(Exception error, FutureWithTrigger<T> futureWithTrigger){
        futureWithTrigger.getTrigger().failed(error);
    }

    public static <T> void triggerErrorAsync(long delay, Exception error, FutureWithTrigger<T> futureWithTrigger){
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                futureWithTrigger.getTrigger().failed(error);
            }
        }, delay);
    }

}
