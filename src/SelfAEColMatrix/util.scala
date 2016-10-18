package SelfAEColMatrix

import scala.math.exp
/**
 * Created by suanec on 2016/4/4 : 20:05.
 */
object util {
  /// MTrick 单个元素类型。
  type SWord = (Int, Int, Double)
  type SElem = (Int, Double)
  type SDoc = Array[SElem]
  type SVector = Array[Double]

  def sigmoid( z : Double ) : Double = 1d / ( 1 + exp(-z) )

  def tanh( z : Double ) : Double = {
    val z1 = exp(-z)
    val z2 = exp(z)
    (z2 - z1)/(z2 + z1)
  }

  def Dif_sigmoid( z : Double ) : Double = sigmoid(z) / ( 1 - sigmoid(z) )

  def Dif_tanh( z : Double ) : Double = 1 - tanh(z) * tanh(z)

  def SVectorPlus ( _a : SVector, _b : SVector ) : SVector = {
    require(_a.size == _b.size)
    (0 until _a.size).map{
      i =>
        _a(i) + _b(i)
    }.toArray
  }

  def SVectorSub( _a : SVector, _b : SVector ) : SVector = {
    require(_a.size == _b.size)
    (0 until _a.size).map{
      i =>
        _a(i) - _b(i)
    }.toArray
  }

  def SVectorMul( _a : SVector, _b : SVector ) : Double = {
    require(_a.size == _b.size)
    var sum = 0d
    (0 until _a.size).map{
      i =>
        sum += _a(i) * _b(i)
    }
    sum
  }

  def SVectorDotMul( _a : SVector, _b : SVector ) : SVector = {
    require(_a.size == _b.size)
    (0 until _a.size).map{
      i =>
        _a(i) * _b(i)
    }.toArray
  }

  def SDocSub( _a : SDoc, _b : SDoc ) : SDoc = {
    require(_a.size == _b.size)
    (0 until _a.size).map{
      i =>
        _a(i)._1 -> (_a(i)._2 - _b(i)._2)
    }.toArray
  }

  def SDocDotMul( _a : SDoc, _b : SDoc ) : SDoc = {
    require(_a.size == _b.size)
    (0 until _a.size).map{
      i =>
        _a(i)._1 -> _a(i)._2 * _b(i)._2
    }.toArray
  }

  def SDoc2SVector( _doc : SDoc, _line : SVector ) = {
    require( _doc.last._1 < _line.size+1 )
    _doc.map( x => _line(x._1) = x._2 )
  }

}
