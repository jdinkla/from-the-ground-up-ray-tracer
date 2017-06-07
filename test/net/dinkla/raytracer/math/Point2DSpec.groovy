package net.dinkla.raytracer.math

import spock.lang.Specification

/**
 * Created by jdinkla on 06.06.17.
 */
class Point2DSpec extends Specification {

    def "add a Vector2D"() {
        def p = new Point2D(1, 2)
        def v = new Vector2D(-1, -2)
        def r = p.plus(v)
        expect: r == new Point2D(0, 0)
    }

    def "subtract a Vector2D"() {
        def p = new Point2D(1, 2)
        def v = new Vector2D(1, 2)
        def r = p.minus(v)
        expect: r == new Point2D(0, 0)
    }

}
