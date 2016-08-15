package net.dinkla.raytracer.math

/**
 * Created by Dinkla on 10.07.2015.
 */
abstract class Vector2D[@specialized(Float, Double) F](x: F, y: F)(implicit numType: Fractional[F]) extends Element2D[F](x, y) {

  def unary_-(): Vector2D[F]           // unary minus

  def +(v: Vector2D[F]): Vector2D[F]        = plus(v)     // Vector addition gives Vector
  def plus(v: Vector2D[F]): Vector2D[F]     // Vector addition gives Vector

  def -(v: Vector2D[F]): Vector2D[F]        = plus(-v)  // Vector addition gives Vector
  def minus(v: Vector2D[F]): Vector2D[F]    = plus(-v)  // Vector addition gives Vector

  def dot(v: Vector2D[F]): F                // dot product

  def toPoint: Point2D[F]

  def normalize: Vector2D[F]                // the normal vector with length 1

  override def equals(obj: scala.Any): Boolean
  = obj match {
    case that: Vector2D[F] => x == that.x && y == that.y
    case _ => false
  }

  override def hashCode(): Int = (x, y).hashCode()

}

object Vector2D {

  def Vector2D(x: Double, y: Double) = new Vector2DD(x, y)
  def Vector2D(x: Float, y: Float) = new Vector2DF(x, y)

}
