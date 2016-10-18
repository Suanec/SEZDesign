package testTemps

import scala.collection.mutable

/**
 * Created by suanec on 2016/5/10 : 18:16.
 */
object testMap {
  def testMap(): Unit = {
    import scala.collection.mutable.HashMap
    val arr = new Array[Char](26)
    arr.indices.map{
      i =>
        arr(i) = ('a'.toInt + i).toChar
    }
    val map = HashMap[Char,Int]()
    arr.indices.map{
      i =>
        map += (arr(i)->(i+1))
    }
    map('a') += 1
    val mat = new mutable.HashMap[(Int,Int),Double]()
    mat += ((1,1) -> 10)
    println(mat)


  }

}
