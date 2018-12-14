package WikiGraph

import org.apache.spark.sql.SparkSession
import org.apache.log4j.{Logger, Level}


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

    println("Hello world!")
}
