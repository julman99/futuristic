package com.julman99.futuristic.http;

import java.util.Map;

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

    static class Builder{

        private String url;
        private HttpParams headers = new HttpParams();
        private HttpParams query = new HttpParams();
        private HttpBody body;
        private HttpVerb verb = HttpVerb.GET;
        private boolean followRedirects = false;

        private HttpRequest request;

        public Builder(){

        }

        public Builder(String url){
            this.url = url;
        }

        public Builder query(String name, String value){
            this.query.put(name, value);
            return this;
        }

        public Builder query(HttpParams query){
            this.query.putAll(query);
            return this;
        }

        public Builder header(String name, String value){
            this.headers.put(name, value);
            return this;
        }

        public Builder headers(HttpParams headers){
            this.headers.putAll(headers);
            return this;
        }

        public Builder headers(Map<String, String> headers){
            this.headers.putAll(headers);
            return this;
        }

        public Builder body(String body){
            this.body(new HttpStringBody(body));
            return this;
        }

        public Builder body(HttpBody body){
            this.body = body;
            return this;
        }

        public Builder method(HttpVerb verb){
            this.verb = verb;
            return this;
        }

        public Builder url(String url){
            this.url = url;
            return this;
        }

        public Builder followRedirects(boolean followRedirects){
            this.followRedirects = followRedirects;
            return this;
        }

        public HttpRequest create(){

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
