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
  def openFile(file : File, codec : String = "") = {
    var res = Source.fromFile(file)
    if(codec.size != 0)
      res = Source.fromFile(file)(codec)
    res.getLines
  }
  def getContent( file : String , codec : String = "") : (String, String) = {
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
    (size.toString -> str )
  }
  def main(args : Array[String]): Unit ={
    var path = new File("")
    if(args.size == 0)path = new File("").getAbsoluteFile
    else path = new File(args.head)
    val files = subDirFiles(path)
    val set = Array("txt","sql","csv")
    val buffer = new ArrayBuffer[String]
    files.filter(_.isFile).foreach{
      file =>
        try{
          val fName = file.toString
          print("File Name : " + fName + " : ")
          if(set.indexOf(fName.split('.').last) != -1){
            val f = openFile(file,"utf-8")
            val fsize = f.size
            print("File Size : " + fsize + " : ")
            val f1 = getContent(file,"utf-8")._2
            println("File Example : " + f1)
          }
        }
        catch{
          case ex : Throwable => {
            val f = openFile(file)
            val fsize = f.size
            print("File Size : " + fsize + " : ")
            val f1 = getContent(file)._2
            println("File Example : " + f1)

          }
          // case ex : Throwable => buffer += (ex.toString + file.toString)

        }
        println
    }
    println("=======================Fail Files===========================")
    println("Fail Files Count : " + buffer.size)
    buffer.foreach{
      file =>
        println("Fail File Name : " + file)
    }

  }

}