object MacAddressToNum {
  val macStr = "F8-A4-5F-51-85-D4"
  def macAddressToBigInt( _str : String ) : BigInt = {
    binaryStringToBigInt( macAddressToString(_str) )
  }
  def macAddressToString( _str : String ) : String = {
    if(_str.size < 1) {
      println("str.size < 1 in macAddressToString")
      return ""
    }
    val splits = _str.split('-')
    if(splits.size < 2){
      println("splits.size < 2 in macAddressToString")
      return ""
    }
    splits.flatMap(_.to).map(charToBinary).mkString
  }
  def charToBinary( _c : Char ) : String = {
    if( '0' <= _c && '9' >= _c ) return _c.toInt.toBinaryString
    if( 'A' <= _c && 'F' >= _c ) return (_c - 'A' + 10).toBinaryString
    else {
    println("${_c} is not a 0x num.") return ""
  }
  def binaryStringToBigInt( _str : String ) : BigInt = {
    if(_str.size < 1){
      println("str.size < 1 in binaryStringToBigInt")
      return BigInt.int2bigInt(0)
    }
    if(_str.filter( _ != '0').filter(_ != '1').size > 0) {
      println("str not binaryString in binaryStringToBigInt")
      return 0
    }
    var sum = BigInt.int2bigInt(0)
    _str.indices.map{
      i => 
        _str(i) match {
          case '0' => sum <<= 1
          case '1' => {
            sum <<= 1 
            sum += 1
          }
        }
    }
    sum 
  }
}