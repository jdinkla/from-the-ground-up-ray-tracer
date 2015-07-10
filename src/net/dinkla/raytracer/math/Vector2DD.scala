package net.dinkla.raytracer.math

/**
  * Created by Dinkla on 10.07.2015.
  */
final class Vector2DD(x: Double, y: Double) extends Vector2D[Double](x, y) {

   override def length: Double = math.sqrt(sqrLength)

   override def unary_-(): Vector2D[Double] = new Vector2DD(-x, -y)

   override def plus(v: Vector2D[Double]): Vector2D[Double] = new Vector2DD(x + v.x, y + v.y)

   override def dot(v: Vector2D[Double]): Double = x * v.x + y * v.y

   override def normalize: Vector2D[Double] = {
     val l = length
     new Vector2DD(x / l, y / l)
   }

   override def toPoint: Point2D[Double] = new Point2DD(x, y)
 }
