## S3STORE

Function used to stored wrapped series in Amazon S3. It takes as input a wrapped series, a S3 storage key, and a parameter map.

```
// Series wrapped as bytes array
$gts WRAPRAW 

// S3 storage key (example: Series selector)
$gts TOSELECTOR '-WRAP' + 

// Parameter map
{ 
'accessKey' 'accessKey1'
'secretKey' 'verySecretKey1'
'endPoint' 'http://localhost:8000'
}
S3STORE

```
