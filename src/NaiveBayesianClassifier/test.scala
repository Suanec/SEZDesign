package NaiveBayesianClassifier

/**
 * Created by suanec on 2016/4/25 : 10:44.
 */
object test {
  def testNBC/*FileHelper*/(args : Array[String]) : Unit = {
    import FileHelper._
    val data = readBayesian()
    data.map{
      i =>
        print( i._1 + "\t" )
        i._2.map( j => print( j + "\t") )
        println()
    }
    val a = dataInfo( data )
    println( a._1, a._2 )
    val prob = NaiveBayesian.getAllProbability(data,a)
    val x = data.head
    val y = NaiveBayesian.calcY(x,prob)
    println( y == x._1 )
  }
  def testFile20News() = {
//    FileHelper.convertSpace2Commas()
    val d = NaiveBayesianClassifier.FileHelper.read20News()
    println(d._1 -> d._2)
  }


}
