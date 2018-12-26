package WikiGraph

import scala.util.matching.Regex


object Parser {
    val internalPattern = "\\[{2}([^\\]]*)\\]{2}".r

    def findConnections(str: String) : Array[String] = {
        return internalPattern.findAllMatchIn(str)
            .map(_.group(1))
            .toArray
            .filter(parseSections)
            .map(parsePipeTrick)
            .map(parseColon)
            .filter(x => x != null)
    }
    def parsePipeTrick(str: String) : String = {
        val parts = str.split("\\|")
        if(parts.length == 1)
            return str
        if(parts(0) != "")
            return parts(0)
        else
            return parts(1)
    }
    def parseSections(str: String) : Boolean = {
        return !str.contains("#")
    }
    def parseColon(str: String) : String = {
        val parts : Array[String] = str.split("\\:")

        def default(n:Int) : String = {
            return parts.drop(n).reduce(_ + ":" + _)
        }

        if(parts.length == 1)
            return str

        return parts(0) match {
            case "" => default(2)   //":<text>:<text>"
            case "Kuva" => null     //Image
            case "Luokka" => null   //Class
            case x => default(1)    //Default
        }
    }
}