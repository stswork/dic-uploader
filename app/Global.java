import com.amazonaws.services.s3.model.GetBucketLocationRequest;
import play.Application;
import play.GlobalSettings;
import play.Logger;

/**
 * Created by Sagar Gopale on 12/21/13.
 */
public class Global extends GlobalSettings {

    public static final String AWS_S3_BUCKET = "aws.s3.bucket";

    @Override
    public void onStart(Application application) {
        GetBucketLocationRequest getBucketLocationRequest = new GetBucketLocationRequest(AWS_S3_BUCKET);
        Logger.info("BUCKET LOCATION : " + getBucketLocationRequest.toString());
    }
}
