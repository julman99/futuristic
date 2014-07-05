package com.julman99.futuristic.http.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Client to perform http requests that return a json element
 * @author julio
 * @author moiesk
 */
public class JsonHttpClient extends BaseHttpClient<JsonElement> {

    public JsonHttpClient(HttpAsyncEngine client) {
        super(client);
    }

    @Override
    protected JsonElement responseToObject(HttpResponse<InputStream> response) {
        JsonParser parser = new JsonParser();
        JsonElement obj = null;

        try {
            if(response.getBody() != null){
                InputStreamReader requestBody = new InputStreamReader(response.getBody());
                obj = parser.parse(requestBody);
            }
        } catch (JsonSyntaxException ex) {
            if(response.getStatusCode() == 200){
                throw ex;
            }
        }
        return obj;
    }

}
