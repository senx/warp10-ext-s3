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

import java.util.HashMap;
import java.util.Map;

import io.warp10.warp.sdk.WarpScriptExtension;

public class S3WarpScriptExtension extends WarpScriptExtension {

  private static final Map<String, Object> functions;

  public static final String DEFAULT_BUCKET = "warp10";
  
  public static final String KEY_BUCKET = "bucket";
  public static final String KEY_ACCESS_KEY = "accessKey";
  public static final String KEY_SECRET_KEY = "secretKey";
  public static final String KEY_ENDPOINT = "endPoint";
  
  static {
    functions = new HashMap<String, Object>();
    functions.put("S3STORE", new S3STORE("S3STORE"));
    functions.put("S3LOAD", new S3LOAD("S3LOAD"));
    functions.put("S3BUCKETS", new S3BUCKETS("S3BUCKETS"));
  }
  
  public Map<String, Object> getFunctions() {    
    return functions;
  }
}
