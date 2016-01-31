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
 * TaskDelegation
 *
 * A sample application that demonstrates the basic usage of SAMBA
 * to delegate selected Spark RDD tasks to execute on AWS Lambda
 * compute infrastructure in the cloud.
 */
public class TaskDelegation {

  public static void main(String[] args) {

    try {

      JavaSparkContext sc = initSparkContext();

      /*
       * Initialize a basic batch data source for the example by
       * generating an RDD<Int>.
       */
      JavaRDD<Integer> dataRDD =
        sc.parallelize(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8));

      /*
       * API_GATEWAY represents the API on the AWS API Gateway
       * implemented by an AWS Lambda function. We register the gateway
       * as a Spark broadcast variable so it can be safely referenced
       * later within the Spark RDD.map operation that builds our AWSTask.
       */
      Broadcast<AWSGateway> gateway = sc.broadcast(API_GATEWAY);

      /*
       * Map over dataRDD<Int> to produce an RDD<AWSTask>.
       * Each AWSTask will execute an AWS Lambda function exposed
       * by the API_SCORE_ENDPOINT endpoint on the AWS API Gateway.
       */
      JavaRDD<AWSTask> aTaskRDD = dataRDD.map(number -> {

        Map data = new HashMap();
        data.put("num", number);

        return AWS.Task(gateway.value())
                  .resource(API_SCORE_ENDPOINT)
                  .input(data)
                  .post();
      });

      /*
       * Delegate aTaskRDD<AWSTask> execution to AWS Lambda compute
       * infrastructure to produce aTaskResultRDD<AWSResult>.
       */
      JavaRDD<AWSResult> aTaskResultRDD =
                            aTaskRDD.map(aTask -> aTask.execute());

      /*
       * As this is an example application we can simply use the
       * foreach() operation on the RDD to force the computation
       * and to output the results. And as we are using a mock API
       * on the AWS API Gateway there is no response data, the
       * result simply indicates success or failure.
       */
      aTaskResultRDD.foreach(aTaskResult -> {
        System.out.println("TaskDelegation: compute score input=" +
          aTaskResult.input() + " result=" + aTaskResult.success());
      });

    } catch(Exception ex) {
      System.out.println("TaskDelegation: caught ex=" + ex);
    }

  }

  private static JavaSparkContext initSparkContext() {
    SparkConf conf = new SparkConf().setAppName(APP_NAME);
    return new JavaSparkContext(conf);
  }

  private static String APP_NAME = "SAMBA Task Delegation Example";
  private static String API_ID = "06ti6xmgg2";
  private static String API_STAGE = "mock";
  private static String API_SCORE_ENDPOINT = "/score";
  private static AWSGateway API_GATEWAY = AWS.Gateway(API_ID)
                                             .region(AWS.Region.OREGON)
                                             .stage(API_STAGE)
                                             .build();
}
