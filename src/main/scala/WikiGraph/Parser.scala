package WikiGraph

import scala.util.matching.Regex


object Parser {
    val internalPattern = "\\[{2}[^\\]]*\\]{2}".r

    def findConnections(str: String) : Array[String] = {
        return internalPattern.findAllIn(str).toArray
    }
}