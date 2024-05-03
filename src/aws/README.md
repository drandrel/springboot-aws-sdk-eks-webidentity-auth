# Setting up AWS accounts

## Requirements
* EKS
* EKS OpenID connection enabled
* IAM Identity provider oidc configured as 
  * provider = EKS OpenID url
  * audience = sts.amazonaws.com
* Role installed as s3-access (please check the file src/aws/sa-role.yaml and src/aws/sa-policy.json)
* service-account for the Deployment as
  * Role has access to s3:Get*, s3:List*, s3:Describe*
  * S3 bucket with ACL enabled or Policy as following
  ```
    {
      "Version": "2012-10-17",
      "Statement": [
          {
              "Sid": "Allow-EKS",
              "Effect": "Allow",
              "Principal": {
                  "AWS": "arn:aws:sts::${REPLACE_WITH_AWS_ACCOUND_ID}:assumed-role/s3-access/s3-access-java"
              },
              "Action": "s3:*",
              "Resource": "arn:aws:s3:::java-sdk-example-2"
          }
      ]
    }
  ```