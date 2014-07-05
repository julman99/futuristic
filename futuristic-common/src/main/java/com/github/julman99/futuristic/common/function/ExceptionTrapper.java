package com.github.julman99.futuristic.common.function;

/**
 * @autor: julio
 */
public interface ExceptionTrapper<E extends Exception, R> {

    R trap(E exception) throws Exception;

}
