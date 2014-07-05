package com.julman99.futuristic.http.util;

import com.github.julman99.futuristic.common.Future;
import com.github.julman99.futuristic.common.Futures;
import com.julman99.futuristic.http.HttpAsyncEngine;
import com.julman99.futuristic.http.HttpParams;
import com.julman99.futuristic.http.HttpRequest;
import com.julman99.futuristic.http.HttpResponse;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Random;

/**
 * @autor: julio
 */
public class RandomResponseAndLatencyHttpEngine implements HttpAsyncEngine {

    private final Random random = new Random();

    @Override
    public Future<HttpResponse<InputStream>> dispatch(HttpRequest request) {
        return Futures.withCallable(() -> {
            Thread.sleep(random.nextInt(400));
            String response = Long.toString(random.nextLong());
            return new HttpResponse<InputStream>() {
                @Override
                public HttpParams getHeader() {
                    return new HttpParams();
                }

                @Override
                public int getStatusCode() {
                    return 200;
                }

                @Override
                public String getStatusMessage() {
                    return "";
                }

                @Override
                public InputStream getBody() {
                    return new ByteArrayInputStream(response.getBytes());
                }
            };
        });
    }

    @Override
    public void shutdown() {

    }

}
