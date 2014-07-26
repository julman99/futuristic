package io.futuristic.http.ning;

import io.futuristic.http.AbstractHttpAsyncEngineTest;
import io.futuristic.NingHttpAsyncEngine;
import io.futuristic.http.HttpAsyncEngine;
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
