
# file manipulating helpers


## TextFile

Reading text file into string

```java
 String content = TextFile.read("/tmp/file.txt", "utf-8");
```
Save string to text file

```java
 Stirng content = ...
 TextFile.save("/tmp/file.txt", "utf-8", content);
```

## BinFile

Reading file into byte array

```java
 byte[] content = BinFile.readBytes("/tmp/file.txt");
```
Save byte array to file

```java
 byte[] content = ...
 BinFile.save("/tmp/file.txt", content);
```


