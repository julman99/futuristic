package com.github.julman99.http.ning;

import com.julman99.futuristic.http.AbstractHttpAsyncEngineTest;
import com.julman99.futuristic.http.HttpAsyncEngine;
import com.ning.http.client.AsyncHttpClient;

/**
 * @autor: julio
 */
public class NingHttpAsyncEngineTest extends AbstractHttpAsyncEngineTest {

    @Override
    protected HttpAsyncEngine createHttpAsyncEngine(){
        AsyncHttpClient client = new AsyncHttpClient();
        return new NingHttpAsyncEngine(client);
    }

}
