package WikiGraph

import java.io._
import org.apache.spark.sql.{DataFrame, Row}


object Utils {
    def listDir(path: String) : Array[String] = {
        return new File(path).listFiles.map(f => f.getPath())
    }
    def fileName(path: String) : String = {
        return path.tail.split("/").last.split("\\.")(0)
    }
    def df2csv(df:DataFrame, filename:String, delimiter:String = ",") : Unit = {

        val rows = df.rdd.map(_.toSeq.mkString(delimiter) + "\n")

        val file = new BufferedWriter(new FileWriter(filename))
        val w = new PrintWriter(file)

        w.write(df.columns.mkString(delimiter) + "\n")
        rows.collect.map(r => w.write(r))
        w.close
    }
    def dfShape(df:DataFrame) : Unit = {
        val rows = df.count()
        val cols = df.columns.size
        println("(" + rows + ", " + cols + ")")
    }
}