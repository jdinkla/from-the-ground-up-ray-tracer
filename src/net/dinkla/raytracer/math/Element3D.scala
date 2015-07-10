package net.dinkla.raytracer.math

/**
  * Created by Dinkla on 10.07.2015.
  */
abstract class Element3D[F](val x: F, val y: F, val z: F)(implicit numType: Fractional[F]) {

  import numType.mkNumericOps

  //def this(e: Element3D[F]) = this(e.x, e.y, e.z)

  def sqrLength: F = x * x + y * y + z * z

  def length: F

  def distanceSquared(p: Element3D[F]): F = (x - p.x) * (x - p.x) + (y - p.y) * (y - p.y) + (z - p.z) * (z - p.z)

  def ith(axis: Axis): F = {
    axis match {
      case Axis.X => return x
      case Axis.Y => return y
      case Axis.Z => return z
    }
  }
//  def ith(axis: Axis.Type): F = {
//    axis match {
//      case Axis.X =>
//        return x
//      case Axis.Y =>
//        return y
//      case Axis.Z =>
//        return z
//    }
//  }

  override def toString: String = s"($x, $y, $z)"

  def getX = x
  def getY = y
  def getZ = z

}
