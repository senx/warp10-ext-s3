## S3STORE

Function used to stored wrapped series in Amazon S3. It takes as input a wrapped series, it's selector, and a parameter map.

```
// Series wrapped as bytes array
$gts WRAPRAW 

// Series selector - key
$gts TOSELECTOR '-WRAP' + 

// Parameter map
{ 
'accessKey' 'accessKey1'
'secretKey' 'verySecretKey1'
'endPoint' 'http://localhost:8000'
}
S3STORE

```