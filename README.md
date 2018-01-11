Alchemy HTTP Mock
==============================================
[<img src="https://raw.githubusercontent.com/SirWellington/alchemy/develop/Graphics/Logo/Alchemy-Logo-v7-name.png" width="500">](https://github.com/SirWellington/alchemy)

## "Mock the World"

[![Build Status](http://jenkins.redroma.tech/job/Alchemy%20HTTP%20Mock/badge/icon)](http://jenkins.redroma.tech/job/Alchemy%20HTTP%20Mock/)
![Maven Central Version](http://img.shields.io/maven-central/v/tech.sirwellington.alchemy/alchemy-http-mock.svg)

# Purpose
Part of the [Alchemy Collection](https://github.com/SirWellington/alchemy).

Alchemy HTTP Mock makes Unit Testing with [Alchemy HTTP](https://github.com/SirWellington/alchemy-http) breezy.
This allows testing without hitting any actual networks.

You can Stub Behavior using Mockito style syntax.

# Download

To use, simply add the following maven dependency.

## Release

```xml
<dependency>
	<groupId>tech.sirwellington.alchemy</groupId>
	<artifactId>alchemy-http-mock</artifactId>
    <version>1.1</version>
    <!--  Designed for Unit Testing -->
    <scope>test</scope>
</dependency>
```

## Snapshot

>First add the Snapshot Repository
```xml
<repository>
	<id>ossrh</id>
    <url>https://oss.sonatype.org/content/repositories/snapshots</url>
</repository>
```

```xml
<dependency>
	<groupId>tech.sirwellington.alchemy</groupId>
	<artifactId>alchemy-http-mock</artifactId>
	<version>2.0-SNAPSHOT</version>
</dependency>
```

# API

Use `AlchemyHttpMock` to create Mock Http Clients.

```java

AlchemyHttp http;

//...

http = AlchemyHttpMock.begin()
            .whenPost()
            .anyBody()
            .at(url)
            .thenReturnResponse(response)
            .build();

//Use mock...

//Verify expected requests were made.
AlchemyHttpMock.verifyAllRequestsMade(http);
```



# [Javadocs](http://www.javadoc.io/doc/tech.sirwellington.alchemy/alchemy-http-mock/)


# Requirements

+ Java 8
+ Maven installation

# Building
To build, just run a `mvn clean install` to compile and install to your local maven repository


# Feature Requests
Feature Requests are definitely welcomed! **Please drop a note in [Issues](https://github.com/SirWellington/alchemy-http-mock/issues).**

# Release Notes

## 1.1
+ Bugfixes and improvements

## 1.0
+ Initial Public Release

# License

This Software is licensed under the Apache 2.0 License

http://www.apache.org/licenses/LICENSE-2.0
