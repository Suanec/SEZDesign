package SelfAERowMatrix
//import SelfAERowMatrix.Matrix
/**
 * Created by suanec on 2016/4/10 : 19:16.
 */
object test {
  def testLine/**/(args : Array[String]) = {
    import util._
    import java.io.{PrintWriter,File}
    import MLMLP._
    val path : String  = "D:\\ScalaSpace\\data\\"
    val fileName : String = "Loss.result"

    val data = new Array[Array[Double]](8).map( line => new Array[Double](8) )
    (0 until data.size).map( i => data(i)(i) = 1d )
    val W1 = new Matrix(3,8,System.currentTimeMillis())
    val W2 = new Matrix(8,3,System.currentTimeMillis())
    val b1 = SVectorRandom(new SVector(3), System.currentTimeMillis())
    val b2 = SVectorRandom(new SVector(8), System.currentTimeMillis())
    val writer = new PrintWriter(new File(path+fileName))
    val resultFile = "decode.result"
    val resWriter = new PrintWriter(new File(path + resultFile))
    (0 until data.size).map {
      i =>
        val line = data(i)
        val enc = encode(W1, b1, line)
        val dec = decode(W2, b2, enc)
        line.map( i => resWriter.write(i + " : ") )
        resWriter.write("\n")
        resWriter.write("\n")

        enc.map( i => resWriter.write(i + " : ") )
        resWriter.write("\n")
        resWriter.write("\n")
        dec.map( i => resWriter.write(i + " : ") )
        resWriter.write("\n")
        resWriter.write("\n")
        resWriter.write("\n")
        resWriter.write("\n")
        resWriter.write("\n")
    }
    resWriter.flush()
    (0 to 3000).map{
      i =>
        data.map{
          line =>
//          j =>
//            val line = data.head
            val loss = BackPropagation(W1, W2, b1, b2, line)
            writer.write("Loss : " + loss + "\n")
        }
    }
    writer.flush()
    (0 until data.size).map {
      i =>
        val line = data(i)
        val enc = encode(W1, b1, line)
        val dec = decode(W2, b2, enc)
        line.map( i => resWriter.write(i + " : ") )
        resWriter.write("\n")
        resWriter.write("\n")

        enc.map( i => resWriter.write(i + " : ") )
        resWriter.write("\n")
        resWriter.write("\n")
        dec.map( i => resWriter.write(i + " : ") )
        resWriter.write("\n")
        resWriter.write("\n")
        resWriter.write("\n")
        resWriter.write("\n")
        resWriter.write("\n")
    }
    resWriter.flush()
    writer.close()
    resWriter.close()

  }
  def testDoc(args : Array[String]) = {//DocAndSave
    import util._
    import java.io.{PrintWriter, File}
    import MLMLP._
    import fileHelper._
    val path: String = "D:\\ScalaSpace\\data\\"
    val lossFile: String = "Loss.result"
    val lossWriter = new PrintWriter(new File(path + lossFile))
    val modelFile = "Model.result"
    val modelWriter = new PrintWriter(new File(path + modelFile))
    val resultFile = "decode.result"
    val resWriter = new PrintWriter(new File(path + resultFile))

    val a = readMTrick()
    val size = a._1
    val row = size._1
    val col = size._2
    val data = a._2.map { x => softmax(x) }

    //    val doc = data.head
    //    val line = new SVector(col)
    //    SDoc2SVector(doc,line)
    val W1 = new Matrix(100, col, System.currentTimeMillis())
    val W2 = new Matrix(col, 100, System.currentTimeMillis())
    val b1 = SVectorRandom(new SVector(100), System.currentTimeMillis())
    val b2 = SVectorRandom(new SVector(col), System.currentTimeMillis())
    (0 until 100).map {
      i =>
        data.map {
          doc =>
            val loss = BackPropagation(W1, W2, b1, b2, doc)
            println("Loss : " + loss)
            lossWriter.write("Loss : " + loss + "\n")
        }
    }
//    (1 to W1.getRows()).map{
//      i =>
//        val line = W1.getRow(i)
//        line.map( x => modelWriter.write(x + ",") )
//        modelWriter.write("\n")
//    }
    saveModel(modelFile,W1,b1,path)
    resWriter.write("=================================================================\n")
    data.map{
      doc =>
        val (hOut, lOut) = FeedForward(W1, W2, b1, b2, doc)
        doc.map(x => resWriter.write(x._1 + "," + x._2 + "\n") )
        resWriter.write("\n\n")
        lOut.map(x => resWriter.write(x._1 + "," + x._2 + "\n"))
        resWriter.write("=================================================================\n")
    }
    lossWriter.flush()
    modelWriter.flush()
    resWriter.flush()
    lossWriter.close()
    modelWriter.close()
    resWriter.close()

  }
  def testRead( args : Array[String] ) = {
    val (m,b) = fileHelper.loadModel()
    m.size()
    b.size
    m.getRow(100).foreach(println)
  }

}
