package net.dinkla.raytracer.math

/**
 * Created by Dinkla on 10.07.2015.
 */
class Ray(val o: Point3DF, val d: Vector3DF) {

  def this(ray: Ray) = this(ray.o, ray.d)

  def linear(t: Float): Point3DF = o + (d * t)

  override def toString: String = s"Ray($o, $d)"

  def getO = o
  def getD = d

}

/*

later:

class Ray[F](val o: Point3D[F], val d: Vector3D[F]) {

  def this(ray: Ray) = this(ray.o, ray.d)

  def linear(t: F): Point3D[F] = o + (d * t)

  override def toString: String = s"Ray($o, $d)"
}

*/

