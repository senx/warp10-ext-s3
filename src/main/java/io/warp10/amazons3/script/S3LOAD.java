package io.warp10.amazons3.script;

import io.warp10.crypto.OrderPreservingBase64;
import io.warp10.script.NamedWarpScriptFunction;
import io.warp10.script.WarpScriptException;
import io.warp10.script.WarpScriptStack;
import io.warp10.script.WarpScriptStackFunction;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3ClientOptions;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.google.common.base.Charsets;

/**
 * Demo function to load blobs from a directory
 */
public class S3LOAD extends NamedWarpScriptFunction implements WarpScriptStackFunction {
  
  public S3LOAD(String name) {
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
    
   // File file = new File(this.root, new String(OrderPreservingBase64.encode(key.getBytes(Charsets.UTF_8)), Charsets.US_ASCII));
    
     
   try {
     S3Object s3object = s3client.getObject("javabucket", new String(OrderPreservingBase64.encode(key.getBytes(Charsets.UTF_8))));
     S3ObjectInputStream in = null;
     
     try { 
       in = s3object.getObjectContent();
       
       ByteArrayOutputStream baos = new ByteArrayOutputStream((int) s3object.getObjectMetadata().getContentLength());
       byte[] buf = new byte[1024];
       
       while(true) {
         int len = in.read(buf);
         if (len < 0) {
           break;
         }
         baos.write(buf, 0, len);
       }      
       stack.push((new String (baos.toByteArray(), Charsets.UTF_8)).getBytes(Charsets.ISO_8859_1));
     }
     catch (IOException ioe) {
       throw new WarpScriptException(ioe);
     } finally {
       if (null != in) {
         try {
           in.close();
         } catch (IOException ioe) {
         }
       }
     } 
   } 
   catch (SdkClientException ex) {
     System.out.println(ex.getMessage());
     stack.push(null);
   } 
    
   return stack;
  }
}
