package io.futuristic.http;

import io.futuristic.Future;

import java.io.InputStream;

/**
 * @autor: julio
 */
public interface HttpAsyncEngine {

    Future<HttpResponse<InputStream>> dispatch(HttpRequest request);

    void shutdown();

}
