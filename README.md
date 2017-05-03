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
NEWGTS 'test2-WRAP' RENAME
NOW NaN NaN NaN 2 ADDVALUE


'gts' STORE

//
// Wrap GTS
//

$gts WRAPRAW

//
// S3 storage key
//

$gts TOSELECTOR

//
// S3 parameter map
//

{ 
  'accessKey' 'accessKey1'
  'secretKey' 'verySecretKey1'
  'endPoint' 'http://localhost:8000'
}
S3STORE
```

### Load

```
//
// S3 Selector key
//

'test2-WRAP{}'

//
// S3 parameter map
//

{ 
  'accessKey' 'accessKey1'
  'secretKey' 'verySecretKey1'
  'endPoint' 'http://localhost:8000'
}
S3LOAD
UNWRAP
```