package NaiveBayesianClassifier
import scala.io.Source
/**
 * Created by suanec on 2016/4/25 : 10:32.
 */
object FileHelper {
  type labeledPoint = (Int, Array[Double])
  type labeledPoints = Array[labeledPoint]
  type dataInfo = ( Int, Array[Int], Int )/// 类标数，类标数组，特征数。
  type labelInfo = (Double, Array[Double])
  type labelsInfo = Array[labelInfo]
  val _path : String = "D:\\ScalaSpace\\data\\"
  def read20News( _file : String = "comatrain.data",_file1 : String = "20newsgroups\\20news-bydate\\") ={
    val data = SelfAERowMatrix.fileHelper.readMTrick(_path+_file1+_file)
    data
  }
  def convertSpace2Commas( _file : String = "train.data", _file1 : String = "20newsgroups\\20news-bydate\\"): Unit ={
    import scala.io.Source
    import java.io.PrintWriter
    import java.io.File
    val data = Source.fromFile(_path+_file1+_file)
    val iter = data.getLines()
    val w = new PrintWriter(new File(_path+_file1+ "coma" + _file))
    iter.foreach{
      line =>
        val splits = line.split(" ")
        w.write(splits(1)+","+splits(0)+","+splits(2)+"\n")
    }
    w.flush()
    w.close()
  }
  def readBayesian( _file : String = "mllib\\sample_naive_bayes_data.txt"): labeledPoints ={
    val data = Source.fromFile(_path + _file).getLines().map{
      row =>
        val splits = row.split(",")
        val label = splits.head.toInt
        val features = splits.last.split(" ").map( s => s.toDouble )
        (label,features)
    }.toArray
    data
  }
  def dataInfo( _data : labeledPoints ) : dataInfo = {
    val classes = _data.map( _._1 ).distinct
    val classN = classes.size
    val featuresN = _data.map( _._2.size ).max
    (classN, classes, featuresN)
  }


}
