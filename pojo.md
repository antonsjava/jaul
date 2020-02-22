
# pojo helpers

In term of pojo I mean mostly data classes.

## Messer

Some times you need crate mock instance of data class. (For example someone wants to 
have example of json representation) 
Messer class create such dummy instance for you.

```java
 Address address = Pojo.messer().junk(Address.class);
 log.debug("json: {}", objectMapper.writeValueAsString(address));
```
prints something like 
```
 json: {"street": "Street", "city":"City", "country": {"code":"Code", "name":"Name"}}
```

## Dumper

Sometimes you need to see content of data class instance and toString() is not implemented 
as you need.
Dumper class create such property by property dump of instance for you. (Not too effective
don't use them as regular toString() implementation)

```java
 Address address = ...
 log.debug("example: {}", Pojo.dumper().dump(address));
```
prints something like 
```
 example: instance of class foo.bar.Address hash: 1775488894
.street: Street
.city: City
.country.code: Code
.country.name: Name
...
```

## Differ

Sometimes you need to compare content of two data class instances. (In some tests)
Differ class create such property by property diff of instances for you. 

```java
 Address address = Pojo.messer().junk(Address.class);
 long id = repository.save(address);
 Address address2 = repository.load(Address.class, id);
 log.debug("changes: {}", Pojo.differ().diff(address, address2));
```
prints something like 
```
 changes: Changes[
 change /id -- '1' vs '2332'
 change /country/id -- '1' vs '33'
 ]
```

## ToJsonString / JsonString

Json is readable but preety compact form of data string representation.
It is possible to use some frameworks for converting objects to json string. 
But It is wery usefull to create such json like string also as notmal toString() 
representation. 

If you have own data classes you can create toString like this 
```java
public claxx XXX implements ToJsonString {
  private String value1;
  private String value2;

  public void toJsonString(JsonString json) {
    json.objectStart();
    json.attr("value1", value1);
    json.attr("value2", value2);
    json.objectEnd();
  }

  public String toString() {
    JsonString json = JsonString.instance().indent("  ");
    toJsonString(json);
    return json.toString();
  }
} 
```

