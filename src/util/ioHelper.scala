package util

import scala.{specialized => spec}
import scala.reflect.ClassTag
import java.io.File
import java.io.PrintWriter
import scala.io.Source
import breeze.linalg.DenseMatrix

/**
 * Created by suanec on 2016/5/13 : 21:51.
 */
object ioHelper {
  type tyInt = Int
  type tyDouble = Double
  type BDM = breeze.linalg.DenseMatrix[tyDouble]
  type BDV = breeze.linalg.DenseVector[tyDouble]

  val workSpace : String = "D:\\ScalaSpace\\"
  val path : String = "data\\CDL\\"
  val file : String = "data.tem"
  /// specialized cannot contain class such as String 不建议使用@Deprecated
  class NumberWriter[@spec(Double,Int,Float,Long,Short) V](_file : String = file,
                                                            _path : String = path,
                                                            _workSpace : String = workSpace,
                                                            _writer : PrintWriter = null
                                                            )
  {
    def writeArray( _arr : Array[V] ): Unit ={
      val file = new java.io.File(_workSpace+_path+_file)
      val writer = new java.io.PrintWriter(file)
      _arr.foreach( x => writer.write( x.toString + "\n") )
    }
    def writeMatrix( _mat : DenseMatrix[V] ): Unit = {
      val file = new File(_workSpace+_path+_file)
      val writer = new PrintWriter(file)
      (0 until _mat.rows).map{
        i =>
          (0 until _mat.cols).map{
            j =>
              writer.write( _mat(i,j).toString + "," )
          }
          writer.write("\n")
      }
    }
  }
  /// ClassTag can covert any type but manipulate as classes(any)
  /// TypeTag operates as exactly type such as Double
  class GeneralWriter[T : ClassTag]()
  {
    implicit var m_file : String = file
    implicit var m_path : String = path
    implicit var m_workSpace : String = workSpace
    implicit var m_writer : PrintWriter = new PrintWriter(new File(m_workSpace + m_path + m_file))
    /// 默认构造
    ///    def this() = this()
    /// 自定义构造
//    def this(_file : String = file) = {
//      this
//      m_file = _file
//      m_path = path
//      m_workSpace = workSpace
//      m_writer = new PrintWriter(new File(m_workSpace + m_path + m_file))
//
//    }
//    def this(_file : String = file, _path : String = path) = {
//      this()
//      m_file = _file
//      m_path = _path
//      m_workSpace = workSpace
//      m_writer = new PrintWriter(new File(m_workSpace + m_path + m_file))
//    }
    def this(_file : String = file, _path : String = path, _workSpace : String = workSpace) = {
      this()
      m_file = _file
      m_path = _path
      m_workSpace = _workSpace
      m_writer = new PrintWriter(new File(m_workSpace + m_path + m_file))
    }
    /// 写入
    def write( _str : String = "" ) = m_writer.write( _str )
    /// 写入并换行
    def writeln( _str : String = "" ) = m_writer.write( _str + "\n" )
    /// 写入并以逗号分隔
    def writeWithComma( _str : String = "" ) = m_writer.write( _str + "," )
    /// 写入并以空格分隔
    def writeWithSpace( _str : String = "" ) = m_writer.write( _str + " " )
    /// 写入并以制表符分隔
    def writeWithTab( _str : String = "" ) = m_writer.write( _str + "\t" )
    /// flush
    def flush() = m_writer.flush()
    /// 写入数组
    def writeArray( _arr : Array[T] ): Unit ={
      _arr.foreach( x => writeln(x.toString) )
      flush
    }
    /// 写入矩阵
    def writeMatrix( _mat : DenseMatrix[T] ) = {
      writeWithComma( _mat.rows.toString )
      writeln( _mat.cols.toString )
      (0 until _mat.rows).foreach{
        i =>
          (0 until _mat.cols).foreach{
            j =>
              writeWithComma( _mat(i,j).toString )
          }
          writeln()
          flush
      }
    }/// writeMatrix
  }/// GeneralWriter
  class GeneralReader[T : ClassTag]()
  {
    implicit var m_file : String = file
    implicit var m_path : String = path
    implicit var m_workSpace : String = workSpace
    implicit var m_read :  scala.io.BufferedSource = Source.fromFile( new File(m_workSpace + m_path + m_file) )
    implicit var m_iter : scala.collection.Iterator[scala.Predef.String] = m_read.getLines
    def this(_file : String = file, _path : String = path, _workSpace : String = workSpace) = {
      this()
      m_file = _file
      m_path = _path
      m_workSpace = _workSpace
      m_iter = Source.fromFile( new File(m_workSpace + m_path + m_file) ).getLines()
    }
    ///
    def reset() = {
      m_read = Source.fromFile( new File(m_workSpace + m_path + m_file) )
      m_iter = m_read.getLines()
    }
    ///
    def readByComma() : Array[String] ={
      if(m_iter.hasNext) m_iter.next().split(",")
      else null
    }
    ///
    def readMatrix(): DenseMatrix[Double] = {
      val size = readByComma().map( _.toInt )
      val mat = new DenseMatrix[Double](size(0),size(1))
      (0 until mat.rows).foreach{
        i =>
          val line = readByComma().map(_.toDouble)
          (0 until mat.cols).foreach{
            j =>
              mat(i,j) = line(j)
          }
      }
      mat
    }/// readMatrix
  }/// GeneralReader

  /// 返回当前路径下所有子目录
  def subDirs(_dir: File): Iterator[File] = {
    val children = _dir.listFiles.filter(_.isDirectory)
    children.toIterator ++ children.toIterator.flatMap(subDirs _)
  }
  def subDirs(_path : String): Iterator[File] = {
    val dir = new File(_path)
    subDirs(dir)
  }
  /// 返回当前路径下所有文件
  def subDirFiles(_dir: File): Iterator[File] = {
    val d = _dir.listFiles.filter(_.isDirectory)
    val f = _dir.listFiles.filter(_.isFile).toIterator
    f ++ d.toIterator.flatMap(subDirFiles _)
  }
  def subDirFiles(_path : String) : Iterator[File] = {
    val dir = new File(_path)
    subDirFiles(dir)
  }
  /// 返回当前目录下所有目录和文件
  def subDirAndFiles(_dir: File): Iterator[File] = {
    val d = _dir.listFiles.filter(_.isDirectory)
    val f = _dir.listFiles.toIterator
    f ++ d.toIterator.flatMap(subDirAndFiles _)
  }
  def subDirAndFiles(_path : String) : Iterator[File] = {
    val dir = new File(_path)
    subDirAndFiles(dir)
  }
}