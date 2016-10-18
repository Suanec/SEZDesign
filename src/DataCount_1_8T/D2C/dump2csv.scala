import java.io.PrintWriter
import io.Source


object D2C {
  def Dump2CSV( file : String = "", codec : String = "" ){
    var iter = Source.fromFile(file).getLines
    if(codec.size != 0) iter = Source.fromFile(file)(codec).getLines
    val p = new PrintWriter( file.split('.').head + ".rst" )
    while(iter.hasNext){
      val str = iter.next
      if(str.size < 100)println(str)
      val strt = str.split(" VALUES ")
      if(strt.size > 1){
        val t = strt.last
        t.split("[(^)]").tail.init.
          filter(_.size > 1).
          foreach( l => p.write(l + "\n") )//p.write
      }
    }
    p.flush
    p.close
  }
  def main(args : Array[String]) {
    val file = args.head
    try {
      Dump2CSV(file)
    }
    catch{
      case ex : Throwable => Dump2CSV(file,"utf-8")
    }
  }
}
