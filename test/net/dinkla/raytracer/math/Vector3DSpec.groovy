package net.dinkla.raytracer.math

import spock.lang.Specification

/**
 * Created by jdinkla on 10.06.17.
 */
class Vector3DSpec extends Specification {

    def v0 = new Vector3D(0, 0, 0)
    def v = new Vector3D(2, 3, 5)
    def w = new Vector3D(-2, -3, -5)

    final float a = 3
    final float b = 5
    final float c = 7
    final float d = 11
    final float e = 13
    final float f = 17

    def v1 = new Vector3D(a, b, c)
    def v2 = new Vector3D(d, e, f)

    def "add vector"() {
        expect: v.plus(w) == v0
    }

    def "subtract vector"() {
        expect: v.minus(v) == v0
    }

    def "multiply with a scalar"() {
        float s = 2.0
        expect: v.mult(s) == new Vector3D(v.x*s, v.y*s, v.z*s)
    }

    def "dot product with Vector2D"() {
        expect: v1.dot(v2) == a*d + b*e + c*f
    }

    def "dot product with Normal"() {
        expect: v1.dot(new Normal(d, e, f)) == a*d + b*e + c*f
    }

    def "cross product"() {
        expect: v1.cross(v2) == new Vector3D(v1.y * v2.z - v1.z * v2.y, v1.z * v2.x - v1.x * v2.z, v1.x * v2.y - v1.y * v2.x);
    }

    def "normalize"() {
        final double x = v.x/v.length()
        final double y = v.y/v.length()
        final double z = v.z/v.length()
        expect: v.normalize() == new Vector3D(x, y, z)
    }

    def "negate"() {
        expect: w.negate() == v
    }

}
