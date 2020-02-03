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
//import UserGraph._


import org.apache.spark.sql.{DataFrame, Row}
//import org.apache.spark.rdd.RDD
import org.apache.spark.graphx.{Graph, Edge, VertexId}

object GraphLoader {
    val defaultConnection = ("Missing")

    def initializeGraph(edges:DataFrame, nodes:DataFrame) : Graph[String, String] = {
        return Graph(parseNodes(nodes), parseEdges(edges), defaultConnection)
    }

    def parseEdges(edges:DataFrame) : RDD[Edge[String]] = {
        return edges.rdd
            .map{case Row(src: Int, dst:Int) => Edge(src, dst, "reference")}
    }

    def parseNodes(nodes:DataFrame) : RDD[(VertexId, (String))] = {
        return nodes.rdd
            .map{case Row(id: Int, property:String) => (id, (property))}
    }
}

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

    val t2 = System.nanoTime

    val inputPath = opt("input_path")
    var paths : RDD[String] = sc.emptyRDD
    //if(isCsv(inputPath))
    val df = spark.read
        .format("csv")
        .option("header", "true")
        .option("delimiter", ";")
        .load(inputPath)
        .select("abspath")
    println((System.nanoTime - t2)/1e9d)

    df.show()

    paths = df
        .rdd
        .map{case Row(value: String) => value}
    //else
        //paths = sc.parallelize(listDir(inputPath))

    println("Readed all the paths!")

    println("Loaded the files...")
    val t1 = System.nanoTime

    //paths.collect().foreach(println)

    val parsed = paths
        .map(x => (parseFilename(x), connectionsFromFile(x)))

    println("Parsing step 1. Done!")

    val duration = (System.nanoTime - t1) / 1e9d
    println(duration)

    val edges = parsed
        .flatMap(pair => pair._2.map(value => (pair._1, value)))
        .toDF(Seq("SrcId", "Property"): _*)
        .as("edges")
        .dropDuplicates()
        .join(nodes, Seq("Property"), "inner")
        .withColumnRenamed("Id", "DstId")
        .select("SrcId", "DstId")

    println("Acquired edges")

    //val graph = GraphLoader.initializeGraph(nodes, edges)

    df2csv(edges, "edges.csv")

    dfShape(edges)
}
