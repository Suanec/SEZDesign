package util

import scala.reflect.ClassTag
import scala.{specialized=>spec}
import breeze.linalg.{DenseVector, DenseMatrix}

/**
 * Created by suanec on 2016/5/16 : 21:02.
 */
object randomHelper {

  type BDM = DenseMatrix[Double]
  /// 随机洗牌
  def randPerm[@spec(Int, Double, Long, Float) T]( _arr : Array[T] ): Array[T] ={
    var idx : Int = 0
    var tmp : T = _arr.head
    val size = _arr.size
    val rand = new scala.util.Random(System.currentTimeMillis())
    _arr.indices.map{
      i =>
        idx = scala.math.abs(rand.nextInt % (size-i)) + i
        require(idx <= size && idx >= 0,idx.toString + "," + i.toString + "," + size.toString)
        tmp =  _arr(idx)
        _arr(idx) = _arr(i)
        _arr(i) = tmp
    }
    _arr
  }
  def randMatrix( _mat : BDM, _arr : Array[Int] ): Unit ={
//    var res = _mat(_arr.head,::)
  }
}
