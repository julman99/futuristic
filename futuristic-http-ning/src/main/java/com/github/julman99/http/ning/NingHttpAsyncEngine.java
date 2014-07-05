package com.github.julman99.http.ning;

import com.ning.http.client.*;
import com.ning.http.client.generators.ByteArrayBodyGenerator;
import com.ning.http.client.generators.InputStreamBodyGenerator;
import com.pixable.utils.callback.Callback;
import com.pixable.utils.future.Future;
import com.pixable.utils.future.Futures;
import com.pixable.utils.future.Transformer;
import com.pixable.utils.future.Trigger;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @autor: julio
 */
public class NingHttpAsyncEngine implements HttpAsyncEngine {

    private AsyncHttpClient client;

    public NingHttpAsyncEngine(AsyncHttpClient client) {
        this.client = client;
    }

    @Override
    public void shutdown() {
        this.client.close();
    }

    @Override
    public Future<HttpResponse<InputStream>> dispatch(HttpRequest request) {
        Request ningRequest = buildRequest(request);
        return executeRequest(ningRequest);
    }

    protected Request buildRequest(HttpRequest request) {

        try{

            RequestBuilder ningRequest = new RequestBuilder(request.getVerb().name());

            for (Map.Entry<String, String> entry : request.getHeaders().entries()) {
                ningRequest.setHeader(entry.getKey(), entry.getValue());
            }

            if (request.getVerb() != HttpVerb.GET) {
                InputStream bodyStream = request.getBody().toInputStream();
                if(bodyStream instanceof ByteArrayInputStream){
                    ningRequest.setBody(new ByteArrayBodyGenerator(IOUtils.toByteArray(bodyStream)));
                } else {
                    ningRequest.setBody(new InputStreamBodyGenerator(bodyStream));
                }

            }

            ningRequest.setUrl(request.getUrl());
            ningRequest.setFollowRedirects(request.followRedirects());

            return ningRequest.build();
        }catch (Exception ex){
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Error building http request", ex);
            return null;
        }
    }

    protected Future<HttpResponse<InputStream>> executeRequest(final Request httpRequest) {
        return Futures.withTrigger(new Trigger<Response>() {
            @Override
            public void triggerLater(final Callback<Response> trigger) throws Exception {
                client.executeRequest(httpRequest, new AsyncCompletionHandler<Response>() {
                    @Override
                    public Response onCompleted(final Response ningResponse) throws Exception {
                        trigger.completed(ningResponse);
                        return ningResponse;
                    }

                    @Override
                    public void onThrowable(Throwable t) {
                        if (t instanceof Exception) {
                            trigger.failed((Exception) t);
                        } else {
                            trigger.failed(new RuntimeException(t));
                        }
                    }
                });
            }
        }).then(new Transformer<Response, HttpResponse<InputStream>>() {
            @Override
            public HttpResponse<InputStream> completed(final Response ningResponse) throws Exception {
                final HttpParams headers = new HttpParams();
                for(Map.Entry<String, List<String>> entry: ningResponse.getHeaders().entrySet()){
                    headers.putAll(entry.getKey(), entry.getValue());
                }

                HttpResponse<InputStream> response = new HttpResponse<InputStream>() {

                    @Override
                    public HttpParams getHeader() {
                        return headers;
                    }

                    @Override
                    public int getStatusCode() {
                        return ningResponse.getStatusCode();
                    }

                    @Override
                    public String getStatusMessage() {
                        return ningResponse.getStatusText();
                    }

                    @Override
                    public InputStream getBody() {
                        try {
                            return ningResponse.getResponseBodyAsStream();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                };
                return response;
            }
        });
    }
}
