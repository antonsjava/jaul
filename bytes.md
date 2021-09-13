
# byte manipulating utilities

## Bytes

Converts byte array to stream  
```java
  byte[] data = ...
  InputStream is = Bytes.asStream(data);
```

Converts stream to byte array
```java
  InputStream is = ...
  byte[] data = Bytes.fromStream(is);
```

Transfer byte array to stream
```java
  byte[] data = ...
  OutputStream os = ...
  Bytes.toStream(data, os);
```

Transfer stream to stream
```java
  InputStream is = ...
  OutputStream os = ...
  int size = Bytes.transfer(is, os);
```


## Base64

Simple base64 encoder

```java
  byte[] data = ...
  String text = Base64.encode(data);
  byte[] newdata = Base64.decode(text);
```

## Any64

Simple bytes to text encoder baset on Base64 but uses different charset.
It produce encoded text with only characters from given charset (no '=' 
padding)

```java
  byte[] charset = new char[] {'a', 'b', ..... 64 unique chars};
  byte[] data = ...
  Any64 encoder = Any64.instance(charset);
  String text = encoder.encode(data);
  byte[] newdata = encoder.decode(text);
```
If you are satisfied with Base64 charset and you want to replace only plus 
and slash characters. (Usually only those characters are problematic.)
```java
  byte[] data = ...
  Any64 encoder = Any64.instance('?', '!');
  String text = encoder.encode(data);
  byte[] newdata = encoder.decode(text);
```

## Hex

Simple Hex encoder

```java
  byte[] data = ...
  String text = Hex.encode(data);
  byte[] newdata = Hex.decode(text);
```

## Unicode

Simple unicode encoder

```java
  String data = ...
  String text = Unicode.escape(data, false);
  String newdata = Unicode.unescape(text);
```


## Html

Simple html escaping encoder

```java
  String data = ...
  String text = Html.escapeSimple(data);
  String newdata = Html.unescape(text);
```

