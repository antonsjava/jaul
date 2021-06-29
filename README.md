
# jaul

Jaul stands for just another utility library. There is many of such libraries.
This one is my and I put it here so I can use it in my projects. Feel free to 
use it too if you like it. It is small (less than 100KB) library, so it is not 
a problem to add it to any project, even you want to use only one class.

It provides set of individual classes with very simple functionality. They are 
used mostly to avoid unboxing nullpointers and some little string processing 
helper functions.

There are several types of utilities 
 - [common](./common.md) java types helpers
 - [bytes](./bytes.md) helpers
 - [file](./files.md) helpers
 - [pojo](./pojo.md) helpers
 - [xml](./xml.md) helpers
 - [utilities](./utilities.md)

## Maven usage

```
   <dependency>
      <groupId>com.github.antonsjava</groupId>
      <artifactId>jaul</artifactId>
      <version>LASTVERSION</version>
   </dependency>
```
You can find LASTVERSION [here](https://mvnrepository.com/artifact/com.github.antonsjava/jaul)

## OSGI usage (Karaf)

```
   bundle:install mvn:com.github.antonsjava/jaul/1.10
   bundle:start com.github.antonsjava.jaul/1.10.0
```



