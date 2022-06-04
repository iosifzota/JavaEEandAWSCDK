package com.myorg;

import software.constructs.Construct;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.route53.targets.ElasticBeanstalkEnvironmentEndpointTarget;
// import software.amazon.awscdk.Duration;
// import software.amazon.awscdk.services.sqs.Queue;
import software.amazon.awscdk.services.s3.Bucket;
import software.amazon.awscdk.services.ses.actions.S3;
import software.amazon.awscdk.services.s3.assets.Asset;
import software.amazon.awscdk.services.elasticbeanstalk.*;

public class EjbCdkStack extends Stack {
    public EjbCdkStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public EjbCdkStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        // The code that defines your stack goes here

        // example resource
        // final Queue queue = Queue.Builder.create(this, "EjbCdkQueue")
        //         .visibilityTimeout(Duration.seconds(300))
        //         .build();
        final Asset earFile = Asset.Builder.create(this, "Ear")
        		.path("").build();
        
        final String appName = "MyEarApp";
        final CfnApplication app = CfnApplication.Builder.create(this, "Application")
        		.applicationName(appName).build();
    }
}
