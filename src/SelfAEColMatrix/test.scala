package SelfAEColMatrix

//package SelfAE
//import scala.io.Source


/**
 * Created by suanec on 2016/4/3.
 */
object test {
//  def main(args : Array[String] ) = {
//    println("hello!")
////    val path = "D:\\ScalaSpace\\data\\Train.data"
////    val source = Source.fromFile(path)
////    val a = source.getLines()
////    val a = fileHelper.readMTrick(path)
////    val a1 = a
////    val mat = new Matrix(2,3)
////    val data = new Array[Array[Double]](2)
////    data(0) = Array(0.5, 1d, 1.5)
////    data(1) = Array(1d, 2d, 1d )
////    val l = Array(0d,1d,2d)
////    val mat = new Matrix(data)
////    val size = mat.getCols() -> mat.getRows()
////    val res = mat.MulRow(l)
////    res.foreach(println)
////    val tem = (-100 to 100).toArray.map( i => i/10d ).map( i => sigmoid(i) )
//
/////===============================================================================
////    val data = new Array[SVector](8).map( _ =>  new SVector(8) )
////    (0 until data.size).map{
////      i =>
////        data(i)(i) = 1
////    }
////    val rand = new Random(System.currentTimeMillis())
////    val W1 = new Matrix(3,8,System.currentTimeMillis())
////    val W2 = new Matrix(8,3,System.currentTimeMillis())
////    val b1 = new SVector(3).map( x => rand.nextDouble() )
////    val b2 = new SVector(8).map( x => rand.nextDouble() )
////    (0 until 8000 ).map{
////      adsf =>
////        data.map{
////          line =>
//////            val line = data.head
////            //        line.foreach(println)
////            //        println
////            val features = encode(W1,b1,line)
//////            features.foreach(println)
//////            println
////            val outPut = decode(W2, b2, features)
//////            outPut.foreach(println)
//////            println
////            val errOut = calcErrOut(line, outPut)
////            //        println
////            //        errOut.foreach(println)
////            //        println
////            val errHidden = calcErrHidden( errOut, W2, features )
////            //        errHidden.foreach(println)
////            //        println
////            updateW(line, features, errHidden, errOut, W1, W2, b1, b2)
////        }
////      }
////    data.map{
////      line =>
////        //            val line = data.head
////                line.foreach(println)
////                println
////        val features = encode(W1,b1,line)
////                    features.foreach(println)
////                    println
////        val outPut = decode(W2, b2, features)
////                    outPut.foreach(println)
////                    println
////        val errOut = calcErrOut(line, outPut)
////                println
////                errOut.foreach(println)
////                println
////        val errHidden = calcErrHidden( errOut, W2, features )
////                errHidden.foreach(println)
////                println
////        updateW(line, features, errHidden, errOut, W1, W2, b1, b2)
////    }
/////===============================================================================
//    //    data.foreach( x => x.foreach(println) )
//
////    val writer = new PrintWriter(new File(path))
//
////    println(a)
//val a = fileHelper.readMTrick()
//    val line = a._2.head
//    val data = new SVector(line.last._1+1)
//    line.map{
//      x =>
//        data(x._1) = x._2
//    }
//    val W1 = new Matrix(100,6616,System.currentTimeMillis)
//    val W2= new Matrix(6616,100,System.currentTimeMillis)
//    import scala.util.Random
//    val rand = new Random(System.currentTimeMillis)
//    val b1 = new SVector(100).map( _ => rand.nextDouble)
//    val b2 = new SVector(6616).map( _ => rand.nextDouble)
//
//    import SelfAutoEncoder._
//    (0 until 500 ).map{
//      adsf =>
//        val features = encode(W1,b1,data)
//        //            features.foreach(println)
//        //            println
//        val outPut = decode(W2, b2, features)
//        //            outPut.foreach(println)
//        //            println
//        val errOut = calcErrOut(data, outPut)
//        //        println
//        //        errOut.foreach(println)
//        //        println
//        val errHidden = calcErrHidden( errOut, W2, features )
//        //        errHidden.foreach(println)
//        //        println
//        updateW(data, features, errHidden, errOut, W1, W2, b1, b2)
//    }
//  }
//
//  def test(args : Array[String]) = SelfAERowMatrix.test.test(args)
}
