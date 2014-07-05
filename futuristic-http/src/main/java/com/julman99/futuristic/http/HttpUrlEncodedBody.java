package com.julman99.futuristic.http;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * @autor: julio
 */
public class HttpUrlEncodedBody extends HttpParams implements HttpBody {

    public HttpUrlEncodedBody(){

    }

    public HttpUrlEncodedBody(HttpParams params){
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
}
