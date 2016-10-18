package CDL.PMF

import breeze.linalg.DenseMatrix
import breeze.linalg.sum
import scala.util.control.Breaks
import scala.math.pow
import CDL.util.MatrixUtil._
import CDL.util.fileHelper._

/**
 * Created by suanec on 2016/5/11 : 14:51.
 */
object MF {
  /**
   *     R     : a matrix to be factorized, dimension N x M
   *     P     : an initial matrix of dimension N x K
   *     Q     : an initial matrix of dimension M x K
   *     K     : the number of latent features
   *     steps : the maximum number of steps to perform the optimisation
   *     alpha : the learning rate
   *     beta  : the regularization parameter
   *
   */
  val maxEpoches = 5000
  val alpha = 0.002
  val beta = 0.02

  /**
   * 只计算R中非零元素。
   * @param _R
   * @param _P
   * @param _Q
   * @param _K
   * @return
   */
  def matrixFractorization( _R : BDM, _P : BDM, _Q : BDM, _K : Int ) : (BDM,BDM) = {
    val Q = _Q.t
    val brk = new Breaks
    import brk.{break,breakable}
    /// Gradient Descend for each element
    breakable {
      (0 until maxEpoches).map{
        step =>
          (0 until _R.rows).map{
            i =>
              (0 until _R.cols).map{
                j =>
                  if(_R(i,j)>0){
                    var eij = _R(i,j) - _P(i,::) * Q(::,j)
                    (0 until _K).map{
                      k =>
                        _P(i,k) += alpha * ( 2 * eij * Q(k,j) - beta * _P(i,k) )
                        Q(k,j) += alpha * ( 2 * eij * _P(i,k) - beta * Q(k,j) )
                    }
                  }
              }
          }
          /// calcLoss
          /// lossFunction :
          /// min(q*,p*) SIGMA(u,i belong to K) ( Rui - QiT * Pu )**2 + lamda( Qi ** 2 + Pu ** 2 )

                val eR = _P * Q
                var e = 0d
                (0 until _R.rows).map{
                  i =>
                    (0 until _R.cols).map{
                      j =>
                        if(_R(i,j) > 0){
                          e += pow( (_R(i,j) - _P(i,::) * Q(::,j)), 2)
                          (0 until _K).map{
                            k =>
                              e += (beta/2) * ( pow( _P(i,k) , 2 ) + pow( Q(k,j) , 2 ) )
                          }
                        }
                    }
                }
          println(step)
          if(e < 0.001) break()
      }
    }
    _P -> Q.t
  }
}
object PMF{
  val lr : Double = 0.3
  val lamda : Double = 0.01
  val moment : Double = 0.8
  val epoches : Int = 50
  val numbatches : Int = 9
  val uNum : Int = 0
  val vNum : Int = 0
  val lNum : Int = 0
  var uMu : Double = 0d
  var vMu : Double = 0d
  var uSigma : Double = 1d;
  var vSigma : Double = 1d;
  val rMu : Double = 0d
  val rSigma : Double = 1d

