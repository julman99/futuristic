package io.futuristic.http;

import java.io.InputStream;

/**
 * @autor: julio
 */
public interface HttpBody {
    int getContentLength();
    String getContentType();
    InputStream toInputStream();
}
