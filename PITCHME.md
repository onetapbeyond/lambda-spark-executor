---

@title[Introduction]
### Apache Spark
### AWS Lambda Executor
### (SAMBA)

@color[gray](An Apache Spark Package)

---

@title[Apache Spark Package]
### SAMBA Apache Spark Package

  - Offers seamless integration with the AWS Lambda compute service
  - Within Spark batch and streaming apps on the JVM

---

### SAMBA API

@ol
- New `delegate` operation on RDD[@color[gray](AWSTask)]
- This operation executes AWS Lambda functions
- And generates RDD[@color[gray](AWSResult)]
@olend

<span class="fragment" style="font-size: 0.8em; color:gray">The SAMBA API is built on top of the <a target="_blank" href="https://github.com/onetapbeyond/aws-gataway-executor">aws-gateway-executor</a> library.</span>

---

### aws-gateway-executor

- A lightweight, fluent Java library
- For calling APIs on the Amazon Web Service API Gateway
- Inside any application running on the JVM
- Defines @color[gray](AWSGateway), @color[gray](AWSTask) and @color[gray](AWSResult)

+++

### AWSGateway

@color[gray](A handle that represents an API on the AWS API Gateway.)

```Java
AWSGateway gateway = AWS.Gateway(echo-api-key)
                        .stage("beta")
                        .region(AWS.Region.OREGON)
                        .build();
```


+++

### AWSTask

@color[gray](An executable object that represents an AWS Gateway call.)

```Java
AWSTask aTask = AWS.Task(gateway)
                   .resource("/echo")
                   .get();

```

+++

### AWSResult

@color[gray](An object that represents the result of an AWS Gateway call.)

```Java
AWSResult aResult = aTask.execute();
```

---

@title[Batch Processing]
### SAMBA + Apache Spark Batch Processing

+++?gist=onetapbeyond/494e0fecaf0d6a2aa2acadfb8eb9d6e8&title=SAMBA Code-Walk
@[41-53](Build RDD[AWSTask])
@[57-62](Delegate RDD[AWSTask] to AWS Lambda)
@[64-75](Process RDD[AWSResult] from AWS Lambda)

---

#### SAMBA Deployment Architecture

@title[Deployment Architecture]
![SAMBA Deployment](https://onetapbeyond.github.io/resource/img/samba/new-samba-deploy.jpg)

---

#### Some Related Links

- [GitHub: SAMBA Package](https://github.com/onetapbeyond/lambda-spark-executor)
- [GitHub: SAMBA Examples](https://github.com/onetapbeyond/lambda-spark-executor#samba-examples)
- [GitHub: aws-gateway-executor](https://github.com/onetapbeyond/aws-gateway-executor)
- [GitHub: Apache Spark](https://github.com/apache/spark)
- [Apache Spark Packages](https://spark-packages.org/package/onetapbeyond/lambda-spark-executor)
