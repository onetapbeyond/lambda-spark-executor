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

import io.onetapbeyond.lambda.spark.executor.Gateway._
import io.onetapbeyond.aws.gateway.executor._
import org.apache.spark._
import org.scalatest._
import scala.collection.JavaConverters._

/*
 * Apache Spark AWS Lambda Executor (SAMBA) unit tests.
 */
class SAMBATestSpec extends FlatSpec with Matchers with BeforeAndAfter {

	private var sc: SparkContext = _

	/*
   * Prepare SparkContext (sc) per test.
   */
	before {
    val conf = new SparkConf().setMaster(SAMBATestSpec.MASTER)
                              .setAppName(SAMBATestSpec.APP_NAME)
    sc = new SparkContext(conf)
	}

	/*
   * Release SparkContext (sc) following each unit test.
   */
	after {
		if (sc != null)
  		sc.stop()
	}

	"SAMBA RDD[AWSTask] delegate transformation" should "generate RDD[AWSResult]." in {

    /*
     * MOCK_API_GATEWAY represents the API on the AWS API Gateway
     * used by this test. We register the gateway as a
     * Spark broadcast variable so it can be safely referenced later
     * within the Spark RDD.map operation that builds our AWSTask.
     */
    val gateway = sc.broadcast(SAMBATestSpec.MOCK_API_GATEWAY)

		/*
     *Prepare sample Spark batch test data.
     */
		val numRDD = sc.parallelize(1 to SAMBATestSpec.BATCH_DATA_SIZE)

		/*
     * Map over dataRDD[Int] to produce an RDD[AWSTask].
     * Each AWSTask will process the Spark batch data by executing
     * a mock score computation on the AWS Lambda compute service.
     */
		val taskRDD = numRDD.map(num => { 

			/*
       * Build and return mock score computation AWSTask instance.
       */
			AWS.Task(gateway.value)
         .resource(SAMBATestSpec.MOCK_API_SCORE)
         .input(Map("num" -> num).asJava)
         .post()
		})

		/*
     * Generate RDD[AWSResult] by executing the delegate operation
		 * on RDD[AWSTask] provided by SAMBA.
     */
    val resultRDD = taskRDD.delegate
		resultRDD.cache

		/*
     * Process sample RDD[AWSResult].
     */
		val resultCount = resultRDD.count
		val successCount = resultRDD.filter(result => result.success).count

		/*
     * Verify RDD[AWSResult] data.
     */
		assert(resultCount == SAMBATestSpec.BATCH_DATA_SIZE)
		assert(successCount == SAMBATestSpec.BATCH_DATA_SIZE)

	}

}

/*
 * SAMBATestSpec AWS API Gateway mock test constants.
 */
object SAMBATestSpec {

  private val MASTER = "local[2]"
  private val BATCH_DATA_SIZE = 10
  private val APP_NAME = "lambda-spark-executor-test"
  private val MOCK_API_ID = "06ti6xmgg2"
  private val MOCK_API_STAGE = "mock"
  private val MOCK_API_SCORE= "/score"
  private val MOCK_API_GATEWAY:AWSGateway = AWS.Gateway(MOCK_API_ID)
                                               .region(AWS.Region.OREGON)
                                               .stage(MOCK_API_STAGE)
                                               .build()
}
