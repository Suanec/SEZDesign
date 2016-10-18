package util

import java.io.{File, PrintWriter}

import breeze.linalg.DenseMatrix

import scala.reflect.ClassTag

/**
 * Created by suanec on 2016/5/20 : 15:00.
 */
object LogSelf{
  val workSpace : String = "D:\\ScalaSpace\\"
  val path : String = "data\\CDL\\"
  val file : String = "Log"
  val fileExtension : String = ".log"
  /// 日志@deprecated
  class LogSelf(){
    implicit var m_file : String = file + fileExtension
    implicit var m_path : String = path
    implicit var m_workSpace : String = workSpace
    implicit var m_writer : PrintWriter = new PrintWriter(new File(m_workSpace + m_path + m_file))

    def this(_file : String = file, _path : String = path, _workSpace : String = workSpace) = {
      this()
      m_file = _file + fileExtension
      m_path = _path
      m_workSpace = _workSpace
      m_writer = new PrintWriter(new File(m_workSpace + m_path + m_file))
    }
    implicit def write( _str : String = "" ) = m_writer.write( _str )
    /// 写入并换行
    implicit def writeln( _str : String = "" ) = m_writer.write( _str + "\n" )
    /// 写入并以逗号分隔
    implicit def writeWithComma( _str : String = "" ) = m_writer.write( _str + "," )
    /// flush
    def flush() = m_writer.flush()
    /// 写入数组
    def log( _x : Int ) = writeln(_x.toString)
    def log( _x : Double ) = writeln(_x.toString)
    def log( _x : Long ) = writeln(_x.toString)
    def log( _x : Short ) = writeln(_x.toString)
    def log( _x : Float ) = writeln(_x.toString)
    def log( _x : Byte ) = writeln(_x.toString)
    def log( _s : String ) = writeln(_s)
    def log( _name : String , _x : String ) = {
//      write( _name + "," )
//      log(_x)
    }
    def log( _arr : Array[Int] ): Unit ={
      _arr.foreach( x => writeln(x.toString) )
      flush
    }
    def log( _arr : Array[Double] ): Unit ={
      _arr.foreach( x => writeln(x.toString) )
      flush
    }
    def log( _arr : Array[Float] ): Unit ={
      _arr.foreach( x => writeln(x.toString) )
      flush
    }
    def log( _arr : Array[Long] ): Unit ={
      _arr.foreach( x => writeln(x.toString) )
      flush
    }
  }
}
