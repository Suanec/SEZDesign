package CDL.test

import breeze.linalg._
import breeze.numerics._
import util.ioHelper._
import util.mathHelper._
import util.randomHelper._
import CDL.util.fileHelper._
import CDL.util.MatrixUtil._
import CDL.PMF.PMF

/**
 * Created by suanec on 2016/5/11 : 14:05.
 */
object testCDL {
  def run(): Unit ={
    println("CDL.test.testCDL.")
    testFileHelper.run()
  }
}
object testFileHelper {
  import CDL.util.fileHelper._
  def run(): Unit ={
    println("CDL.test.testFileHelper.run")
    testPMF()

//    testBreezeMatrix()
  }
  def testRandomPerm(): Unit ={
    val arr = (0 to 100).toArray
    arr.foreach(x=> print( x + "," ))
    println
    (0 until 200).map{
      i =>
        val brr = randPerm[Int](arr.clone())
        println(i)
        brr.foreach(x=> print( x + "," ))
        println
        (0 until 100000).map( j => j.toDouble)
    }
  }
  def testBreezeMatrix0(){
    val matA = genStandardGaussianMatrix(4,4)
    val matB = genStandardGaussianMatrix(9,4)
    val matC = new DenseMatrix(4,4,matA.data)
    val matD = genStandardGaussianMatrix(1,2)
    println("matC-matA")
    println(matC-matA)
    val a = matA(2,::)
    val b = matB(5,::)
    println("a")
    println(a)
    println("b")
    println(b)
    println("a * b.t")
    println(a * b.t)
    println("sum( a :* b )")
    println(sum(a :* b))
    /// 对矩阵求二范数，将矩阵转化为向量调用norm函数求二范数。norm只能对Vector运算。
    val ra = CDL.util.MatrixUtil.regularFNorm(matD)
    /// Vector Frobenius Norm
    val anorm = norm(matD(0,::),2.0)
    println("ra-anorm")
    println(matD)
    println(matD(0,::))
    println(round(ra-anorm*anorm) )
    /// 逆矩阵，Moore-penrose Pseudoinverse广义逆矩阵pinv(a)
    val ainv = inv(matA)
    println("matA * ainv")
    println( (matA * ainv).map( round(_) ) )

    val svd.SVD(u,s,v) = svd(matB)
    println("u,s,v")
    println(u,s,v)
    println(pow(5,0.5) - powSelf(5,0.5))
    println(powSelf(0,1))
    println(powSelf(-10,1),powSelf(-10,2))
    println(pow(-5,3) - powSelf(-5,3) < 0.001)
//    val matC0 = matC * 0.5
    println(matA)

    println((matA*100d))

  }
  def testBreezeMatrix1(): Unit ={
    val l = new DenseVector(Array(2,3,1,0))
    val mat = new DenseMatrix[Double](4,1,Array(1d,2,3,4))
    println(mat)
    println(mat(1,::))
    println( sum(l :* l) - l.t * l )
    l.toDenseMatrix
  }
  def testBreezeGaussian(): Unit ={
    import breeze.stats.distributions.Gaussian
    val rand = new Gaussian(0,1)
    val arr = new Array[Double](1000).map( _ => rand.get() )
    val gw = new GeneralWriter[Double]()
    gw.writeArray(arr)
    val mat = CDL.util.MatrixUtil.genGaussianMatrix(10,7)
    val gwm = new GeneralWriter[Double]("mat.d","data\\CDL\\")
    gwm.writeMatrix(mat)
    val grm = new GeneralReader[Double]("mat.d","data\\CDL\\")
    val mat0 = grm.readMatrix()
    println(mat0 - mat)

  }
  def testReadU(): Unit ={
    val d = CDL.util.fileHelper.readU2R("u.data")
    println(d.size)
  }
  def testGenRmat(): Unit ={
//    import breeze.linalg._
//    val mat = DenseMatrix.rand[Double](3,4)
//    println(mat)
//    println()
//    mat(1,1) = 0d
//    println(mat)
    val mat = genRmat("u.data")
    println(mat.rows->mat.cols)
    println(mat(915,1681))
    val gsw = new GeneralWriter[Double]()
    gsw.writeArray(mat.data.filter(_ != 0))
  }
  def testMF(): Unit = {
    println("testMF")
    val R = new DenseMatrix(4,5,Array(
      5d,3,0,1,4,
      0,0,1,1,1,
      0,5,1,0,0,
      4,0,1,5,4)).t
    println(R)
    val N = R.rows
    val M = R.cols
    val K = 2
    val P = DenseMatrix.rand[tyDouble](N,K)
    val Q = DenseMatrix.rand[tyDouble](M,K)
    println(P);println
    var tem = P(0,0)
//    P(0,0) = 0d
    println(R(1,::))
    println(R(::,1))
    val t = R(1,::)
    println(t * t.t)


    val (nP,nQ) = CDL.PMF.MF.matrixFractorization(R,P,Q,K)
    println(nP);println()
    println(nQ.t);println
    println(R);println()
    println(nP * nQ.t);println()
    val resMat = nP * nQ.t - R
    (0 until R.rows).map{
      i =>
        (0 until R.cols).map{
          j =>
            if(R(i,j) == 0) resMat(i,j) = 0f
        }
    }
    println(resMat);println()
  }
  def testPMF(): Unit ={
    val data = readU2R()
//    val data = genRmat()
    val uNum = 943
    val vNum = 1682
    val lNum = 30
//    val (U,V) = PMF.probabilityMatrixFactorization(data,uNum,vNum,lNum)
    val uV = PMF.probabilityMatrixFactorization(data,uNum,vNum,lNum,20)
    val writer = new GeneralWriter[Double]("PMF.d")
    writer.writeMatrix(uV._1)
    writer.writeln("===========================================")
    writer.writeMatrix(uV._2)
    writer.writeln()
    val test = data(74931)
    println(test.rate + "," + uV._1(test.user_id,::) * uV._2(test.item_id,::).t )
    println(uV._1.rows,uV._2.cols)
  }
}
