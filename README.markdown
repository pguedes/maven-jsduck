Introduction
------------

maven-jsduck is a Maven plugin that produces JavaScript API documentation using [jsduck](http://rubygems.org/gems/jsduck).

It uses [JRuby](http://www.jruby.org/) to run; the lib directories of [jsduck](http://rubygems.org/gems/jsduck)
version 3.10.5 and its dependencies are unrolled in src/main/resources.

The [Markdown](http://daringfireball.net/projects/markdown/) implementation used by [jsduck](http://rubygems.org/gems/jsduck)
is [RDiscount](http://rubygems.org/gems/rdiscount) which is written in C; this was replaced by a native java implementation
([markdownj](http://code.google.com/p/markdownj/)) to allow for better portability and performance.


Usage
-----
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
    $ mvn jsduck:jsduck
```
Clean

```sh
    $ mvn jsduck:clean-jsduck
```

Configuration
-------------

| *parameter*            | *description*                                               | *default*                               |
|:-----------------------|:------------------------------------------------------------|:----------------------------------------|
|  verbose               | Enable or disable more logging.                             |  true                                   |
|  source                | Directories containing the javascript files to document.    |                                         |
|  javascriptDirectory   | The directory containing the JavaScript files to document.  |  src/main/webapp/js                     |
|  targetDirectory       | The directory to write the API documentation to.            |  target/jsduck-api                      |
|  welcome               | The welcome page to use.                                    |  src/main/jsduck/welcome.html           |
|  title                 | The title to use for the documentation.                     |  ${project.name} ${project.version}     |
|  header                | The header to use for the documentation.                    |  ${project.name} ${project.version} API |
|  guides                | The guides to include                                       |                                         |
|  egIframe              | Sample iframe for running inline examples                   |                                         |

To change the configuration while running command line use the plugin name as prefix: -Djsduck.verbose=false.

Maven
-----
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
          <configuration>
            <verbose>false</verbose>
          </configuration>
        </plugin>
      </plugins>
    </build>
```

Wishlist
--------
* Embed javadoc documentation in the same documentation browser to have a full API documentation pack
* Figure out how Atlassian generated their JIRA REST/HTTP API doc and find a way to incorporate something like that as well
  * (Update) appears to be xslt on jersey's wadl output with some additional javadoc tag support
* Investigate if it is possible to do delta generation to sync the API doc with the current state instead of having to clean and regenerate
* Wrap jsduck logging in the maven plugin's log framework
* Allow usage of customized templates.
