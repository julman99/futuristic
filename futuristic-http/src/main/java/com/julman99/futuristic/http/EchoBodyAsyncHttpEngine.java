package com.julman99.futuristic.http;


import com.github.julman99.futuristic.common.Future;
import com.github.julman99.futuristic.common.Futures;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Implements an http engine that will echo back the body of a request to the response
 * @autor: julio
 */
public class EchoBodyAsyncHttpEngine implements HttpAsyncEngine {

    @Override
    public Future<HttpResponse<InputStream>> dispatch(HttpRequest request) {
        final InputStream bodyInputStream;

        if(request.getBody() != null){
            bodyInputStream = request.getBody().toInputStream();
        } else {
            bodyInputStream = new ByteArrayInputStream(new byte[0]);
        }
        return Futures.withValue((HttpResponse<InputStream>) new HttpResponse<InputStream>() {
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
                return "OK";
            }

            @Override
            public InputStream getBody() {
                return bodyInputStream;
            }
        });
    }

    @Override
    public void shutdown() {
        //nothing
    }
}
