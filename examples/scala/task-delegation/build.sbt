lazy val root = (project in file(".")).
  settings(
    name := "task-delegation-example",
    organization := "io.onetapbeyond",
    version := "1.0",
    scalaVersion := "2.10.6",
    libraryDependencies ++= Seq(
    	"org.apache.spark" % "spark-core_2.10" % "1.6.0" % "provided",
      "io.onetapbeyond" % "lambda-spark-executor_2.10" % "1.0"
  	),
    assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)
  )
