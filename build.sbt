lazy val root = (project in file(".")).
  settings(
    name := "lambda-spark-executor",
    organization := "io.onetapbeyond",
    version := "1.0",
    scalaVersion := "2.10.6",
    libraryDependencies ++= Seq(
    	"org.apache.spark" % "spark-core_2.10" % "1.6.0" % "provided",
      "io.onetapbeyond" % "aws-gateway-executor" % "1.2",
    	"org.scalatest" % "scalatest_2.10" % "2.2.4" % "test"
  	),
    assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)
  )
