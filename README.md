[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.exampledriven/hierarchy-maven-plugin/badge.svg)](https://maven-badges.herokuapp.com/com.github.exampledriven/hierarchy-maven-plugin/rsql-parser)
[![Build Status](https://travis-ci.org/ExampleDriven/hierarchy-maven-plugin.svg?branch=master)](https://travis-ci.org/ExampleDriven/hierarchy-maven-plugin)

# Hierarchy maven plugin
Dependencies of a maven project can be easily listed, but it is quite challenging to know where were they defined. This is especially true for projects having complex parent structures with multiple level of parent poms and importing other poms.

This plugin displays the pom hierarchy including parents and imported poms in the dependencyManagement section. The result is somewhat similar to ``` dependency:tree ```, but while that displays the actual dependencies, this plugin shows the location where a dependency was defined.

## Usage

### Add the plugin to your project
```xml
  <build>
    <plugins>
      <plugin>
        <groupId>com.github.exampledriven</groupId>
        <artifactId>hierarchy-maven-plugin</artifactId>
        <version>1.4</version>
      </plugin>
    </plugins>
  </build>
```
### Execute the plugin

```shell
 mvn hierarchy:tree
```

### See the output 

```shell
[INFO] Displaying hierarchy. Set level=full to display dependencies in dependencyManagement
[INFO]  PARENT org.springframework.boot:spring-boot-samples:1.4.1.BUILD-SNAPSHOT
[INFO]    PARENT org.springframework.boot:spring-boot-starter-parent:1.4.1.BUILD-SNAPSHOT
[INFO]      PARENT org.springframework.boot:spring-boot-dependencies:1.4.1.BUILD-SNAPSHOT
[INFO]        IMPORT org.springframework:spring-framework-bom:4.3.3.BUILD-SNAPSHOT
[INFO]        IMPORT org.springframework.data:spring-data-releasetrain:Hopper-BUILD-SNAPSHOT
[INFO]          PARENT org.springframework.data.build:spring-data-build:1.8.4.BUILD-SNAPSHOT
[INFO]        IMPORT org.springframework.integration:spring-integration-bom:4.3.1.RELEASE
[INFO]        IMPORT org.springframework.security:spring-security-bom:4.1.3.RELEASE

```

### to display dependencies in dependencyManagementsection add the -Dlevel=full parameter 

```shell
 mvn hierarchy:tree -Dlevel=full
```
### And more detailed output is generated

```shell
[INFO] Displaying hierarchy.
[INFO]  PARENT org.springframework.boot:spring-boot-starter-parent:1.4.0.RELEASE
[INFO]    PARENT org.springframework.boot:spring-boot-dependencies:1.4.0.RELEASE
[INFO]          DEP_MANAGEMENT org.springframework.boot:spring-boot:1.4.0.RELEASE
[INFO]          DEP_MANAGEMENT org.springframework.boot:spring-boot:1.4.0.RELEASE
[INFO]          DEP_MANAGEMENT org.springframework.boot:spring-boot-test:1.4.0.RELEASE
[INFO]          DEP_MANAGEMENT org.springframework.boot:spring-boot-test-autoconfigure:1.4.0.RELEASE
[INFO]          DEP_MANAGEMENT org.springframework.boot:spring-boot-test:1.4.0.RELEASE
...
..
.


```


## Support, contact
Questions, comments can be placed as github issues. Pull requests, welcome!
