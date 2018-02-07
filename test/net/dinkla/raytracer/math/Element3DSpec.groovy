package net.dinkla.raytracer.math

import spock.lang.Specification

import static java.lang.StrictMath.sqrt

/**
 * Created by jdinkla on 07.06.17.
 */
class Element3DSpec extends Specification {

    def "SqrLength"() {
        def e = new Element3D(2, 3, 5)
        expect: e.sqrLength() == 2*2 + 3*3 + 5*5
    }

    def "Length"() {
        def e = new Element3D(2, 3, 5)
        expect: e.length() == sqrt(2*2 + 3*3 + 5*5)
    }

    def "Equals"() {
        def e = new Element3D(2, 3, 5)
        expect: !e.equals(new Float(2))
        and: e.equals(new Element3D(2,3, 5))
    }

    def "ith"() {
        def e = new Element3D(2, 3, 5)
        expect: e.ith(Axis.X) == 2
        and: e.ith(Axis.Y) == 3
        and: e.ith(Axis.Z) == 5
    }

    def "distanceSquared"() {
        def e = new Element3D(2, 3, 5)
        def f = new Element3D(3, 5, 8)
        expect: e.distanceSquared(f) == (2-3)*(2-3)+(3-5)*(3-5)+(5-8)*(5-8)
    }
}
