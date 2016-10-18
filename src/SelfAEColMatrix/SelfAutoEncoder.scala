package SelfAEColMatrix

import util._

/**
 * Created by suanec on 2016/4/4 : 20:37.
 */
object SelfAutoEncoder {
  /// learningRate
  var lr : Double = 0.3
  /// Weight Decay lamda1/lamda2, to calc more exactly, use 2 Ints instead of 1 Double.
  var lamda1 : Double = 1d
  var lamda2 : Double = 2d

  def setLR(_lr : Double ) = lr = _lr

  def FeedForward0( W1 : Matrix, W2 : Matrix, b1 : SVector, b2 : SVector, _in : SVector ) : (SVector, SVector) ={
    val z0 = SVectorPlus(W1.MulRow(_in), b1)
    val a0 = z0.map( x => sigmoid(x) )
    val z1 = SVectorPlus(W2.MulRow(a0), b2)
    val a1 = z1.map( x => sigmoid(x) )
    (a0 -> a1)
  }
  def FeedForward0( W1 : Matrix, W2 : Matrix, b1 : SVector, b2 : SVector, _in : SDoc ) : (SVector, SDoc) = {
    val z0 = SVectorPlus(W1.MulDoc(_in), b1)
    val a0 = z0.map( x => sigmoid(x) )
    val z1 = (0 until _in.size).map{
      i =>
        val c = W2.getCol(_in(i)._1 -1)
        _in(i)._1 -> SVectorMul(c,a0)
    }.toArray
    val a1 = z1.map( x => x._1 -> sigmoid(x._2) )
    (a0, a1)
  }

  def encode(W1 : Matrix, b1: SVector, _in : SVector) : SVector = {
    SVectorPlus( W1 * _in, b1).map( x => sigmoid(x) )
  }

  def decode(W2 : Matrix, b2 : SVector, _hOut : SVector ) : SVector = {
    SVectorPlus( W2 * _hOut , b2 ).map( x => sigmoid(x) )
  }

  def encode(W1 : Matrix, b1 : SVector, _in : SDoc) : SVector = {
    SVectorPlus( W1 * _in, b1 ).map( x => sigmoid(x) )
  }

  def decode(W2 : Matrix, b2 : SVector, _hOut : SVector, _in : SDoc) : SDoc = {
    (0 until _in.size).map{
      i =>
        val c = W2.getCol(_in(i)._1 -1)
        _in(i)._1 -> SVectorMul(c, _hOut)
    }.map( x => x._1 -> sigmoid(x._2) ).toArray
  }

  def FeedForward( W1 : Matrix, W2 : Matrix, b1 : SVector, b2 : SVector, _in : SVector ) : (SVector, SVector) ={
    val a0 = encode(W1,b1,_in)
    val a1 = decode(W2, b2, a0)
    a0 -> a1
  }

  def FeedForward( W1 : Matrix, W2 : Matrix, b1 : SVector, b2 : SVector, _in : SDoc ) : (SVector, SDoc) = {
    val a0 = encode(W1, b1, _in)
    val a1 = decode(W2, b2, a0, _in)
    a0 -> a1
  }

  def calcLossOut( _in : SVector, _out : SVector ) : Double = {
    val subRes = SVectorSub(_out, _in)
    val res = subRes.map{
      x =>
        x * x / 2d
    }.sum
    res
  }

  def calcLossOut( _subRes : SVector ) : Double = {
    _subRes.map {
      x =>
        x * x / 2d
    }.sum/_subRes.size
  }

  def calcLossOut( _in : SDoc, _out : SDoc ) : Double = {
    val subRes = SDocSub(_out, _in)
    val res = subRes.map{
      x =>
        x._2 * x._2 / 2d
    }.sum
    res
  }

  def calcLossOut( _subRes : SDoc ) : Double = {
    _subRes.map {
      x =>
        x._2 * x._2 / 2d
    }.sum
  }

  def calcErrOut( _in : SVector, _out : SVector ) : SVector = {
    val subRes = SVectorSub(_in,_out )
    val loss = calcLossOut(subRes)
    println("Loss : " + loss )
    val dif_out = _out.map( x => Dif_sigmoid(x) )
    SVectorDotMul(subRes, dif_out)
  }

  def calcErrOut( _in : SDoc, _out : SDoc ) : SDoc = {
    val subRes = SDocSub(_in, _out)
    val loss = calcLossOut(subRes)
    println("Loss : " + loss)
    val dif_out = _out.map( x => x._1 -> Dif_sigmoid(x._2) )
    SDocDotMul(subRes, dif_out)
  }

  def calcErrHidden( _errOut : SVector, W2 : Matrix, _hiddenOut : SVector ) : SVector = {
    val errHidden = new SVector(_hiddenOut.size)
    (0 until _errOut.size).map{
      i =>
        (0 until errHidden.size).map{
          j =>
            errHidden(j) += _errOut(i) * W2(i,j)
        }
    }
    val dif_hidden = _hiddenOut.map( x => Dif_sigmoid(x) )
    SVectorDotMul(errHidden, dif_hidden)
  }

  def calcErrHidden( _errOut : SDoc, W2 : Matrix, _hiddenOut : SVector ) : SVector = {
    val errHidden = new SVector(_hiddenOut.size)
    (0 until _errOut.size).map{
      i =>
        val k = _errOut(i)._1
        (0 until errHidden.size).map{
          j =>
            errHidden(j) += _errOut(i)._2 * W2(k,j)
        }
    }
    val dif_hidden = _hiddenOut.map( x => Dif_sigmoid(x) )
    SVectorDotMul(errHidden, dif_hidden)
  }

  /// Wij -> W(j,i) -> in(j) -> Out(i) -> err(i)
  /// Wji -> W(i,j) -> in(i) -> out(j) -> err(j)
  def updateW(_in : SVector, _hiddenOut : SVector,
               _errHidden : SVector, _errOut : SVector,
               W1 : Matrix, W2 : Matrix,
               b1 : SVector, b2 : SVector) = {
    (0 until _errHidden.size).map{
      i =>
        ( 0 until W1.getCol(i).size ).map{
          j =>
            W1(i,j) += lr * ( lamda1 * ( _in(j) * _errHidden(i) ) - lamda2 * W1(i,j) )
        }
        b1(i) += lr * _errHidden(i)
    }
    ( 0 until _errOut.size).map{
      i =>
        ( 0 until W2.getCol(i).size ).map{
          j =>
            W2(i,j) += lr * (lamda1 * _hiddenOut(j) * _errOut(i) - lamda2 * W2(i,j))
        }
        b2(i) += lr * _errOut(i)
    }
  }
  /// a1 = f( W11x1 + W12x2 + W13x3 + b1)
  /// a2 = f( W21x1 + W22x2 + W23x3 + b2)
  /// a3 = f( W31x1 + W22x2 + W33x3 + b3)
  def updateW(_in : SDoc, _hiddenOut : SVector,
               _errHidden : SVector, _errOut : SDoc,
               W1 : Matrix, W2 : Matrix,
               b1 : SVector, b2 : SVector) = {
    (0 until _errHidden.size).map{
      i =>

    }

  }

}
