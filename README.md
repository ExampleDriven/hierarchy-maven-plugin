[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.exampledriven/hierarchy-maven-plugin/badge.svg)](https://maven-badges.herokuapp.com/com.github.exampledriven/hierarchy-maven-plugin/rsql-parser)
[![Build Status](https://travis-ci.org/ExampleDriven/hierarchy-maven-plugin.svg?branch=master)](https://travis-ci.org/ExampleDriven/hierarchy-maven-plugin)

# Hierarchy maven plugin

This plugin displays the hierarchy of parent poms and imported poms. The result is somewhat similar to ``` dependency:tree ```, but that displays the actual dependencies, this plugin may shed light on which dependency was defined where.

## Usage

1. Add the plugin to your project
```xml
  <build>
    <plugins>
      <plugin>
        <groupId>com.github.exampledriven</groupId>
        <artifactId>hierarchy-maven-plugin</artifactId>
        <version>1.3</version>
      </plugin>
    </plugins>
  </build>
```
  
2. start the plugin like this

```shell
 mvn hierarchy:tree
```

3. See output like this

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

4. to display dependencies in dependencyManagementsection add the -Dlevel=full parameter 

```shell
 mvn hierarchy:tree -Dlevel=full
```

## Support, contact
Questions, comments can be placed as github issues, pull requests, welcome
