package showSizeAndContent


/// scala DataCount >> result
/**
 * Created by suanec on 2016/6/20 : 17:30.
 */
object showSizeAndContent {
  import io.Source
  import java.io.{File,PrintWriter}
  // import scala.util.Random
  def getHead( file : String , codec : String = ""){
    println("File Name : " + file.toString + " : ")
    val printer = new PrintWriter(file.split('.').head + ".result")
    var size = 0d
    var str = ""
    if(codec.size == 0){
      size = Source.fromFile(file).getLines.size
      // val rand = new Random(System.currentTimeMillis())
      // val step = math.abs(rand.nextInt % size + size/10)
      var step = 0d
      if(size > 1000) step = 1000d
      else step = size
      val iter = Source.fromFile(file).getLines
      val res = iter.take(step.toInt)
      while( res.hasNext ) printer.write(res.next.toString + "\n")
    }else{
      size = Source.fromFile(file)(codec).getLines.size
      // val rand = new Random(System.currentTimeMillis())
      var step = 0d
      if(size > 1000) step = 1000d
      else step = size
      val iter = Source.fromFile(file)("utf-8").getLines
      val res = iter.take(step.toInt)
      while( res.hasNext ) printer.write(res.next.toString + "\n")
    }
    println("Size : " + size.toInt.toString)
    printer.flush
    printer.close
  }
  def main(args : Array[String]): Unit ={
    var file = args.head
    if(args.size == 2) file = args.head + "\\" + args.last
    try{
      getHead(file, "utf-8")
    }
    catch{
      case ex : Throwable => {
        println("utf-8 : " + ex.toString)
        getHead(file)
      }
    }
  }

}