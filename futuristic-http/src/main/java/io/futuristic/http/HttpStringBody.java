package io.futuristic.http;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Created by rubioz on 12/5/13.
 */
public class HttpStringBody implements HttpBody{
    private final String contentType;
    private final String body;

    public HttpStringBody(String body){
        this("text/plain", body);

    }
    public HttpStringBody(String contentType, String body){
        this.contentType = contentType;
        this.body = body;
    }

    @Override
    public int getContentLength() {
        return body.length();
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public InputStream toInputStream() {
        return new ByteArrayInputStream(body.getBytes());
    }
}
