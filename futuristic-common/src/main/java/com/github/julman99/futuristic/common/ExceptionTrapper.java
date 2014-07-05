package com.github.julman99.futuristic.common;

/**
 * @autor: julio
 */
public interface ExceptionTrapper<E extends Exception, R> {

    R trap(E exception) throws Exception;

}
