package util

/**
 * Created by suanec on 2016/6/17 : 10:57.
 */
object MNISTSELF {
  def run(): Unit ={
    testBDV2BDM()
  }
  
  def parseInt(_fis : java.io.FileInputStream) : Int = {
    var rst = 0
    (0 until 4).map{
      i =>
        rst <<= 8
        rst |= _fis.read 
    }
    rst
  }

  def parsePic( _fis : java.io.FileInputStream, _size : Int) : Array[Short] = {
    Array.fill[Short](_size)(0).map( c => _fis.read.toShort )
  }

  def showPicArr(_arr : Array[Short], _rows : Int) : Unit = {
    var rstStr = ""
    var rest = _arr
    (0 until _rows).map{
      row =>
        val (arrHead,arrTail) = rest.splitAt(_rows)
        rest = arrTail
        rstStr += arrHead.mkString(" ")
        rstStr += "\n"
    }
    println(rstStr)
  }

  def readMNIST( _file : String ) : Array[Array[Short]] = {
    val fis = new java.io.FileInputStream(_file)
    val magicInt = parseInt(fis)
    val numElems = parseInt(fis)
    val rows = parseInt(fis) 
    val cols = parseInt(fis)
    val rstBuffer = new scala.collection.mutable.ArrayBuffer[Array[Short]]
    (0 until numElems).map( i => rstBuffer += parsePic(fis, rows*cols) )
    println(fis.read)
    rstBuffer.toArray
  }

  def using[A <: { def close(): Unit }, B](resource: A)(f: A => B): B =  
    try {  
      f(resource)  
    } finally {  
      resource.close()  
    } 

}

// import java.io.File
// import java.io.FileInputStream
// import java.nio.ByteBuffer

// import scala.collection.mutable.ArrayBuffer

// import org.apache.spark.mllib.linalg.Vectors
// import org.apache.spark.mllib.linalg.Vector

/**
 * http://yann.lecun.com/exdb/mnist/
 *
 */
class MNISTData(val dataDir: String, val numCount: Int = 2) {
  val trainLabeFileName = "/train-labels-idx1-ubyte/train-labels.idx1-ubyte"
  val trainImageFileName = "/train-images-idx3-ubyte/train-images.idx3-ubyte"
  val testLabeFileName = "/t10k-labels-idx1-ubyte/t10k-labels.idx1-ubyte"
  val testImageFileName = "/t10k-images-idx3-ubyte/t10k-images.idx3-ubyte"

  /**
   * 安全打开文件流方法
   */
  def using[A <: { def close(): Unit }, B](resource: A)(f: A => B): 
  B = {
    try {
      f(resource)
    } finally {
      resource.close()
    }
  }

  def loadTrainLabelData(): Array[Byte] = {
    loadLabelData(trainLabeFileName)
  }

  def loadTestLabelData(): Array[Byte] = {
    loadLabelData(testLabeFileName)
  }

  /**
   * 加载MNIST train label数据
   */
  def loadLabelData(filename: String): Array[Byte] = {
    val file = new File(dataDir + filename)
    val in = new FileInputStream(file)
    val labelDS = new Array[Byte](file.length.toInt)
    using(new FileInputStream(file)) { source =>
      {
        in.read(labelDS)
      }
    }

    //32 bit integer  0x00000801(2049) magic number (MSB first--high endian) 
    val magicLabelNum = ByteBuffer.wrap(labelDS.take(4)).getInt
    println(s"magicLabelNum=$magicLabelNum")
    //32 bit integer  60000            number of items 
    val numOfLabelItems = ByteBuffer.wrap(labelDS.slice(4, 8)).getInt
    println(s"numOfLabelItems=$numOfLabelItems")
    //打印测试数据
    for ((e, index) <- labelDS.drop(8).take(3).zipWithIndex) {
      println(s"image$index is $e")
    }

    labelDS
  }

  def loadTrainImageData(): Array[Byte] = {
    loadImageData(trainImageFileName)
  }

  def loadTestImageData(): Array[Byte] = {
    loadImageData(testImageFileName)
  }

  /**
   * 加载MNIST train data数据
   */
  def loadImageData(filename: String): Array[Byte] = {
    val file = new File(dataDir + filename)
    val in = new FileInputStream(file)
    val trainingDS = new Array[Byte](file.length.toInt)
    using(new FileInputStream(file)) { source =>
      {
        in.read(trainingDS)
      }
    }

    //32 bit integer  0x00000803(2051) magic number 
    val magicNum = ByteBuffer.wrap(trainingDS.take(4)).getInt
    println(s"magicNum=$magicNum")
    //32 bit integer  60000            number of items 
    val numOfItems = ByteBuffer.wrap(trainingDS.slice(4, 8)).getInt
    println(s"numOfItems=$numOfItems")
    //32 bit integer  28               number of rows 
    val numOfRows = ByteBuffer.wrap(trainingDS.slice(8, 12)).getInt
    println(s"numOfRows=$numOfRows")
    //32 bit integer  28               number of columns 
    val numOfCols = ByteBuffer.wrap(trainingDS.slice(12, 16)).getInt
    println(s"numOfCols=$numOfCols")

    println(s"numOfItems=" + trainingDS.drop(16).length + "=" + (numOfItems * numOfRows * numOfRows))

    trainingDS
  }

  def loadTrainData(): Array[(Double, Vector)] = {
    loadData(loadTrainLabelData, loadTrainImageData)
  }

  def loadTestData(): Array[(Double, Vector)] = {
    loadData(loadTestLabelData, loadTestImageData)
  }

  def loadData(loadLabelFunc: () => Array[Byte], loadImageFunc: () => Array[Byte]): Array[(Double, Vector)] = {
    val labelDS: Array[Byte] = loadLabelFunc()
    val labels = labelDS.drop(8)

    val trainingDS: Array[Byte] = loadImageFunc()
    val numOfItems = ByteBuffer.wrap(trainingDS.slice(4, 8)).getInt
    val itemsBuffer = new ArrayBuffer[Array[Byte]]
    for (i <- 0 until numOfItems) {
      //16->16 + 28 * 28
      //16 + 28 * 28->16 + 2*28 * 28
      itemsBuffer += trainingDS.slice(16 + i * 28 * 28, 16 + (i + 1) * 28 * 28)
    }
    println("numOfImages=" + itemsBuffer.length)
    val itemsArray = itemsBuffer.toArray
    val data = labels.zip(itemsArray)
    //打印测试数据概况
    println("image digit count:")
    data.groupBy(a => a._1).mapValues(b => b.size).foreach(println)

    //only 0/1 image
    data.filter(p => p._1 < numCount).map(p => (p._1.toDouble, Vectors.dense(p._2.map(c => c.toDouble))))
  }
}

object MNISTData {
  def loadTrainData(dataDir: String, numCount: Int = 2): Array[(Double, Vector)] = {
    new MNISTData(dataDir, numCount).loadTrainData()
  }
  def loadTestData(dataDir: String, numCount: Int = 2): Array[(Double, Vector)] = {
    new MNISTData(dataDir, numCount).loadTestData()
  }
}