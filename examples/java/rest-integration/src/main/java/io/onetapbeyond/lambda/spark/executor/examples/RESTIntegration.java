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
package io.onetapbeyond.lambda.spark.executor.examples;

import io.onetapbeyond.aws.gateway.executor.*;
import org.apache.spark.*;
import org.apache.spark.api.java.*;
import org.apache.spark.broadcast.Broadcast;
import java.util.*;

/*
 * RESTIntegration
 *
 * A sample application that demonstrates the basic usage of SAMBA
 * to call a REST API on the AWS API Gateway.
 */
public class RESTIntegration {

  public static void main(String[] args) {

    try {

      JavaSparkContext sc = initSparkContext();

      /*
       * Initialize a basic batch data source for the example by
       * generating an RDD[Int].
       */
      JavaDoubleRDD dataRDD =
        sc.parallelizeDoubles(Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0));

      /*
       * Simple RDD.max represents minimal Apache Spark application.
       */
      double max = dataRDD.max();

      /*
       * Call the REST "report" endpoint on the API indicated by
       * our instance of AWSGateway, pushing the max data value detected
       * within our Spark driver program. As we are using a mock API on
       * the AWS API Gateway there is no response data, in this case
       * the result simply indicates success or failure.
       */
      Map data = new HashMap();
      data.put("max", max);
      AWSResult aTaskResult = AWS.Task(API_GATEWAY)
                                 .resource(API_REPORT_ENDPOINT)
                                 .input(data)
                                 .post()
                                 .execute();

      /*
       * Verify REST "report" call on API was a success.
       */
      System.out.println("RESTIntegration: report call success=" +
                                            aTaskResult.success());


    } catch(Exception ex) {
      System.out.println("RESTIntegration: caught ex=" + ex);
    }

  }

  private static JavaSparkContext initSparkContext() {
    SparkConf conf = new SparkConf().setAppName(APP_NAME);
    return new JavaSparkContext(conf);
  }

  private static String APP_NAME = "SAMBA REST Integration Example";
  private static String API_ID = "06ti6xmgg2";
  private static String API_STAGE = "mock";
  private static String API_REPORT_ENDPOINT = "/report";
  private static AWSGateway API_GATEWAY = AWS.Gateway(API_ID)
                                             .region(AWS.Region.OREGON)
                                             .stage(API_STAGE)
                                             .build();

}
