package DCF.PMF

import util.ioHelper._
import scala.math._
import scala.util.control.Breaks

/**
 * Created by suanec on 2016/6/14 : 20:35.
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
