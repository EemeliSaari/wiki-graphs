package WikiGraph

import java.io.File


object Utils {
    def listDir(path: String) : Array[String] = {
        return new File(path).listFiles.map(f => f.getPath())
    }
    def fileName(path: String) : String = {
        return path.tail.split("/").last
    }
}