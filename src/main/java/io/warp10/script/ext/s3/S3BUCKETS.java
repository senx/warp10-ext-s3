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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3ClientOptions;
import com.amazonaws.services.s3.model.Bucket;

import io.warp10.script.NamedWarpScriptFunction;
import io.warp10.script.WarpScriptException;
import io.warp10.script.WarpScriptStack;
import io.warp10.script.WarpScriptStackFunction;

/**
 * List the buckets the keys give access to in S3
 */
public class S3BUCKETS extends NamedWarpScriptFunction implements WarpScriptStackFunction {
  
  public S3BUCKETS(String name) {
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
    
   try {
     List<Bucket> bckts = s3client.listBuckets();
     List<String> buckets = new ArrayList<String>(bckts.size());
     
     for (Bucket bucket: bckts) {
       buckets.add(bucket.getName());
     }
     
     stack.push(buckets);
   } catch (SdkClientException ex) {
     throw new WarpScriptException("Error while listing buckets.", ex);
   } 
    
   return stack;
  }
}
