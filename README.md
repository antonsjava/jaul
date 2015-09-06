
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


For exampla for config file like 
```xml
<?xml version='1.0' encoding='UTF-8'?>
<zippy>
  <zip file="c:\bak\zippy\zips\project-doc-2012.zip" removeMissing="true">
   <source file="c:\project-doc\2012" type="docs"/>
  </zip>
  <zip file="c:\bak\zippy\zips\project-doc-2013.zip" removeMissing="true">
   <source file="c:\project-doc\2013" type="docs"/>
  </zip>
  <zip file="c:\bak\zippy\zips\project-doc-2014.zip" removeMissing="true">
   <source file="c:\project-doc\2014" type="docs"/>
  </zip>
  <zip file="c:\bak\zippy\zips\project-doc-2015.zip" removeMissing="true">
   <source file="c:\project-doc\2015" type="docs"/>
  </zip>
  <zip file="c:\bak\zippy\zips\project-doc-active.zip" removeMissing="true">
   <source file="c:\project-doc\_active" type="docs"/>
  </zip>
  <type name="docs">
    <!--exclude size="+100m"/-->
  </type>
</zippy>
```
You can use code like this
```java
 Document doc = Xml.documentFromFile(filename);
 if(doc == null) { ... }

 EW zippyE = EW.elem(doc.getDocumentElement());
 if(!"zippy".equals(zippyE.tag())) { ... }
 for(EW element : zippyE.childrenByTagName("type")) {
     String name = element.attr("name");
     for(EW elem : element.childrenByTagName("include")) {
         String file = elem.attr("name");
         String size = elem.attr("size");
         String time = elem.attr("time");
         ...
     }
     for(EW elem : element.childrenByTagName("exclude")) {
         String file = elem.attr("name");
         String size = elem.attr("size");
         String time = elem.attr("time");
         ...
     }
 }
 for(EW element : zippyE.childrenByTagName("zip")) {
     String file = element.attr("file");
     String remove = element.attr("removeMissing");
     String verbose = element.attr("verbose");
     for(EW elem : element.childrenByTagName("source")) {
         String fname = elem.attr("file");
         String type = elem.attr("type");
         String sverbose = elem.attr("verbose");
         String prefix = elem.attr("prefix");
         for(EW ielem : elem.childrenByTagName("include")) {
             String ename = ielem.attr("name");
             String size = ielem.attr("size");
             String time = ielem.attr("time");
             ...
         }
         for(EW ielem : elem.childrenByTagName("exclude")) {
             String ename = ielem.attr("name");
             String size = ielem.attr("size");
             String time = ielem.attr("time");
             ...
         }
         for(EW telem : elem.childrenByTagName("type")) {
             String tname = telem.attr("name");
             ...
         }
     }
 }
```

## TransitiveProperties

If you like to use Properties for configuration and you miss transitive value computation
like this
```
  host=http://localhost:8080
		main.url=${host}/index.jsp
		ws.hello.endpoint=${host}/ws/hello
```
Where "${host}" is replaced by value of property "host". so real properties looks like
```
  host=http://localhost:8080
		main.url=http://localhost:8080/index.jsp
		ws.hello.endpoint=http://localhost:8080/ws/hello
```

If you have read only properties (ussually once initiated from file), it is possible 
to compute transitive values and replace it in properties - than simply used changed 
values. 

```java
  Properties props = ....l;
  TransitiveProperties.makeClosure(props);
```

If your properties are changing or it is not possible to change them you can wrap 
existing instance. Wrapped instance then dynamically compute property values. 

```java
  Properties props = ....l;
  props = TransitiveProperties.wrap(props);
```


## Maven usage

```
   <dependency>
      <groupId>com.github.antonsjava</groupId>
      <artifactId>jaul</artifactId>
      <version>1.0</version>
   </dependency>
```



