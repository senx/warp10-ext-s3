//
//   Copyright 2018  SenX S.A.S.
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.
//
package io.warp10.script.ext.s3;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3ClientOptions;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

import io.warp10.script.NamedWarpScriptFunction;
import io.warp10.script.WarpScriptException;
import io.warp10.script.WarpScriptStack;
import io.warp10.script.WarpScriptStackFunction;

/**
 * Demo function to load blobs from a bucket
 */
public class S3LOAD extends NamedWarpScriptFunction implements WarpScriptStackFunction {
  
  public S3LOAD(String name) {
    super(name);
  }
  
  @Override
  public Object apply(WarpScriptStack stack) throws WarpScriptException {
    
    Object param = stack.pop();
    
    if (!(param instanceof Map)) {
      throw new WarpScriptException(getName() + " expects a parameter map on top of the stack.");
    }
    
    Map mapParam = (Map) param;
    
    if (!mapParam.containsKey(S3WarpScriptExtension.KEY_ACCESS_KEY) || !mapParam.containsKey(S3WarpScriptExtension.KEY_SECRET_KEY) || !mapParam.containsKey(S3WarpScriptExtension.KEY_ENDPOINT) ) {
      throw new WarpScriptException(getName() + " expects the parameter map to have keys '" + S3WarpScriptExtension.KEY_SECRET_KEY + "', '" + S3WarpScriptExtension.KEY_ACCESS_KEY + "' and '" + S3WarpScriptExtension.KEY_ENDPOINT + "'.");
    }
    
    AWSCredentials credentials = new BasicAWSCredentials(mapParam.get(S3WarpScriptExtension.KEY_ACCESS_KEY).toString(), mapParam.get(S3WarpScriptExtension.KEY_SECRET_KEY).toString());

    //
    // Create a client connection based on credentials
    //
    
    AmazonS3 s3client = new AmazonS3Client(credentials);
    s3client.setEndpoint(mapParam.get(S3WarpScriptExtension.KEY_ENDPOINT).toString());
    
    // Using path-style requests
    // (deprecated) s3client.setS3ClientOptions(new S3ClientOptions().withPathStyleAccess(true));
    s3client.setS3ClientOptions(S3ClientOptions.builder().setPathStyleAccess(true).build());
    
    Object top = stack.pop();
    
    if (!(top instanceof String) && !(top instanceof List)) {
      throw new WarpScriptException(getName() + " expects the key to be a string or a list thereof.");
    }
    
    List<String> keys = new ArrayList<String>();
    
    boolean isList = false;
    
    if (top instanceof String) {
      keys.add(top.toString());
    } else {
      isList = true;
      for (Object o: (List) top) {
        keys.add(o.toString());
      }
    }
    
    try {
     String bucket = mapParam.getOrDefault(S3WarpScriptExtension.KEY_BUCKET, S3WarpScriptExtension.DEFAULT_BUCKET).toString();

     List<byte[]> values = new ArrayList<byte[]>();
     
     for (String key: keys) {
       
       S3ObjectInputStream in = null;

       try { 
         S3Object s3object = s3client.getObject(bucket, key);

         in = s3object.getObjectContent();
         
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         
         byte[] buf = new byte[8192];
         
         while(true) {
           int len = in.read(buf);
           
           if (len < 0) {
             break;
           }

           baos.write(buf, 0, len);
         }      
         values.add(baos.toByteArray());
       } catch (AmazonS3Exception s3ex) {
         if ("NoSuchKey".equals(((AmazonS3Exception) s3ex).getErrorCode())) {
           values.add(null);
         } else {
           throw s3ex;
         }           
       } finally {
         if (null != in) {
           try {
             in.close();
           } catch (IOException ioe) {
           }
         }
       }        
     }

     if (isList) {
       stack.push(values);
     } else {
       stack.push(values.get(0));
     }
   } catch (IOException ioe) {
     throw new WarpScriptException("Error while fetching data", ioe);
   } catch (SdkClientException ex) {
     throw new WarpScriptException("Error while fetching data", ex);
   } 
    
   return stack;
  }
}
