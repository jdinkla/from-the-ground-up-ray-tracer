package net.dinkla.raytracer.math

///*
//
//
///**
// * Created by Dinkla on 10.07.2015.
// */
//abstract class Normal[@specialized(Float, Double) F](x: F, y: F, z: F)(implicit numType: Fractional[F]) extends Element3D[F](x, y, z)  {
//
//  import numType.mkNumericOps
//
//  def unary_-(): Normal[F]           // unary minus
//
//  //def this(v: Vector3D[F]) = this(v.normalize)
//
//
//
//  def +(n: Normal[F]): Vector3D[F]        = plus(n)    // Normal plus Normal gives a Vector
//  def plus(n: Normal[F]): Vector3D[F]     // Normal plus Normal gives a Vector
//
//  def -(n: Normal[F]): Vector3D[F]        = plus(-n)    // Point minus Point gives a Vector
//  def minus(n: Normal[F]): Vector3D[F]    = plus(-n)    // Point minus Point gives a Vector
//
//  def toVector: Vector2D[F]
//
//}
//
//object Normal {
//
//  val RIGHT: Normal = new Normal(1, 0, 0)
//  val LEFT: Normal = new Normal(-1, 0, 0)
//  val UP: Normal = new Normal(0, 1, 0)
//  val DOWN: Normal = new Normal(0, -1, 0)
//  val FRONT: Normal = new Normal(0, 0, 1)
//  val BACK: Normal = new Normal(0, 0, -1)
//  val ZERO: Normal = new Normal(0, 0, 0)
//
//}*/

class Normal(x: Float, y: Float, z: Float) extends Vector3DF(x, y, z){

  def this(v: Vector3DF) = this(v.x, v.y, v.z)
  def this(p0: Point3DF, p1: Point3DF, p2: Point3DF) = this(new Vector3DF((p1 - p0).cross(p2 - p0).normalize))

  override def normalize: Normal = {
    val v = super.normalize
    new Normal(v.x, v.y, v.z)
  }

  // unary minus
  override def negate(): Normal = new Normal(-x, -y, -z)

}

object Normal {
  val RIGHT: Normal = new Normal(1, 0, 0)
  val LEFT: Normal = new Normal(-1, 0, 0)
  val UP: Normal = new Normal(0, 1, 0)
  val DOWN: Normal = new Normal(0, -1, 0)
  val FRONT: Normal = new Normal(0, 0, 1)
  val BACK: Normal = new Normal(0, 0, -1)
  val ZERO: Normal = new Normal(0, 0, 0)
}

//

