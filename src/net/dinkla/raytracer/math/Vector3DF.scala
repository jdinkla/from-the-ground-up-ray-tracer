package net.dinkla.raytracer.math

/**
  * Created by Dinkla on 10.07.2015.
  */
class Vector3DF(x2: Float, y2: Float, z2: Float) extends Vector3D[Float](x2, y2, z2) {

  override val x: Float = x2
  override val y: Float = y2
  override val z: Float = z2

  def this(v: Vector3D[Float]) = this(v.x, v.y, v.z)

  override def length: Float = math.sqrt(sqrLength).toFloat

   override def unary_-(): Vector3D[Float] = new Vector3DF(-x, -y, -z)

   override def plus(v: Vector3D[Float]): Vector3DF = new Vector3DF(x + v.x, y + v.y, z + v.z)

   //override def mult(f: Float): Vector3D[Float] = new Vector3DF(x * f, y * f, z * f)

   override def cross(v: Vector3D[Float]): Vector3DF
    = new Vector3DF(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x)

   override def dot(v: Vector3D[Float]): Float = x * v.x + y * v.y + z * v.z

   override def normalize: Vector3DF = {
     val l = length
     new Vector3DF(x/l, y/l, z/l)
   }

   override def toPoint: Point3D[Float] = new Point3DF(x, y, z)


  // temp
  override def mult(f: Float): Vector3DF = new Vector3DF(x * f, y * f, z * f)
  override def *(f: Float): Vector3DF = new Vector3DF(x * f, y * f, z * f)

  // unary minus
  override def negate(): Vector3DF = new Vector3DF(-x, -y, -z)

  override def getX: Float = x

  override def getY: Float = y

  override def getZ: Float = z
}
