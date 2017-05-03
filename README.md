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

*2 mains functions are provided in this extention: STORE and LOAD*

### STORE

This function is used to store data in a S3 storage, here a small example of it's usage. It takes as input an element, a storage key and a S3 storage parameter map.

```
NEWGTS 'test2-WRAP' RENAME
NOW NaN NaN NaN 2 ADDVALUE


'gts' STORE

//
// Wrap GTS
//

$gts WRAPRAW

//
// S3 storage key (series selector)
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

This function is used to load data from a S3 storage, here a small example of it's usage. It takes as input a storage key and a S3 storage parameter map.

```
//
// S3 Selector key (series selector)
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