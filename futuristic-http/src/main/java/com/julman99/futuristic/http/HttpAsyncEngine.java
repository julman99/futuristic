package com.julman99.futuristic.http;

import com.github.julman99.futuristic.common.Future;

import java.io.InputStream;

/**
 * @autor: julio
 */
public interface HttpAsyncEngine {

    Future<HttpResponse<InputStream>> dispatch(HttpRequest request);

    void shutdown();

}
