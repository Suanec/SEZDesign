package IJCAI2016

/**
 * Created by suanec on 2016/5/7 : 20:17.
 */
object FileHelper {
  val _path : String = "D:\\ScalaSpace\\data\\ijcai16\\"
  val _source : String = "source\\"
  val _splits : String = "splits\\"
  val _calc : String = "calc\\"
  val totalSplits : Int = 223
  val singleMax : Int = 200000
  /**
   * Table 1: Online user behavior before Dec. 2015. (ijcai2016_taobao)
   * @param user_id                     unique user id
   * @param seller_id                   unique online seller id
   * @param item_id                     unique item id
   * @param category_id                 unique category id
   * @param online_action_id          “0” denotes “click” while “1” for “buy”
   * @param time_stamp                  date of the format “yyyymmdd”
   */
  case class taobao( user_id : Int = 0,
                   seller_id : Int = 0,
                   item_id : Int = 0,
                   category_id : Int = 0,
                   online_action_id : Int = 0,
                   time_stamp : Int = 0) extends Serializable

  /**
   * Table 2: Users’ shopping records at brick-and-mortar stores before Dec. 2015. (ijcai2016_koubei_train)
   * @param user_id                     unique user id
   * @param merchant_id                	unique merchant id
   * @param location_id	                unique location id
   * @param time_stamp                  date of the format “yyyymmdd”
   */
  case class koubei( user_id : Int = 0,
                     merchant_id : Int = 0,
                     location_id : Int = 0,
                     time_stamp : Int = 0) extends Serializable;

  /**
   * Table 3: Merchant information. (ijcai2016_merchant_info)
   * @param merchant_id               unique merchant id
   * @param budget                     budget constraints imposed on the merchant
   * @param location_list             available location list, e.g. 1:356:89
   */
  case class merchant( merchant_id : Int = 0,
                     budget : Int = 0,
                     location_list : Array[Int] = null) extends Serializable

  /**
   * Table 4: Prediction result. (ijcai2016_koubei_test)
   * @param user_id                    unique user id
   * @param location_id               unique location id
   * @param merchant_list             you may recommend at most 10 merchants here, separated by “:”, e.g. 1:5:69
   */
  case class prediction( user_id : Int = 0,
                         location_id : Int = 0,
                         merchant_list : Array[Int] = null) extends Serializable

  /**
   * split big taobao data to splits with 20W rows per file
   * @param _file
   */
  def splitTaobao( _file : String = "ijcai2016_taobao" ) : Int ={
    val size : Int = 44528127
    var fileIdx : Int = 0;
//    val singleMax : Int = 200000
    var rowIdx : Int = 0;
    import scala.io.Source
    import java.io.File
    import java.io.PrintWriter

    val file = new File(_path + _source + _file)
    val data = Source.fromFile(file)
    val iter = data.getLines()
    while(iter.hasNext){
      val w = new PrintWriter(new File( _path + _splits + "taobao_parts" + fileIdx.toString))
      while( rowIdx < singleMax && iter.hasNext ){
        w.write(iter.next())
        w.write("\n")
        rowIdx += 1
      }
      rowIdx = 0
      w.flush()
      w.close()
      fileIdx += 1
      if( fileIdx % 30 == 0 ) println(fileIdx + "to be done!!")
    }
    fileIdx
  }
  def splitKoubei( _file : String = "ijcai2016_koubei_train" ) : Int ={
    var fileIdx : Int = 0;
    //    val singleMax : Int = 200000
    var rowIdx : Int = 0;
    import scala.io.Source
    import java.io.File
    import java.io.PrintWriter

    val file = new File(_path + _source + _file)
    val data = Source.fromFile(file)
    val iter = data.getLines()
    while(iter.hasNext){
      val w = new PrintWriter(new File( _path + _splits + "koubei_parts" + fileIdx.toString))
      while( rowIdx < singleMax && iter.hasNext ){
        w.write(iter.next())
        w.write("\n")
        rowIdx += 1
      }
      rowIdx = 0
      w.flush()
      w.close()
      println(fileIdx + "Finished!!")
      fileIdx += 1
    }
    fileIdx
  }
  /**
   * line2taobao
   * @param _line
   * @return
   */
  def line2taobao( _line : String ) : taobao = {
//    1980536,9666,1450952,1,0,20150826
    val s = _line.split(",").map( _.toInt )
    require( 6 == s.size, "line2taobao.error!!" )
    val res = new taobao(s(0),s(1),s(2),s(3),s(4),s(5))
    res
  }
  def line2koubei( _line : String ) : koubei = {
    val s = _line.split(",").map(_.toInt)
    val res = new koubei(s(0),s(1),s(2),s(3))
    res
  }
  def line2merchant( _line : String ) : merchant = {
    val str = _line.split(",")
    val s = str.init.map(_.toInt)
    val arr = str.last.split(":").map(_.toInt)
    val res = new merchant(s(0),s(1),arr)
    res
  }
  def predict2line( _it : prediction ) : String = {
    var str = _it.user_id.toString
    str += ","
    str += _it.location_id.toString
    str += ","
    _it.merchant_list.map{
      item =>
        str += item
        str += ":"
    }
    str.init
  }

  /**
   *  read splits of taobao
   * @param _num  num of splits to read
   * @return
   */
  def readTaobaoSplits( _num : Int = 1 ) : Array[Array[taobao]] = {
    import scala.io.Source
    import java.io.File
    val _file: String = "taobao_parts"

    val res = new Array[Array[taobao]](_num)

    res.indices.map {
      fileIdx =>
        val file = new File(_path + _splits + _file + fileIdx.toString)
        val iter = Source.fromFile(file).getLines()
        res(fileIdx) = iter.map(line2taobao(_)).toArray
    }
    res
  }
  def readKoubeiSplits( _num : Int = 1  ) : Array[Array[koubei]] = {
    import scala.io.Source
    import java.io.File
    val _file : String = "koubei_parts"

    val res = new Array[Array[koubei]](_num)

    res.indices.map{
      i =>
        val file = new File( _path + _splits + _file + i.toString )
        val iter = Source.fromFile(file).getLines()
        res(i) = iter.map(line2koubei(_)).toArray
        println( i + "has read.")
    }
    res
  }
  def readMerchants() : Array[merchant] = {
    import scala.io.Source
    import java.io.File
    val _file : String = "ijcai2016_merchant_info"

    val file = new File( _path + _source + _file )
    val iter = Source.fromFile(file).getLines()
    val res = iter.map(line2merchant(_)).toArray
    res
  }

}
