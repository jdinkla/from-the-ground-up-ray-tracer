package net.dinkla.raytracer.colors

import spock.lang.Specification

/**
 * Created by jdinkla on 06.06.17.
 */
class ColorSpec extends Specification {

    def "asInt"() {
        def c1 = new Color(0, 0, 1);
        expect: c1.asInt() == 255
    }

    def "createFromInt"() {
        float r = 3 / 255.0f;
        float g = 31 / 255.0f;
        float b = 139 / 255.0f;
        int rgb = new Color(r, g, b).asInt();
        def c = (Color) Color.WHITE.createFromInt(rgb);
        expect: c.red == r
        and: c.green == g
        and: c.blue == b
    }

}
