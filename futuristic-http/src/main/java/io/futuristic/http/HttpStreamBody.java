package io.futuristic.http;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * @autor: julio
 */
public final class HttpStreamBody implements HttpBody {

    private final String contentType;
    private final InputStream data;
    private final int contentLength;

    HttpStreamBody(String contentType, byte[] data) {
        this.contentType = contentType;
        this.data = new ByteArrayInputStream(data);
        this.contentLength = data.length;
    }

    HttpStreamBody(String contentType, InputStream data, int length) {
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
