## Example

### SETUP
Build jar

```
gradle shadowJar
```

Add in Warp10 conf current extension

```
warpscript.extensions = io.warp10.amazons3.script.AmazonExtension
```

### STORE

```
'READ'
'TOKEN'
STORE

$TOKEN AUTHENTICATE
200000000 LIMIT

//
// Read all GTS from each station and store them in blobs
// containing the wrapped content
//

[ '2' ] 'ids' STORE

$ids
<%
  DROP
  'id' STORE
  [ $TOKEN '~.*' { 'station' 'KOL' 'id' $id } NOW -5000000 ] FETCH
  <%
    DROP
    // GTS on top of the stack
    'gts' STORE
    $gts WRAPRAW $gts TOSELECTOR '-WRAP' + 
    { 
      'accessKey' 'accessKey1'
      'secretKey' 'verySecretKey1'
      'endPoint' 'http://localhost:8000'
    }
    S3STORE
    0
    //SIZE $gts TOSELECTOR $gts SIZE ROT 3 ->LIST
  %> LMAP
%> LMAP
```

### Load

```
//
// Read the raw blobs and convert them to FLOATS
//

'READ'
'TOKEN'
STORE

//
// Find the GTS
//

[ $TOKEN '~.*' { 'station' 'KOL' 'id' '2' } ] FIND
0 GET
TOSELECTOR '-WRAP' + 
{ 
  'accessKey' 'accessKey1'
  'secretKey' 'verySecretKey1'
  'endPoint' 'http://localhost:8000'
} S3LOAD
UNWRAP 
SIZE
```