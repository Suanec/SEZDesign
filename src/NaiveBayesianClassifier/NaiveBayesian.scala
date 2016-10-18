package NaiveBayesianClassifier

import NaiveBayesianClassifier.FileHelper._

/**
 * Created by suanec on 2016/4/25 : 11:17.
 */
object NaiveBayesian {
  def getAllProbability( _data : labeledPoints, _info : dataInfo ) : labelsInfo = {
    val classM = _info._2.max
    val fSize = _info._3
    val res0 = new Array[labeledPoint](classM+1) /// 直接映射哈希表，位置为类标，_1为类标在数据集中的计数，_2为各特征在当前类下计数。
    _data.map{
      i =>
        val c = i._1
        val f = i._2
        if( res0(c) == null ) res0(c) = ( 0 , new Array[Double](fSize) )
        val arr = res0(c)._2.clone()
        (0 until f.size).map {
          j =>
            if (f(j) != 0) arr(j) += 1
        }
        res0(c) = ( res0(c)._1 + 1, arr )
    }
    val res = res0.map{
      r =>
        val c = r._1
        val arr = r._2
        val f = arr.map{
          i =>
            i / c
        }
        (c.toDouble/_data.size,f)
    }
    res
  }
  def getYProb( _data : labeledPoints, _y : Int, _featureSize : Int ) : labelInfo = {
    val fSize = _featureSize
    var res0 = new labeledPoint(0,new Array[Double](fSize))
    _data.foreach{
      r =>
        if( r._1 == _y ){
          val arr = res0._2
          arr.indices.map{
            i =>
              if( r._2(i) != 0 ) {
                arr(i) += 1
              }
          }
          res0 = ( res0._1 + 1 , arr )
        }
    }
    val res = ( res0._1.toDouble / _data.size, res0._2.map( _ / res0._1 ) )
    res
  }
  def calcY( _x : labeledPoint, _prob : labelsInfo ) : Int = {
    val labelProb = new Array[Double](_prob.size)
    _prob.indices.map{
      i =>
        val li = _prob(i)
        var t = 1d;
        val py = li._1
        li._2.indices.map{
          j =>
            if( _x._2(j) != 0 ) t *= li._2(i)
        }
        labelProb(i) = t * py
    }
    labelProb.indexOf(labelProb.max)
  }

}
