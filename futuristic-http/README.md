#Futuristic Http

This is a lightweight Http Client wrapper built over the [Futuristic core project](../futuristic-core). The idea of this
project is not to replace any of the well known asynchronous http engines, but only wrap them in a common interface based
on Futuristic.

An implementation is provided on [Futuristic Http Ning](../futuristic-http-ning), but you can make your own implementations
if you use a different http client.

## Usage

To get a simple url as a String:  

```java
HttpAsyncEngine someHttpAsyncEngine = ... ; //Get some http async engine 

StringHttpClient httpClient = new StringHttpClient(someHttpAsyncEngine); //Create the actual http client
http.client.get("http://google.com")
    .consume(r -> {
        String responseText = r.getBody();
    });
```

You can also create your custom HttpClient, see [Futuristic Http Gson](../futuristic-http-gson). This allows you to
easily convert the responses into any kind of type.

### GET with Query parameters

```java
HttpAsyncEngine someHttpAsyncEngine = ... ; //Get some http async engine 

StringHttpClient httpClient = new StringHttpClient(someHttpAsyncEngine); //Create the actual http client

HttpParams queryParams = new HttpParams();
queryParams.put("some", "param");
queryParams.put("other", "param with spaces"); //will be automaticall url encoded

http.client.get("http://google.com", queryParams)
    .consume(r -> {
        //code to handle response
        String body = r.getBody();            //Gets the body of the response
        HttpParams headers = r.getHeaders();  //Response headers
    });
```

### POST with Url Form Encoded parameters

```java
HttpAsyncEngine someHttpAsyncEngine = ... ; //Get some http async engine 

StringHttpClient httpClient = new StringHttpClient(someHttpAsyncEngine); //Create the actual http client

HttpParams formParams = new HttpParams();
formParams.put("some", "param");
formParams.put("other", "param with spaces"); //will be automaticall url encoded

HttpBody body = new HttpUrlEncodedBody(formParams);

http.client.post("http://some/url", body)
    .consume(r -> {
        //code to handle response
    });
```

### POST with custom body

```java
HttpAsyncEngine someHttpAsyncEngine = ... ; //Get some http async engine 

StringHttpClient httpClient = new StringHttpClient(someHttpAsyncEngine); //Create the actual http client

HttpBody body = new HttpStringBody("text/plain", "This is a text body");

http.client.post("http://some/url", body)
    .consume(r -> {
        //code to handle response
    });
```

### Common Headers for all request

```java
HttpAsyncEngine someHttpAsyncEngine = ... ; //Get some http async engine 

StringHttpClient httpClient = new StringHttpClient(someHttpAsyncEngine); //Create the actual http client
httpClient.addDefaultHeader("Authorization", "Basic john:123"); //This header will be sent for all requests
```

### Maven
Add to your ```pom.xml```

```xml
<repositories>
    <repository>
        <id>julman99-github</id>
        <url>https://raw.github.com/julman99/mvn-repo/master</url>
    </repository>
<repositories>
<dependencies>
    <dependency>
        <groupId>com.github.julman99</groupId>
        <artifactId>futuristic-http</artifactId>
        <version>0.2.0</version>
    </dependency>
</dependencies>
```
### Jar

Download from [here](https://github.com/julman99/mvn-repo/raw/master/com/github/julman99/futuristic/0.2.0/futuristic-http-0.2.0.jar)
