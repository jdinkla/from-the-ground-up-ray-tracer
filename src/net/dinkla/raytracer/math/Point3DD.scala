package net.dinkla.raytracer.math

/**
  * Created by Dinkla on 10.07.2015.
  */
final class Point3DD(x: Double, y: Double, z: Double) extends Point3D[Double](x, y, z) {

   override def length: Double = math.sqrt(sqrLength)

   override def unary_-(): Point3DD = new Point3DD(-x, -y, -z)

   override def plus(v: Vector3D[Double]): Point3D[Double] = new Point3DD(x + v.x, y + v.y, z + v.z)

   override def plus(p: Point3D[Double]): Vector3D[Double] = new Vector3DD(x + p.x, y + p.y, z + p.z)

   override def plus(f: Double): Point3D[Double] = new Point3DD(x + f, y + f, z + f)

   override def toVector: Vector3D[Double] = new Vector3DD(x, y, z)
 }
