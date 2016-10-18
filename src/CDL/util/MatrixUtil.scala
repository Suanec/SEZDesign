package CDL.util

import breeze.stats.distributions.Gaussian

/**
 * Created by suanec on 2016/5/14 : 18:15.
 */
object MatrixUtil {
  import fileHelper._
  import breeze.stats.distributions.Gaussian
  import breeze.linalg.DenseMatrix
  /// 生成高斯随机数生成器
  def genGaussianRand( _mu : Double = 0,
                       _sigma : Double = 1
                       ) : Gaussian = new Gaussian(_mu,_sigma)
  /// 根据rand生成高斯矩阵
  def genMatrixWithGaussianRand( _rowNum : Int = 0,
                                  _colNum : Int = 0,
                                  _rand : Gaussian = new Gaussian(0,1)
                                 ) : BDM = DenseMatrix.tabulate[Double](_rowNum,_colNum)( (_,_) => _rand.get )
  /// 直接根据mu和sigma生成高斯矩阵，默认标准正态分布
  def genGaussianMatrix( _rowNum : Int = 0,
                         _colNum : Int = 0,
                         _mu : Double = 0,
                         _sigma : Double = 1d
                         ) : BDM = genMatrixWithGaussianRand(_rowNum,_colNum,genGaussianRand(_mu,_sigma))
  /// 直接生成标准高斯矩阵
  def genStandardGaussianMatrix( _rowNum : Int = 0,
                               _colNum : Int = 0
                               ) : BDM = genGaussianMatrix(_rowNum,_colNum,0d,1d)

  /// 对矩阵计算F范数
  def regularFNorm( _m : BDM ): Double = breeze.linalg.sum(_m :* _m)
  /// 对行向量计算F范数
  def regularFNorm( _v : breeze.linalg.Transpose[BDV] ): Double = _v * _v.t
  /// 对列向量计算F范数
  def regularFNorm( _v : BDV ): Double = _v.t * _v

}
