package net.dinkla.raytracer.colors

import spock.lang.Specification
import static spock.util.matcher.HamcrestMatchers.closeTo

class ColorSpec extends Specification {

    def "asInt"() {
        def c = new Color(0.0, 0.0, 1.0);
        expect: c.asInt() == 255
    }

    def "createFromInt"() {
        double r = 3.0 / 255.0
        double g = 31.0 / 255.0
        double b = 139.0 / 255.0
        int rgb = new Color(r, g, b).asInt();
        def c = (Color) Color.WHITE.createFromInt(rgb)

        def r2 = c.red
        def g2 = c.green
        def b2 = c.blue
        expect:
                r2 closeTo (r, 0.01)
        and:    g2 closeTo (g, 0.01)
        and:    b2 closeTo (b, 0.01)
    }

}
