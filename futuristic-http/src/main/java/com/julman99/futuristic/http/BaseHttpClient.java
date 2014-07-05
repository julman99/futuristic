package com.julman99.futuristic.http;

import com.github.julman99.futuristic.common.Future;

import java.io.InputStream;

/**
 * @autor: julio
 */
public abstract class BaseHttpClient<T> {

    private HttpAsyncEngine engine;
    private HttpParams defaultHeaders = new HttpParams();
    private boolean nonStatus200Error = false;

    /*
     * If a status code different that 200 is received, an exception will be thrown.
     * Otherwise, a json element will be tried to be parsed from the response if present
     * and will be returned in the result
     */
    public void setNonStatus200Error(boolean nonStatus200Error) {
        this.nonStatus200Error = nonStatus200Error;
    }

    public boolean isNonStatus200Error() {
        return nonStatus200Error;
    }

    public BaseHttpClient(HttpAsyncEngine engine) {
        this.engine = engine;
    }


    public void addDefaultHeader(String key, String value) {
        defaultHeaders.put(key, value);
    }


    //GET
    public Future<HttpResponse<T>> get(String url) {
        return this.get(url, new HttpParams());
    }

    public Future<HttpResponse<T>> get(String url,  HttpParams query) {
        HttpRequest request = new HttpRequest.Builder()
                .url(url)
                .method(HttpVerb.GET)
                .query(query)
                .create();
        return exec(request);
    }

    //POST
    public Future<HttpResponse<T>> post(String url) {
        return post(url, new HttpParams(), new HttpUrlEncodedBody());
    }

    public Future<HttpResponse<T>> post(String url,  HttpBody body) {
        return post(url, new HttpParams(), body);
    }

    public Future<HttpResponse<T>> post(String url,  HttpParams query, HttpBody body) {
        HttpRequest request = new HttpRequest.Builder()
                .url(url)
                .method(HttpVerb.POST)
                .query(query)
                .body(body)
                .create();
        return exec(request);
    }

    //PUT
    public Future<HttpResponse<T>> put(String url) {
        return put(url, new HttpParams(), new HttpUrlEncodedBody());
    }

    public Future<HttpResponse<T>> put(String url,  HttpParams query) {
        return put(url, query, new HttpUrlEncodedBody());
    }

    public Future<HttpResponse<T>> put(String url,  HttpParams query, HttpBody body) {
        HttpRequest request = new HttpRequest.Builder()
                .url(url)
                .method(HttpVerb.PUT)
                .query(query)
                .body(body)
                .create();
        return exec(request);
    }


    //DELETE
    public Future<HttpResponse<T>> delete(String url) {
        return delete(url, new HttpParams(), new HttpUrlEncodedBody());
    }

    public Future<HttpResponse<T>> delete(String url,  HttpParams query) {
        return delete(url, query, new HttpUrlEncodedBody());
    }

    public Future<HttpResponse<T>> delete(String url,  HttpParams query, HttpBody body) {
        HttpRequest request = new HttpRequest.Builder()
                .url(url)
                .method(HttpVerb.DELETE)
                .query(query)
                .body(body)
                .create();
        return exec(request);
    }

    //ANY REQUEST
    public Future<HttpResponse<T>> exec(final HttpRequest request) {
        request.getHeaders().putAll(defaultHeaders);
        return engine.dispatch(request)
            .map(response -> {
                if (response.getStatusCode() / 100 != 2 && nonStatus200Error) {
                    String reason = response.getStatusMessage();
//                    if (response.getBody() != null) {
//                        InputStreamReader requestBody = new InputStreamReader(response.getBody());
//                        reason += " - " + IOUtils.toString(requestBody);
//                    }
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
