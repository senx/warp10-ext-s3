## S3LOAD

S3LOAD is used to load data stored as GTS in Amazon S3. It takes as input a key for S3 storage and a parameter map.

```
// S3 storage key (example: series selector)
'GTS-WRAP{}'

// Params map
{ 
  'accessKey' 'accessKey1'
  'secretKey' 'verySecretKey1'
  'endPoint' 'http://localhost:8000'
} S3LOAD
```
