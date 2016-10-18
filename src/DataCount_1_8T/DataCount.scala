package DataCount_1_8T


/// scala DataCount >> result
/**
 * Created by suanec on 2016/6/20 : 17:30.
 */
object DataCount {
  import io.Source
  import java.io.File
  import collection.mutable.ArrayBuffer
  import scala.util.Random
  /// 返回当前路径下所有文件
  def subDirFiles(_dir: File): Iterator[File] = {
    val d = _dir.listFiles.filter(_.isDirectory)
    val f = _dir.listFiles.filter(_.isFile).toIterator
    f ++ d.toIterator.flatMap(subDirFiles _)
  }
  def subDirFiles(_path : String) : Iterator[File] = {
    val dir = new File(_path)
    subDirFiles(dir)
  }
  def main(args : Array[String]): Unit ={
    var path = new File("")
    if(args.size == 0)path = new File("").getAbsoluteFile
    else path = new File(args.head)
    val files = subDirFiles(path)
    val buffer = new ArrayBuffer[String]
    files.filter(_.isFile).foreach{
      file =>
        try{
          print("File Name : " + file.toString + " : ")
          val f = Source.fromFile(file).getLines
          val fsize = f.size
          print("File Size : " + fsize + " : ")
          val rand = new Random(System.currentTimeMillis())
          val f1 = Source.fromFile(file).getLines.take(math.abs(rand.nextInt % 47 + 10)).toArray.last.toString
          println("File Example : " + f1 + " : ")
        }
        catch{
          case ex : Throwable => buffer += file.toString
        }
    }
    println("=======================Fail Files===========================")
    println("Fail Files Count : " + buffer.size)
    buffer.foreach{
      file =>
        println("Fail File Name : " + file)
    }

  }

}