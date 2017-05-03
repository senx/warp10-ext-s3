package io.warp10.amazons3.script;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3ClientOptions;
import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;

import io.warp10.crypto.OrderPreservingBase64;
import io.warp10.script.NamedWarpScriptFunction;
import io.warp10.script.WarpScriptException;
import io.warp10.script.WarpScriptStack;
import io.warp10.script.WarpScriptStackFunction;

import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

/**
 * Demo function to store blobs under a directory
 */
public class S3STORE extends NamedWarpScriptFunction implements WarpScriptStackFunction {
  

  //private AmazonS3 s3client = null;
  
  public S3STORE(String name) {
    super(name);
  }
  
  @Override
  public Object apply(WarpScriptStack stack) throws WarpScriptException {
    
    Object param = stack.pop();
    
    if (!(param instanceof Map)) {
      throw new WarpScriptException(getName() + " can only accept param as Map.");
    }
    
    Map mapParam = (Map) param;
    
    if (!mapParam.containsKey("accessKey") || !mapParam.containsKey("secretKey") || !mapParam.containsKey("endPoint") ) {
      throw new WarpScriptException(getName() + " expect a secretKey, an accessKey and an endPoint keys in param map");
    }
    
    AWSCredentials credentials = new BasicAWSCredentials(mapParam.get("accessKey").toString(),
        mapParam.get("secretKey").toString());

    // Create a client connection based on credentials
    AmazonS3 s3client = new AmazonS3Client(credentials);
    s3client.setEndpoint(mapParam.get("endPoint").toString());
    // Using path-style requests
    // (deprecated) s3client.setS3ClientOptions(new S3ClientOptions().withPathStyleAccess(true));
    s3client.setS3ClientOptions(S3ClientOptions.builder().setPathStyleAccess(true).build());
    

    Object top = stack.pop();
    
    String key = top.toString();
    
    top = stack.pop();
    
    if (!(top instanceof byte[]) && !(top instanceof String)) {
      throw new WarpScriptException(getName() + " can only store byte arrays.");
    }
    
    if (top instanceof byte[]) {
      
      //
      // Verify if Java bucket already exists
      //
      
      boolean javaBucket =false;
      for (Bucket bucket : s3client.listBuckets()) {
        if(bucket.getName().equals("javabucket")) {
          javaBucket=true;
          break;
        }
      }
      
      //
      // Otherwise create it
      //
      
      if(!javaBucket) {
        s3client.createBucket("javabucket");
      }
      
      //
      // Push object on S3 client
      //
      
      s3client.putObject("javabucket", new String(OrderPreservingBase64.encode(key.getBytes(Charsets.UTF_8))), new String((byte[]) top, Charsets.ISO_8859_1));
      //ByteStreams.copy(in, top);
      //s3client.putObject("javabucket", key, input, metadata)
    } 
    return stack;
  }
}
