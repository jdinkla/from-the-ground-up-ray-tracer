package net.dinkla.raytracer.math

/**
 * Created by Dinkla on 10.07.2015.
 */
abstract class Vector3D[@specialized(Float, Double) F](x: F, y: F, z: F)(implicit numType: Fractional[F]) extends Element3D[F](x, y, z) {

  def unary_-(): Vector3D[F]

  // unary minus
  def negate(): Vector3D[F] = -this

  def +(v: Vector3D[F]): Vector3D[F] = plus(v)

  // Vector addition gives Vector
  def plus(v: Vector3D[F]): Vector3D[F] // Vector addition gives Vector

  def -(v: Vector3D[F]): Vector3D[F] = plus(-v)

  // Vector addition gives Vector
  def minus(v: Vector3D[F]): Vector3D[F] = plus(-v) // Vector addition gives Vector

  def *(f: F): Vector3D[F] = mult(f)

  def mult(f: F): Vector3D[F]

  def cross(v: Vector3D[F]): Vector3D[F] // cross product

  def dot(v: Vector3D[F]): F // dot product

  def toPoint: Point3D[F]

  def normalize: Vector3D[F] // the normal vector with length 1

  override def equals(obj: scala.Any): Boolean
    = obj match {
      case that: Vector3D[F] => x == that.x && y == that.y && z == that.z
      case _ => false
    }

  override def hashCode(): Int = (x, y, z).hashCode()

}

object Vector3D {

  def Vector3D(x: Double, y: Double, z: Double) = new Vector3DD(x, y, z)

  def Vector3D(x: Float, y: Float, z: Float) = new Vector3DF(x, y, z)

  val ZERO: Vector3DF = new Vector3DF(0, 0, 0)
  val UP: Vector3DF = new Vector3DF(0, 1, 0)
  val DOWN: Vector3DF = new Vector3DF(0, -1, 0)
  val JITTER: Vector3DF = new Vector3DF(0.0072f, 1.0f, 0.0034f)
  val BACK: Vector3DF = new Vector3DF(0, 0, -1)

}
