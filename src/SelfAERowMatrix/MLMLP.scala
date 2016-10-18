package SelfAERowMatrix
import util._
//import SelfAERowMatrix.Matrix

/**
 * Created by suanec on 2016/4/6 : 21:22.
 */
object MLMLP {
  var lr = 0.3
  var lamda1 = 1d
  var lamda2 = 0d

  /// z = W1 * _line + b1
  /// a = sigmoid(z)
  def encode( W1 : Matrix, b1 : SVector, _line : SVector ) : SVector = SVectorPlus( W1 * _line, b1 ).map(sigmoid(_))
//  {
//    val z = SVectorPlus( W1 * _line, b1)
//    val a = z.map(sigmoid(_))
//    z
//  } //

  def decode( W2 : Matrix, b2 : SVector, _hOut : SVector ) : SVector = SVectorPlus( W2 * _hOut, b2 ).map(sigmoid(_))

  def encode( W1 : Matrix, b1 : SVector, _doc : SDoc ) : SVector = SVectorPlus( W1 * _doc, b1 ).map(sigmoid(_))

  def decode( W2 : Matrix, b2 : SVector, _hOut : SVector, _doc : SDoc ) : SDoc = {
    val res_doc = new SDoc(_doc.size)
    (0 until _doc.size).map{
      i =>
        val row = _doc(i)._1
        res_doc(i) = row -> sigmoid(SVectorMul( W2.getRow(row), _hOut ) + b2(i))
    }
    res_doc
  }

  def FeedForward( W1 : Matrix, W2 : Matrix, b1 : SVector, b2 : SVector, _in : SVector ) : (SVector, SVector) = {
    val hOut = encode( W1, b1, _in )
    val lOut = decode( W2, b2, hOut )
    (hOut, lOut)
  }
  def FeedForward( W1 : Matrix, W2 : Matrix, b1 : SVector, b2 : SVector, _doc : SDoc ) : ( SVector, SDoc ) = {
    val hOut = encode( W1, b1, _doc )
    val lOut = decode( W2, b2, hOut, _doc )
    (hOut, lOut)
  }

  def calcLoss( _lOut : SVector,  _line : SVector) : Double = {
    val subRes = SVectorSub(_lOut, _line)
    subRes.map{
      x =>
        (x * x * 0.5)
    }.sum / subRes.size
  }

  def calcLoss( _lOut : SDoc, _doc : SDoc ) : Double = {
    val subRes = SDocSub(_lOut, _doc)
    val resArr = subRes.map{
      x =>
        (x._2 * x._2 * 0.5)
    }
    resArr.sum/resArr.size
  }
  def calcErrOut( _lOut : SVector, _line : SVector ) : SVector = {
    val subRes = SVectorSub( _lOut, _line )
    val res = (0 until subRes.size).map{
      i =>
        _lOut(i) * ( 1 - _lOut(i) ) * subRes(i)
    }.toArray
    res
  }
  def calcErrOut( _lOut : SDoc, _doc : SDoc ) : SDoc = {
    val subRes = SDocSub( _lOut, _doc )
    val res = (0 until subRes.size).map{
      i =>
        val row = _doc(i)._1
        row -> ( _lOut(i)._2 * ( 1 - _lOut(i)._2 ) * subRes(i)._2 )
    }.toArray
    res
  }
  def calcErrHidden( _errOut : SVector, W2 : Matrix, _hOut : SVector ) : SVector = {
    val res = new SVector(_hOut.size)
    (0 until res.size).map{
      i =>
        (0 until _errOut.size).map{
          j =>
            res(i) += _errOut(j) * W2(j+1,i+1)
        }
        res(i) *= ( _hOut(i) * ( 1 - _hOut(i)) )
    }
    res
  }
  def calcErrHidden( _errOut : SDoc, W2 : Matrix, _hOut : SVector ) : SVector = {
    val res = new SVector(_hOut.size)
    (0 until res.size).map{
      i =>
        (0 until _errOut.size).map{
          j =>
            val k = _errOut(j)._1
            res(i) += _errOut(j)._2 * W2(k, i+1)
        }
        res(i) *= ( _hOut(i) * ( 1 - _hOut(i)) )
    }
    res
  }
  def updateW( W1 : Matrix, W2 : Matrix,
             b1 : SVector, b2 : SVector,
             _line : SVector, _hOut : SVector,
             _errOut : SVector, _errHidden : SVector ) = {
    (0 until _errOut.size).map{
      i =>
        (0 until _errHidden.size).map{
          j =>
            W2(i+1,j+1) -= lr * ( lamda1*(_errOut(i) * _hOut(j)) + lamda2*W2(i+1,j+1) )
        }
        b2(i) -= lr * _errOut(i)
    }
    (0 until _hOut.size).map{
      i =>
        (0 until _line.size).map{
          j =>
            W1(i+1,j+1) -= lr * ( lamda1*(_errHidden(i) * _line(j)) + lamda2*W1(i+1,j+1) )
        }
        b2(i) -= lr * _errHidden(i)
    }
  }
  def updateW( W1 : Matrix, W2 : Matrix,
                b1 : SVector, b2 : SVector,
               _doc : SDoc, _hOut : SVector,
               _errOut : SDoc, _errHidden : SVector) = {
    (0 until _errOut.size).map{
      i =>
        val k = _errOut(i)._1
        (0 until _errHidden.size).map{
          j =>
            W2(k, j+1) -= lr * ( lamda1 * (_errOut(i)._2 * _hOut(j)) + lamda2 * W2(k, j+1))
        }
        b2(k - 1) -= lr * _errOut(i)._2
    }
    (0 until _hOut.size).map{
      i =>
        (0 until _doc.size).map{
          j =>
            val k = _doc(j)._1
            W1(i+1, k) -= lr * ( lamda1 * ( _errHidden(i) * _doc(j)._2) + lamda2 * W1(i+1, k) )
        }
        b1(i) -= lr * _errHidden(i)
    }
  }
  def BackPropagation( W1 : Matrix, W2 : Matrix,
                       b1 : SVector, b2 : SVector,
                       _in : SVector ) : Double = {
//    val hOut = encode(W1, b1, _in)
//    val lOut = decode(W2, b2, hOut)
    val (hOut, lOut) = FeedForward(W1,W2,b1,b2,_in)
    val loss = calcLoss(lOut,_in)
//    println("Loss : " + loss)
    val errOut = calcErrOut(lOut, _in)
    val errHidden = calcErrHidden(errOut, W2, hOut)
    updateW(W1, W2, b1, b2, _in, hOut, errOut, errHidden)
    loss
  }
  def BackPropagation( W1 : Matrix, W2 : Matrix,
                        b1 : SVector, b2 : SVector,
                        _doc : SDoc) : Double = {
    val (hOut, lOut) = FeedForward(W1, W2, b1, b2, _doc)
    val loss = calcLoss(lOut, _doc)
    val errOut = calcErrOut(lOut, _doc)
    val errHidden = calcErrHidden(errOut, W2, hOut)
    updateW(W1, W2, b1, b2, _doc, hOut, errOut, errHidden)
    loss
  }

}
