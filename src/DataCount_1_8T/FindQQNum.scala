import io.Source
import java.io.File
import java.io.PrintWriter
import collection.mutable.ArrayBuffer
object FindQQNum {
  def search(_target : Int = 81123597) = {
    val path = new File("").getAbsolutePath
    val p = new File(path)
    val files = p.listFiles
    val target = _target
    val buffer = new ArrayBuffer[String]
    files.foreach{
      file =>
        try {
          val f = Source.fromFile(file).getLines
          f.map{
            s =>
              val splits = s.split("@").head
              // if(splits.size == 8 && splits.toInt == 81123597) writer.write( s + "\n")
          }//.map( s => buffer += s )
        }
        catch {
          case ex : Throwable => println(file)
        }
    }
  }
  def splitFile() = {
    val path = new File("").getAbsolutePath
    val p = new File(path)
    val files = p.listFiles
    var count = 0
    files.map{
      file =>
        val f = Source.fromFile(file).getLines
        count = 1
        val print = new PrintWriter(file.toString + count.toString)
        try {
          while(f.hasNext){
            f.take(10000).foreach{
              s =>
                val head = s.split("@").head
                if( head.size == 8 && head.toInt == 81123597) print.write( s + "\n")
            }
            count += 1
          }
        }
        catch {
          case ex : Throwable => {
            println(ex.toString + " : " + file.toString)
            print.flush
          }
        }
        print.flush
        print.close
    }
  }
}

