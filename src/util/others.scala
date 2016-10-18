package util

/**
 * Created by suanec on 2016/5/20 : 11:09.
 */
object others {
  def classpath = "D:\\ScalaSpace\\SEZDesign\\lib\\VbreezeLib-2.10.0-0.12.jar"

  def command = "scala -cp  D:\\ScalaSpace\\SEZDesign\\lib\\VbreezeLib-2.10.0-0.12.jar"
  /// 测试矩阵乘操作时间。结果直接矩阵相乘效率最高
  def testMulTime() = {
    val line = new breeze.linalg.DenseVector(Array(2d,3,1,4,5))
    val linearMulStart = System.nanoTime()
    (0 until 1000000).map{
      i =>
      breeze.linalg.sum(line :* line)
    }
    val linearMulEnd = System.nanoTime()
    val matMulStart = System.nanoTime()
    (0 until 1000000).map{
      i =>
        line.t * line
    }
    val matMulEnd = System.nanoTime()
    val powStart = System.nanoTime()
    (0 until 1000000).map{
      i =>
        breeze.linalg.sum(breeze.numerics.pow(line,2))
    }
    val powEnd = System.nanoTime()
    ((matMulEnd - matMulStart)/1e6->(linearMulEnd - linearMulStart)/1e6->(powEnd - powStart)/1e6)
  }

}
/// 扑克花色枚举，枚举使用Demo
object pokerColor extends Enumeration{
  // 梅花♣（club，又名草花）
  val club = Value(5.toChar.toString )
  // 方块♦（diamond，又名阶砖或方片，川渝地区称为"巴片"）
  val diamond = Value(4.toChar.toString )
  /// 红心♥（heart，又名红桃）
  val heart = Value(3.toChar.toString )
  /// 黑桃♠（spade，又名葵扇）
  val spade = Value(6.toChar.toString )
//  val a = pokerColor.club
}
