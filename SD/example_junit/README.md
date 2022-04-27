# JUnit example

This is a simple Java application with JUnit tests.  
[JUnit](http://junit.org/) is a simple framework to write repeatable tests.

## Maven instructions

### Dependency

To use JUnit in Maven, it is necessary to put the test classes under `src/test/java`.  
It is also necessary to add the following dependency:

```
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter-engine</artifactId>
        <version>5.6.0</version>
        <scope>test</scope>
    </dependency>
```

The scope says that the library should only be used for testing.

### Commands

To print the project dependencies:

```
mvn dependency:tree
```

To compile the source code:

```
mvn compile
```

To test the source code:

```
  mvn test
```

### Resource files

The following configuration in the POM file copies resource files to target folder, both in the `main` source and in the `test` source.

```
    <build>
        <resources>
            <resource>
                <directory>src/main/resources</directory>
                <filtering>true</filtering>
            </resource>
        </resources>
        <testResources>
            <testResource>
                <directory>src/test/resources</directory>
                <filtering>true</filtering>
            </testResource>
        </testResources>
```

The filtering option replaces `${propertyName}` in the files by the respective property value.  
Properties can be defined in the Maven file and their value can be propagated to the property files using the copy and filter mechanism.

A property file can be read by a Java program with the following or similar lines:

```
import java.io.InputStream;
import java.util.Properties;

...

    final String resourcePath = "config.properties";

    InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(resourcePath);
    Properties properties = new Properties();
    properties.load(inputStream);

...

    String propertyValue = properties.getProperty("propertyName");
```

## To configure the Maven project in Eclipse

'File', 'Import...', 'Maven'-'Existing Maven Projects'

'Select root directory' and 'Browse' to the project base folder.

Check that the desired POM is selected and 'Finish'.

----

[SD Faculty](mailto:leic-sod@disciplinas.tecnico.ulisboa.pt)
