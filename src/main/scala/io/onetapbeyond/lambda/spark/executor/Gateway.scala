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
package io.onetapbeyond.lambda.spark.executor

import org.apache.spark.rdd.RDD
import io.onetapbeyond.aws.gateway.executor.AWSTask
import io.onetapbeyond.aws.gateway.executor.AWSResult

/**
 * Apache Spark  AWS Lambda Executor (SAMBA).
 */
class Gateway(rdd:RDD[AWSTask]) {

  /**
   * RDD transformation that maps each {@link AWSTask} to {@link AWSResult}.
   */
  def delegate():RDD[AWSResult] = rdd.map(aTask => aTask.execute)

}

object Gateway {
  implicit def addGateway(rdd: RDD[AWSTask]) = new Gateway(rdd)
}
