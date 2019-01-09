package WikiGraph

import org.apache.spark.sql.SparkSession
import org.apache.log4j.{Logger, Level}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types.StringType
import org.apache.spark.sql.Row
import org.apache.spark.sql.functions.col

import Options._
import Utils._
import Parser._
import Connection._

/**
 * Entrypoint for the software.
 */
object WikiGraph extends App {

    Logger.getLogger("org").setLevel(Level.OFF)

    val spark = SparkSession.builder()
        .appName("WikiGraph")
        .config("spark.driver.host", "localhost")
        .master("local[*]")
        .getOrCreate()

    val sc = spark.sparkContext

    import spark.implicits._

    val opt = Options(args.toList).argv()

    val nodes = spark.read
        .format("csv")
        .option("header", "true")
        .option("delimiter", ";")
        .load(opt("reference_path"))
        .select($"ns".alias("Id"), $"title".alias("Property"))
        .as("nodes")

    nodes.show()
    val paths = sc.parallelize(listDir(opt("input_path"))).reduce(_ + "," + _)
    val raw = sc.wholeTextFiles(paths)

    val t1 = System.nanoTime

    val parsed = raw
        .map(x => (fileName(x._1), findConnections(x._2)))

    val duration = (System.nanoTime - t1) / 1e9d

    val edges = parsed
        .flatMap(pair => pair._2.map(value => (pair._1, value)))
        .toDF(Seq("SrcId", "Property"): _*)
        .as("edges")
        .dropDuplicates()
        .join(nodes, Seq("Property"), "inner")
        .withColumnRenamed("Id", "DstId")
        .select("SrcId", "DstId")

    val graph = initializeGraph(nodes, edges)

    df2csv(edges, "edges.csv")

    println(duration)
    edges.show()
    dfShape(edges)
}
