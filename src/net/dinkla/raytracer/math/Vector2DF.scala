package net.dinkla.raytracer.math

/**
  * Created by Dinkla on 10.07.2015.
  */
final class Vector2DF(x: Float, y: Float) extends Vector2D[Float](x, y) {

   override def length: Float = math.sqrt(sqrLength).toFloat

   override def unary_-(): Vector2D[Float] = new Vector2DF(-x, -y)

   override def plus(v: Vector2D[Float]): Vector2D[Float] = new Vector2DF(x + v.x, y + v.y)

   override def dot(v: Vector2D[Float]): Float = x * v.x + y * v.y

   override def normalize: Vector2D[Float] = {
     val l = length
     new Vector2DF(x / l, y / l)
   }

   override def toPoint: Point2D[Float] = new Point2DF(x, y)

 }
