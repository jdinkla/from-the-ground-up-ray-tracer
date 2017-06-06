package net.dinkla.raytracer.gui

import net.dinkla.raytracer.colors.RGBColor
import spock.lang.Specification

/**
 * Created by jdinkla on 06.06.17.
 */
class GuiUtilitiesSpec extends Specification {

    def "getOutputPngFileName #1"() {
        String s = GuiUtilities.getOutputPngFileName("World73.groovy");
        expect:
        s.substring(s.length() - 11) == "World73.png"
    }

    def "getOutputPngFileName #2"() {
        String s = GuiUtilities.getOutputPngFileName("ABC.World73.groovy");
        expect:
        s.substring(s.length() - 11) == "World73.png"
    }

}
