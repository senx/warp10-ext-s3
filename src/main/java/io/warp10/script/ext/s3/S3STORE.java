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


import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3ClientOptions;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectMetadata;

import io.warp10.script.NamedWarpScriptFunction;
import io.warp10.script.WarpScriptException;
import io.warp10.script.WarpScriptStack;
import io.warp10.script.WarpScriptStackFunction;

/**
 * Store a blob in S3
 */
public class S3STORE extends NamedWarpScriptFunction implements WarpScriptStackFunction {
  
  public S3STORE(String name) {
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

    // Create a client connection based on credentials
    AmazonS3 s3client = new AmazonS3Client(credentials);
    s3client.setEndpoint(mapParam.get(S3WarpScriptExtension.KEY_ENDPOINT).toString());
    // Using path-style requests
    // (deprecated) s3client.setS3ClientOptions(new S3ClientOptions().withPathStyleAccess(true));
    s3client.setS3ClientOptions(S3ClientOptions.builder().setPathStyleAccess(true).build());
    

    Object top = stack.pop();
        
    if (!(top instanceof String) && !(top instanceof List)) {
      throw new WarpScriptException(getName() + " expects the key to be a string or a list thereof.");
    }

    List<String> keys = null;
    
    // Check that each key is a string 
    if (top instanceof List) {
      for (Object o: (List) top) {
        if (!(o instanceof String)) {
          throw new WarpScriptException(getName() + " expects the list of keys to be a list of strings.");
        }
      }
      keys = (List<String>) top;
    } else {
      keys = new ArrayList<String>(1);
      keys.add(top.toString());  
    }
            
    top = stack.pop();
    
    List<byte[]> values = new ArrayList<byte[]>();
    
    if ((top instanceof byte[]) && 1 != keys.size()) {
      throw new WarpScriptException(getName() + " needs a list of values when specifying a list of keys.");
    }
    
    if (top instanceof byte[]) {
      values.add((byte[]) top);
    } else if (top instanceof List) {
      for (Object o: (List) top) {
        if (!(o instanceof byte[])) {
          throw new WarpScriptException(getName() + " needs a list of byte arrays.");
        }
        values.add((byte[]) o);
      }
    } else {
      throw new WarpScriptException(getName() + " needs a byte array value or a list thereof.");
    }
    
    //
    // Check that both lists have the same size
    //
    
    if (values.size() != keys.size()) {
      throw new WarpScriptException(getName() + " encountered key and value lists with different sizes.");
    }
    
    //
    // Verify if bucket already exists
    //
      
    String bucket = mapParam.getOrDefault(S3WarpScriptExtension.KEY_BUCKET, S3WarpScriptExtension.DEFAULT_BUCKET).toString();

    boolean existingBucket = false;
    
    for (Bucket b : s3client.listBuckets()) {
      if(b.getName().equals(bucket)) {
        existingBucket = true;
        break;
      }
    }
      
    if(!existingBucket) {
      s3client.createBucket(bucket);
    }
      
    //
    // Push objects on S3 client
    //
      
    for (int i = 0; i < keys.size(); i++) {
      ObjectMetadata metadata = new ObjectMetadata();
      metadata.setContentLength(values.get(i).length);
      metadata.setContentType("application/octet-stream");
      s3client.putObject(bucket, keys.get(i), new ByteArrayInputStream(values.get(i)), metadata);
    }
    
    return stack;    
  }
}
