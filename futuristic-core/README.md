# Futuristic  Core

Futuristic Core lets you create and consume Futures.

## Consuming futures

### Synchronous consuming

If you just want to wait for the result of a Future, blocking the thread, this is the best way to do it.

```java
Future<Integer> someFuture = ...;
Integer result = someFuture.await(); //This will block until the result of the future is available
```

### Asynchronous consuming

This is a classic way of consuming a future. You register a consumer, and it will get called when the future is
available

```java
Future<Integer> someFuture = ...;
someFuture.consume(result -> {
    System.out.println(result); //This callback will get executed once the result is available    
});
```

### Transforming the result

Allows you to chain different callbacks to transform the result of a future. For example, image a method that must
return the concatenated name and the age of a User. Now imagine the User must be retrieved asynchronously from a server.
 
With the ```map``` method, you can transform a Future\<User\> to a Future\<String\> using very little code and without doing
any callback-based programming.

Look at the following example:

```java

public Future<User> getUser(){
    //this code loads the user asynchronously from a server
}

public Future<String> getNameAndAgeFormatted(){
    Future<User> userFuture = getUser();
    return userFuture.map(user -> {
        return user.getName() + ", age " + user.getAge();
    });
}

```

Now lets see the same example without the ```map``` feature:

```java

public Future<User> getUser(){
    //this code loads the user asynchronously from a server
}

public Future<String> getNameAndAgeFormatted(){
    Future<User> userFuture = getUser();
    FutureWithTrigger<String> futureWithTrigger = Futures.withTrigger();
    return userFuture.consume(user -> {
        String result = user.getName() + ", age " + user.getAge();
        futureWithTrigger.getTrigger().completed(result);
    });
    return futureWithTrigger().getFuture();
}

```

As you can see, using the ```map``` feature is much simpler, there is less code involved, it is easier to read and
most importantly, the code will not compile if a formatted string is not returned. Without the ```map``` approach, you
can still not call the ```futureWithTrigger``` and it will compile. 

### Handling Exceptions

Futuristic allows you to trap, and recover from Exceptions. In regular Callback programming, you usually get a ```failed```
callback, with an ```Exception```. Then you manually need to check for the type of the ```Exception```. Futuristic does
this work for you automatically.

Lets look at the same example of ```map```

```java

public Future<User> getUser(){
    //this code loads the user asynchronously from a server
}

public Future<String> getNameAndAgeFormatted(){
    Future<User> userFuture = getUser();
    return userFuture.map(user -> {
        return user.getName() + ", age " + user.getAge();
    }).trap(InvalidUserException.class, e -> {
        return "The user does not exists"; //Futuristic will only call this lambda if the Exception is of type
                                           //InvalidUserException. Since this function is returning a String, it is
                                           //effectively recovering from the Exception
    }).trap(Exception.class, e -> {
        return "Unkown error ocurred";     //Futuristic will call this lambda for the rest of the Exceptions. 
    });
}

```

### More about Exceptions

Futuristic is designed so your code can throw exceptions at any point during any of the Future handler methods. 
The Exceptions will be correctly catched and they will be bubbled up to the ```trap``` handlers (if any).

### Synchronizing futures

Sometimes you have multiple Futures and you want to wait for any or all. 

```java
//Resolve all Futures
Future<Set<Integer>> allFuture = Futures.all(
    Futures.withValue(1),
    Futures.withValue(2),
    //... any number of futures
    Futures.withValue(999)
);

//Or resolve any Future
Future<Set<Integer>> allFuture = Futures.any(
    Futures.withValue(1),
    Futures.withValue(2),
    //... any number of futures
    Futures.withValue(999)
);
```

## Creating futures

All type of Futures can be created by the ```Futures``` class.

### With Callable

This will execute the ```Callable``` in a separate thread and trigger the ```Future``` with the result of the Callable 
 
```java
Future<Integer> future = Futures.withCallable(()->1);
```

You can also specify and Executor

```java
Future<Integer> future = Futures.withCallable(someExecutor, ()->1);
```

### With a know value

Sometimes you need to return a known value wrapped inside a future. In these cases you can do:

```java
Future<Integer> future = Futures.withValue(1);
```

This will create a future that is inmedialty triggered and will produce the value. This kind of future triggers itself
in the same thread as it was created.

### With a know Exception

Sometimes you need to return an Exception wrapped inside a future. In these cases you can do:

```java
Future future = Futures.withException(new RuntimeException("Some exception");
```

This will create a future that is inmedialty triggered and will produce the Exception. This kind of future triggers 
itself in the same thread as it was created.

### With a trigger

It is the most flexible Future object since it allows you to get a Future that is linked with a trigger. 
You can then trigger it from any part-thread of your code to deliver the result to the listener of the Future.

```java
FutureWithTrigger<AnyClass> future = Futures.withTrigger();

//in one part of your code you can get the trigger 
future.getTrigger().completed(new AnyClass(); //this is used to trigger the Future. This can be triggered from 
                                              //any thread, or usually inside a callback that you are converting
                                              //to Future-based code
                                              
//in another part of your code you can get the corresponding future 
future.getFuture();                           //This is a regular Future, that will get triggered by the trigger
                                              //explained above

```

## Usage

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
        <artifactId>futuristic-core</artifactId>
        <version>0.2.0</version>
    </dependency>
</dependencies>
```
### Jar

Download from [here](https://github.com/julman99/mvn-repo/raw/master/io/futuristic/futuristic-core/0.2.0/futuristic-core-0.2.0.jar)
