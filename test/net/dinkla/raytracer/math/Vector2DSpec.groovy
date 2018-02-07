package net.dinkla.raytracer.math

import spock.lang.Specification

/**
 * Created by jdinkla on 10.06.17.
 */
class Vector2DSpec extends Specification {

    def v0 = new Vector2D(0, 0)
    def v = new Vector2D(2, 3)
    def w = new Vector2D(-2, -3)

    final float a = 3
    final float b = 5
    final float c = 7
    final float d = 11

    def "add vector"() {
        expect: v.plus(w) == v0
    }

    def "subtract vector"() {
        expect: v.minus(v) == v0
    }

    def "multiply with a scalar"() {
        final float s = 2.0
        expect: v.mult(s) == new Vector2D(v.x*s, v.y*s)
    }

    def "dot product with Vector2D"() {
        expect: new Vector2D(a, b).dot(new Vector2D(c, d)) == a*c + b*d
    }

    def "dot product with Normal"() {
        expect: new Vector2D(a, b).dot(new Normal(c, d, 0)) == a*c + b*d
    }

    def "normalize"() {
        final double x = v.x/v.length()
        final double y = v.y/v.length()
        expect: v.normalize() == new Vector2D(x, y)
    }
}
