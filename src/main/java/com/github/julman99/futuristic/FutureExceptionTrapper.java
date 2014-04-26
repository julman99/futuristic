package com.github.julman99.futuristic;

/**
 * @autor: julio
 */
public interface FutureExceptionTrapper<E extends Exception, R> {

    Future<R> trap(E exception) throws Exception;

}
