package io.futuristic.http;

/**
 * @autor: julio
 */
public interface HttpResponse<T> {

    HttpParams getHeader();

    int getStatusCode();

    String getStatusMessage();

    T getBody();

}
