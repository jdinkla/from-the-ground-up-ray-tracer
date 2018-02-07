package net.dinkla.raytracer.math

import spock.lang.Specification

import static java.lang.StrictMath.sqrt

/**
 * Created by jdinkla on 07.06.17.
 */
class Element2DSpec extends Specification {

    def "SqrLength"() {
        def e = new Element2D(2, 5)
        expect: e.sqrLength() == 2*2 + 5*5
    }

    def "Length"() {
        def e = new Element2D(2, 5)
        expect: e.length() == sqrt(2*2 + 5*5)
    }

    def "Equals"() {
        def e = new Element2D(2, 5)
        expect: !e.equals(new Float(3))
        and: e.equals(new Element2D(2, 5))
    }
}
