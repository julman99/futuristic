package io.futuristic.http;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @autor: julio
 */
public abstract class AbstractHttpAsyncEngineTest {


    protected abstract HttpAsyncEngine createHttpAsyncEngine();

    private StringHttpClient createStringHttpClient(){
        return new StringHttpClient(createHttpAsyncEngine());
    }

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8089); // No-args constructor defaults to port 8080

    @Test
    public void testHttpResponse() throws Exception {
        WireMock.stubFor(WireMock.get(WireMock.urlEqualTo("/test"))
            .willReturn(WireMock.aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "text/plain")
                .withHeader("Server", "Apache")
                .withHeader("ETAG", "\"e5aa5b3c6b6ccf9be109bb685652e4af0b72b241\"")
                .withBody(".idea\n*.iml\ntarget")));

        StringHttpClient stringHttpClient = createStringHttpClient();
        String url = "http://localhost:8089/test";
        HttpResponse<String> response = stringHttpClient.request(
            Requests
            .get(url)
        ).await();

        HttpParams responseHeader = response.getHeader();

        //Some headers test
        assertEquals("18", responseHeader.getFirst("content-length"));
        assertEquals("\"e5aa5b3c6b6ccf9be109bb685652e4af0b72b241\"", responseHeader.getFirst("etag"));
        assertEquals("Apache", responseHeader.getFirst("server"));

        //Body test
        assertEquals(".idea\n*.iml\ntarget", response.getBody());

        //Status code
        assertEquals(200, response.getStatusCode());
        assertEquals("OK", response.getStatusMessage());
    }

    @Test
    public void simpleTestGet() throws Exception {
        WireMock.stubFor(WireMock.get(WireMock.urlEqualTo("/test"))
            .willReturn(WireMock.aResponse()
                .withStatus(200)
                .withBody("OK")));

        StringHttpClient stringHttpClient = createStringHttpClient();
        String url = "http://localhost:8089/test";
        HttpResponse<String> response = stringHttpClient.request(
            Requests
            .get(url)
        ).await();

        assertEquals("OK", response.getBody());
    }

    @Test
    public void simpleTestPost() throws Exception {
        WireMock.stubFor(WireMock.post(WireMock.urlEqualTo("/test"))
            .withRequestBody(WireMock.matching("a=1&b=2"))
            .willReturn(WireMock.aResponse()
                .withStatus(200)
                .withBody("OK")));

        StringHttpClient stringHttpClient = createStringHttpClient();
        String url = "http://localhost:8089/test";

        HttpResponse<String> response = stringHttpClient.request(
            Requests
                .post(url)
                .body(
                    Bodies.withForm()
                    .param("a", 1)
                    .param("b", "2")
                )
        ).await();

        assertEquals("OK", response.getBody());
    }

    @Test
    public void simpleTestPut() throws Exception {
        WireMock.stubFor(WireMock.put(WireMock.urlEqualTo("/test"))
            .willReturn(WireMock.aResponse()
                .withStatus(200)
                .withBody("OK")));

        StringHttpClient stringHttpClient = createStringHttpClient();
        String url = "http://localhost:8089/test";
        HttpResponse<String> response = stringHttpClient.request(
            Requests
            .put(url)
        )
        .await();

        assertEquals("OK", response.getBody());
    }

    @Test
    public void simpleTestDelete() throws Exception {
        WireMock.stubFor(WireMock.delete(WireMock.urlEqualTo("/test"))
            .willReturn(WireMock.aResponse()
                .withStatus(200)
                .withBody("OK")));

        StringHttpClient stringHttpClient = createStringHttpClient();
        String url = "http://localhost:8089/test";
        HttpResponse<String> response = stringHttpClient.request(
            Requests
            .delete(url)
        ).await();

        assertEquals("OK", response.getBody());
    }

    @Test
    public void testHeaderSending() throws Exception {
        WireMock.stubFor(WireMock.get(WireMock.urlEqualTo("/test"))
            .withHeader("Test", WireMock.matching("ok"))
            .withHeader("Test2", WireMock.matching("ok2"))
            .willReturn(WireMock.aResponse()
                .withStatus(200)
                .withBody("OK")));

        StringHttpClient stringHttpClient = createStringHttpClient();
        String url = "http://localhost:8089/test";

        HttpResponse<String> response = stringHttpClient.request(
            Requests
                .get(url)
                .header("Test", "ok")
                .header("Test2", "ok2")
        ).await();

        assertEquals("OK", response.getBody());
    }
}
