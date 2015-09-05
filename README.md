
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

## Base64

Simple base64 encoder

```java
  byte[] data = ...
  String text = Base64.encode(data);
  byte[] newdata = Base64.decode(text);
```

## Hex

Simple Hex encoder

```java
  byte[] data = ...
  String text = Hex.encode(data);
  byte[] newdata = Hex.decode(text);
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

## Xml

Simple xml DOM Document creator. Throws an runtime exception if there is problem with 
document creation.

```java
  Document doc = null;
  doc = Xml.document("<root><elem>value</elem></root>");
  doc = Xml.documentFromFile("/tmp/simple.xml");
```

## EW

Xml document Element wrapper. Helps to traverse XML. It hides 'Node' API and manipulates 
only with element structures.

```java
  Document doc = Xml.documentFromFile("/tmp/simple.xml");
		EW ew = EW.elem(doc.getDocumentElement());
  EW echema = ew.firstElementByTagName("schema");
		if(schema != null) jalw.info("schema namespace: {}", schema.attr("targetNamespace"))`
  List<EW> elemets = schema.elementsByTagName("element");
```
EW provides methods for searching elements by tag name and name space value. This implementation
takes names and name space names as they are written in XML document. (Do not accept inherited name space)

For example this element
```java
		<xs:schema 
		    xmlns:xs="http://www.w3.org/2001/XMLSchema" 
						xmlns:tns="services.mzv.portal" 
						elementFormDefault="unqualified" 
						targetNamespace="services.mzv.portal" 
						version="1.0">
```
That element can be find by tag 'schema' or by name space 'xs' and tag 'schema'
Attribute xmlns:tns="services.mzv.portal" can be accesed by 
```java
  EW echema = ....;
  String value = schema.attr("xmlns:xs");
  String value = schema.attr("xmlns", "xs");
  String value = schema.attr("xs");
```
Attribute version="1.0" can be accesed by 
```java
  EW echema = ....;
  String value = schema.attr("version");
```
It is not possible to access it by name space form even there is default name space defined. 

This API is used mostly for parsing simple application configuration files, ehere you can 
access elements and attributes by simple names only and allows you to ignore namespace prefixes.


## Maven usage

```
   <dependency>
      <groupId>com.github.antonsjava</groupId>
      <artifactId>jaul</artifactId>
      <version>1.0</version>
   </dependency>
```



