###Task Delegation

A sample application that demonstrates the basic usage of SAMBA
to delegate selected Spark task operations to execute on AWS Lambda
compute infrastructure in the cloud.

####Source

Check out the example source code provided and you'll see that SAMBA
integrates seamlessly within any traditional Spark application. The source
code also provides extensive comments that help guide you through
the integration.

####Build

Run the following [Gradle](http://gradle.org/) command within
the `task-delegation` directory to build a `fatJar` for the example application
that can then be deployed directly to your Spark cluster.

``
gradlew clean shadowJar
``

The generated `fatJar` can be found in the `build/libs` directory.

####Launch

The simplest way to launch the example application is to use the
[spark-submit](https://spark.apache.org/docs/latest/submitting-applications.html)
shell script provided as part of the Spark distribution.

The submit command you need should look something like this:

```
spark-submit --class io.onetapbeyond.lambda.spark.executor.examples.TaskDelegation --master local[*] /path/to/fat/jar/task-delegation-[version]-all.jar
```
