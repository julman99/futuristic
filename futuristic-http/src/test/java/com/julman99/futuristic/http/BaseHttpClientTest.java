package com.julman99.futuristic.http;

import com.github.julman99.futuristic.common.Future;
import com.github.julman99.futuristic.common.Futures;
import com.julman99.futuristic.http.util.RandomResponseAndLatencyHttpEngine;
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
        int n = 10;
        final CountDownLatch latch = new CountDownLatch(n);

        StringHttpClient http = new StringHttpClient(new RandomResponseAndLatencyHttpEngine());
        for(int i=0;i<n;i++){
            http.get("SOME_URL").consume(r -> {
                latch.countDown();
                latch.await();
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
        StringHttpClient http = new StringHttpClient(new HttpAsyncEngine() {
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
        });
        http.setNonStatus200Error(true);

        try{
            http.get("200").get();
            http.get("201").get();
            http.get("202").get();
            http.get("203").get();
            http.get("204").get();
            http.get("205").get();
            http.get("206").get();
        }catch (Exception ex){
            fail("No exception should have been thrown");
        }

        try{
            http.get("404").get();
        }catch (HttpException ex){
            assertEquals(404, ex.getStatusCode());
        }catch (Exception ex){
            fail("An HttpException exception should have been thrown");
        }

        http.setNonStatus200Error(false);
        try{
            http.get("404").get();
            http.get("500").get();
        }catch (Exception ex){
            fail("No exception should have been thrown");
        }
    }

    /**
     * TODO:
     * IMPORTANT: these tests need to be done asap:
     * 1. Test every method, GET, POST, PUT, DELETE, OPTIONS
     * 2. Test that GET /somePath?a=1 and passing additional getParameters on the ArrayList are correclty concatenated
     *
     */
}
