package net.dinkla.raytracer.math

/**
  * Created by Dinkla on 10.07.2015.
  */
final class Point2DD(x: Double, y: Double) extends Point2D[Double](x, y) {

   override def length: Double = math.sqrt(sqrLength)

   override def unary_-(): Point2DD = new Point2DD(-x, -y)

   override def plus(v: Vector2D[Double]): Point2D[Double] = new Point2DD(x + v.x, y + v.y)

   override def plus(p: Point2D[Double]): Vector2D[Double] = new Vector2DD(x + p.x, y + p.y)

   override def toVector: Vector2D[Double] = new Vector2DD(x, y)
 }
