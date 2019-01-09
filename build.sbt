name := "WikiGraphs"
version := "0.0"
scalaVersion := "2.11.8"

lazy val sparkVersion = "2.4.0"
lazy val spark = "org.apache.spark"

libraryDependencies += spark %% "spark-core" % sparkVersion
libraryDependencies += spark %% "spark-sql" % sparkVersion
libraryDependencies += spark %% "spark-graphx" % sparkVersion
