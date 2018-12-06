package io.warp10.script.ext.s3;

import io.warp10.script.NamedWarpScriptFunction;
import io.warp10.script.WarpScriptException;
import io.warp10.script.WarpScriptStack;
import io.warp10.script.WarpScriptStackFunction;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3ClientOptions;
import com.amazonaws.services.s3.model.Bucket;

public class AMAZONS3TEST extends NamedWarpScriptFunction implements WarpScriptStackFunction {
  
  public AMAZONS3TEST(String name) {
    super(name);
  }
  
  public Object apply(WarpScriptStack stack) throws WarpScriptException {
   
    AWSCredentials credentials = new BasicAWSCredentials("accessKey1",
        "verySecretKey1");

    // Create a client connection based on credentials
    AmazonS3 s3client = new AmazonS3Client(credentials);
    s3client.setEndpoint("http://localhost:8000");
    // Using path-style requests
    // (deprecated) s3client.setS3ClientOptions(new S3ClientOptions().withPathStyleAccess(true));
    s3client.setS3ClientOptions(S3ClientOptions.builder().setPathStyleAccess(true).build());

    // Create bucket
    //String bucketName = "javabucket";
    //s3client.createBucket(bucketName);

    // List off all buckets
    for (Bucket bucket : s3client.listBuckets()) {
        stack.push(" - " + bucket.getName());
    }
    return stack;  
  }

}
