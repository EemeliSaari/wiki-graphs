package WikiGraph

import scala.util.matching.Regex
import scala.io.Source
import java.io.IOException
import java.lang.{UnsupportedOperationException, ArrayIndexOutOfBoundsException, NullPointerException}
import java.nio.charset.UnmappableCharacterException


class PageParser() {
    val ImageTag: String = "Image"
    val ClassTag: String = "Class"
    val FileTag: String = "File"

    var classes: Array[String] = Array[String]()
    var connections: Array[String] = Array[String]()

    val internalPattern = "\\[{2}([^\\]]*)\\]{2}".r

    def parse(text: String) : Unit = {
        connections = internalPattern.findAllMatchIn(text)
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
            case "" => default(2)                   //":<text>:<text>"
            case ClassTag => handleClass(parts)     //Class
            case ImageTag => null                   //Image
            case FileTag => null                    //File
            case x => default(1)                    //Default
        }
    }

    def handleClass(parts: Array[String]) : String = {
        classes = classes :+ parts(1)
        return null
    }
}


class FinnishParser() extends PageParser() {
    override val ImageTag: String = "Kuva"
    override val ClassTag: String = "Luokka"
}


object Parser {
    var idx = 0

    def parserFromFile(path: String) : PageParser = {
        val parser = new FinnishParser()

        var text = ""
        try {
            text = Source.fromFile(path).mkString
        } catch {
            case e: UnmappableCharacterException => {
                println(s"Error with path: $path")
            }
            return parser
        }
        parser.parse(text)

        idx = idx + 1
        if (idx % 100 == 0) {
            println(idx)
        }
        return parser
    }
}
