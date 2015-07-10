package net.dinkla.raytracer.math

/**
 * Created by Dinkla on 10.07.2015.
 */

abstract class Point2D[@specialized(Float, Double) F](x: F, y: F)(implicit numType: Fractional[F]) extends Element2D[F](x, y)  {

  def unary_-(): Point2D[F]           // unary minus

  def +(v: Vector2D[F]): Point2D[F]        = plus(v)     // Point plus Vector gives a Point
  def +(p: Point2D[F]): Vector2D[F]        = plus(p)     // Point plus Point gives a Vector

  def plus(v: Vector2D[F]): Point2D[F]     // Point plus Vector gives a Point
  def plus(p: Point2D[F]): Vector2D[F]     // Point plus Point gives a Vector

  def -(v: Vector2D[F]): Point2D[F]        = plus(-v)    // Point minus Vector gives a Point
  def -(p: Point2D[F]): Vector2D[F]        = plus(-p)    // Point minus Point gives a Vector

  def minus(v: Vector2D[F]): Point2D[F]    = plus(-v)    // Point minus Vector gives a Point
  def minus(p: Point2D[F]): Vector2D[F]    = plus(-p)    // Point minus Point gives a Vector

  def toVector: Vector2D[F]

}

object Point2D {

  def Point2D(x: Double, y: Double) = new Point2DD(x, y)
  def Point2D(x: Float, y: Float) = new Point2DF(x, y)

}