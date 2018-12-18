package WikiGraph

import scala.annotation.switch
import scala.collection.{Map, mutable, immutable}


case class Option(fixture: String = "=", default: String = null) {
    def parse(args: List[String], name: String) : String = {
        val matches = args.filter(a => a.contains(name))

        if(matches.size > 1)
            throw new Exception("Found " + matches.size + " matches for " + name)
        else if(matches.size < 1 && default == null)
            throw new Exception("Provide option for: " + name)
        else if(matches.size < 1 && default != null)
            return default
        else
            return matches(0).split(fixture)(1)
    }
}


case class Options(args: List[String]) {

    var optionsMap = immutable.Map[String, Option](
        "input_path" -> Option(),
        "output_path" -> Option(default="results/"),
        "reference_path" -> Option()
    )

    def argv() : Map[String, String] = {
        return mutable.Map[String, String]() ++ optionsMap.map(
            x => x._1 -> x._2.parse(args, x._1)
        )
    }
}