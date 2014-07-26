package io.futuristic.http;

import java.io.InputStream;

/**
 * @autor: julio
 */
public final class Bodies {

    private Bodies() {

    }

    public static HttpUrlEncodedBody.Builder withForm(){
        return new HttpUrlEncodedBody.Builder();
    }

    public static HttpBody withString(String contentType, String text){
        return new HttpStringBody(contentType, text);
    }

    public static HttpBody withInputStream(String contentType, int length, InputStream data){
        return new HttpStreamBody(contentType, data, length);
    }

}
