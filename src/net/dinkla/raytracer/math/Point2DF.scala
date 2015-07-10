package net.dinkla.raytracer.math

/**
* Created by Dinkla on 10.07.2015.
*/

final class Point2DF(x2: Float, y2: Float) extends Point2D[Float](x2, y2) {

   override val x: Float = x2
   override val y: Float = y2

   override def length: Float = math.sqrt(sqrLength).toFloat

   override def unary_-(): Point2D[Float] = new Point2DF(-x, -y)

   override def plus(v: Vector2D[Float]): Point2D[Float] = new Point2DF(x + v.x, y + v.y)

   override def plus(p: Point2D[Float]): Vector2D[Float] = new Vector2DF(x + p.x, y + p.y)

   override def toVector: Vector2D[Float] = new Vector2DF(x, y)

}

