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
httpClient.request(Requests.get("http://google.com"))
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

httpClient.request(
	    Requests.get("http://google.com")
	    .query("some", "param")
    	.query("other", "param with spaces"); //will be automaticall url encoded
    	.header("some header", "for your request")
	).consume(r -> {
        //code to handle response
        String body = r.getBody();            //Gets the body of the response
        HttpParams headers = r.getHeaders();  //Response headers
    });
```

### POST with Url Form Encoded parameters

```java
HttpAsyncEngine someHttpAsyncEngine = ... ; //Get some http async engine 

StringHttpClient httpClient = new StringHttpClient(someHttpAsyncEngine); //Create the actual http client

HttpBody body = new HttpUrlEncodedBody(formParams);

httpClient.post(
	Requests.post("http://some/url")
	.body(
		Bodies.withForm()
		.param("some", "param");
		.param("other", "param with spaces"); //will be automaticall url encoded
	).consume(r -> {
        //code to handle response
    });
```

### POST with custom body

```java
HttpAsyncEngine someHttpAsyncEngine = ... ; //Get some http async engine 

StringHttpClient httpClient = new StringHttpClient(someHttpAsyncEngine); //Create the actual http client

httpClient.post(
	Requests.post("http://some/url")
	.body(
		Bodies.withString("text/plain", "arbitrary string")
	).consume(r -> {
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
        <groupId>io.futuristic</groupId>
        <artifactId>futuristic-http</artifactId>
        <version>0.3.0</version>
    </dependency>
</dependencies>
```
### Jar

Download from [here](https://github.com/julman99/mvn-repo/raw/master/io/futuristic/futuristic-http/0.3.0/futuristic-http-0.3.0.jar)
