package net.dinkla.raytracer.colors

import spock.lang.Specification

/**
 * Created by jdinkla on 06.06.17.
 */
class RGBColorSpec extends Specification {

    def "asInt"() {
        def c1 = new RGBColor(0, 0, 1);
        expect: c1.asInt() == 255
    }

    def "createFromInt"() {
        float r = 3 / 255.0f;
        float g = 31 / 255.0f;
        float b = 139 / 255.0f;
        int rgb = new RGBColor(r, g, b).asInt();
        def c = (RGBColor) RGBColor.WHITE.createFromInt(rgb);
        expect: c.red == r
        and: c.green == g
        and: c.blue == b
    }

}
