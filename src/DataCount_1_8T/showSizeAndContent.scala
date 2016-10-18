package showSizeAndContent


/// scala DataCount >> result
/**
 * Created by suanec on 2016/6/20 : 17:30.
 */
object showSizeAndContent {
  import io.Source
  import java.io.{File,PrintWriter}
  import scala.util.Random
  def showContent( file : String , codec : String = ""){
    println("File Name : " + file.toString + " : ")
    var size = 0d
    var str = ""
    if(codec.size == 0){
      size = Source.fromFile(file).getLines.size
      val rand = new Random(System.currentTimeMillis())
      val step = math.abs(rand.nextInt % size + size/10)
      val iter = Source.fromFile(file).getLines
      iter.drop(step)
      str = iter.next.toString
      while( str.size < 5 && iter.hasNext ) str = iter.next.toString
    }else{
      size = Source.fromFile(file)(codec).getLines.size
      val rand = new Random(System.currentTimeMillis())
      val step = math.abs(rand.nextInt % size + size/10)
      val iter = Source.fromFile(file)(codec).getLines
      iter.drop(step)
      str = iter.next.toString
      while( str.size < 5 && iter.hasNext ) str = iter.next.toString
    }
    println("Size : " + size.toString + "\n" + "Lines : " + str )
  }
  def main(args : Array[String]): Unit ={
    var file = args.head
    if(args.size == 2) file = args.head + "\\" + args.last
    try{
      showContent(file, "utf-8")
    }
    catch{
      case ex : Throwable => {
        println("utf-8 : " + ex.toString)
        showContent(file)
      }
    }
  }

}