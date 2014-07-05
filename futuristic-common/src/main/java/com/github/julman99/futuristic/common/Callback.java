package com.github.julman99.futuristic.common;

/**
 * @autor: julio
 */
public interface Callback<T> {

    void completed(T result);
    void failed(Exception throwable);

}
