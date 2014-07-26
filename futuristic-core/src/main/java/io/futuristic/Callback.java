package io.futuristic;

/**
 * @autor: julio
 */
public interface Callback<T> {

    void completed(T result);
    void failed(Exception throwable);

}
