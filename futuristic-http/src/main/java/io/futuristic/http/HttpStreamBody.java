package io.futuristic.http;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * @autor: julio
 */
public class HttpStreamBody implements HttpBody {

    private final String contentType;
    private final InputStream data;
    private final int contentLength;

    public HttpStreamBody(String contentType, byte[] data) {
        this.contentType = contentType;
        this.data = new ByteArrayInputStream(data);
        this.contentLength = data.length;
    }

    public HttpStreamBody(String contentType, InputStream data, int length) {
        this.contentType = contentType;
        this.data = data;
        this.contentLength = length;
    }

    @Override
    public int getContentLength() {
        return contentLength;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public InputStream toInputStream() {
        return data;
    }
}
