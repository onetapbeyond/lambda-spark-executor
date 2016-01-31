###REST Integration

A sample Spark application that demonstrates the basic usage of SAMBA
to call a REST API on the AWS API Gateway.

####Source

Check out the example source code provided and you'll see that SAMBA
integrates seamlessly within any traditional Spark application. The source
code also provides extensive comments that help guide you through
the integration.

####Build

Run the following [scala-sbt](http://www.scala-sbt.org) command within
the `rest-integration` directory to build a `fatJar` for the example application
that can then be deployed directly to your Spark cluster.

``
sbt clean assembly
``

The generated `fatJar` can be found in the `target/scala-2.10` directory.

####Launch

The simplest way to launch the example application is to use the
[spark-submit](https://spark.apache.org/docs/latest/submitting-applications.html)
shell script provided as part of the Spark distribution.

The submit command you need should look something like this:

```
spark-submit --class io.onetapbeyond.lambda.spark.executor.examples.RESTIntegration --master local[*] /path/to/fat/jar/rest-integration-assembly-[version].jar
```
