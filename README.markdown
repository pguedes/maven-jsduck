Introduction
============

maven-jsduck is a maven plugin that produces javascript API documentation using jsduck.

http://rubygems.org/gems/jsduck
The lib directories of jsduck version 0.6 and it's dependencies are unrolled in src/main/resources.

The Markdown implementation used by jsduck is RDiscount which is written in C, this was replaced by
a native java implementation (markdownj) to allow for better portability and performance.
http://code.google.com/p/markdownj/


Usage
=====
Get
```sh
    $ git clone git://github.com/pguedes/maven-jsduck.git
```
Install
```sh
    $ cd maven-jsduck/maven-jsduck
    $ mvn install
```
Run
```sh
    $ cd ~/myproject
    $ mvn -Djsduck.verbose=true nl.secondfloor.mojo.jsduck:jsduck-maven-plugin:jsduck
```
To clean
```sh
    $ mvn -Djsduck.verbose=true nl.secondfloor.mojo.jsduck:jsduck-maven-plugin:clean-jsduck
```

Configuration
=============



Maven
=====
To automatically run the clean-jsduck goal during clean add the following to your pom.xml:
```xml
    <build>
      <plugins>
        <plugin>
          <groupId>nl.secondfloor.mojo.jsduck</groupId>
          <artifactId>jsduck-maven-plugin</artifactId>
          <executions>
            <execution>
              <phase>clean</phase>
              <goals>
                <goal>clean-jsduck</goal>
              </goals>
            </execution>
          </executions>
        </plugin>
      </plugins>
    </build>
```

Wishlist
========
* Embed javadoc documentation in the same documentation browser to have a full API documentation pack
* Figure out how Atlassian generated their JIRA REST/HTTP API doc and find a way to incorporate something like that as well
* Investigate if it is possible to do delta generation to sync the API doc with the current state instead of having to clean and regenerate
