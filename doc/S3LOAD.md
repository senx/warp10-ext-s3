## S3LOAD

S3LOAD is used to load data stored as GTS in Amazon S3. It takes as input a gts selector and a parameter map.

```
// GTS selector
'GTS-WRAP'

// Params map
{ 
  'accessKey' 'accessKey1'
  'secretKey' 'verySecretKey1'
  'endPoint' 'http://localhost:8000'
} S3LOAD
```