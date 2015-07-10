package net.dinkla.raytracer.math

/**
  * Created by Dinkla on 10.07.2015.
  */
final class Point3DF(x2: Float, y2: Float, z2: Float) extends Point3D[Float](x2, y2, z2) {

   override val x: Float = x2
   override val y: Float = y2
   override val z: Float = z2

   override def length: Float = math.sqrt(sqrLength).toFloat

   override def unary_-(): Point3D[Float] = new Point3DF(-x, -y, -z)

   //override def plus(v: Vector3D[Float]): Point3D[Float] = new Point3DF(x + v.x, y + v.y, z + v.z)

  override def plus(p: Point3D[Float]): Vector3DF = new Vector3DF(x + p.x, y + p.y, z + p.z)
  override def +(v: Vector3D[Float]): Point3DF = plus(v)

   override def plus(f: Float): Point3DF = new Point3DF(x + f, y + f, z + f)

   override def toVector: Vector3DF = new Vector3DF(x, y, z)

  // temp
  override def plus(v: Vector3D[Float]): Point3DF = new Point3DF(x + v.x, y + v.y, z + v.z)

  override def minus(p: Point3D[Float]): Vector3DF = new Vector3DF(x - p.x, y - p.y, z - p.z)

  // Point minus Point gives a Vector
  override def minus(f: Float): Point3DF = new Point3DF(x - f, y - f, z - f)

  override def getX: Float = x

  override def getY: Float = y

  override def getZ: Float = z

}