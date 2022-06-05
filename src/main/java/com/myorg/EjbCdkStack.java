package com.myorg;

import software.constructs.Construct;

import java.util.ArrayList;
import java.util.List;

import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.route53.targets.ElasticBeanstalkEnvironmentEndpointTarget;
// import software.amazon.awscdk.Duration;
// import software.amazon.awscdk.services.sqs.Queue;
import software.amazon.awscdk.services.s3.Bucket;
import software.amazon.awscdk.services.ses.actions.S3;
import software.amazon.awscdk.services.s3.assets.Asset;
import software.amazon.awscdk.services.elasticbeanstalk.*;
import software.amazon.awscdk.services.elasticbeanstalk.CfnApplicationVersion.SourceBundleProperty;
import software.amazon.awscdk.services.elasticbeanstalk.CfnEnvironment.OptionSettingProperty;
import software.amazon.awscdk.services.iam.Role;
import software.amazon.awscdk.services.iam.ServicePrincipal;
import software.amazon.awscdk.services.iam.CfnInstanceProfile;
import software.amazon.awscdk.services.iam.IManagedPolicy;
import software.amazon.awscdk.services.iam.ManagedPolicy;

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
//        final Asset earFile = Asset.Builder.create(this, "Ear")
//        		.path("").build();
        final Asset dockerFile = Asset.Builder.create(this, "Dockerfile")
        		.path("Dockerfile").build();
        
        final String appName = "MyEarApp";
        final CfnApplication app = CfnApplication.Builder.create(this, "Application")
        		.applicationName(appName).build();
        
        final SourceBundleProperty sourceBundleProperty = SourceBundleProperty.builder()
        		.s3Bucket(dockerFile.getS3BucketName())
        		.s3Key(dockerFile.getS3ObjectKey())
        		.build();
        
        final CfnApplicationVersion appVersionProps = CfnApplicationVersion.Builder.create(this, "AppVersion")
        		.applicationName(appName)
        		.sourceBundle(sourceBundleProperty)
        		.build();
        
        appVersionProps.addDependsOn(app);
        
        final Role myRole = Role.Builder.create(this, appName + "-aws-eb-ec2-role")
        		.assumedBy( ServicePrincipal.Builder.create("ec2.amazonaws.com").build())
        		.build();
        
        final IManagedPolicy managedPolicy = ManagedPolicy.fromAwsManagedPolicyName("AWSElasticBeanstalkWebTier");
        myRole.addManagedPolicy(managedPolicy);
        
        final String myProfileName = appName + "-InstanceProfile";
        
        final List<String> roles = new ArrayList<String>();
        roles.add(myRole.getRoleName());
        final CfnInstanceProfile instanceProfile = CfnInstanceProfile.Builder.create(this, myProfileName)
        		.instanceProfileName(myProfileName)
        		.roles(roles)
        		.build();
        
        // EB options
        final OptionSettingProperty optionSettingProperty1 = CfnEnvironment.OptionSettingProperty.builder()
        		.namespace("aws:autoscaling:launchconfiguration")
        		.optionName("IamInstanceProfile")
        		.value(myProfileName)
        		.build();
        
        final OptionSettingProperty optionSettingProperty2 = CfnEnvironment.OptionSettingProperty.builder()
        		.namespace("aws:autoscaling:asg")
        		.optionName("MinSize")
        		.value("1")
        		.build();
        
        final OptionSettingProperty optionSettingProperty3 = CfnEnvironment.OptionSettingProperty.builder()
        		.namespace("aws:autoscaling:asg")
        		.optionName("MaxSize")
        		.value("1")
        		.build();
        
        final OptionSettingProperty optionSettingProperty4 = CfnEnvironment.OptionSettingProperty.builder()
        		.namespace("aws:ec2:instances")
        		.optionName("InstanceTypes")
        		.value("t2.micro")
        		.build();
        
        final List<OptionSettingProperty> optionSettingProperties = new ArrayList<>();
        optionSettingProperties.add(optionSettingProperty1);
        optionSettingProperties.add(optionSettingProperty2);
        optionSettingProperties.add(optionSettingProperty3);
        optionSettingProperties.add(optionSettingProperty4);
        
        final CfnEnvironment ebEnv = CfnEnvironment.Builder.create(this, "Environment")
        		.environmentName("MyEarEnvironment")
        		.applicationName(app.getApplicationName())
        		.solutionStackName("64bit Amazon Linux 2 v3.4.16 running Docker")
        		.optionSettings(optionSettingProperties)
        		.versionLabel(appVersionProps.getRef())
        		.build();
        
        
    }
}
