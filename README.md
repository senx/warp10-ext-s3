## Example

### SETUP
Build jar

```
gradle shadowJar
```

Add in Warp10 conf current extension

```
warpscript.extensions = io.warp10.script.ext.s3.S3WarpScriptExtension
```

*2 mains functions are provided in this extention: S3STORE and S3LOAD*

### STORE

This function is used to store data in S3, here a small example of its usage. It takes as input an element (a byte array), a storage key (a string) and a parameter map.

```
NEWGTS 'test-WRAP' RENAME
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

This function is used to load data from a S3, here a small example of its usage. It takes as input a storage key (a string) and a parameter map.

```
//
// S3 Selector key (series selector)
//

'test-WRAP{}'

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

#### Parameter map

The parameter map required by the various functions of this extension can contain the following keys:

* accessKey

Mandatory, the access key to use to authenticate with the S3 endpoint.

* secretKey

Mandatory, the secret key to use to authenticate with the S3 endpoint.

* endPoint

Mandatory, the S3 endpoint to access.

* bucket

Optional, the S3 bucket to use, if not specified, 'warp10' will be used. The specified bucket will be created as part of the call to S3STORE if it does not yet exist.

