/// del cols card_lb_cnt
/// del cols card_lb
/// del cols transin_cnt1
/// del cols transin_cnt3
/// del cols transin_amt1
/// del cols transin_amt6
/// del cols transin_amt3
/// del cols transinmax_amt1
/// del cols transinmax_amt6
/// del cols transinmax_amt3
/// del cols Array("card_lb_cnt","card_lb","transin_cnt1","transin_cnt3","transin_amt1","transin_amt6","transin_amt3","transinmax_amt1","transinmax_amt6","transinmax_amt3")

/// card_zh : 6   space all label 1 so space to 1 ; label 0 : 1,748/3,40. laabel 1 : 1,4469/3,192/""/4
/// card_product : 10   AA -> 7
/// top_trdtz : 20  aftr-> 0 ,dawn-> 1 ,dusk-> 2 ,earl-> 3 ,even-> 4 ,morn-> 5 ,nigt-> 6 ,noon -> 7


val path = "D:\\ScalaSpace\\data\\financialCardMatch\\matchData"
val trainFile = path + "\\train1.csv"
val card_zh = 6 /////head.indexOf("card_zh")
val card_product = 10   //head.indexOf("card_product")
val top_trdtz = 20 /// //head.indexOf("top_trdtz")
val file = "train.csv"
val arr = scala.io.Source.fromFile(file).getLines.toArray
val head = arr.head.split(',')
val del_cols = Array("card_lb_cnt","card_lb","transin_cnt1","transin_cnt3","transin_amt1","transin_amt6","transin_amt3","transinmax_amt1","transinmax_amt6","transinmax_amt3")
val del_idx = del_cols.map(head.indexOf(_))
val mtrdtz = Array("aftr"-> "0" ,"dawn"-> "1" ,"dusk"-> "2" ,"earl"-> "3" ,"even"-> "4" ,"morn"-> "5" ,"nigt"-> "6" ,"noon"-> "7").toMap
var t = head
val data = arr.tail.map(_.split(',')).map{
  line =>
    t = line
    if(line(card_zh).size < 1) line(card_zh) = "1"
    if(line(card_product).equals("AA")) line(card_product) = "7"
    // println(line(top_trdtz),mtrdtz.get(line(top_trdtz)))
    line(top_trdtz) = mtrdtz.get(line(top_trdtz)).get
    line.indices.filterNot(del_idx.contains(_)).map{
      x => 
        // println(head(x),line(x))
        line(x).toDouble
      }.toArray.mkString(",")
}.mkString("\n")
val p = new java.io.PrintWriter(trainFile)
p.write(head.mkString(",") + "\n")
p.write(data + "\n")
p.flush
p.close