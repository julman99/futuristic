package io.futuristic.function;

/**
 * @autor: julio
 */
public interface ExceptionTrapper<E extends Exception, R> {

    R trap(E exception) throws Exception;

}
