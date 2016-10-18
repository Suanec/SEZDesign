package SelfAERowMatrix

import util._

import scala.collection.mutable.ArrayBuffer
import scala.io.Source

/**
 * Created by suanec on 2016/4/10 : 15:45.
 */
object fileHelper {
  /// MTrick字符类型转数字SElem
  def MTrickStr2ElemNum( _line : String ) : SElem = {
    val arr = _line.split(',')
    (arr.head.toInt, arr(1).toInt, arr.last.toDouble)
  }

  /// MTrick字符类型转数字Sword
  def MTrickStr2Num( _line : String ) : SWord = {
    val arr = _line.split(',')
    (arr.head.toInt, arr.last.toDouble)
  }

  def softmax( _line : Array[Double] ) : Array[Double] = {
    import scala.math.exp
    var sum = 0d;
    _line.foreach( x => sum += exp(x) )
    _line.map{
      x =>
        exp(x)/sum
    }
  }

  def softmax( _doc : SDoc ) : SDoc = {
    import scala.math.exp
    var sum = 0d;
    var isTooLarge = false
    _doc.foreach( word => if(word._2 < 709) sum += exp(word._2) else isTooLarge = true )
    var res : SDoc = null
    if(!isTooLarge){
      res = _doc.map{
        word =>
          word._1 -> exp(word._2)/sum
      }
    }else res = linear(_doc)
    res
  }

  def linear( _doc : SDoc ) : SDoc = {
    var sum = 0d;
    _doc.foreach( word => sum += word._2 )
    val res = _doc.map{
      word =>
        word._1 -> word._2/sum
    }
    res
  }

  /**
   * MTrick 读取数据及其信息
   * 输入MTrick字符型数组，内容为Sword，返回size和data，
   * size为行，列。
   * data中为行数据
   * @param _arr Array[String]
   * @return
   */
  def lines2line( _arr : Array[String] ) : ((Int, Int), Array[Array[SWord]]) = {
    val buf = new ArrayBuffer[SElem]()
    val res = new ArrayBuffer[Array[SElem]]()
    var preLineNum = 0;
    var maxCol = 0;
    _arr.map{
      str =>
        val item = MTrickStr2ElemNum(str)
        if(maxCol<item._1) maxCol = item._1
        if( item._2 == preLineNum ) buf += item
        else {
          if(buf.size != 0) res += buf.toArray
          buf.clear()
          buf += item
          preLineNum = item._2
        }
    }
    ((res.size, maxCol), res.map(arr => arr.map( x => (x._1,x._3) ) ).toArray)
  }

  /**
   * 根据文件路径读取MTrick类型文件，并生成数据稀疏矩阵
   * @param _path : String
   * @return (size,data)
   */
  def readMTrick( _path : String  = "D:\\ScalaSpace\\data\\Train.data") : ((Int, Int), Array[Array[SWord]]) = {
    val source = Source.fromFile(_path)
    val iter = source.getLines()
    val data = iter.toArray
    lines2line(data)
  }

  def readMIter( _path : String  = "D:\\ScalaSpace\\data\\Train.data") : Iterator[String] = Source.fromFile(_path).getLines()

//  def MTrickIter2line( _iter : Iterator[String] ) : Array[Array[SWord]] = {}

  def readSize( _path : String = "D:\\ScalaSpace\\data\\Train.data" ) : (Int, Int) = {
    val source = Source.fromFile(_path)
    val iter = source.getLines()
    var row = 0;
    var col = 0;
    iter.foreach{
      x =>
        val arr = MTrickStr2ElemNum(x)
        if(arr._1 > col) col = arr._1
        if(arr._2 > row) row = arr._2
    }
    (row, col)
  }

  def crePressureData( _fname : String = "pt.stest",_rate : Double = 0.01, _size : (Int, Int) = (1977,7270), _path : String = "D:\\ScalaSpace\\data\\" ) = {
    import java.io.PrintWriter
    import java.io.File
    import scala.util.Random
    val writer = new PrintWriter( new File(_path + _fname) )
    val rand = new Random(System.currentTimeMillis())
    (1 to _size._1).map{
      row =>
        (1 to _size._2).map{
          col =>
            if(rand.nextDouble() < _rate) writer.write(col + "," + row + "," + rand.nextFloat + "\n" )
        }
//        println( (_size._1 - row) )
        writer.flush
    }
    writer.flush()
    writer.close()
  }

  def saveModel( _fname : String = "Model.result", _w : Matrix, _b : SVector, _path : String = "D:\\ScalaSpace\\data\\" ) = {
    import java.io.PrintWriter
    import java.io.File
    import scala.util.Random
    val writer = new PrintWriter( new File(_path + _fname) )
    println("model writing...")
    writer.write( " Size : " + _w.getRows() + " : " + _w.getCols() + "\n")
    (1 to _w.getRows()).map{
      i =>
        _w.getRow(i).map( x => writer.write(x + " : "))
        writer.write("\n")
    }
    writer.write( "b Size : " + _b.size + "\n")
    _b.foreach( x => writer.write( x + " : "))
    writer.flush()
    writer.close()
    println("model save succeed!")
  }

  def loadModel( _fname : String = "Model.result", _path : String = "D:\\ScalaSpace\\data\\" ) : (Matrix, SVector) = {
    import scala.io.Source
    import java.io.File
    val source = Source.fromFile(new File(_path + _fname))
    println("model reading...")
    val iter = source.getLines()
    val str = iter.next.split(" : ")
    val row = str(1).toInt
    val col = str(2).toInt
    val mat = new Matrix(row,col)
    (1 to row).map{
      i =>
        val line = iter.next.split(" : ").map(_.toDouble)
        mat.setRow( i, line )
    }
    val bSize = iter.next.split(" : ").last.toInt
    val b = iter.next.split(" : ").map(_.toDouble)
    if( bSize != b.size ) {
      println("Load Model error!")
      return null
    }
    mat -> b
  }
}
