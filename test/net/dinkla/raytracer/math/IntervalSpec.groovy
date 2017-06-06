package net.dinkla.raytracer.math

import spock.lang.Specification

/**
 * Created by jdinkla on 06.06.17.
 */
class IntervalSpec extends Specification {

    def "has a lower and upper bound"() {
        def i = new Interval(0.1f, 0.9f)
        expect:
        i.p == 0.1f
        and:
        i.q == 0.9f
    }

    def "contains"() {
        def i = new Interval(0.1f, 0.9f)
        expect: i.contains(0.1f)
        and: i.contains(0.2f)
        and: i.contains(0.9f)
        and: !i.contains(0.09f)
        and: !i.contains(0.91f)
    }

    def "isDisjointTo"() {
        final def i0 = new Interval(Integer.MIN_VALUE, 2)
        final def i1 = new Interval(1, 2)
        final def i2 = new Interval(2, 3)
        final def i3 = new Interval(3, 4)
        final def i4 = new Interval(4, Integer.MAX_VALUE)
        expect: i1.isDisjointTo(i3)
        and: !i1.isDisjointTo(i2)
        and: !i2.isDisjointTo(i3)
        and: i3.isDisjointTo(i1)
        and: !i2.isDisjointTo(i1)
        and: !i3.isDisjointTo(i2)
        and: i0.isDisjointTo(i3)
        and: !i0.isDisjointTo(i2)
        and: i3.isDisjointTo(i0)
        and: !i2.isDisjointTo(i0)
        and: i0.isDisjointTo(i4)
        and: i4.isDisjointTo(i0)
        and: !i4.isDisjointTo(i3)
        and: !i3.isDisjointTo(i4)
    }

    def "partialOverlaps"() {
        final def i0 = new Interval(Float.NEGATIVE_INFINITY, 2.5f)
        final def i1 = new Interval(1.0f, 2.5f)
        final def i2 = new Interval(2.0f, 3.5f)
        final def i3 = new Interval(3.0f, 4.5f)
        final def i4 = new Interval(4.0f, Float.POSITIVE_INFINITY)

        expect: !i1.partialOverlaps(i3)
        and: !i3.partialOverlaps(i1)

        and: i1.partialOverlaps(i2)
        and: i2.partialOverlaps(i1)

        and: i2.partialOverlaps(i3)
        and: i3.partialOverlaps(i2)

        and: !i0.partialOverlaps(i3)
        and: !i3.partialOverlaps(i0)

        and: i0.partialOverlaps(i2)
        and: i2.partialOverlaps(i0)

        and: !i0.partialOverlaps(i4)
        and: !i4.partialOverlaps(i0)

        and: i4.partialOverlaps(i3)
        and: i3.partialOverlaps(i4)
    }

    def "fullyOverlaps"() {
        final def  i0 = new Interval(Float.NEGATIVE_INFINITY, 2.5f)
        final def  i1 = new Interval(1.0f, 2.0f)
        final def  i2 = new Interval(1.0f, 2.5f)
        final def  i3 = new Interval(1.0f, 3.5f)

        expect: i0.fullyOverlaps(i1)
        and: i0.fullyOverlaps(i2)
        and: !i0.fullyOverlaps(i3)

        and: !i1.fullyOverlaps(i0)
        and: !i2.fullyOverlaps(i0)
        and: i3.fullyOverlaps(i2)
    }
}
