package io.futuristic.http;

import com.google.common.io.ByteStreams;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @autor: julio
 */
public class InputStreamHttpClient extends BaseHttpClient<byte[]> {

    public InputStreamHttpClient(HttpAsyncEngine client) {
        super(client);
    }

    @Override
    protected byte[] responseToObject(HttpResponse<InputStream> response) {
        try {
            if(response.getBody() != null){
                return ByteStreams.toByteArray(response.getBody());
            }
        } catch (IOException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Error getting request", e);
        }
        return null;
    }

}
