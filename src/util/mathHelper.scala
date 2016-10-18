package util

/**
 * Created by suanec on 2016/6/17 : 10:31.
 */
object mathHelper {
  def powSelf( _x : Double, _y : Double ) : Double = {
    if(_x == 0)return 0d;
    var res = 0d;
    if( _x < 0 ) {
      if (_y % 2 != 0) res = -scala.math.exp(_y * scala.math.log(-_x))
      else res = scala.math.exp(_y * scala.math.log(-_x))
    }
    else res = scala.math.exp( _y * scala.math.log(_x) )
    res
  }

}
