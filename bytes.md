
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

## Hex

Simple Hex encoder

```java
  byte[] data = ...
  String text = Hex.encode(data);
  byte[] newdata = Hex.decode(text);
```

## Unicode

Simple inocode encoder

```java
  String data = ...
  String text = Unicode.escape(data, false);
  String newdata = Unicode.unescape(text);
```



