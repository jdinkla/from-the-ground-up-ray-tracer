package net.dinkla.raytracer.math

import org.scalatest.FunSuite

/**
 * Created by Dinkla on 10.07.2015.
 */
class Point3Suite extends FunSuite {

  import Point3D._

  val p1 = Point3D(1.1, 2.2, 3.3)
  val p2 = Point3D(1.2, 2.3, 3.4)
  val p3 = Point3D(1.1, 2.2, 3.3)

  test("Constructor") {
    assert(p1.x == 1.1)
    assert(p1.y == 2.2)
    assert(p1.z == 3.3)
  }

  test("Equality") {
    assert(p1 == p1);
    assert(p1 != p2);
    assert(p1 == p3);
  }

}
