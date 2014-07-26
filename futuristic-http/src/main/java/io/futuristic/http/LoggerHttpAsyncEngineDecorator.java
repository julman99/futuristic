package io.futuristic.http;

import io.futuristic.Future;
import com.google.common.io.ByteStreams;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @autor: julio
 */
public class LoggerHttpAsyncEngineDecorator implements HttpAsyncEngine {


    private final HttpAsyncEngine forwarded;

    public LoggerHttpAsyncEngineDecorator(HttpAsyncEngine forwarded) {
        this.forwarded = forwarded;
    }

    @Override
    public Future<HttpResponse<InputStream>> dispatch(final HttpRequest originalRequest) {
        final HttpBody originalBody = originalRequest.getBody();
        final HttpBody newBody = new HttpStreamBody(originalBody.getContentType(), originalBody.toInputStream(), originalBody.getContentLength());
        final HttpRequest wrappedRequest = new HttpRequest() {
            @Override
            public String getUrl() {
                return originalRequest.getUrl();
            }

            @Override
            public HttpParams getHeaders() {
                return originalRequest.getHeaders();
            }

            @Override
            public HttpBody getBody() {
                return newBody;
            }

            @Override
            public HttpVerb getVerb() {
                return originalRequest.getVerb();
            }

            @Override
            public boolean followRedirects() {
                return originalRequest.followRedirects();
            }
        };

        try {
            Logger.getLogger(this.getClass().getName()).info(">>> " + wrappedRequest.getVerb() + ": " + wrappedRequest.getUrl() + "\n" + new String(ByteStreams.toByteArray(wrappedRequest.getBody().toInputStream())));
        } catch (IOException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Error logging request", e);
        }

        return forwarded.dispatch(wrappedRequest).map(result -> {

            final byte[] response = ByteStreams.toByteArray(result.getBody());

            HttpResponse<InputStream> newResponse = new HttpResponse<InputStream>() {
                @Override
                public HttpParams getHeader() {
                    return result.getHeader();
                }

                @Override
                public int getStatusCode() {
                    return result.getStatusCode();
                }

                @Override
                public String getStatusMessage() {
                    return result.getStatusMessage();
                }

                @Override
                public InputStream getBody() {
                    return new ByteArrayInputStream(response);
                }
            };

            Logger.getLogger(this.getClass().getName()).info("<<< " + wrappedRequest.getVerb() + ": " + wrappedRequest.getUrl() + "\n" + new String(response));

            return newResponse;
        });
    }

    @Override
    public void shutdown() {
        forwarded.shutdown();
    }

}
