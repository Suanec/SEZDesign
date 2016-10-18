package SelfAERowMatrix

import scala.util.Random
import util._

/**
 * Created by suanec on 2016/4/6 : 19:26.
 */
class Matrix ( private var m_row : Int,
                private var m_col : Int,
                private var m_data : Array[Array[Double]] ) {
  def this() = this(0,0,null)
  def this( _row : Int, _col : Int ) = this(_row, _col, new Array[Array[Double]](_row).map( line => new Array[Double](_col)) )
  def this( _data : Array[Array[Double]] ) = {
    this( _data.size, _data.map( line => line.size ).max, _data )
    m_data = new Array[Array[Double]](m_row).map( line => new Array[Double](m_col))
    (0 until _data.size).map{
      i =>
        (0 until _data(i).size).map{
          j =>
            m_data(i)(j) = _data(i)(j)
        }
    }
  }
  def this( _row : Int, _col : Int, _seed : Long ) {
    this(_row, _col, null)
    val rand = new Random(_seed)
    m_data = new Array[Array[Double]](_row).map {
      line =>
        new Array[Double](_col).map( x => rand.nextGaussian() % 0.1 )
    }
  }

  def getRows() = m_row
  def getCols() = m_col
  def size() = m_row -> m_col
  def getValue() = m_data
  def getRow( _x : Int ) = {
    require( _x > 0 && _x <= m_row )
    val row = _x - 1
    m_data(row)
  }
  def setRow( _x : Int, _arr : Array[Double] ) = {
    require( _x > 0 && _x <= m_row && _arr.size == m_col )
    val row = _x - 1
    m_data(row) = _arr.clone
    m_data(row).last == _arr.last
  }

  /**
   * Grammar suger!!
   * @param _y
   * @param _x
   * @return
   */
  def apply( _x : Int, _y : Int ) : Double = {
    require(_x > 0 && _y > 0 && _x <= m_row && _y <= m_col)
    val row = _x - 1
    val col = _y - 1
    m_data(row)(col)
  }
  def update( _x : Int, _y : Int, _v : Double ) : Double = {
    require(_x > 0 && _y > 0 && _x <= m_row && _y <= m_col)
    val row = _x - 1
    val col = _y - 1
    m_data(row)(col) = _v
    m_data(row)(col)
  }

  def MulRow( _line : SVector ) : SVector = {
    require( m_col == _line.size )
    val res = new Array[Double](m_row)
    (0 until m_row).map{
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
    require(m_col >= d.last._1)
    val res = new SVector(m_row)
    (0 until m_row).map{
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
  def * (_doc : SDoc) = MulDoc(_doc)
  def * (_line : SVector) = MulRow(_line)
  def + (_m : Matrix) : Matrix = {
    require(_m.getRows()==m_row && _m.getCols() == m_col)
    val res = new Matrix(_m.getValue)
    val arr = res.getValue()
    (0 until arr.size).map{
      i =>
        (0 until arr(i).size).map{
          j =>
            arr(i)(j) += m_data(i)(j)
        }
    }
    res
  }
  def - (_m : Matrix) : Matrix = {
    require(_m.getRows()==m_row && _m.getCols() == m_col)
    val res = new Matrix(m_data)
    val arr = res.getValue()
    (0 until arr.size).map{
      i =>
        (0 until arr(i).size).map{
          j =>
            arr(i)(j) -= m_data(i)(j)
        }
    }
    res
  }

//  def >> ( _file : java.io.File = "" )

}
