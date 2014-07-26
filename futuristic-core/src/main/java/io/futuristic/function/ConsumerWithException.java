package io.futuristic.function;

/**
 * @autor: julio
 */
public interface ConsumerWithException<T> {

    /**
     * Performs this operation on the given argument.
     *
     * @param t the input argument
     */
    void accept(T t) throws Exception;
}
