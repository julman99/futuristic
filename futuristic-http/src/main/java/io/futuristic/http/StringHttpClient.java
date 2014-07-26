package io.futuristic.http;

import com.google.common.io.ByteStreams;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @autor: julio
 */
public class StringHttpClient extends BaseHttpClient<String> {

    public StringHttpClient(HttpAsyncEngine client) {
        super(client);
    }

    @Override
    protected String responseToObject(HttpResponse<InputStream> response) {
        try {
            if(response.getBody() != null){
                return new String(ByteStreams.toByteArray(response.getBody()));
            } else {
                return null;
            }
        } catch (IOException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Error converting response to string", e);
            return null;
        }
    }
}
