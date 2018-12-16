package WikiGraph

import java.io.File


object Utils {
    
    def listdir(path: String) : Array[String] = {
        return new File(path).listFiles.map(f => f.getPath())
    }
}