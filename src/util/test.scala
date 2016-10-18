package util

/**
 * Created by suanec on 2016/6/17 : 10:57.
 */
object test {
  def run(): Unit ={
    testBDV2BDM()
  }
  def testBDV2BDM(): Unit ={
    import MatrixUtil._
    import ioHelper._
    val arr = (0 to 12).map(_.toDouble).toArray
    val v = new BDV(arr)
    val m = new BDM(4,3,arr)
    val m1 = BDV2BDM(v)
    println(m1)
    val m2 = BDV2BDM(v.t)
    println(m2)
  }

}
