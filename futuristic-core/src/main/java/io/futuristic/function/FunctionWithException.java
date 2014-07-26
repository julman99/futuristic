package io.futuristic.function;

/**
 * @autor: julio
 */
public interface FunctionWithException<T, R> {

    /**
     * Applies this function to the given argument.
     *
     * @param t the function argument
     * @return the function result
     */
    R apply(T t) throws Exception;
}
