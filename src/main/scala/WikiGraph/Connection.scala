package WikiGraph

import org.apache.spark.sql.{DataFrame, Row}
import org.apache.spark.rdd.RDD
import org.apache.spark.graphx.{Graph, Edge, VertexId}


object Loader {
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