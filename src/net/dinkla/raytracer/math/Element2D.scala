package net.dinkla.raytracer.math

/**
  * Created by Dinkla on 10.07.2015.
  */
abstract class Element2D[F](val x: F, val y: F)(implicit numType: Fractional[F]) {

   import numType.mkNumericOps

   def sqrLength: F = x * x + y * y

   def length: F

   override def toString: String = s"($x, $y)"
 }