  def probabilityMatrixFactorization( _Rtuple : rData ,
                                      _userNum : Int = uNum,
                                      _itemNum : Int = vNum,
                                      _latentDimision : Int = lNum,
                                      _maxEpoches : Int = epoches ): (BDM,BDM) ={
    val userNum = _userNum + 1
    val itemNum = _itemNum + 1
    val uUser = genGaussianMatrix(userNum,_latentDimision,uMu,uSigma)
    val vItem = genGaussianMatrix(itemNum,_latentDimision,vMu,vSigma)
    val rateCount = _Rtuple.size
    val rMean = _Rtuple.map(_.rate).sum / rateCount
    val rMu = 0d
    val rSigma = 1d
    var preErr = 99999d
    var curErr = 9999d
    var epoch = 0
    while( preErr > curErr && epoch < _maxEpoches){
      var errSum = 0d
      val userDelta = DenseMatrix.zeros[Double](userNum, _latentDimision)
      val itemDelta = DenseMatrix.zeros[Double](itemNum, _latentDimision)
//      _Rtuple.foreach{
//        line =>
//          val uId = line.user_id -1
//          val vId = line.item_id -1
//          val realRating = line.rate - rMean
//          val precision = sum( uUser(uId,::) :* vItem(vId,::) )
//          val uRegular = sum(uUser(uId,::))
//          val vRegular = sum(vItem(vId,::))
//          val err = pow(precision - realRating, 2) + 0.5 * lamda * ( pow(uRegular, 2) + pow(vRegular, 2) )
//
//          errSum += err
//
//          val errOut = 2 * (precision - realRating)
//          (0 until _latentDimision).map{
//            k =>
//              val uGradient = errOut * vItem(vId,k) + lamda * uUser(uId,k)
//              val uDelta = ( uGradient * moment + uGradient * (lr / rateCount) ) * -1
//              val vGradient = errOut * uUser(uId,k) + lamda * vItem(vId,k)
//              val vDelta = ( vGradient * moment + vGradient * (lr / rateCount) ) * -1
//              uUser(uId,k) += uDelta
//              vItem(vId,k) += vDelta
//          }
//            println
////          if(uId % 213 == 0) println( uId + "," + vId + "," + line.rate)
//      }
      val brk = new Breaks
      import brk.{break,breakable}
      breakable{
        (1 until userNum).map{
          u =>
            val itemList = _Rtuple.filter( _.user_id == u )
            if( itemList.size == 0 ) break()
            itemList.map{
              vTuple =>
                val v = vTuple.item_id
                val realRating = vTuple.rate - rMean
                val prediction = uUser( u,:: ) * vItem( v, :: ).t
                val uFeatureSum = sum(uUser( u, :: ))
                val vFeatureSum = sum(vItem( v, :: ))
                val err = pow(prediction - realRating, 2) + 0.5 * lamda *
                  ( pow(uFeatureSum,2) + pow(vFeatureSum, 2) )
                errSum += err

                val errOut = 2 * (prediction - realRating)
                (0 until _latentDimision).map{
                  k =>
                    val uDelta = errOut * vItem(v,k) + lamda * uUser(u,k)
                    val vDelta = errOut * uUser(u,k) + lamda * vItem(v,k)

                    uUser(u,k) += uDelta
                    vItem(v,k) += vDelta
                }
            }
        }

      }
      epoch += 1
      preErr = curErr
      print("errSum :" + errSum  + ",")
      curErr = errSum / rateCount
      print("errSum :" + errSum  + ",")
      println(curErr)
    }
    (uUser -> vItem)
  }
//  def probabilityMatrixFactorization( _Rmat : BDM ,
//                                      _userNum : Int = uNum,
//                                      _itemNum : Int = vNum,
//                                      _latentDimision : Int = lNum,
//                                      _maxEpoches : Int = epoches): (BDM,BDM) ={
//    val uUser = genGaussianMatrix(_userNum,_latentDimision,uMu,uSigma) * 0.1
//    val vItem = genGaussianMatrix(_itemNum,_latentDimision,vMu,vSigma) * 0.1
//    val uUserInc = DenseMatrix.zeros[Double](_userNum,_latentDimision)
//    val vItemInc = DenseMatrix.zeros[Double](_itemNum,_latentDimision)
//    val rate = sum(_Rmat)
//    val rMean = rate/_Rmat.data.filter(_!=0).size
//    for( epoch <- 0 until epoches){
//
//
//    }
//
//
//
//
//
//    (uUser -> vItem)
//  }
  def pmfLoss( _u : BDM, _v : BDM, _r : BDM, _sigma0 : Double, _sigmaU : Double, _sigmaV : Double ) : Double = {
    val predOut : BDM = _u * _v.t
    val errOut : BDM = _r - predOut
    (0 until _r.rows).foreach{
      i =>
        (0 until _r.cols).foreach{
          j =>
            if( _r(i,j) == 0 ) errOut(i,j) = 0
        }
    }
    val uFNorm = regularFNorm(_u)
    val vFNorm = regularFNorm(_v)
    val Loss = .5 * (
      sum(errOut :* errOut) +
        ((_sigma0*_sigma0)/(_sigmaU*_sigmaU)) * uFNorm +
        ((_sigma0*_sigma0)/(_sigmaV*_sigmaV)) * vFNorm
      )
    Loss
  }
  def pmfSingleLoss( _u : BDM, _v : BDM, _r : Udata, _sigma0 : Double, _sigmaU : Double, _sigmaV : Double ) : Double = {
//    val predOut : Double = (_u * _v.t)(0)
//    val errOut = _r.rate - predOut
    val uFNorm = regularFNorm(_u)
    val vFNorm = regularFNorm(_v)
//    val Loss = 0.5 * (
//      errOut * errOut +
//        ((_sigma0*_sigma0)/(_sigmaU*_sigmaU)) * uFNorm +
//        ((_sigma0*_sigma0)/(_sigmaV*_sigmaV)) * vFNorm
//      )
//    Loss
    uFNorm
  }
  //// rData
  def pmfLikelihood( _R : rData, _u : BDM, _v : BDM ) : Double = {
    val user = _u
    val item = _v
    var sqErr = 0d
    _R.map{
      line =>
        val i = line.user_id
        val j = line.item_id
        val r = line.rate
        val err = r - sum(_u(i,::) :* _v(j,::))
        sqErr += err * err
    }
    val L2 = regularFNorm(_u) + regularFNorm(_v)
    -sqErr - lamda * L2
  }
  def calcUser2ItemRate( _i : Int, _j : Int, _u : BDM, _v : BDM ) : Double = sum(_u(_i,::) :* _v(_j,::))
  def update(): Unit ={
    val updates_u = DenseMatrix.zeros[Double](uNum,lNum)
  }
}
