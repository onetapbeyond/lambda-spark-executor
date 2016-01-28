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
 * RESTIntegration
 *
 * A sample application that demonstrates the basic usage of SAMBA
 * to call a REST API on the AWS API Gateway.
 */
object RESTIntegration {

  def main(args:Array[String]):Unit = {

    try {

      val sc = initSparkContext()

      /*
       * Initialize a basic batch data source for the example by
       * generating an RDD[Int].
       */
      val dataRDD = sc.parallelize(1 to BATCH_DATA_SIZE)

      /*
       * Simple RDD.collect represents minimal Apache Spark application.
       */
      val max = dataRDD.collect.max

      /*
       * MOCK_API_GATEWAY represents the REST API on the AWS API Gateway
       * used by this example application.
       */
      val gateway = sc.broadcast(MOCK_API_GATEWAY)

      /*
       * Call the REST "report" service on the API indicated by
       * our instance of AWSGateway, pushing the max data value detected
       * within our Spark driver program. Using a Mock API on the AWS
       * API Gateway there is no response data, the result simply
       * indicates success or failure.
       */
      val aTaskResult = AWS.Task(gateway.value)
                           .resource(MOCK_API_REPORT_ENDPOINT)
                           .input(Map("max" -> max).asJava)
                           .post()
                           .execute()

      /*
       * Verify REST "report" call on API was a success.
       */
      println("RESTIntegration: report call success=" + aTaskResult.success)


    } catch {
      case t:Throwable => println("RESTIntegration: caught ex=" + t)
    }

  }

  def initSparkContext():SparkContext = {
    val conf = new SparkConf().setAppName(APP_NAME)
    new SparkContext(conf)
  }

  private val APP_NAME = "SAMBA REST Integration Example"
  private val BATCH_DATA_SIZE = 10

  private val MOCK_API_ID = "06ti6xmgg2"
  private val MOCK_API_STAGE = "mock"
  private val MOCK_API_REPORT_ENDPOINT = "/report"
  private val MOCK_API_GATEWAY:AWSGateway = AWS.Gateway(MOCK_API_ID)
                                               .region(AWS.Region.OREGON)
                                               .stage(MOCK_API_STAGE)
                                               .build()

}
