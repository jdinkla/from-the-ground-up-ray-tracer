package net.dinkla.raytracer.math

/**
  * Created by Dinkla on 10.07.2015.
  */
abstract class Point3D[@specialized(Float, Double) F](x: F, y: F, z: F)(implicit numType: Fractional[F]) extends Element3D[F](x, y, z)  {

   import numType.mkNumericOps         // -f

   def unary_-(): Point3D[F]           // unary minus

   def +(v: Vector3D[F]): Point3D[F]        = plus(v)     // Point plus Vector gives a Point
   def +(p: Point3D[F]): Vector3D[F]        = plus(p)     // Point plus Point gives a Vector
   def +(f: F): Point3D[F]                  = plus(f)

   def plus(v: Vector3D[F]): Point3D[F]     // Point plus Vector gives a Point
   def plus(p: Point3D[F]): Vector3D[F]     // Point plus Point gives a Vector
   def plus(f: F): Point3D[F]

   def -(v: Vector3D[F]): Point3D[F]        = plus(-v)    // Point minus Vector gives a Point
   def -(p: Point3D[F]): Vector3D[F]        = plus(-p)    // Point minus Point gives a Vector
   def -(f: F)                              = plus(-f)
   def minus(v: Vector3D[F]): Point3D[F]    = plus(-v)    // Point minus Vector gives a Point
   def minus(p: Point3D[F]): Vector3D[F]    = plus(-p)    // Point minus Point gives a Vector
   def minus(f: F)                          = plus(-f)

  def toVector: Vector3D[F]

  override def equals(obj: scala.Any): Boolean
    = obj match {
    case that: Point3D[F] => x == that.x && y == that.y && z == that.z
    case _ => false
  }

  override def hashCode(): Int = (x, y, z).hashCode()

}

object Point3D {

  def Point3D(x: Double, y: Double, z: Double) = new Point3DD(x, y, z)

  def Point3D(x: Float, y: Float, z: Float) = new Point3DF(x, y, z)

  val ORIGIN: Point3DF = new Point3DF(0, 0, 0)
  val MAX: Point3DF = new Point3DF(Float.PositiveInfinity, Float.PositiveInfinity, Float.PositiveInfinity)
  val MIN: Point3DF = new Point3DF(Float.NegativeInfinity, Float.NegativeInfinity, Float.NegativeInfinity)
  val DEFAULT_CAMERA: Point3DF = new Point3DF(0, 10, 10)

}