package DataCount_1_8T


/// scala DataCount >> result
/**
 * Created by suanec on 2016/6/20 : 17:30.
 */
object DataCount1 {
  import io.Source
  import java.io.{File,PrintWriter}
  def main(args : Array[String]): Unit ={
    println("args(2) : src File , @@!!dst File(NoUseful) (del Empty Lines). ")
    var path = new File("")
    var printFile = ""
    if(args.size == 0)path = new File("").getAbsoluteFile
    else path = new File(args.head)
    printFile = path.toString + ".1.result"
    val p = new PrintWriter(printFile)
    val file = path
    try{
      print("File Name : " + file.toString + " : ")
      val lines = Source.fromFile(file).getLines
      lines.filter( _.size != 0 ).foreach(s => p.write(s + "\n"))
      p.flush
    }
    catch{
      case ex : Throwable => {}
    }
    p.close
    println
  }

}