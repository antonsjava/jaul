
# general utility classes


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
to compute transitive values and replace it in properties - than simply use changed 
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

## MapCache

If you like to use map like cache with some limitations you can use MapCache.

To get size limited cache where only last limited number of instances can be stored.
```java
  Map<Long, Entity> map = MapCache.instance(Long.class, Entity.class).limit(1000);
```
To get size limited cache where stored entities expired after specified number of ms.
```java
  Map<Long, Entity> map = MapCache.instance(Long.class, Entity.class).expiration(1000l*60*5);
```
You can also combine this two limitations.

## Resource

If you like to get resource from classpath you can use this class

```java
  InputStream is = Resource.url("properties/my.properties").inputStream();
```

