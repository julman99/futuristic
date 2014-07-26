package io.futuristic.http;

import io.futuristic.Future;
import com.google.common.io.ByteStreams;

import java.io.InputStream;

/**
 * @autor: julio
 */
public abstract class BaseHttpClient<T> {

    private HttpAsyncEngine engine;
    private HttpParams defaultHeaders = new HttpParams();

    public BaseHttpClient(HttpAsyncEngine engine) {
        this.engine = engine;
    }

    public void addDefaultHeader(String key, String value) {
        defaultHeaders.put(key, value);
    }


    public Future<HttpResponse<T>> request(final HttpRequest.Builder request) {
        return this.send(request.build());
    }

    public Future<HttpResponse<T>> send(final HttpRequest request) {
        request.getHeaders().putAll(defaultHeaders);
        return engine.dispatch(request)
            .map(response -> {
                if (response.getStatusCode() / 100 != 2) {
                    String reason = response.getStatusMessage();
                    if (response.getBody() != null) {
                        reason += " - " + new String(ByteStreams.toByteArray(response.getBody()));
                    }
                    throw new HttpException(response.getStatusCode(), reason);
                } else {
                    return buildResponse(response);
                }
            });
    }

    private HttpResponse<T> buildResponse(HttpResponse<InputStream> originalResponse){
        final int statusCode = originalResponse.getStatusCode();
        final String statusMessage = originalResponse.getStatusMessage();
        final T object = responseToObject(originalResponse);
        final HttpParams headers = originalResponse.getHeader();

        return new HttpResponse<T>() {
            @Override
            public int getStatusCode() {
                return statusCode;
            }

            @Override
            public String getStatusMessage() {
                return statusMessage;
            }

            @Override
            public T getBody() {
                return object;
            }

            @Override
            public HttpParams getHeader() {
                return headers;
            }
        };
    }

    protected abstract T responseToObject(HttpResponse<InputStream> response);

}
