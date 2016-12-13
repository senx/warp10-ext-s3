package io.warp10.amazons3.script;

import java.util.HashMap;
import java.util.Map;

import io.warp10.warp.sdk.WarpScriptExtension;

public class AmazonExtension extends WarpScriptExtension {

  private final Map<String, Object> functions;
  
  public AmazonExtension() {
    this.functions = new HashMap<String, Object>();
    this.functions.put("S3STORE", new S3STORE("S3STORE"));
    this.functions.put("S3LOAD", new S3LOAD("S3LOAD"));
    this.functions.put("AMAZONS3TEST", new AMAZONS3TEST("AMAZONS3TEST"));
  }
  
  public Map<String, Object> getFunctions() {    
    return this.functions;
  }
}
