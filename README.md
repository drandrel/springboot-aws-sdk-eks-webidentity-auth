# Example JAVA-SDK v2 with WebIdentity authentication for EKS

This example has a purpose of exemplify a connection to S3 using SDK WebIdentityTokenFileCredentialsProvider, with this provider no AWS credentials are required, but POD must be running with proper Service Account configured.
The service account which runs the pdo must have the the tags `eks.amazonaws.com/role-arn: arn:aws:iam::${REPLACE_WITH_AWS_ACCOUNT}:role/s3-access`
and `eks.amazonaws.com/sts-regional-endpoints: "true"` (the regional endpoint is optional, but rightly recommended as it reduces latency).

## How does it wotks.
1. K8s deploys the pod using a pre-defined service account, this service account assumes a pre-defined role through eks oidc
2. Once the pod is running, some environemnt variables are injected by EKS:
```
AWS_ROLE_ARN=arn:aws:iam::${AWS_ACCOUNT_ID}:role/s3-access
AWS_WEB_IDENTITY_TOKEN_FILE=/var/run/secrets/eks.amazonaws.com/serviceaccount/token
AWS_STS_REGIONAL_ENDPOINTS=regional
AWS_DEFAULT_REGION=${AWS_REGION}
AWS_REGION=${AWS_REGION}
```
3. S3 client bean (see src/main/java/com/examples/springboot/S3Config) fetches those environemnts in order to fetch the role and the session token
```
.credentialsProvider(WebIdentityTokenFileCredentialsProvider
                        .builder()
                        .roleArn(System.getenv("AWS_ROLE_ARN"))
                        .roleSessionName(UUID.randomUUID().toString())
                        .webIdentityTokenFile(Path.of(System.getenv("AWS_WEB_IDENTITY_TOKEN_FILE")))
                        .build())
                .region(Region.EU_CENTRAL_1)

```
4. By using the session token, the java app is able to request S3 service by assuming the s3-access role

## How to call the service without an ingress
As this is a simple example, we are not using any ingress config. In order to call the service, please use port-forward
```
kubectl port-forward svc/test-chart 8080:8080
```
Then use your preferred browser to call one of the endopoints:
- http://localhost:8080/api/v1
- http://localhost:8080/api/v1/list_buckets
- http://localhost:8080/api/v1/view/${filename} # the file has to exist in the s3 bucket prior to the call
