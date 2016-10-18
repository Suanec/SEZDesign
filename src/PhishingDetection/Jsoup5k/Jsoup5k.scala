import org.jsoup._
import scala.collection.JavaConverters._
import collection.mutable.Buffer

object Jsoup5k{

  val url = "http://www.5000best.com/websites/"
  /// grammar sugar
  def get(url : String ) = Jsoup.connect(url).timeout(1000*60).get
  /// give a url of 5kbest website convert it to rst:Buffer[String]
  def url2Rst(url : String) : Buffer[String] = {
    val doc = get(url)
    val content = doc.getElementById("content")
    val tab = content.getElementsByTag("tr").asScala.tail
    val rst = tab.map{
      item =>
        val list = item.getElementsByTag("td")
        val rank = list.get(0).
          text.toFloat.toInt.toString
        val score = list.get(1).text
        val category = list.get(2).text
        val audience = list.get(3).text
        val elem = list.get(4)
        val name = elem.text
        val href = elem.
          select("a").attr("href")
        val description = list.get(7).text
        val res = name + "," +
          rank + "," +
          score + "," + 
          category + "," + 
          audience + "," + 
          href + "," + 
          description + "\n"
        res 
    }
    rst
  }
  /// get all 5kbest websites rst
  def get5k(rootURL : String) : Array[Buffer[String]] = {
    val range = (1 to 50).toArray
    val rst = range.map{
      i => 
        println(i.toString + ":")
        url2Rst(rootURL + i.toString)
    }
    rst
  }
  /// write rst to File
  def saveRst(rst : Array[Buffer[String]]) : Unit = {
    val printer = new java.io.PrintWriter("Jsoup5k.rst")
    rst.map{
      buf =>
        buf.map{
          item =>
            printer.write(item + "\n")
        }
    }
    printer.close
    println("save Done.")
  }

  /// main
  def main(args : Array[String]) : Unit = {
    val rst = get5k(url)
    saveRst(rst)
    println("Mission Completed.")
  }

}  