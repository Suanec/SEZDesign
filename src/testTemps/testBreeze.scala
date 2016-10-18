package testTemps

/**
 * Created by suanec on 2016/5/9 : 18:24.
 */
object testBreeze {
  def testbreeze(): Unit ={
    println("testBreeze.test")
    import breeze.linalg._
    import breeze.plot._

    val m = DenseMatrix.zeros[Double](3,4)
    println(m)

    val f = Figure()
    val p = f.subplot(0)
    val x = linspace(0.0,1.0)
    p += plot(x, x :^ 2.0)
    p += plot(x, x :^ 3.0, '.')
    p.xlabel = "x axis"
    p.ylabel = "y axis"
    f.saveas("lines.png")
    val p2 = f.subplot(2,1,1)
    val g = breeze.stats.distributions.Gaussian(0,1)
    p2 += hist(g.sample(100000),100)
    p2.title = "A normal distribution"
    f.saveas("subplots.png")
    val f2 = Figure()
    f2.subplot(0) += image(DenseMatrix.rand(200,200))
    f2.saveas("image.png")
  }

}
