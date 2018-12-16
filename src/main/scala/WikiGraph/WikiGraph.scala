package WikiGraph

import org.apache.spark.sql.SparkSession
import org.apache.log4j.{Logger, Level}
import org.apache.spark.rdd.RDD

import Options._
import Utils._
import Parser._

/**
 * Entrypoint for the software.
 */
object WikiGraph extends App {

    Logger.getLogger("org").setLevel(Level.OFF)

    val spark = SparkSession.builder()
        .appName("WikiGraph")
        .config("spark.driver.host", "localhost")
        .master("local")
        .getOrCreate()

    val sc = spark.sparkContext

    import spark.implicits._

    val opt = Options(args.toList).argv()

    println(opt("input_path"))
    val paths : RDD[String] = sc.parallelize(listdir(opt("input_path")))
    //paths.foreach(println)
    val raw = spark.read.textFile("data/articles/291.txt")
    println(raw.reduce(_ + _))

    val conn : RDD[(String, Set[String])] = paths
        .map(p => (p, findConnections(spark.read.textFile(p).reduce(_ + _))))
    //conn.collect().foreach{case (name, set) => println(set)}
}
