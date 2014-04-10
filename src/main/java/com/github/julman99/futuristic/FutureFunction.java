package com.github.julman99.futuristic;

import java.util.function.Function;

/**
 * @autor: julio
 */
public interface FutureFunction<T,R> extends Function<T, Future<R>> {
}
