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
