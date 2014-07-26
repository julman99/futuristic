package io.futuristic.http;

/**
 * @autor: julio
 */
public class Requests {

    public static <T> HttpRequest.Builder<T> get(String url) {
        return new HttpRequest.Builder<>(url, HttpVerb.GET);
    }

    public static <T> HttpRequest.Builder<T> post(String url) {
        return new HttpRequest.Builder<>(url, HttpVerb.POST);
    }

    public static <T> HttpRequest.Builder<T> delete(String url) {
        return new HttpRequest.Builder<>(url, HttpVerb.DELETE);
    }

    public static <T> HttpRequest.Builder<T> put(String url) {
        return new HttpRequest.Builder<>(url, HttpVerb.PUT);
    }

    public static <T> HttpRequest.Builder<T> patch(String url) {
        return new HttpRequest.Builder<>(url, HttpVerb.PATCH);
    }


}
