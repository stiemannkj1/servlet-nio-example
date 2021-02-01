[![Build Status](https://travis-ci.org/stiemannkj1/servlet-nio-example.svg?branch=master)](https://travis-ci.org/stiemannkj1/servlet-nio-example)

# Servlet NIO Example

Install Gradle Wrapper on the project if it's not installed already:

```
$ gradle wrapper --gradle-version=6.8.2
```

Run the code on Tomcat 9.x:

```
$ ./gradlew clean build war cargoRunLocal
```

Access the servlet at:

http://localhost:8080/servlet-nio-example/?name=Test

## Debugging

To start the app with remote debugging enabled, use the following command:

```
$ ./gradlew clean build war cargoRunLocal \
    -Pcargo.start.jvmargs='-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5005'
```

## Formatting

To format the code run:

```
$ ./gradlew spotlessApply
```

## Related

* https://docs.oracle.com/javaee/7/tutorial/servlets013.htm
* https://github.com/jetty-project/jetty-bench/blob/master/bench-9-server/src/main/java/org/eclipse/jetty/benchmark/Servlet31AsyncIOEchoBenchmark.java
* https://github.com/synchronoss/nio-multipart
* https://jakarta.ee/specifications/servlet/4.0/servlet-spec-4.0.html
* https://jakarta.ee/specifications/servlet/4.0/apidocs
* https://download.eclipse.org/jakartaee/servlet/4.0/jakarta-servlet-tck-4.0.0.zip
