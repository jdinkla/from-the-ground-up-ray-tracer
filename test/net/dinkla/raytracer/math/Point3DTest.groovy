package net.dinkla.raytracer.math

import spock.lang.Specification

/**
 * Created by jdinkla on 07.06.17.
 */
class Point3DTest extends Specification {

    def "add a vector"() {
        def p = new Point3D(2, 3, 5)
        def v = new Vector3D(-2, -3, -5)
        def r = p.plus(v)
        expect: r == new Point3D(0, 0, 0)
    }

    def "add a scalar"() {
        def p = new Point3D(2, 3, 5)
        def v = new Float(-2)
        def r = p.plus(v)
        expect: r == new Point3D(0, 1, 3)
    }

    def "subtract a vector"() {
        def p = new Point3D(2, 3, 5)
        def v = new Vector3D(2, 3, 5)
        def r = p.minus(v)
        expect: r == new Point3D(0, 0, 0)
    }

    def "subtract a point"() {
        def p = new Point3D(2, 3, 5)
        def q = new Point3D(2, 3, 5)
        def r = p.minus(q)
        expect: r == new Vector3D(0, 0, 0)
    }

    def "subtract a scalar"() {
        def p = new Point3D(2, 3, 5)
        def v = new Float(2)
        def r = p.minus(v)
        expect: r == new Point3D(0, 1, 3)
    }

    def "construct from Element2D"() {
        final int x = 2
        final int y = 3
        final int z = 5
        def p = new Point3D(new Element3D(x, y, z))
        expect: p.x == x
        and: p.y == y
        and: p.z == z
    }

}
