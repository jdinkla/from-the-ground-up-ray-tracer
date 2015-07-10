package net.dinkla.raytracer.math

/**
 * Created by Dinkla on 10.07.2015.
 */
class Basis(eye: Point3DF, lookAt: Point3DF, up: Vector3DF) {

  lazy val u: Vector3D[Float] = up.cross(w).normalize
  lazy val v: Vector3D[Float] = w.cross(u)
  lazy val w: Vector3D[Float] = eye.minus(lookAt).normalize

  def pm(x: Float, y: Float, z: Float): Vector3D[Float]
    = u * x + v * y - w * z

  def pp(x: Float, y: Float, z: Float): Vector3D[Float]
    = u * x + v * y + w * z

}
