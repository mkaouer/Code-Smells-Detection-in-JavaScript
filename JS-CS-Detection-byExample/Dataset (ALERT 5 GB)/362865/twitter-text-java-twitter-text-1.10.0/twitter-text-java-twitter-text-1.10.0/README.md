## twitter-text-java [![Build Status](https://secure.travis-ci.org/twitter/twitter-text-java.png?branch=master)](http://travis-ci.org/twitter/twitter-text-java) [![Coverage Status](https://coveralls.io/repos/twitter/twitter-text-java/badge.png?branch=master)](https://coveralls.io/r/twitter/twitter-text-java?branch=master)

Java port of the twitter-text handling libraries.

## Getting Started

The latest artifacts are published to maven central. 

Bringing twitter-text-java into your project should be as simple as adding the following to your pom.xml:

```xml
  <dependencies>
    <dependency>
      <groupId>com.twitter</groupId>
      <artifactId>twitter-text</artifactId>
      <version>1.6.1</version> <!-- or whatever the latest version is -->
    </dependency>
  </dependencies>
```

## Building

To build, ensure you have the conformance test suite checked out:

```git submodule init && git submodule update```

Then simply run maven:

```mvn clean install```

## Issues

Have a bug? Please create an issue here on GitHub!

https://github.com/twitter/twitter-text-java/issues

## License

Copyright 2011,2014 Twitter, Inc. and other contributors

Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
