package io.futuristic.http;

import io.futuristic.Future;
import io.futuristic.Futures;
import io.futuristic.http.*;
import io.futuristic.http.util.RandomResponseAndLatencyHttpEngine;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @autor: julio
 */
public class BaseHttpClientTest {

    @Test
    public void testConcurrency(){
        int n = 1000;
        final CountDownLatch latch = new CountDownLatch(n);

        StringHttpClient http = new StringHttpClient(new RandomResponseAndLatencyHttpEngine(1, 30));
        for(int i=0;i<n;i++){
            http.request(HttpRequests
                .get("SOME_URL")
            ).consume(r -> {
                latch.countDown();
            });
        }

        try {
            latch.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
        }
        assertEquals(0, latch.getCount());
    }

    @Test
    public void test200(){
        StringHttpClient http = new StringHttpClient(new StatusCodeReplyHttpAsyncEngine());

        try{
            http.request(HttpRequests.get("200")).get();
            http.request(HttpRequests.get("201")).get();
            http.request(HttpRequests.get("202")).get();
            http.request(HttpRequests.get("203")).get();
            http.request(HttpRequests.get("204")).get();
            http.request(HttpRequests.get("205")).get();
            http.request(HttpRequests.get("206")).get();
        }catch (Exception ex){
            fail("No exception should have been thrown");
        }

        try{
            http.request(HttpRequests.get("404")).get();
        }catch (HttpException ex){
            assertEquals(404, ex.getStatusCode());
        }catch (Exception ex){
            fail("An HttpException exception should have been thrown");
        }
    }

    @Test
    public void test404(){
        StringHttpClient http = new StringHttpClient(new StatusCodeReplyHttpAsyncEngine());

        try{
            http.request(HttpRequests.get("404")).get();
        }catch (HttpException ex){
            assertEquals(404, ex.getStatusCode());
        }catch (Exception ex){
            fail("An HttpException exception should have been thrown");
        }
    }

    @Test
    public void test500(){
        StringHttpClient http = new StringHttpClient(new StatusCodeReplyHttpAsyncEngine());

        try{
            http.request(HttpRequests.get("500")).get();
        }catch (HttpException ex){
            assertEquals(500, ex.getStatusCode());
        }catch (Exception ex){
            fail("An HttpException exception should have been thrown");
        }
    }

    /**
     * TODO:
     * IMPORTANT: these tests need to be done asap:
     * 1. Test every method, GET, POST, PUT, DELETE, OPTIONS
     * 2. Test that GET /somePath?a=1 and passing additional getParameters on the ArrayList are correclty concatenated
     *
     */

    private static class StatusCodeReplyHttpAsyncEngine implements HttpAsyncEngine {
        @Override
        public Future<HttpResponse<InputStream>> dispatch(final HttpRequest request) {
            HttpResponse<InputStream> rawResponse = new HttpResponse<InputStream>() {
                @Override
                public HttpParams getHeader() {
                    return new HttpParams();
                }

                @Override
                public int getStatusCode() {
                    return Integer.parseInt(request.getUrl());
                }

                @Override
                public String getStatusMessage() {
                    return "";
                }

                @Override
                public InputStream getBody() {
                    return new ByteArrayInputStream("NO BODY".getBytes());
                }
            };
            return Futures.withValue(rawResponse);
        }

        @Override
        public void shutdown() {
            //nothing
        }
    }
}
