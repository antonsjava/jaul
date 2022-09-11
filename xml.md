
# xml reading helpers

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

## Elem

Simplified Xml API. For simple xml with 
 - no declarations
 - no process instrauctions
 - no namespace logic
 - no comments
 - text only in leaves
It handle xml as simple data structure, which can be craated, manipulated and searched.  

It is possible to create xml using api
```java
 Elem elem = Elem.of("root")
              .addAttr("id", "1212");
              .addChild(Elem.of("ns1:child").text("a text"));
```
to create xml like 
```
 <root id="1212">
   <ns1:child>a text</ns1:child>
 </root>
```

You can search in elements by name
```java
 Elem elem = Elem.parse(new FileInputStream("/tmp/example.xml"));
 List<Elem> surnames = elem.find("book", "author", "surname").all();
 List<String> surnamestexts = elem.find("book", "author", "surname").allText();
```
 Elem addressCityElem = elem.find("address", "city").first();
 String addressCitytext = elem.find("address", "city").firstText();
```

You can modify elements
```java
 Elem elem = Elem.parse(new FileInputStream("/tmp/example.xml"));
 Elem addressCityElem = elem.find("address", "city").first();
 addressCityElem.replace(Elem.of("city").text("Brno"));
```
