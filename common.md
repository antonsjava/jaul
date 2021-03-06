
# simple java type helpers

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

## Split

Splits strings and text files. 

Strings are split by specifying one or more substring delimiters. Exact match 
is used (no regexp, ..) and all divided parts are returned (no ignoring 
empty parts, ...). 

It returns all substring delimited by delimiter including empty one and also 
first and last id delimiter is on begin or end of provided string.

Following code
```java
  List<String> list = Split.string(", the text,, is separated, by comma,").bySubstringToList(",");
```
return "", " the text", "", " is separated", " by comma", ""


Text files are split to lines.
```java
  List<String> list = Split.file("input.dat", "utf-8").byLinesToList();
```

