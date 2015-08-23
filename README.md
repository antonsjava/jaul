
# jaul

Jaul stands for just another utility library. There is many of such libraries.
This one is my and I put it here so I can use it in my projects. Feel free to 
use it too if you like it. 

It provides set of individual classes with very simple functionality. They are 
used mostly to avoid unboxing nullpointers and some little string processing 
helper functions.

## Is

This class provides some helper functions for making boolean expressions more 
easily for some basic conditions. 

instead of 
```java
  String value = .....
  if((value != null) && (!"".equals(value))) {...}
```
you can use
```java
  String value = .....
  if(!Is.empty(value)) {...}
```

instead of 
```java
  List list = .....
  if((list != null) && (!list.isEmpty())) {...}
```
you can use
```java
  List list = .....
  if(!Is.empty(list)) {...}
```

instead of 
```java
  Boolean bool = .....
  if((bool != null) && (bool)) {...}
```
you can use
```java
  Boolean bool = .....
  if(Is.truth(bool)) {...}
```


## Get

This class helps with unboxing primitives and accessins collection information. 

instead of 
```java
  Integer integer = .....
  int i = (integer == null) ? 0 : integer;
```
you can use
```java
  Integer integer = .....
  int i = Get.safeInt(integer);
```


instead of 
```java
  List<String> list = .....
  int size = (list == null) ? 0 : list.size();
  String firstElem = (list == null) ? null : list.get(0);
```
you can use
```java
  List<String> list = .....
  int size = Get.size(list);
  String firstElem = Get.first(list);
```

## Replace

This class helps with string substitution. It provide basic text ti text substitution. 

```java
  String text = "The Mickey is mouse ";
		text = Replace.all(text, "mouse", "the Mouse");
```



## Maven usage

```
   <dependency>
      <groupId>com.github.antonsjava</groupId>
      <artifactId>jaul</artifactId>
      <version>1.0</version>
   </dependency>
```



