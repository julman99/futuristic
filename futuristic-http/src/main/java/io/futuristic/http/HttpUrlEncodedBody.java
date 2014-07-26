package io.futuristic.http;

import io.futuristic.http.util.GenericBuilder;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * @autor: julio
 */
public final class HttpUrlEncodedBody extends HttpParams implements HttpBody {

    private HttpUrlEncodedBody(){

    }

    private HttpUrlEncodedBody(HttpParams params){
        this.putAll(params);
    }

    @Override
    public int getContentLength() {
        return this.toUrlEncodedString().length();
    }

    @Override
    public String getContentType() {
        return "application/x-www-form-urlencoded";
    }

    @Override
    public InputStream toInputStream() {
        try {
            return new ByteArrayInputStream(this.toUrlEncodedString().getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            //nothing because UTF-8 is hardcoded
        }
        return null;
    }

    public final static class Builder implements GenericBuilder<HttpUrlEncodedBody> {

        private final HttpParams body = new HttpParams();

        Builder() {
        }

        public Builder param(String name, Object value){
            body.put(name, value);
            return this;
        }

        public HttpUrlEncodedBody build(){
            return new HttpUrlEncodedBody(body);
        }

    }
}
