#Futuristic Http Ning

Allows to use the awesome [Ning Http Async Client](https://github.com/AsyncHttpClient/async-http-client) with Futuristic

## Usage

```java
AsyncHttpClient ningClient = new AsyncHttpClient(); //This creates the Ning Http Client

HttpAsyncEngine httpEngine = new NingHttpAsyncEngine(ningClient); //This creates the Futuristic wrapper
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
        <artifactId>futuristic-http-ning</artifactId>
        <version>0.3.0</version>
    </dependency>
</dependencies>
```
### Jar

Download from [here](https://github.com/julman99/mvn-repo/raw/master/io/futuristic/futuristic-http-ning/0.3.0/futuristic-http-ning-0.3.0.jar)
