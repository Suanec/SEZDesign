package SelfAEColMatrix

import util._

import scala.util.Random
/**
 * Created by suanec on 2016/4/4 : 18:36.
 */
class Matrix( private var m_cols : Int,
               private var m_rows : Int,
               private var m_data : Array[Array[Double]]) {
  def this() = this(0,0,null)
  def this( _col : Int, _row : Int ){
    this(_col, _row, null)
    m_data = new Array[Array[Double]](_col)
    m_data.map( x => new Array[Double](_row))
  }
  def this( _data : Array[Array[Double]] ) = {
    this(_data.size, _data.map( line => line.size ).max , _data)
  }
  def this( _col : Int, _row : Int, _rand : Long ) = {
    this(_col,_row)
    val rand = new Random(_rand)
    val tData = new Array[Array[Double]](_col).map( line => new Array[Double](_row).map( x => rand.nextDouble() ) )
    m_data = tData
  }
  def getCols() = m_cols
  def getRows() = m_rows
  def size() = m_cols -> m_rows
  def getElem( _y : Int, _x : Int ) = {
    require(_x >= 0 && _x < m_rows && _y >= 0 && _y < m_cols )
    m_data(_y)(_x)
  }
  def getValue() = m_data
  def updateElem( _y : Int, _x : Int, _elem : Double ) : Double = {
    require(_x >= 0 && _x < m_rows && _y >= 0 && _y < m_cols)
    m_data(_y)(_x) = _elem
    m_data(_y)(_x)
  }
  def getCol( _y : Int ) = m_data(_y)
  def MulRow( _line : SVector ) : SVector = {
    require( m_rows == _line.size )
    val res = new Array[Double](m_cols)
    (0 until m_cols).map{
      i =>
        val tmp = m_data(i)
        var tmpSum = 0d
        (0 until tmp.size).map{
          j =>
            tmpSum += _line(j) * tmp(j)
        }
        res(i) = tmpSum
    }
    res
  }
  def MulDoc( _doc : SDoc ) : SVector = {
    val d = _doc.sorted
    require(m_rows <= d.last._1)
    val res = new SVector(m_cols)
    (0 until m_cols).map{
      i =>
        var sum = 0d
        val c = m_data(i)
        (0 until d.size).map{
          j =>
            sum += c(d(j)._1 - 1) * d(j)._2
        }
        res(i) = sum
        sum = 0d
    }
    res
  }

  /**
   * Grammar suger!!
   * @param _y
   * @param _x
   * @return
   */
  def apply( _y : Int, _x : Int ) = getElem( _y, _x )
  def update( _y : Int, _x : Int, _v : Double ) = updateElem(_y, _x, _v)
  def * (_doc : SDoc) = MulDoc(_doc)
  def * (_line : SVector) = MulRow(_line)
}

