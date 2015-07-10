package net.dinkla.raytracer.math

/**
  * Created by Dinkla on 10.07.2015.
  */
final class Vector3DD(x: Double, y: Double, z: Double) extends Vector3D[Double](x, y, z) {

   override def length: Double = math.sqrt(sqrLength)

   override def unary_-(): Vector3D[Double] = new Vector3DD(-x, -y, -z)

   override def plus(v: Vector3D[Double]): Vector3D[Double] = new Vector3DD(x + v.x, y + v.y, z + v.z)

   override def mult(f: Double): Vector3D[Double] = new Vector3DD(x * f, y * f, z * f)

   override def cross(v: Vector3D[Double]): Vector3D[Double]
     = new Vector3DD(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x)

   override def dot(v: Vector3D[Double]): Double = x * v.x + y * v.y + z * v.z

   override def normalize: Vector3D[Double] = {
     val l = length
     new Vector3DD(x/l, y/l, z/l)
   }

   override def toPoint: Point3D[Double] = new Point3DD(x, y, z)
 }
