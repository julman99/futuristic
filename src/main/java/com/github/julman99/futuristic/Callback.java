package com.github.julman99.futuristic;

/**
 * @autor: julio
 */
public interface Callback<T> {

    void completed(T result);
    void failed(Exception throwable);
}
