package IJCAI2016

import IJCAI2016.FileHelper.taobao

/**
 * Created by suanec on 2016/5/7 : 20:56.
 */
object test {
  def test(args : Array[String]): Unit = {
    println("hello ijcai16 taobao")
//    testTaobaoSplit()
    testKoubeiSplit()
    println("hello ijcai16 taobao")
  }
  def testTaobaoSplit(): Unit ={
//    val a = FileHelper.splitTaobao()
    val a = FileHelper.readTaobaoSplits()
    println(a.size)
    a.map{
      tbs =>
        println(tbs.size)
        tbs.map{
          tb =>
            println( tb.user_id + "," + tb.seller_id + "," + tb.item_id + "," + tb.category_id + "," + tb.online_action_id + "," + tb.time_stamp)
        }
    }
  }
  def testKoubeiSplit(): Unit ={
//    val a = FileHelper.splitKoubei()
    val a = FileHelper.readKoubeiSplits()
    println(a.size)
    a.map{
      kbs =>
        println(kbs.size)
        kbs.map{
          kb =>
            println( kb.user_id + "," + kb.merchant_id + "," + kb.location_id + "," + kb.time_stamp)
        }
    }
  }

}
