Sportradar Unified Odds SDK Bundle
------------------------------------
Copyright (c) 2016, Sportradar AG
All rights reserved.
------------------------------------

This bundle includes a few different things:

* The sportradar-odds-sdk - just the jar itself. If you include this in your project you will have to 
  include all Java libraries it depends on too. (See pom.xml for those Java libraries. 
  in general the Odds SDK should not be that sensitive to Java Library versions so if you use another
  version of the same libraries that should most of the time work too.)

* A pom.xml for the sdk, that can be used to install the sdk in your local repository
  To install the sportradar-odds-sdk in your maven repository do the following:
  mvn install:install-file -Dfile=<path-to-sportradar-odds-sdk.jar> -DpomFile=<path-to-sportradar-odds-sdk-pom.xml>

* A jar-with-dependencies, that includes the sportradar-odds-sdk and all the Java libraries it transitively needs.

* A jar-with-dependencies-shaded, that includes the sportradar-odds-sdk and all the needed Java libraries shaded.

* A javadoc jar which can be included in the IDE project.

* A javadoc folder that contains the javadocs for the sportradar-odds-sdk.
  
* An example folder that contains various basic examples in how to use the sportradar-odds-sdk.

IMPORTANT NOTICE:
The SDK uses the SLF4J logging facade, which gives you the option to use any logging framework that suits best
your needs. In the included basic JAR example we use the recommended Logback setup.

