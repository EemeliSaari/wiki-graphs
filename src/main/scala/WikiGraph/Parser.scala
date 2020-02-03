package WikiGraph

import scala.util.matching.Regex
import scala.io.Source
import java.io.IOException
import java.lang.{UnsupportedOperationException, ArrayIndexOutOfBoundsException, NullPointerException}
import java.nio.charset.UnmappableCharacterException


object Parser {
    val internalPattern = "\\[{2}([^\\]]*)\\]{2}".r

    def connectionsFromFile(path: String) : Array[String] = {
        try {
            return connectionsFromString(Source.fromFile(path).mkString)
        } catch {
            case e: UnmappableCharacterException => {
                println(s"Error with path: $path")
                return Array[String]()
            }
        }
    }

    def connectionsFromString(str: String) : Array[String] = {
        return internalPattern.findAllMatchIn(str)
            .map(_.group(1))
            .toArray
            .filter(parseSections)
            .map(parsePipeTrick)
            .map(parseColon)
            .filter(x => x != null)
    }

    def parsePipeTrick(str: String) : String = {
        if (str == null) {
            return str
        }

        val parts = str.split('|')

        try {
            if(parts.length == 1)
                return str
            if(parts(0) != "")
                return parts(0)
            else
                return parts(1)
        } catch {
            case e: ArrayIndexOutOfBoundsException => {
                println(s"Index Out of bounds with pipe trick: $str")
                return null
            }
            case e: NullPointerException => {
                println(s"NullPointer exception with pipe: $str")
                return null
            }
        }
    }

    def parseSections(str: String) : Boolean = {
        return !str.contains('#')
    }

    def parseColon(str: String) : String = {
        if (str == null) {
            return str
        }

        val parts : Array[String] = str.split(':')

        def default(n: Int) : String = {
            if (n == 0) {
                return parts.reduce(_ + ':' + _)
            }
            try {
                return parts.drop(n).reduce(_ + ":" + _)
            } catch {
                case e: UnsupportedOperationException => {
                    println(s"Error with $str, $n")
                    parts.foreach(println)
                    return default(n - 1)
                }
            }
        }

        if(parts.length == 1)
            return str

        return parts(0) match {
            case "" => default(2)   //":<text>:<text>"
            case "Kuva" => null     //Image
            case "Luokka" => null   //Class
            case "File" => null     //File
            case x => default(1)    //Default
        }
    }
}
