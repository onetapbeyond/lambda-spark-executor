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
