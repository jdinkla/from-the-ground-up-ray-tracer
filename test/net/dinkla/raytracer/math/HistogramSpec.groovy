package net.dinkla.raytracer.math

import spock.lang.Specification

/**
 * Created by jdinkla on 06.06.17.
 */
class HistogramSpec extends Specification {

    def "an empty histogram has no keys"() {
        def h = new Histogram()
        expect: h.keySet().size() == 0
    }

    def "adding one element"() {
        def h = new Histogram()
        h.add(3)
        expect: h.keySet().size() == 1
    }

    def "adding one element repeatedly"() {
        def h = new Histogram()
        h.add(3)
        h.add(3)
        expect: h.keySet().size() == 1
    }

    def "adding two different elements"() {
        def h = new Histogram()
        h.add(3)
        h.add(3)
        h.add(2)
        h.add(2)
        expect:
        h.keySet().size() == 2
    }

    def "adding multiple different elements"() {
        def h = new Histogram()
        h.add(3)
        h.add(3)
        h.add(3)
        h.add(3)
        h.add(2)
        h.add(2)
        expect: h.get(3) == 4
        and: h.get(2) == 2
        and: h.get(0) == 0
    }

    def "clearing the histogram"() {
        def h = new Histogram()
        h.add(3)
        h.add(3)
        h.add(3)
        h.add(3)
        h.add(2)
        h.add(2)
        h.clear()
        expect: h.get(3) == 0
        and: h.get(2) == 0
        and: h.get(0) == 0
    }

}
