package CDL.util

/**
 * Created by suanec on 2016/5/11 : 12:00.
 */
object fileHelper {
  type tyInt = Int
  type tyDouble = Double
  type BDM = breeze.linalg.DenseMatrix[tyDouble]
  type BDV = breeze.linalg.DenseVector[tyDouble]
  /// 四元组结构体
  case class Udata( user_id : tyInt = 0,
                    item_id : tyInt = 0,
                    rate : tyInt = 0,
                    time_stamp : Int = 0)extends Serializable
  type rData = Array[Udata]

  val _workSpace : String = "D:\\ScalaSpace\\data"
  val _path : String = "\\ml-100k\\"
  val _uFile : String = "u.data"

  val uNum = 943
  val vNum = 1682
  /// 由文件名读入数据到rData中。
  def readU2R( _file : String = "u1.base", _path : String = _path, _workspace : String = _workSpace ) : rData = {
    import scala.io.Source
    val data = Source.fromFile(_workspace+_path+_file)
    val iter = data.getLines()
    val res = iter.map{
      l =>
        val splits = l.split('\t')
        Udata(splits(0).toInt,splits(1).toInt,splits(2).toInt,splits.last.toInt)
    }.toArray
    res
  }
  /// 根据四元组数据文件生成矩阵（一般用于R评分矩阵的生成）
  def genRmat( _file : String = "u1.base", _path : String = _path, _workspace : String = _workSpace ) : BDM = {
    val data = readU2R(_file,_path,_workspace)
    val mat = new BDM(uNum,vNum)
    data.sortBy(_.user_id)
    data.foreach{
      u =>
        mat(u.user_id-1,u.item_id-1) = u.rate
    }
    mat
  }
  def genRandomRmat( _file : String = "u1.base", _path : String = _path, _workspace : String = _workSpace ) : BDM = {
    val userIdMap = (0 until uNum).toArray
    util.randomHelper.randPerm(userIdMap)
    val data = readU2R(_file,_path,_workspace)
    val mat = new BDM(uNum,vNum)
    data.foreach{
      u =>
        mat(userIdMap(u.user_id-1),u.item_id-1) = u.rate
    }
    mat
  }
}
