package com.julman99.futuristic.http;

/**
 * @autor: julio
 */
public interface HttpResponse<T> {

    HttpParams getHeader();

    int getStatusCode();

    String getStatusMessage();

    T getBody();

}
