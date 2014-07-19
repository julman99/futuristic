package com.julman99.futuristic.http;

import com.julman99.futuristic.http.util.GenericBuilder;

/**
 * @autor: julio
 */
public interface HttpRequest {

    static final String HEADER_CONTENT_TYPE = "Content-Type";
    static final String HEADER_CONTENT_LENGTH = "Content-Length";

    String getUrl();

    HttpParams getHeaders();

    HttpBody getBody();

    HttpVerb getVerb();

    boolean followRedirects();

    static class Builder<T> implements GenericBuilder<HttpRequest> {

        private final String url;
        private final HttpVerb verb;
        private final HttpParams headers = new HttpParams();
        private final HttpParams query = new HttpParams();

        private HttpBody body;
        private boolean followRedirects = false;

        public Builder(String url, HttpVerb verb){
            this.url = url;
            this.verb = verb;
        }

        public Builder<T> query(String name, String value){
            this.query.put(name, value);
            return this;
        }

        public Builder<T> query(HttpParams query){
            this.query.putAll(query);
            return this;
        }

        public Builder<T> header(String name, String value){
            this.headers.put(name, value);
            return this;
        }

        public Builder<T> header(HttpParams headers){
            this.headers.putAll(headers);
            return this;
        }

        public Builder<T> body(String contentType, String body){
            this.body(new HttpStringBody(contentType, body));
            return this;
        }

        public Builder<T> body(HttpBody body){
            this.body = body;
            return this;
        }

        public Builder<T> body(GenericBuilder<? extends HttpBody> body){
            this.body = body.build();
            return this;
        }

        public Builder<T> followRedirects(boolean followRedirects){
            this.followRedirects = followRedirects;
            return this;
        }

        public HttpRequest build(){

            if(verb == HttpVerb.GET){
                this.headers.removeAll(HEADER_CONTENT_TYPE); //Quick-dirty fix to prevent content type on get request
            }

            if(body != null){
                if(body.getContentType() != null && !body.getContentType().isEmpty() && !this.headers.containsKey(HEADER_CONTENT_TYPE)){
                    this.headers.put(HEADER_CONTENT_TYPE, body.getContentType());
                }
                if(body.getContentLength() >= 0  && !this.headers.containsKey(HEADER_CONTENT_LENGTH)){
                    this.headers.put(HEADER_CONTENT_LENGTH, body.getContentLength());
                }
            }

            return new HttpRequest() {
                @Override
                public String getUrl() {
                    StringBuilder urlFinal = new StringBuilder(url);
                    if(!query.isEmpty()){
                        if(url.contains("?")){
                            if(!url.endsWith("&")){
                                urlFinal.append("&");
                            }
                        } else {
                            urlFinal.append("?");
                        }
                        String queryStr = query.toUrlEncodedString();
                        urlFinal.append(queryStr);
                    }
                    return urlFinal.toString();
                }

                @Override
                public HttpParams getHeaders() {
                    return headers;
                }

                @Override
                public HttpBody getBody(){
                    return body;
                }

                @Override
                public HttpVerb getVerb() {
                    return verb;
                }

                @Override
                public boolean followRedirects() {
                    return followRedirects;
                }
            };
        }

    }
}
