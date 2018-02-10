package net.dinkla.raytracer.math

import spock.lang.Specification

/**
 * Created by jdinkla on 07.06.17.
 */
class AxisSpec extends Specification {

    def "FromInt"() {
        expect: Axis.fromInt(0) == Axis.X
        and: Axis.fromInt(1) == Axis.Y
        and: Axis.fromInt(2) == Axis.Z
    }

    def "Next"() {
        expect: Axis.X.next() == Axis.Y
        and: Axis.Y.next() == Axis.Z
        and: Axis.Z.next() == Axis.X
    }
}
