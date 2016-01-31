/*
 * Copyright 2016 David Russell
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.onetapbeyond.lambda.spark.executor.examples

import io.onetapbeyond.lambda.spark.executor.Gateway._
import io.onetapbeyond.aws.gateway.executor._
import org.apache.spark._
import scala.collection.JavaConverters._

/*
 * TaskDelegation
 *
 * A sample application that demonstrates the basic usage of SAMBA
 * to delegate selected Spark RDD tasks to execute on AWS Lambda
 * compute infrastructure in the cloud.
 */
object TaskDelegation {

  def main(args:Array[String]):Unit = {

    try {

      val sc = initSparkContext()

      /*
       * Initialize a basic batch data source for the example by
       * generating an RDD[Int].
       */
      val dataRDD = sc.parallelize(1 to BATCH_DATA_SIZE)

      /*
       * API_GATEWAY represents the API on the AWS API Gateway
       * implemented by an AWS Lambda function. We register the gateway
       * as a Spark broadcast variable so it can be safely referenced
       * later within the Spark RDD.map operation that builds our AWSTask.
       */
      val gateway = sc.broadcast(API_GATEWAY)

      /*
       * Map over dataRDD[Int] to produce an RDD[AWSTask].
       * Each AWSTask will execute an AWS Lambda function exposed
       * by the API_SCORE_ENDPOINT endpoint on the AWS API Gateway.
       */
      val aTaskRDD = dataRDD.map(num => {

        AWS.Task(gateway.value)
           .resource(API_SCORE_ENDPOINT)
           .input(Map("num" -> num).asJava)
           .post()
      })

      aTaskRDD.foreach { aTask => println(aTask) }

      /*
       * Apply the SAMBA delegate transformation to aTaskRDD[AWSTask]
       * in order to generate aTaskResultRDD[AWSResult].
       */
      val aTaskResultRDD = aTaskRDD.delegate

      /*
       * As this is an example application we can simply use the
       * foreach() operation on the RDD to force the computation
       * and to output the results. And as we are using a mock API
       * on the AWS API Gateway there is no response data, the
       * result simply indicates success or failure.
       */
      aTaskResultRDD.foreach { result => {
        println("TaskDelegation: compute score input=" +
          result.input + " result=" + result.success)
      }}

    } catch {
      case t:Throwable => println("TaskDelegation: caught ex=" + t)
    }

  }

  def initSparkContext():SparkContext = {
    val conf = new SparkConf().setAppName(APP_NAME)
    new SparkContext(conf)
  }

  private val APP_NAME = "SAMBA Task Delegation Example"
  private val BATCH_DATA_SIZE = 10
  private val API_ID = "06ti6xmgg2"
  private val API_STAGE = "mock"
  private val API_SCORE_ENDPOINT = "/score"
  private val API_GATEWAY:AWSGateway = AWS.Gateway(API_ID)
                                          .region(AWS.Region.OREGON)
                                          .stage(API_STAGE)
                                          .build()
}
