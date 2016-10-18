package SelfAEColMatrix

import SelfAEColMatrix.util._

import scala.collection.mutable.ArrayBuffer
import scala.io.Source

/**
 * Created by suanec on 2016/4/3.
 */
object fileHelper {
  /// MTrick字符类型转数字Sword
  def MTrickStr2Num( _line : String ) = {
    val arr = _line.split(',')
    (arr.head.toInt, arr(1).toInt, arr.last.toDouble)
  }

  /**
   * MTrick 读取数据及其信息
   * 输入MTrick字符型数组，内容为Sword，返回size和data，
   * size为行，列。
   * data中为行数据
   * @param _arr Array[String]
   * @return
   */
  def lines2line( _arr : Array[String] ) : ((Int, Int), Array[Array[SElem]]) = {
    val buf = new ArrayBuffer[SWord]()
    val res = new ArrayBuffer[Array[SWord]]()
    var preLineNum = 0;
    var maxCol = 0;
    _arr.map{
      str =>
        val item = MTrickStr2Num(str)
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
  def readMTrick( _path : String  = "D:\\ScalaSpace\\data\\Train.data") : ((Int, Int), Array[Array[SElem]]) = {
    val source = Source.fromFile(_path)
    val iter = source.getLines()
    val data = iter.toArray
    lines2line(data)
  }
}
