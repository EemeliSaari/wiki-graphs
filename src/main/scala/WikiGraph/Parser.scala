package WikiGraph

import scala.util.matching.Regex


object Parser {
    val internalPattern = new Regex("\\[{2}[^\\]]*\\]{2}")

    def findConnections(str: String) : Set[String] = {
        return internalPattern.findAllIn(str).subgroups.toSet
    }
}