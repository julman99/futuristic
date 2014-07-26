package io.futuristic.http;

/**
 * @autor: julio
 */
public class HttpException extends RuntimeException{

    private int code;

    public HttpException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getStatusCode(){
        return code;
    }
}
